package com.github.longkerdandy.evo.api.protocol;

/**
 * Privilege Definition
 */
@SuppressWarnings("unused")
public class Privilege {

    public static final int NONE = 0;       // Level 0  Default, No Privilege
    public static final int READ = 3;       // Level 3  Read Privilege
    public static final int READ_WRITE = 6; // Level 6  Read & Write Privilege
    public static final int FULL = 9;       // Level 9  Full Privilege
    public static final int OWNER = 10;     // Level 10 Owner Privilege
}
