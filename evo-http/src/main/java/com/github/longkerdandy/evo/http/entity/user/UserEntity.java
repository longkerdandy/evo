package com.github.longkerdandy.evo.http.entity.user;

/**
 * User related entity
 */
@SuppressWarnings("unused")
public class UserEntity {

    private String id;              // user id
    private String alias;           // nick name
    private String mobile;          // mobile phone
    private String verifyCode;      // verify code
    private String password;        // password

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

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
