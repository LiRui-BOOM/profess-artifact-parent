package cn.boom.framework.model.vo;

import lombok.Data;


@Data
public class ChatFromClientMessageVo {

    private Long fromUserId;

    private Long toUserId;

    private Object message;
}
