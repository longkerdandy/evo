package com.github.longkerdandy.evo.api.message;

/**
 * TrigAck Message
 * Acknowledge for Trigger Message
 */
@SuppressWarnings("unused")
public class TrigAck {

    // Return Codes
    public static final int SUCCESS = 100;

    private String trigMsgId;       // TriggerMessage's message id
    private int returnCode;         // Return Code

    public String getTrigMsgId() {
        return trigMsgId;
    }

    public void setTrigMsgId(String trigMsgId) {
        this.trigMsgId = trigMsgId;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
