package com.github.longkerdandy.evo.api.message;

/**
 * Connect Message
 */
@SuppressWarnings("unused")
public class ConnectMessage {

    private String user;        // User ID (as controller)
    private String token;       // Token

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
