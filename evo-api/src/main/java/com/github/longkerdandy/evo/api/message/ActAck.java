package com.github.longkerdandy.evo.api.message;

/**
 * ActAck Message
 * Acknowledge for Action Message
 */
@SuppressWarnings("unused")
public class ActAck {

    // Return Code
    public static final int RECEIVED = 100;                 // Message received and re-directed to the target device
    public static final int PERMISSION_INSUFFICIENT = 110;  // User doesn't has enough permission to execute the action

    protected String actMsgId;                // ActionMessage's message id
    protected int returnCode;                 // Return Code

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
}
