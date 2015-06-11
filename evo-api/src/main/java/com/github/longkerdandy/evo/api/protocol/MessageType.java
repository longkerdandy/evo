package com.github.longkerdandy.evo.api.protocol;

/**
 * Message Type
 */
@SuppressWarnings("unused")
public class MessageType {

    public static final int CONNECT = 1;
    public static final int CONNACK = 2;
    public static final int DISCONNECT = 3;
    public static final int DISCONNACK = 4;
    public static final int TRIGGER = 5;
    public static final int TRIGACK = 6;
    public static final int ACTION = 7;
    public static final int ACTACK = 8;

    private MessageType() {
    }

    /**
     * Whether give message type is valid
     *
     * @param msgType Message Type
     * @return True if valid
     */
    public static boolean isValid(int msgType) {
        return msgType >= 1 && msgType <= 8;
    }
}
