package com.diboot.iam.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class BaseUserInfoDTO implements Serializable {
    private static final long serialVersionUID = -3940498946290302396L;

    @NotNull(message = "真实姓名不能为空")
    @Length(max = 50, message = "真实姓名长度应小于50")
    private String realname;

    @NotNull(message = "性别不能为空")
    @Length(max = 10, message = "性别长度应小于10")
    private String gender;

    @Length(max = 20, message = "手机号长度应小于20")
    private String mobilePhone;

    @Length(max = 50, message = "Email长度应小于50")
    private String email;

    public String getRealname() {
        return realname;
    }
    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
