package cn.boom.framework.model.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class ChatFromClientMessageVo implements Serializable {

    private Long fromUserId;

    private Long toUserId;

    private Object message;
}
