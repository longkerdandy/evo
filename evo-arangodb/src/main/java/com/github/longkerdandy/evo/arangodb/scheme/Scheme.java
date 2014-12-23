package com.github.longkerdandy.evo.arangodb.scheme;

/**
 * Constants
 */
@SuppressWarnings("unused")
public class Scheme {

    // Graph, Vertex, Edge
    public static final String GRAPH_IOT_RELATION = "iot_relation";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_DEVICES = "devices";
    public static final String EDGE_USER_DEVICE = "user_device";
    public static final String COLLECTION_USER_TOKEN = "user_token";

    // User Collection
    public static final String USER_EMAIL = "email";
    public static final String USER_MOBILE = "mobile";

    // User Device Relation
    public static final String USER_DEVICE_PERMISSION = "permission";

    // User Token
    public static final String USER_TOKEN_USER = "user";
    public static final String USER_TOKEN_DEVICE = "device";
    public static final String USER_TOKEN_TOKEN = "token";

    /**
     * Generate UserDevice Relation Id from User Id and Device Id
     *
     * @param uid User Id
     * @param did Device Id
     * @return UserDevice Relation Id
     */
    public static String userDeviceRelationId(String uid, String did) {
        return uid + "_" + did;
    }
}
