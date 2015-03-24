package com.github.longkerdandy.evo.api.message;

/**
 * DisconnAck Message
 * Acknowledge for Disconnect Message
 */
@SuppressWarnings("unused")
public class DisconnAck {

    // Return Codes, from Platform to Device
    public static final int RECEIVED = 100;                 // Message received and re-directed to followers

    private String disconnMsgId;       // DisconnectMessage's message id
    private int returnCode;            // Return Code

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
