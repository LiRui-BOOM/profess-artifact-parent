package cn.boom.framework.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordByEmailVo implements Serializable {

    private String email;
    private String token;
    private String newPassword;
}
