package com.github.longkerdandy.evo.api.message;

/**
 * ConnAck Message
 * Acknowledge for Connect Message
 */
@SuppressWarnings("unused")
public class ConnAck {

    // Return Codes
    public static final int SUCCESS = 100;
    public static final int EMPTY_USER_OR_TOKEN = 101;
    public static final int USER_TOKEN_INCORRECT = 102;
    public static final int PROTOCOL_VERSION_UNACCEPTABLE = 103;
    public static final int DESCRIPTION_NOT_REGISTERED = 104;

    private String connMsgId;       // ConnectMessage's message id
    private int returnCode;         // Return Code

    public String getConnMsgId() {
        return connMsgId;
    }

    public void setConnMsgId(String connMsgId) {
        this.connMsgId = connMsgId;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
