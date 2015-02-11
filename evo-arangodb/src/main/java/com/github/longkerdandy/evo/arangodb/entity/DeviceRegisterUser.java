package com.github.longkerdandy.evo.arangodb.entity;

import com.arangodb.annotations.DocumentKey;

/**
 * Device Register User Relation
 */
@SuppressWarnings("unused")
public class DeviceRegisterUser {

    @DocumentKey
    private String id;      // id
    private String token;   // token

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
