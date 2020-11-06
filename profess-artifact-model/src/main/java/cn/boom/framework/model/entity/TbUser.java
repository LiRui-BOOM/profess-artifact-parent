package cn.boom.framework.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class TbUser implements Serializable, UserDetails {

    private Long id;
    private String openId;

    @TableField(exist = false)
    private String sessionKey;
    private String username;
    private String password;
    private String status;
    private String nickName;
    private String userPic;
    private String phone;
    private String email;
    private String vxQq;
    private Integer age;
    private String sex;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8") //Jackson包使用注解
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private Integer height;
    private Double weight;
    private Integer sign;
    private String region;
    private String educate;
    private String loveHistory;
    private String aprisKeyWords;
    private String aprisLooksLevel;
    private String aprisSelfLove;
    private String aprisSelfHobby;

    @TableField(exist = false)
    private List<TbRole> roleList;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return status.equals("1") || status.equals("2");
    }

}
