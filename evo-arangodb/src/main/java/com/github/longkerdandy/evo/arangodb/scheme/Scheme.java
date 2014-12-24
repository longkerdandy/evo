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
    public static final String EDGE_USER_FOLLOW_DEVICE = "user_follow_device";
    public static final String COLLECTION_USER_TOKEN = "user_token";

    // User Collection
    public static final String U_EMAIL = "email";
    public static final String U_MOBILE = "mobile";

    // User Follow Device Relation
    public static final String U_F_D_PERMISSION = "permission";

    // User Token
    public static final String USER_TOKEN_USER = "user";
    public static final String USER_TOKEN_DEVICE = "device";
    public static final String USER_TOKEN_TOKEN = "token";

    /**
     * Generate UserFollowDevice Relation Id from User Id and Device Id
     *
     * @param uid User Id
     * @param did Device Id
     * @return UserFollowDevice Relation Id
     */
    public static String userFollowDeviceId(String uid, String did) {
        return uid + ":" + did;
    }
}
