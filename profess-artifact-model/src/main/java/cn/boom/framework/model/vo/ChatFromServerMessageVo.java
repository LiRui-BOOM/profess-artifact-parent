package cn.boom.framework.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ChatFromServerMessageVo {

    private boolean isSystem;

    private Integer code;

    private Long fromUserId;

    private Object message;

    private Date sendTime;
}
