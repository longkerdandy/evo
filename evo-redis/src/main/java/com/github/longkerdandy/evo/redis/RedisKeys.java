package com.github.longkerdandy.evo.redis;

/**
 * Keys in Redis Storage
 */
@SuppressWarnings("unused")
public class RedisKeys {

    // Device Server(Node) Mapping --- Hashes
    public static String deviceNodeMappingKey() {
        return "mapping:device:node";
    }

    // Device User Mapping --- Sets
    public static String deviceUserMappingKey(final String device) {
        return "mapping:device:" + device + ":users";
    }

    // User Device Mapping --- Sets
    public static String userDeviceMappingKey(final String user) {
        return "mapping:user:" + user + ":devices";
    }

    // User Token (per Device) --- String
    public static String userTokenKey(final String user, final String device) {
        return "users:" + user + ":token:" + device;
    }

    // User Following Devices --- Sorted Set
    public static String userFollowingDevicesKey(final String user) {
        return "users:" + user + ":following:devices";
    }

    // Device Follower Users --- Sorted Set
    public static String deviceFollowerUsersKey(final String device) {
        return "devices:" + device + ":follower:users";
    }
}