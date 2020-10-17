package cn.boom.service.chat.controller;

import cn.boom.framework.common.response.R;
import cn.boom.service.chat.ws.ChatEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatEndpoint chatEndpoint;

    @GetMapping("/isLogin/{userId}")
    public R isLogin(@PathVariable("userId") Long userId) {
        return R.ok().put("data", chatEndpoint.isOnLine(userId));
    }
}
