package com.github.longkerdandy.evo.api.mq;

/**
 * MQ Topics
 */
public class Topics {

    public static final String TCP_IN = "tcp-in";       // TCP incoming messages
    public static final String SMS = "sms";             // SMS service

    private Topics() {
    }

    // TCP outgoing messages
    public static String TCP_OUT(String nodeId) {
        return "tcp-out-" + nodeId;
    }
}
