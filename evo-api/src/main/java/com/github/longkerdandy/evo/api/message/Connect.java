package com.github.longkerdandy.evo.api.message;

import com.github.longkerdandy.evo.api.protocol.OverridePolicy;

import java.util.Map;

/**
 * Connect Message
 * Must be sent before all other messages when device connecting to the Cloud
 */
@SuppressWarnings("unused")
public class Connect implements Validatable {

    protected String token;                   // Token
    protected int policy;                     // Attributes Override Policy
    protected Map<String, Object> attributes; // Attributes

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

    @Override
    public void validate() {
        if (!OverridePolicy.isValid(this.policy)) {
            throw new IllegalStateException("Invalid override policy");
        }
    }
}
