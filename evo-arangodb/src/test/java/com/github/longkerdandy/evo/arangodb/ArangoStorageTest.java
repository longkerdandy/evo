package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoException;
import com.github.longkerdandy.evo.api.entity.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.longkerdandy.evo.api.scheme.Scheme.*;
import static com.googlecode.catchexception.CatchException.verifyException;

/**
 * ArangoStorage Test
 */
public class ArangoStorageTest {

    private static ArangoStorage arango;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void before() throws ArangoException {
        arango = new ArangoStorage("127.0.0.1", 8529, null);
        arango.init();
        clear();
    }

    @AfterClass
    public static void after() throws ArangoException {
        clear();
        arango.destroy();
    }

    /**
     * Clear database (Remove all documents)
     */
    public static void clear() throws ArangoException {
        arango.getArangoDriver().truncateCollection(COLLECTION_USERS);
        arango.getArangoDriver().truncateCollection(COLLECTION_DEVICES);
        arango.getArangoDriver().truncateCollection(EDGE_USER_FOLLOW_DEVICE);
        arango.getArangoDriver().truncateCollection(EDGE_DEVICE_REGISTER_USER);
    }

    @Test
    public void userTest() throws ArangoException {
        clear();

        User userA = new User();
        userA.setAlias("User A");
        userA.setEmail("usera@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");

        // create user
        Document<User> d = arango.createUser(userA);
        d = arango.getUserById(d.getId());
        assert d.getEntity().getAlias().equals("User A");
        assert d.getEntity().getEmail().equals("usera@example.com");
        assert d.getEntity().getMobile().equals("18600000000");
        assert d.getEntity().getPassword() == null;

        // create user email already exist
        User userB = new User();
        userB.setAlias("User B");
        userB.setEmail("usera@example.com");
        userB.setPassword("passwr0d");
        verifyException(arango, ArangoException.class).createUser(userB);

        // create user mobile already exist
        userB.setEmail(null);
        userB.setMobile("18600000000");
        verifyException(arango, ArangoException.class).createUser(userB);

        // get user not exist
        verifyException(arango, ArangoException.class).getUserById("00000000");
    }

    @Test
    public void deviceTest() throws ArangoException {
        clear();

        Device deviceA = new Device();
        deviceA.setId("d0000001");
        Map<String, Object> fieldsA = new HashMap<>();
        fieldsA.put("model", "Hue");
        fieldsA.put("switch", 1);
        deviceA.setAttributes(fieldsA);
        deviceA.setUpdateTime(System.currentTimeMillis());

        // create device
        Document<Device> d = arango.createOrReplaceDevice(deviceA);
        d = arango.getDeviceById(d.getId());
        assert d.getEntity().getId().equals(deviceA.getId());
        assert d.getEntity().getAttributes().get("model").equals("Hue");
        assert (double) d.getEntity().getAttributes().get("switch") == 1;
        assert d.getEntity().getUpdateTime() == deviceA.getUpdateTime();

        // replace device
        deviceA.getAttributes().put("switch", 0);
        d = arango.createOrReplaceDevice(deviceA);
        d = arango.getDeviceById(d.getId());
        assert (double) d.getEntity().getAttributes().get("switch") == 0;

        // update device
        Device deviceB = new Device();
        Map<String, Object> fieldsB = new HashMap<>();
        fieldsB.put("model", "Sonos");
        deviceB.setAttributes(fieldsB);
        d = arango.updateDevice(d.getId(), deviceB);
        d = arango.getDeviceById(d.getId());
        assert d.getEntity().getAttributes().get("model").equals("Sonos");
        assert (double) d.getEntity().getAttributes().get("switch") == 0;
    }

