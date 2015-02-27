package com.github.longkerdandy.evo.api.message;

/**
 * DisconnAck Message
 * Acknowledge for Disconnect Message
 */
@SuppressWarnings("unused")
public class DisconnAck {

    // Return Codes
    public static final int SUCCESS = 100;
    public static final int ALREADY_RECONNECTED = 101;
    public static final int TIMESTAMP_FAIL = 102;

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
