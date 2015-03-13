package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * AerospikeStorage Test
 */
public class AerospikeStorageTest {

    private static AerospikeStorage storage;

    @BeforeClass
    public static void before() {
        ClientPolicy policy = new ClientPolicy();
        Host[] hosts = new Host[]{
                new Host("172.16.1.227", 3000),
        };
        storage = new AerospikeStorage(policy, hosts);
    }

    @AfterClass
    public static void after() {
        storage.close();
    }

    @Test
    public void userTest() {
        // create new user
        User userA = EntityFactory.newUser("u000001");
        userA.setAlias("UserA");
        userA.setEmail("usera@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");
        storage.createUser(userA);

        // get user
        userA = storage.getUserById("u000001");
        assert userA != null;
        assert userA.getAlias().equals("UserA");
        assert userA.getEmail().equals("usera@example.com");
        assert userA.getMobile().equals("18600000000");
        assert storage.getUserById("u000002") == null;

        // email exist
        assert storage.isUserEmailExist("usera@example.com");
        assert !storage.isUserEmailExist("userb@example.com");

        // mobile exist
        assert storage.isUserMobileExist("18600000000");
        assert !storage.isUserMobileExist("13600000000");

        // password
        assert storage.isUserPasswordCorrect("u000001", "passwr0d");
        assert !storage.isUserPasswordCorrect("u000001", "1234567890");
        assert !storage.isUserPasswordCorrect("u000002", "passwr0d");

        // clear
        storage.ac.delete(null, new Key(Scheme.NS_EVO, Scheme.SET_USER, "u000001"));
    }
}
