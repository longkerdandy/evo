package com.github.longkerdandy.evo.api.protocol;

/**
 * Evolution Platform
 */
public class Evolution {

    // Platform Id
    // Send message to platform means let platform decide how to handle this message
    public static final String ID = "evolution";

    // Ignore Id
    // Send message to ignore means message should not be re-directed
    public static final String IGNORE = "ignore";

    private Evolution() {
    }
}
