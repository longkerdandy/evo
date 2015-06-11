package com.github.longkerdandy.evo.api.message;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Action
 * Device receives certain type of command(action) and changes its state
 */
@SuppressWarnings("unused")
public class Action implements Validatable {

    protected String actionId;                // Action Id
    protected int lifetime;                   // Action Lifetime, seconds until this action expires
    protected Map<String, Object> attributes; // Attributes

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(this.actionId)) {
            throw new IllegalStateException("Invalid action id");
        }

        if (this.lifetime < 0) {
            throw new IllegalStateException("Invalid lifetime");
        }
    }
}
