package com.github.longkerdandy.evo.api.message;

import java.util.Map;

/**
 * Connect Message
 * Must be sent before all other messages when device connecting to the Cloud
 */
@SuppressWarnings("unused")
public class ConnectMessage {

    private String protocolVersion;         // Protocol Version
    private String description;             // Device Description (File) Id
    private String user;                    // User ID (as controller)
    private String token;                   // Token
    private Map<String, Object> attributes; // attributes

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
