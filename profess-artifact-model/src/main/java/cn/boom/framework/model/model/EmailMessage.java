package cn.boom.framework.model.model;

import java.io.Serializable;

public class EmailMessage implements Serializable {

    private String toEmail;

    private String title;

    private String text;

    public EmailMessage() {
    }

    public EmailMessage(String toEmail, String title, String text) {
        this.toEmail = toEmail;
        this.title = title;
        this.text = text;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "EmailMessage{" +
                "toEmail='" + toEmail + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
