package com.github.longkerdandy.evo.api.message;

/**
 * TrigAck Message
 * Acknowledge for Trigger Message
 */
@SuppressWarnings("unused")
public class TrigAck {

    // Return Code
    public static final int RECEIVED = 100;                 // Message received and re-directed to followers

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
