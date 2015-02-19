package com.github.longkerdandy.evo.api.protocol;

/**
 * Override Policy
 */
@SuppressWarnings("unused")
public class OverridePolicy {

    public static final int IGNORE = 0;                 // Ignore attributes changes
    public static final int REPLACE = 1;                // Replace current attributes
    public static final int REPLACE_IF_NEWER = 2;       // Replace current attributes if is newer
    public static final int UPDATE = 3;                 // Update/Merge current attributes
    public static final int UPDATE_IF_NEWER = 4;        // Update/Merge current attributes if is newer

    private OverridePolicy() {
    }
}
