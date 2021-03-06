package com.github.longkerdandy.evo.api.message;

import com.github.longkerdandy.evo.api.protocol.OverridePolicy;

import java.util.Map;

/**
 * Disconnect Message
 * Device/Gate should try to notify platform when device disconnecting to the Cloud
 */
@SuppressWarnings("unused")
public class Disconnect implements Validatable {

    protected int policy;                     // Attributes Override Policy
    protected Map<String, Object> attributes; // Attributes

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
