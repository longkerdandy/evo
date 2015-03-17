package com.github.longkerdandy.evo.aerospike;

/**
 * Aerospike Database Scheme
 */
public class Scheme {

    // namespace
    public static final String NS_EVO = "evo";

    // set - users
    public static final String SET_USERS = "users";
    public static final String BIN_U_ID = "id";
    public static final String BIN_U_ALIAS = "alias";
    public static final String BIN_U_EMAIL = "email";
    public static final String BIN_U_MOBILE = "mobile";
    public static final String BIN_U_PASSWORD = "password";

    // set - devices
    public static final String SET_DEVICES = "devices";
    public static final String BIN_D_ID = "id";
    public static final String BIN_D_TYPE = "type";
    public static final String BIN_D_DESC_ID = "desc";
    public static final String BIN_D_PV = "pv";
    public static final String BIN_D_CONN = "conn";

    // set - devices attribute
    public static final String SET_DEVICES_ATTR = "devices_attr";

    private Scheme() {
    }
}
