package com.github.longkerdandy.evo.api.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * TrigAck Message
 * Acknowledge for Trigger Message
 */
@SuppressWarnings("unused")
public class TrigAck implements Validatable {

    // Return Code
    public static final int RECEIVED = 100;                 // Message received and re-directed to followers

    protected String trigMsgId;       // TriggerMessage's message id
    protected int returnCode;         // Return Code

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

    @Override
    public void validate() {
        if (StringUtils.isBlank(this.trigMsgId)) {
            throw new IllegalStateException("Invalid trigger message id");
        }

        if (!ArrayUtils.contains(new int[]{RECEIVED}, this.returnCode)) {
            throw new IllegalStateException("Invalid return code");
        }
    }
}
