package com.github.longkerdandy.evo.api.protocol;

/**
 * Protocol Constant
 */
@SuppressWarnings("unused")
public class Const {

    // Protocol
    public static final int PROTOCOL_TCP_1_0 = 10;

    // Message
    public static final int MESSAGE_MAX_BYTES = 8092;

    // Platform Id
    public static final String PLATFORM_ID = "t4a06cp26kom";

    // Common Trigger
    public static final String TRIGGER_ONLINE = "online";
    public static final String TRIGGER_OFFLINE = "offline";

    // Common Attribute
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_FRIENDLY_NAME = "friendly_name";
    public static final String ATTRIBUTE_FIRMWARE_VERSION = "firmware_ver";

    private Const() {
    }
}
