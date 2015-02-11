package com.github.longkerdandy.evo.api.message;

import java.util.Map;

/**
 * ActAck Message
 * Acknowledge for Action Message
 */
@SuppressWarnings("unused")
public class ActAck {

    // Return Codes
    public static final int SUCCESS = 100;
    public static final int SUCCESS_WITH_TRIGGER = 101;

    private String actMsgId;                // ActionMessage's message id
    private int returnCode;                 // Return Code
    private String triggerId;               // Trigger Id
    private int policy;                     // Attributes Override Policy
    private Map<String, Object> attributes; // Attributes

    public String getActMsgId() {
        return actMsgId;
    }

    public void setActMsgId(String actMsgId) {
        this.actMsgId = actMsgId;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

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
