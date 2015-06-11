package com.github.longkerdandy.evo.api.protocol;

/**
 * Qos
 */
@SuppressWarnings("unused")
public class QoS {

    public static final int MOST_ONCE = 0;
    public static final int LEAST_ONCE = 1;
    public static final int EXACTLY_ONCE = 2;

    private QoS() {
    }

    /**
     * Whether give QoS level is valid
     *
     * @param qos QoS Level
     * @return True if valid
     */
    public static boolean isValid(int qos) {
        return qos >= 0 && qos <= 2;
    }
}
