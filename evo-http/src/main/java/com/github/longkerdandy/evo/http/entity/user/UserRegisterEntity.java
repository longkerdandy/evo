package com.github.longkerdandy.evo.http.entity.user;

/**
 * User register related entity
 */
@SuppressWarnings("unused")
public class UserRegisterEntity {

    private String id;              // user id
    private String alias;           // nick name
    private String mobile;          // mobile phone
    private String password;        // password
    private String verifyCode;      // verify code

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
