package com.github.longkerdandy.evo.api.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * DisconnAck Message
 * Acknowledge for Disconnect Message
 */
@SuppressWarnings("unused")
public class DisconnAck implements Validatable {

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

    @Override
    public void validate() {
        if (StringUtils.isBlank(this.disconnMsgId)) {
            throw new IllegalStateException("Invalid disconnect message id");
        }

        if (!ArrayUtils.contains(new int[]{RECEIVED}, this.returnCode)) {
            throw new IllegalStateException("Invalid return code");
        }
    }
}
