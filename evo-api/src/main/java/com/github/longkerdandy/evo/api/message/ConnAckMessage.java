package com.github.longkerdandy.evo.api.message;

/**
 * ConnAck Message
 * Acknowledge for Connect Messgae
 */
@SuppressWarnings("unused")
public class ConnAckMessage {

    // Return Codes
    public static final int EMPTY_USER_OR_TOKEN = 101;
    public static final int USER_TOKEN_INCORRECT = 102;
    public static final int PROTOCOL_VERSION_UNACCEPTABLE = 103;
    public static final int DESCRIPTION_NOT_REGISTERED = 104;

    private String connMsg;     // ConnectMessage's message id
    private int returnCode;     // Return Code

    public String getConnMsg() {
        return connMsg;
    }

    public void setConnMsg(String connMsg) {
        this.connMsg = connMsg;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
