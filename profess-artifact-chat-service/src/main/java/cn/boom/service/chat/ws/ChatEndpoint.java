package cn.boom.service.chat.ws;

import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.utils.CallResultHandler;
import cn.boom.framework.common.utils.JsonUtils;
import cn.boom.framework.model.entity.TbUser;
import cn.boom.framework.model.model.MessageEntity;
import cn.boom.framework.model.vo.ChatFromClientMessageVo;
import cn.boom.framework.model.vo.ChatFromServerMessageVo;
import cn.boom.service.chat.client.UserServiceClient;
import cn.boom.service.chat.config.SpringUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/chat/{id}")
public class ChatEndpoint {

    private Long userId;

    private UserServiceClient userServiceClient = SpringUtil.getBean(UserServiceClient.class);

    private Session session;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。
     */
    private static CopyOnWriteArraySet<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();

    @OnOpen//建立连接
    public void onOpen(Session session, EndpointConfig config,@PathParam("id") Long userId) {

        this.session = session;
        this.userId = userId;

        if (!chatEndpoints.contains(this)) {
            // 加入在线WebSocket集合
            chatEndpoints.add(this);
        }

        System.out.println("userId=" + this.userId + " 连接，当前连接用户数：" + chatEndpoints.size());
    }

    @OnMessage
    public void onMessage(String message,Session session) {
        sendP2PMessage(message);
    }

    @OnClose
    public void onClose(Session session) {
        chatEndpoints.remove(this);
        System.out.println("断开连接：user id is " + userId + "，当前连接用户数：" + chatEndpoints.size());
    }

    /**
     * 点对点广播消息
     * @param jsonMsg 消息JSON
     */
    private void sendP2PMessage(String jsonMsg){

        ChatFromClientMessageVo clientMessageVo = JsonUtils.toBean(jsonMsg, ChatFromClientMessageVo.class);

        System.out.println(clientMessageVo);

        // 参数校验
        if (clientMessageVo == null || clientMessageVo.getFromUserId() == null
                || clientMessageVo.getToUserId() == null || clientMessageVo.getMessage() == null) {
            sendP2PSystemMessage(session, new MessageEntity("参数不合法！"), 400);
            return;
        }

        TbUser fromUser = CallResultHandler.getData(userServiceClient.findOneById(clientMessageVo.getFromUserId()), "data", TbUser.class);

        TbUser toUser = CallResultHandler.getData(userServiceClient.findOneById(clientMessageVo.getToUserId()), "data", TbUser.class);

        if (fromUser == null || toUser == null) {
            sendP2PSystemMessage(session, new MessageEntity(ExceptionCodeEnum.DATA_NOT_EXIST_EXCEPTION.getMsg()), 400);
            return;
        }

        // 参数校验结束

        if (!isOnLine(clientMessageVo.getToUserId())) {
            sendP2PSystemMessage(session, new MessageEntity("对方未上线！"), 400);
            return;
        }

        // 封装服务端发送vo
        ChatFromServerMessageVo serverMessageVo = new ChatFromServerMessageVo();
        serverMessageVo.setFromUserId(clientMessageVo.getFromUserId());
        serverMessageVo.setSystem(false);
        serverMessageVo.setCode(200);
        serverMessageVo.setMessage(clientMessageVo.getMessage());
        serverMessageVo.setSendTime(new Date());

        // 给 toUser 发送message
        for (ChatEndpoint chatEndpoint : chatEndpoints) {

            if (chatEndpoint.userId.equals(clientMessageVo.getToUserId())) {
                try {
                    chatEndpoint.session.getBasicRemote().sendText(JsonUtils.toString(serverMessageVo));
                } catch (IOException e) {
                    e.printStackTrace();
                    sendP2PSystemMessage(session,new MessageEntity("消息发送失败！"),500);
                }
                return;
            }
        }
    }

    private void sendP2PSystemMessage(Session session, MessageEntity entity, Integer code) {

        ChatFromServerMessageVo vo = new ChatFromServerMessageVo();
        vo.setSystem(true);
        vo.setCode(code);
        vo.setSendTime(new Date());
        vo.setFromUserId(null);
        vo.setMessage(entity);
        try {
            session.getBasicRemote().sendText(JsonUtils.toString(vo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnLine(Long userId) {

        for (ChatEndpoint endpoint : chatEndpoints) {
            if (endpoint.userId.equals(userId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatEndpoint)) return false;

        ChatEndpoint that = (ChatEndpoint) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userServiceClient != null ? !userServiceClient.equals(that.userServiceClient) : that.userServiceClient != null)
            return false;
        return session != null ? session.equals(that.session) : that.session == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (userServiceClient != null ? userServiceClient.hashCode() : 0);
        result = 31 * result + (session != null ? session.hashCode() : 0);
        return result;
    }
}
