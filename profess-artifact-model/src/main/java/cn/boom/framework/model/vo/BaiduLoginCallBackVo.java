package cn.boom.framework.model.vo;

import lombok.Data;

@Data
public class BaiduLoginCallBackVo {

    private String errno;
    private String error;
    private String error_description;
    private String openid;
    private String session_key;
}
