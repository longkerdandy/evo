package com.github.longkerdandy.evo.api.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * ConnAck Message
 * Acknowledge for Connect Message
 */
@SuppressWarnings("unused")
public class ConnAck implements Validatable {

    // Return Codes
    public static final int RECEIVED = 100;                         // Message received and re-directed to the target device
    public static final int USER_TOKEN_INCORRECT = 110;             // User token incorrect
    public static final int DESCRIPTION_NOT_REGISTERED = 111;       // Device description is not registered

    protected String connMsgId;       // ConnectMessage's message id
    protected int returnCode;         // Return Code

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

    @Override
    public void validate() {
        if (StringUtils.isBlank(this.connMsgId)) {
            throw new IllegalStateException("Invalid connect message id");
        }

        if (!ArrayUtils.contains(new int[]{RECEIVED, USER_TOKEN_INCORRECT, DESCRIPTION_NOT_REGISTERED}, this.returnCode)) {
            throw new IllegalStateException("Invalid return code");
        }
    }
}
