package com.github.longkerdandy.evo.redis;

/**
 * Redis Lua Script
 */
@SuppressWarnings("unused")
public class RedisLuaScript {

    // KEYS[1]: deviceNodeMappingKey ARGV[1]: device id ARGV[2]: node
    public static final String SET_DEVICE_DISCONNECTED = "local node = redis.call('HGET', KEYS[1], ARGV[1]) " +
            "if not not node and node == ARGV[2] then " +
            "redis.call('HDEL', KEYS[1], ARGV[1]) end";

    // KEYS[1]: deviceNodeMappingKey KEYS[2]: userDeviceMappingKey KEYS[3]: deviceUserMappingKey ARGV[1]: user id ARGV[2]: device id ARGV[3]: node
    public static final String SET_USER_CONNECTED = "redis.call('HSET', KEYS[1], ARGV[2], ARGV[3]) " +
            "redis.call('SADD', KEYS[2], ARGV[2]) " +
            "redis.call('SADD', KEYS[3], ARGV[1])";

    // KEYS[1]: deviceNodeMappingKey KEYS[2]: userDeviceMappingKey KEYS[3]: deviceUserMappingKey ARGV[1]: user id ARGV[2]: device id ARGV[3]: node
    public static final String SET_USER_DISCONNECTED = "local node = redis.call('HGET', KEYS[1], ARGV[2]) " +
            "if not not node and node == ARGV[3] then " +
            "redis.call('HDEL', KEYS[1], ARGV[2]) " +
            "redis.call('SREM', KEYS[2], ARGV[2]) " +
            "redis.call('SREM', KEYS[3], ARGV[1])" +
            "end";

    // KEYS[1]: userFollowingDevicesKey KEYS[2]: deviceFollowerUsersKey ARGV[1]: user id ARGV[2]: device id ARGV[3]: privilege
    public static final String SET_USER_FOLLOW_DEVICE = "redis.call('ZADD', KEYS[1], ARGV[3], ARGV[2]) " +
            "redis.call('ZADD', KEYS[2], ARGV[3], ARGV[1])";

    // KEYS[1]: userFollowingDevicesKey KEYS[2]: deviceFollowerUsersKey ARGV[1]: user id ARGV[2]: device id
    public static final String SET_USER_UNFOLLOW_DEVICE = "redis.call('ZREM', KEYS[1], ARGV[2]) " +
            "redis.call('ZREM', KEYS[2], ARGV[1])";

    // KEYS[1]: deviceFollowerUsersKey ARGV[1]: user id ARGV[2]: privilege
    public static final String IS_USER_FOLLOW_DEVICE = "local privilege = redis.call('ZSCORE', KEYS[1], ARGV[1]) " +
            "if not not privilege and privilege >= ARGV[2] then " +
            "return true else " +
            "return false end";
}
