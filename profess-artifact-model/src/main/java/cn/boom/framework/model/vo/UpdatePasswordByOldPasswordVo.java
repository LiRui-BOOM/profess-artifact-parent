package cn.boom.framework.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordByOldPasswordVo implements Serializable {

    private Long userId;
    private String oldPassword;
    private String newPassword;
}
