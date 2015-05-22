package com.github.longkerdandy.evo.api.message;

/**
 * DisconnAck Message
 * Acknowledge for Disconnect Message
 */
@SuppressWarnings("unused")
public class DisconnAck {

    // Return Code
    public static final int RECEIVED = 100;                 // Message received and re-directed to followers

    protected String disconnMsgId;       // DisconnectMessage's message id
    protected int returnCode;            // Return Code

    public String getDisconnMsgId() {
        return disconnMsgId;
    }

    public void setDisconnMsgId(String disconnMsgId) {
        this.disconnMsgId = disconnMsgId;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
