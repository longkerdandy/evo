package com.github.longkerdandy.evo.aerospike.entity;

import java.util.List;
import java.util.Map;

/**
 * User Entity
 */
@SuppressWarnings("unused")
public class User {

    private String id;                          // id
    private String alias;                       // nick name
    private String email;                       // email
    private String mobile;                      // mobile phone
    private String password;                    // password (encoded before saving to db)
    private List<Map<String, Object>> own;      // own relations
    private List<String> ctrl;                  // control relations

    protected User() {
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<Map<String, Object>> getOwn() {
        return own;
    }

    public void setOwn(List<Map<String, Object>> own) {
        this.own = own;
    }

    public List<String> getCtrl() {
        return ctrl;
    }

    public void setCtrl(List<String> ctrl) {
        this.ctrl = ctrl;
    }
}
