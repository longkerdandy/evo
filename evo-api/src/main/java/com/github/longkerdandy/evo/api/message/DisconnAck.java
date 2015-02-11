package com.github.longkerdandy.evo.api.message;

/**
 * DisconnAck Message
 * Acknowledge for Disconnect Message
 */
@SuppressWarnings("unused")
public class DisconnAck {

    private String disconnMsgId;       // DisconnectMessage's message id

    public String getDisconnMsgId() {
        return disconnMsgId;
    }

    public void setDisconnMsgId(String disconnMsgId) {
        this.disconnMsgId = disconnMsgId;
    }
}
