package cn.boom.framework.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChatFromServerMessageVo implements Serializable {

    private boolean isSystem;

    private Integer code;

    private Long fromUserId;

    private Object message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8") //Jackson包使用注解
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sendTime;
}
