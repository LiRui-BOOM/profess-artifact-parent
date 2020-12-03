package cn.boom.framework.model.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class BaiduCryptVo implements Serializable {

    private String encryptedData;
    private String errno;
    private String error;
    private String iv;
}
