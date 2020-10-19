package cn.boom.service.note.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.utils.JsonUtils;
import cn.boom.framework.common.utils.RegexUtils;
import cn.boom.framework.model.enums.TencentSignEnum;
import cn.boom.framework.model.enums.TencentTemplateEnum;
import cn.boom.framework.model.model.SMSParameter;
import cn.boom.service.note.service.SendSmsService;
import cn.boom.service.note.utils.TencentSmsUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SendSmsServiceImpl implements SendSmsService {

    @RabbitListener(queues = "profess.sms")
    @Override
    public void SendSms(Message message) {

        // 解析数据
        String json = new String(message.getBody());

        SMSParameter smsParameter = JsonUtils.toBean(json, SMSParameter.class);

        if (smsParameter == null || smsParameter.getParams() == null || smsParameter.getParams().size() == 0
                || !TencentSignEnum.contains(smsParameter.getSmsSign()) || !RegexUtils.validateMobilePhone(smsParameter.getPhoneNumber())
                || !TencentTemplateEnum.contains(smsParameter.getTemplateId())) {
            ExceptionCast.cast("参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        TencentSmsUtil.sendSms(smsParameter);
    }
}
