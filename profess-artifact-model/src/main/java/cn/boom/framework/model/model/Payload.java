package cn.boom.framework.model.model;

import java.util.Date;

/**
 * token 载荷对象
 * @param <T>
 */
public class Payload<T> {

    private String id;
    private T userInfo;
    private Date expiration;//过期时间

    public Payload() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(T userInfo) {
        this.userInfo = userInfo;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
