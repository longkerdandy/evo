package com.github.longkerdandy.evo.api.message;

/**
 * ActAck Message
 * Acknowledge for Action Message
 */
@SuppressWarnings("unused")
public class ActAck {

    // Return Codes, from Platform to Controller
    public static final int RECEIVED = 100;                 // Message received and re-directed to the target device
    public static final int RECEIVED_CACHED = 101;          // Message received but target device is offline, message has been cached
    public static final int PERMISSION_INSUFFICIENT = 110;  // User doesn't has enough permission do execute the action

    // Return Codes, from Device to Platform/Controller
    public static final int SUCCESS = 200;                  // Message received and executed
    public static final int FAIL = 210;                     // Message received but failed to execute

    private String actMsgId;                // ActionMessage's message id
    private int returnCode;                 // Return Code

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
