package com.github.longkerdandy.evo.api.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * ActAck Message
 * Acknowledge for Action Message
 */
@SuppressWarnings("unused")
public class ActAck implements Validatable {

    // Return Code
    public static final int RECEIVED = 100;                 // Message received and re-directed to the target device
    public static final int PERMISSION_INSUFFICIENT = 110;  // User doesn't have enough permission to execute the action

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

    @Override
    public void validate() {
        if (StringUtils.isBlank(this.actMsgId)) {
            throw new IllegalStateException("Invalid action message id");
        }

        if (!ArrayUtils.contains(new int[]{RECEIVED, PERMISSION_INSUFFICIENT}, this.returnCode)) {
            throw new IllegalStateException("Invalid return code");
        }
    }
}
