package com.github.longkerdandy.evo.api.message;

/**
 * Connect Message
 */
@SuppressWarnings("unused")
public class ConnectMessage {

    private String protocolVersion; // Protocol Version
    private String gateway;         // Gateway Device ID
    private String user;            // User ID (as controller)
    private String token;           // Token

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

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
