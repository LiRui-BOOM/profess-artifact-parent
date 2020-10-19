package cn.boom.service.note.controller;

import cn.boom.framework.common.response.R;
import cn.boom.framework.common.utils.TokenUtils;
import cn.boom.framework.model.enums.TencentSignEnum;
import cn.boom.framework.model.enums.TencentTemplateEnum;
import cn.boom.framework.model.model.SMSParameter;
import cn.boom.service.note.utils.TencentSmsUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("sms")
public class SmsTestController {

    @GetMapping("/test/{phone}")
    public R sendSmsTest(@PathVariable("phone") String phone) {
        SMSParameter parameter = new SMSParameter();
        ArrayList<String> params = new ArrayList<>();
        params.add(TokenUtils.getToken());
        parameter.setParams(params);
        parameter.setPhoneNumber(phone);
        parameter.setSmsSign(TencentSignEnum.TENCENT_SIGN_YAO_CHUAN_BU_YONG_JIANG.getSign());
        parameter.setTemplateId(TencentTemplateEnum.TENCENT_TEMPLATE_CHECKCODE.getTemplateId());

        return R.ok().put("data", TencentSmsUtil.sendSms(parameter));
    }
}
