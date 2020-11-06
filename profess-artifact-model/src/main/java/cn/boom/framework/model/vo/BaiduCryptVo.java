package cn.boom.framework.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaiduCryptVo implements Serializable {

    private String data;
    private String errno;
    private String error;
    private String iv;
}
