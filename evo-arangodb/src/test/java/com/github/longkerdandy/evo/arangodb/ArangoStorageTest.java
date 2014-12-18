package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoException;
import com.arangodb.entity.DeletedEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.EdgeEntity;
import com.github.longkerdandy.evo.api.entity.Device;
import com.github.longkerdandy.evo.api.entity.User;
import com.github.longkerdandy.evo.api.entity.UserDeviceRelation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static com.github.longkerdandy.evo.arangodb.Const.*;
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
        arango = new ArangoStorage("127.0.0.1", 8529);
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
        arango.getArangoDriver().truncateCollection(EDGE_USER_DEVICE);
    }

    @Test
    public void userTest() throws ArangoException {
        clear();

        User userA = new User();
        userA.setAlias("User A");
        userA.setEmail("usera@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");

        // normal behavior
        DocumentEntity<User> de = arango.createUser(userA);
        de = arango.getUserById(de.getDocumentKey());
        assert de.getEntity().getAlias().equals("User A");
        assert de.getEntity().getEmail().equals("usera@example.com");
        assert de.getEntity().getMobile().equals("18600000000");
        assert de.getEntity().getPassword() == null;

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
        Map<String, Object> fields = new HashMap<>();
        fields.put("model", "Hue");
        fields.put("switch", 1);
        deviceA.setAttributes(fields);
        deviceA.setUpdateTime(System.currentTimeMillis());

        // normal behavior
        DocumentEntity<Device> de = arango.createDevice(deviceA);
        assert de.getDocumentKey().equals(deviceA.getId());
        de = arango.getDeviceById(deviceA.getId());
        assert de.getEntity().getId().equals(deviceA.getId());
        assert de.getEntity().getAttributes().get("model").equals("Hue");
        assert (double) de.getEntity().getAttributes().get("switch") == 1;
        assert de.getEntity().getUpdateTime() == deviceA.getUpdateTime();
    }

    @Test
    public void userDeviceRelationTest() throws ArangoException {
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
        UserDeviceRelation relationA = new UserDeviceRelation();
        relationA.setPermission(1);

        // create user device relation
        DocumentEntity<User> deu = arango.createUser(userA);
        DocumentEntity<Device> ded = arango.createDevice(deviceA);
        EdgeEntity<UserDeviceRelation> ee = arango.createUserDeviceRelation(deu.getDocumentKey(), ded.getDocumentKey(), relationA);
        assert ee.getFromVertexHandle().equals(userHandle(deu.getDocumentKey()));
        assert ee.getToVertexHandle().equals(deviceHandle(ded.getDocumentKey()));
        assert ee.getDocumentKey().equals(userDeviceRelationId(deu.getDocumentKey(), ded.getDocumentKey()));

        // replace user device relation
        relationA.setPermission(2);
        ee = arango.replaceUserDeviceRelation(deu.getDocumentKey(), ded.getDocumentKey(), relationA);
        assert ee.getDocumentKey().equals(userDeviceRelationId(deu.getDocumentKey(), ded.getDocumentKey()));

        // delete user device relation
        DeletedEntity de = arango.deleteUserDeviceRelation(deu.getDocumentKey(), ded.getDocumentKey());
        assert de.getDeleted();
    }
}
