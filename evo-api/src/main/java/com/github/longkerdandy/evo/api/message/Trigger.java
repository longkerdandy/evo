package com.github.longkerdandy.evo.api.message;

import com.github.longkerdandy.evo.api.protocol.OverridePolicy;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Trigger
 * Device sends certain type of notification(trigger) when state changed
 */
@SuppressWarnings("unused")
public class Trigger implements Validatable {

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

    @Override
    public void validate() {
        if (StringUtils.isBlank(this.triggerId)) {
            throw new IllegalStateException("Invalid trigger id");
        }

        if (!OverridePolicy.isValid(this.policy)) {
            throw new IllegalStateException("Invalid override policy");
        }
    }
}
