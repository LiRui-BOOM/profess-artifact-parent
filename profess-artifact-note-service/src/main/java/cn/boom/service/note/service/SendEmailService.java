package cn.boom.service.note.service;

import org.springframework.amqp.core.Message;

public interface SendEmailService {

    public abstract void SendEmail(Message message);
}