    @Test
    public void userFollowDeviceTest() throws ArangoException {
        clear();

        User userA = new User();
        userA.setAlias("User A");
        userA.setEmail("usera@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");
        Device deviceA = new Device();
        deviceA.setId("d0000001");
        Map<String, Object> fields = new HashMap<>();
        fields.put("model", "Hue");
        fields.put("switch", 1);
        deviceA.setAttributes(fields);
        deviceA.setUpdateTime(System.currentTimeMillis());
        UserFollowDevice relationA = new UserFollowDevice();
        relationA.setPermission(1);

        // create user device relation
        Document<User> du = arango.createUser(userA);
        Document<Device> dd = arango.createOrReplaceDevice(deviceA);
        Relation<UserFollowDevice> rud = arango.createOrReplaceUserFollowDevice(du.getId(), dd.getId(), relationA);
        assert rud.getFrom().equals(du.getId());
        assert rud.getTo().equals(dd.getId());
        assert rud.getId().equals(userFollowDeviceId(du.getId(), dd.getId()));
        rud = arango.getUserFollowDevice(du.getId(), dd.getId());
        assert rud.getEntity().getPermission() == 1;

        // replace user device relation
        relationA.setPermission(2);
        rud = arango.createOrReplaceUserFollowDevice(du.getId(), dd.getId(), relationA);
        assert rud.getId().equals(userFollowDeviceId(du.getId(), dd.getId()));
        rud = arango.getUserFollowDevice(du.getId(), dd.getId());
        assert rud.getEntity().getPermission() == 2;

        UserFollowDevice relation1 = new UserFollowDevice();
        relation1.setPermission(1);
        UserFollowDevice relation2 = new UserFollowDevice();
        relation2.setPermission(2);
        // get device related user
        Set<String> users = arango.getDeviceFollowedUserId(dd.getId(), relation1, relation2);
        assert users.contains(du.getId());
        // get user related device
        Set<String> devices = arango.getUserFollowingDeviceId(du.getId(), relation1, relation2);
        assert devices.contains(dd.getId());

        // delete user device relation
        assert arango.deleteUserFollowDevice(du.getId(), dd.getId());
    }

    @Test
    public void deviceRegisterUserTest() throws ArangoException {
        clear();

        User userA = new User();
        userA.setAlias("User A");
        userA.setEmail("usera@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");
        Device deviceA = new Device();
        deviceA.setId("d0000001");
        Map<String, Object> fields = new HashMap<>();
        fields.put("model", "Hue");
        fields.put("switch", 1);
        deviceA.setAttributes(fields);
        deviceA.setUpdateTime(System.currentTimeMillis());
        DeviceRegisterUser relationA = new DeviceRegisterUser();
        relationA.setToken("abcdefgh");

        // create device user relation
        Document<User> duA = arango.createUser(userA);
        Document<Device> dd = arango.createOrReplaceDevice(deviceA);
        Relation<DeviceRegisterUser> rdu = arango.createOrReplaceDeviceRegisterUser(dd.getId(), duA.getId(), relationA);
        assert rdu.getFrom().equals(dd.getId());
        assert rdu.getTo().equals(duA.getId());
        assert rdu.getId().equals(deviceRegisterUserId(dd.getId(), duA.getId()));
        rdu = arango.getDeviceRegisterUser(dd.getId(), duA.getId());
        assert rdu.getEntity().getToken().equals("abcdefgh");

        // replace device user relation
        User userB = new User();
        userB.setAlias("User B");
        userB.setEmail("userb@example.com");
        userB.setPassword("passwr0d");

        Document<User> duB = arango.createUser(userB);
        rdu = arango.createOrReplaceDeviceRegisterUser(dd.getId(), duB.getId(), relationA);
        assert rdu.getFrom().equals(dd.getId());
        assert rdu.getTo().equals(duB.getId());
        assert rdu.getId().equals(deviceRegisterUserId(dd.getId(), duB.getId()));
        rdu = arango.getDeviceRegisterUser(dd.getId(), duB.getId());
        assert rdu.getEntity().getToken().equals("abcdefgh");
        verifyException(arango, ArangoException.class).getDeviceRegisterUser(dd.getId(), duA.getId());

        // update device user relation
        relationA.setToken("1234567890");
        arango.updateDeviceRegisterUser(dd.getId(), duB.getId(), relationA);
        rdu = arango.getDeviceRegisterUser(dd.getId(), duB.getId());
        assert rdu.getEntity().getToken().equals("1234567890");

        // delete device user relation
        assert arango.deleteDeviceRegisterUser(dd.getId(), duB.getId());
    }
}
