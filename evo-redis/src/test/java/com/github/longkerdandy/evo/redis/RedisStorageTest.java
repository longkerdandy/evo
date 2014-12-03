package com.github.longkerdandy.evo.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * RedisStorage UnitTest
 */
public class RedisStorageTest {

    private static RedisStorage redis;

    @BeforeClass
    public static void before() {
        redis = new RedisStorage("0.0.0.0", 6379, "");
        redis.init();
    }

    private static void flushRedis() {
        try (final Jedis jedis = redis.getJedisPool().getResource()) {
            jedis.flushDB();
        }
    }

    @AfterClass
    public static void after() {
        flushRedis();
        redis.destroy();
    }

    @Test
    public void connectMappingTest() {
        flushRedis();

        String device1 = "Device 1";
        String user1 = "User 1";
        String node1 = "Node 1";
        String device2 = "Device 2";
        String user2 = "User 2";
        String node2 = "Node 2";

        redis.setDeviceConnected(device1, node1);
        redis.setDeviceDisconnected(device1, node1);
    }
}
