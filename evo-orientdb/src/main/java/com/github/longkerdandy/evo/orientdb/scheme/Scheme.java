package com.github.longkerdandy.evo.orientdb.scheme;

/**
 * Orient Database Scheme
 */
@SuppressWarnings("unused")
public class Scheme {

    // ============================== Vertex ==============================
    public static final String VERTEX_USER = "User";
    public static final String VERTEX_DEVICE = "Device";

    // ============================== Entity ==============================
    public static final String USER_ALIAS = "alias";
    public static final String USER_EMAIL = "email";
    public static final String USER_MOBILE = "mobile";
    public static final String USER_PASSWORD = "password";

    public static final String DEVICE_SN = "sn";
    public static final String DEVICE_ATTRIBUTES = "attributes";
    public static final String DEVICE_UPDATE_TIME = "updateTime";
}
