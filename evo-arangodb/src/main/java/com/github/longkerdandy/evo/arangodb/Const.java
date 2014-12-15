package com.github.longkerdandy.evo.arangodb;

/**
 * Constants
 */
@SuppressWarnings("unused")
public class Const {

    public static final String GRAPH_IOT_RELATION = "iot_relation";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_DEVICES = "devices";
    public static final String EDGE_USER_DEVICE = "user_device";

    /**
     * Generate UserDeviceRelation Id from User Id and Device Id
     *
     * @param uid User Id
     * @param did Device Id
     * @return UserDeviceRelation Id
     */
    public static String relationId(String uid, String did) {
        return uid + "_" + did;
    }
}
