package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoException;
import com.arangodb.entity.DocumentEntity;
import com.github.longkerdandy.evo.api.entity.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ArangoStorage Test
 */
public class ArangoStorageTest {

    private static ArangoStorage arango;

    @BeforeClass
    public static void before() {
        arango = new ArangoStorage("127.0.0.1", 8529);
        arango.init();
    }

    @AfterClass
    public static void after() {
        arango.destroy();
    }

    @Test
    public void userTest() throws ArangoException {
        User userA = new User();
        userA.setAlias("User A");
        userA.setEmail("usera@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");

        DocumentEntity<User> de = arango.createUser(userA);
        de = arango.getUserById(de.getDocumentKey());

        assert de.getEntity().getAlias().equals("User A");
        assert de.getEntity().getEmail().equals("usera@example.com");
        assert de.getEntity().getMobile().equals("18600000000");
        assert de.getEntity().getPassword().equals("passwr0d");
    }
}
