package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.entity.CursorEntity;
import com.arangodb.entity.DocumentEntity;
import com.github.longkerdandy.evo.api.entity.Device;
import com.github.longkerdandy.evo.api.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.github.longkerdandy.evo.arangodb.Const.*;

/**
 * Arango Database Access Layer
 */
@SuppressWarnings("unused")
public class ArangoStorage {

    private final static Logger logger = LoggerFactory.getLogger(ArangoStorage.class);
    // ArangoDB config
    private final String host;
    private final int port;
    // ArangoDB Driver
    private ArangoDriver arango;

    public ArangoStorage(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ArangoDriver getArangoDriver() {
        return arango;
    }

    /**
     * Init ArangoStorage
     */
    public void init() {
        // Initialize configure
        ArangoConfigure configure = new ArangoConfigure();
        configure.setHost(this.host);
        configure.setPort(this.port);
        configure.init();
        // Create Driver (this instance is thread-safe)
        this.arango = new ArangoDriver(configure);
        logger.info("ArangoStorage init.");
    }

    /**
     * Destroy ArangoStorage
     */
    public void destroy() {
        logger.info("ArangoStorage destroy.");
    }

    /**
     * Create new user
     * User entity must been validated before invoking this method.
     *
     * @param user User Entity
     * @return Handler, Key, Revision
     * @throws ArangoException If user's email or mobile already exist
     */
    public DocumentEntity<User> createUser(User user) throws ArangoException {
        // user exist?
        Map<String, Object> example = new HashMap<>();
        if (StringUtils.isNotBlank(user.getEmail())) example.put("email", user.getEmail());
        if (StringUtils.isNotBlank(user.getEmail())) example.put("mobile", user.getMobile());
        CursorEntity<User> cursor = this.arango.executeSimpleByExample(COLLECTION_USERS, example, 0, 1, User.class);
        if (cursor.getCount() > 0) throw new ArangoException("user's email or mobile already exist");

        return this.arango.graphCreateVertex(GRAPH_IOT_RELATION, COLLECTION_USERS, user, false);
    }

    /**
     * Get user entity based on user id
     *
     * @param uid User Id
     * @return User Entity
     * @throws ArangoException If user id not exist
     */
    public DocumentEntity<User> getUserById(String uid) throws ArangoException {
        return this.arango.graphGetVertex(GRAPH_IOT_RELATION, COLLECTION_USERS, uid, User.class);
    }

    /**
     * Create new device
     * Device id must provided.
     *
     * @param device Device Entity
     * @return Handler, Key, Revision
     * @throws ArangoException If device id already exist
     */
    public DocumentEntity<Device> createDevice(Device device) throws ArangoException {
        return this.arango.graphCreateVertex(GRAPH_IOT_RELATION, COLLECTION_DEVICES, device, false);
    }

    /**
     * Get device entity based on device id
     *
     * @param did Device Id
     * @return Device Entity
     * @throws ArangoException If device id not exist
     */
    public DocumentEntity<Device> getDeviceById(String did) throws ArangoException {
        return this.arango.graphGetVertex(GRAPH_IOT_RELATION, COLLECTION_DEVICES, did, Device.class);
    }
}
