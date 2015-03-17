package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.api.protocol.Const;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
        storage.updateUser(userA);

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
        storage.ac.delete(null, new Key(Scheme.NS_EVO, Scheme.SET_USERS, "u000001"));
    }

    @Test
    public void deviceTest() {
        // create new device
        Device deviceA = EntityFactory.newDevice("d000001");
        deviceA.setType(DeviceType.DEVICE);
        deviceA.setDescId("Desc1");
        deviceA.setPv(Const.PROTOCOL_VERSION_1_0);
        deviceA.setConnected("Node1");
        storage.updateDevice(deviceA);

        // get device
        deviceA = storage.getDeviceById("d000001");
        assert deviceA != null;
        assert deviceA.getType() == DeviceType.DEVICE;
        assert deviceA.getDescId().equals("Desc1");
        assert deviceA.getPv() == Const.PROTOCOL_VERSION_1_0;
        assert deviceA.getConnected().equals("Node1");

        // update device attribute
        Map<String, Object> attrA = new HashMap<>();
        attrA.put("Field1", "1");
        attrA.put("Field2", 2);
        attrA.put("Field3", "3");
        storage.updateDeviceAttr("d000001", attrA);
        attrA = storage.getDeviceAttr("d000001");
        assert attrA.get("Field1").equals("1");
        assert NumberUtils.toInt(String.valueOf(attrA.get("Field2"))) == 2;
        assert attrA.get("Field3").equals("3");

        // update device attribute again
        attrA.put("Field1", "1Mod");
        storage.updateDeviceAttr("d000001", attrA);
        attrA = storage.getDeviceAttr("d000001");
        assert attrA.get("Field1").equals("1Mod");
        assert NumberUtils.toInt(String.valueOf(attrA.get("Field2"))) == 2;
        assert attrA.get("Field3").equals("3");

        // replace device attribute
        attrA.put("Field1", "10");
        attrA.put("Field2", 20);
        attrA.remove("Field3");
        storage.replaceDeviceAttr("d000001", attrA);
        attrA = storage.getDeviceAttr("d000001");
        assert attrA.get("Field1").equals("10");
        assert NumberUtils.toInt(String.valueOf(attrA.get("Field2"))) == 20;
        assert attrA.get("Field3") == null;

        // clear
        storage.ac.delete(null, new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, "d000001"));
    }
}
