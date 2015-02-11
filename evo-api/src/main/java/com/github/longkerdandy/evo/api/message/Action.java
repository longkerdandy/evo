package com.github.longkerdandy.evo.api.message;

import java.util.Map;

/**
 * Action
 * Device receives certain type of command(action) and changes its state
 */
@SuppressWarnings("unused")
public class Action {

    private String actionId;                // Action Id
    private int lifetime;                   // Action Lifetime, seconds until this action expires
    private Map<String, Object> attributes; // Attributes

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
}
