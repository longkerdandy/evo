package com.github.longkerdandy.evo.api.message;

/**
 * TrigAck Message
 * Acknowledge for Trigger Message
 */
@SuppressWarnings("unused")
public class TrigAck {

    // Return Codes, from Platform to Device
    public static final int RECEIVED = 100;                 // Message received and re-directed to followers
    public static final int TIMESTAMP_NOT_SATISFIED = 105;  // Message received but device status not updated due to policy

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
