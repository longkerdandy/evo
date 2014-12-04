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
        String node2 = "Node 2";

        redis.setDeviceConnected(device1, node1);
        assert redis.getDeviceConnectedNode(device1).equals(node1);
        redis.setDeviceDisconnected(device1, node1);
        assert redis.getDeviceConnectedNode(device1) == null;

        // slow disconnected notice
        redis.setDeviceConnected(device1, node1);
        redis.setDeviceConnected(device1, node2);
        redis.setDeviceDisconnected(device1, node1);
        assert redis.getDeviceConnectedNode(device1).equals(node2);

        flushRedis();

        redis.setUserConnected(user1, device1, node1);
        redis.setUserConnected(user1, device2, node2);
        assert redis.getDeviceConnectedNode(device1).equals(node1);
        assert redis.getUserConnectedDeviceNode(user1).get(device1).equals(node1);
        assert redis.getDeviceConnectedUser(device1).contains(user1);
        assert redis.getDeviceConnectedNode(device2).equals(node2);
        assert redis.getUserConnectedDeviceNode(user1).get(device2).equals(node2);
        assert redis.getDeviceConnectedUser(device2).contains(user1);
        redis.setUserDisconnected(user1, device1, node1);
        assert redis.getDeviceConnectedNode(device1) == null;
        assert redis.getUserConnectedDeviceNode(user1).get(device1) == null;

        // slow disconnected notice
        redis.setUserConnected(user1, device1, node1);
        redis.setUserConnected(user1, device1, node2);
        redis.setUserDisconnected(user1, device1, node1);
        assert redis.getDeviceConnectedNode(device1).equals(node2);
        assert redis.getUserConnectedDeviceNode(user1).get(device1).equals(node2);
    }

    @Test
    public void tokenTest() {
        flushRedis();

        String user1 = "User 1";
        String device1 = "Device 1";
        String token1 = "Token 1";
        String token2 = "Token 2";

        redis.setUserToken(user1, device1, token1);
        assert redis.isUserTokenCorrect(user1, device1, token1);
        redis.setUserToken(user1, device1, token2);
        assert !redis.isUserTokenCorrect(user1, device1, token1);
        assert redis.isUserTokenCorrect(user1, device1, token2);
    }

    @Test
    public void followTest() {
        flushRedis();

        String device1 = "Device 1";
        String user1 = "User 1";
        String user2 = "User 2";

        redis.setUserFollowDevice(user1, device1, 1);
        redis.setUserFollowDevice(user2, device1, 5);
        assert redis.isUserFollowDevice(user1, device1, 1);
        assert !redis.isUserFollowDevice(user1, device1, 5);
        assert redis.isUserFollowDevice(user2, device1, 5);
        assert redis.getUserFollowingDevices(user1, 1, 10).contains(device1);
        assert redis.getDeviceFollowerUsers(device1, 1, 10).contains(user1);
        assert redis.getDeviceFollowerUsers(device1, 5, 10).contains(user2);
        assert !redis.getDeviceFollowerUsers(device1, 5, 10).contains(user1);
        redis.setUserUnFollowDevice(user1, device1);
        assert !redis.isUserFollowDevice(user1, device1, 1);
        redis.setUserFollowDevice(user2, device1, 10);
        assert redis.isUserFollowDevice(user2, device1, 10);
    }
}
