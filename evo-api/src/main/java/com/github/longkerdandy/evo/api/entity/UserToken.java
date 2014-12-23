package com.github.longkerdandy.evo.api.entity;

import com.arangodb.annotations.DocumentKey;

/**
 * User Token
 */
@SuppressWarnings("unused")
public class UserToken {

    @DocumentKey
    private String id;      // id
    private String user;    // user id
    private String device;  // device id
    private String token;   // token

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
