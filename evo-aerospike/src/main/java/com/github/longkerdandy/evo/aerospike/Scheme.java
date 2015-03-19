package com.github.longkerdandy.evo.aerospike;

/**
 * Aerospike Database Scheme
 */
public class Scheme {

    // namespace
    public static final String NS_EVO = "evo";

    // complex type
    public static final String OWN_USER = "user";
    public static final String OWN_DEVICE = "dev";
    public static final String OWN_PERMISSION = "perm";

    // set - users
    public static final String SET_USERS = "users";
    public static final String BIN_U_ID = "id";
    public static final String BIN_U_ALIAS = "alias";
    public static final String BIN_U_EMAIL = "email";
    public static final String BIN_U_MOBILE = "mobile";
    public static final String BIN_U_PASSWORD = "pwd";
    public static final String BIN_U_OWN = "own";

    // set - devices
    public static final String SET_DEVICES = "devs";
    public static final String BIN_D_ID = "id";
    public static final String BIN_D_TYPE = "type";
    public static final String BIN_D_DESC_ID = "desc";
    public static final String BIN_D_PV = "pv";
    public static final String BIN_D_CONN = "conn";
    public static final String BIN_D_OWN = "own";

    // set - devices attribute
    public static final String SET_DEVICES_ATTR = "devs_attr";

    private Scheme() {
    }
}
