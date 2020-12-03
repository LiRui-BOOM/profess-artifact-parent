package cn.boom.framework.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProfessRecordVo implements Serializable {

    private Long professRecordId;
    private Long fromId;
    private String toName;
    private String toPhone;
    private String toVxQq;
    private String toEmail;
}
