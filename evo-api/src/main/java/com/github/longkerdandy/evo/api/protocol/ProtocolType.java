package com.github.longkerdandy.evo.api.protocol;

/**
 * Protocol Type
 */
public class ProtocolType {

    // Protocol
    public static final int TCP_1_0 = 10;

    private ProtocolType() {
    }

    /**
     * Whether give protocol type is valid
     *
     * @param protocol Protocol Type
     * @return True if valid
     */
    public static boolean isValid(int protocol) {
        return protocol == TCP_1_0;
    }
}
