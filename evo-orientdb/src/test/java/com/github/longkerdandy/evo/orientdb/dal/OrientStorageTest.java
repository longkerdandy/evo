package com.github.longkerdandy.evo.orientdb.dal;

import com.arangodb.ArangoException;
import com.github.longkerdandy.evo.api.entity.Device;
import com.github.longkerdandy.evo.api.entity.User;
import com.github.longkerdandy.evo.api.entity.UserDevice;
import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.longkerdandy.evo.orientdb.scheme.Scheme.VERTEX_DEVICE;
import static com.github.longkerdandy.evo.orientdb.scheme.Scheme.VERTEX_USER;
import static com.googlecode.catchexception.CatchException.verifyException;

/**
 * OrientStorage Test
 */
public class OrientStorageTest {

    private static OrientStorage orient;

    @BeforeClass
    public static void before() throws ArangoException {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost/evo").setupPool(1, 10);
        orient = new OrientStorage(factory);
        clear();
    }

    @AfterClass
    public static void after() throws ArangoException {
        clear();
    }

    public static void clear() {
        OrientGraph graph = orient.factory.getTx();
        try {
            graph.command(new OCommandSQL("TRUNCATE CLASS " + VERTEX_USER)).execute();
            graph.command(new OCommandSQL("TRUNCATE CLASS " + VERTEX_DEVICE)).execute();
        } finally {
            graph.shutdown();
        }
    }

    @Test
    public void userTest() {
        clear();

        User userA = new User();
        userA.setAlias("User A");
        userA.setEmail("aaa@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");

        User userB = new User();
        userB.setAlias("User B");
        userB.setEmail("bbb@example.com");
        userB.setPassword("passwr0d");

        // add user
        String uidA = orient.addUser(userA);
        userA = orient.getUseById(uidA).getPayload();
        assert userA.getAlias().equals("User A");
        assert userA.getEmail().equals("aaa@example.com");
        assert userA.getMobile().equals("18600000000");
        assert userA.getPassword() == null;

        String uidB = orient.addUser(userB);
        assert uidB != null;

        // add user already exist
        verifyException(orient, OException.class).addUser(userB);

        // get user not exist
        assert orient.getUseById("233") == null;
    }

    @Test
    public void deviceTest() {
        clear();

        Device deviceA = new Device();
        deviceA.setSn("d0000001");
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("model", "Hue");
        attrs.put("switch", 1);
        deviceA.setAttributes(attrs);
        deviceA.setUpdateTime(System.currentTimeMillis());

        // add device
        String did = orient.addDevice(deviceA);
        deviceA = orient.getDeviceById(did).getPayload();
        assert deviceA.getSn().equals("d0000001");
        assert deviceA.getUpdateTime() > 0;
    }

    @Test
    public void userDeviceRelationTest() {
        clear();

        User userA = new User();
        userA.setAlias("User A");
        userA.setEmail("aaa@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("123123");

        Device deviceA = new Device();
        deviceA.setSn("d0000001");
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("model", "Hue");
        attrs.put("switch", 1);
        deviceA.setAttributes(attrs);
        deviceA.setUpdateTime(System.currentTimeMillis());

        UserDevice relation = new UserDevice();
        relation.setPermission(3);

        // add user, device, relation
        String uidA = orient.addUser(userA);
        String didA = orient.addDevice(deviceA);
        String ridA = orient.addUserDeviceRelation(uidA, didA, relation);

        // get user id list
        Object users = orient.getDeviceRelatedUser(didA, relation);
        assert users != null;
    }
}
