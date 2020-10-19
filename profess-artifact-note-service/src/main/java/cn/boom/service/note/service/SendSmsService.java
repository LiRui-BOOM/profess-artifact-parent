package cn.boom.service.note.service;

import org.springframework.amqp.core.Message;

public interface SendSmsService {

    public abstract void SendSms(Message message);
}
