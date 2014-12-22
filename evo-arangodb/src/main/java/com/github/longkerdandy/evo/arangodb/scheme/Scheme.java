package com.github.longkerdandy.evo.arangodb.scheme;

/**
 * Constants
 */
@SuppressWarnings("unused")
public class Scheme {

    public static final String GRAPH_IOT_RELATION = "iot_relation";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_DEVICES = "devices";
    public static final String EDGE_USER_DEVICE = "user_device";

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
