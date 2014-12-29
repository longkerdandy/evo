package com.github.longkerdandy.evo.api.message;

import com.github.longkerdandy.evo.api.protocol.Protocol;
import com.github.longkerdandy.evo.api.protocol.QoS;

import java.util.UUID;

/**
 * Message Factory
 */
public class MessageFactory {

    /**
     * Create default Message
     */
    public static <T> Message<T> newMessage() {
        Message<T> msg = new Message<>();
        msg.setMsgId(UUID.randomUUID().toString());     // Random UUID as Message Id
        // msg.setMsgType();
        msg.setProtocolVersion(Protocol.VERSION_1_0);   // Default Protocol Version
        msg.setFrom("Evo Platform");                    // Default from Evolution Platform
        // msg.setTo();
        msg.setQos(QoS.MOST_ONCE);                      // Default QoS 0
        msg.setTimestamp(System.currentTimeMillis());   // Current time as Timestamp
        // msg.setPayload();
        return msg;
    }
}
