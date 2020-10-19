package cn.boom.framework.model.model;

import lombok.Data;

import java.util.ArrayList;

/**
 *  封装发送短信所需的参数
 */
@Data
public class SMSParameter {

    // 短信模板ID，需要在短信控制台中申请，我们查看自己的短信模板ID即可
    private int templateId;

    // 签名，签名参数使用的是`签名内容`，而不是`签名ID`，真实的签名需要在短信控制台申请，这里按自己的来修改就好
    private String smsSign;

    // 需要发送短信的手机号码
    private String phoneNumber;

    // 插值参数
    private ArrayList<String> params;

}
