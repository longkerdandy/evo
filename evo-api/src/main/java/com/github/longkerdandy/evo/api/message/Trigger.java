package com.github.longkerdandy.evo.api.message;

import java.util.Map;

/**
 * Trigger
 * Device sends certain type of notification(trigger) when state changed
 */
@SuppressWarnings("unused")
public class Trigger {

    protected String triggerId;               // Trigger Id
    protected int policy;                     // Attributes Override Policy
    protected Map<String, Object> attributes; // Attributes

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
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
