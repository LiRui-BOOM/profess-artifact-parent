package cn.boom.framework.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TbUserProfess {

    private Long id;
    private Long confessorId;
    private Long confessedId;
    private String title;
    private String context;
    private String isSend;
    private Date updated;
    private Date created;
}
