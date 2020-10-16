package cn.boom.framework.model.model;


import lombok.Data;

import java.util.Date;

@Data
public class MessageEntity {

    private String msg;

    public MessageEntity() {
    }

    public MessageEntity(String msg) {
        this.msg = msg;
    }
}
