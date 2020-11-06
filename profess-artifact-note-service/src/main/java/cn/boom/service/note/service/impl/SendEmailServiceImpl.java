package cn.boom.service.note.service.impl;

import cn.boom.framework.common.exception.ExceptionCast;
import cn.boom.framework.common.exception.ExceptionCodeEnum;
import cn.boom.framework.common.utils.JsonUtils;
import cn.boom.framework.common.utils.RegexUtils;
import cn.boom.framework.model.model.EmailMessage;
import cn.boom.service.note.service.SendEmailService;
import cn.boom.service.note.utils.AliyunEmailUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    @RabbitListener(queues = "profess.email")
    @Override
    public void SendEmail(Message message) {
        // 解析数据
        String json = new String(message.getBody());

        EmailMessage emailMessage = JsonUtils.toBean(json, EmailMessage.class);

        if (emailMessage == null || !RegexUtils.validateEmail(emailMessage.getToEmail())
                || StringUtils.isEmpty(emailMessage.getTitle())|| StringUtils.isEmpty(emailMessage.getText())) {
            ExceptionCast.cast("EmailMessage:参数不合法！", ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode());
        }

        System.out.println("发送邮件：" + emailMessage.getToEmail());
        AliyunEmailUtil.sendSampleEmail(emailMessage.getToEmail(), emailMessage.getTitle(), emailMessage.getText());
    }
}
