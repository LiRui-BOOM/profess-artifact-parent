package cn.boom.framework.model.model;


import lombok.Data;

import java.io.Serializable;

@Data
public class MessageEntity implements Serializable {

    private String msg;

    public MessageEntity() {
    }

    public MessageEntity(String msg) {
        this.msg = msg;
    }
}
