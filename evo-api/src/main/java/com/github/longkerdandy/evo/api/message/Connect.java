package com.github.longkerdandy.evo.api.message;

import java.util.Map;

/**
 * Connect Message
 * Must be sent before all other messages when device connecting to the Cloud
 */
@SuppressWarnings("unused")
public class Connect {

    private String token;                   // Token
    private int policy;                     // Attributes Override Policy
    private Map<String, Object> attributes; // Attributes

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPolicy() {
        return policy;
    }

    public void setPolicy(int policy) {
        this.policy = policy;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
