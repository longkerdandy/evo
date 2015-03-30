package com.github.longkerdandy.evo.api.mq;

/**
 * MQ Topics
 */
public class Topic {

    public static final String TCP_IN = "tcp-in";       // TCP incoming messages

    private Topic() {
    }

    // TCP outgoing messages
    public static String TCP_OUT(String nodeId) {
        return "tcp-out-" + nodeId;
    }
}
