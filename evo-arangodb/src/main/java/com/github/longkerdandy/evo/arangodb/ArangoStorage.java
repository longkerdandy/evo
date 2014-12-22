package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.github.longkerdandy.evo.api.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.longkerdandy.evo.arangodb.converter.Converter.*;
import static com.github.longkerdandy.evo.arangodb.scheme.Scheme.*;

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
     * @return Document WITHOUT User entity
     * @throws ArangoException If user's email or mobile already exist
     */
    public Document<User> createUser(User user) throws ArangoException {
        return toDocument(this.arango.graphCreateVertex(GRAPH_IOT_RELATION, COLLECTION_USERS, user, false));
    }

    /**
     * Get user entity based on user id
     *
     * @param uid User Id
     * @return Document with User entity
     * @throws ArangoException If user id not exist
     */
    public Document<User> getUserById(String uid) throws ArangoException {
        return toDocument(this.arango.graphGetVertex(GRAPH_IOT_RELATION, COLLECTION_USERS, uid, User.class));
    }

    /**
     * Create new device
     * Device id must provided.
     *
     * @param device Device Entity
     * @return Document WITHOUT Device entity
     * @throws ArangoException If device id already exist
     */
    public Document<Device> createDevice(Device device) throws ArangoException {
        return toDocument(this.arango.graphCreateVertex(GRAPH_IOT_RELATION, COLLECTION_DEVICES, device, false));
    }

    /**
     * Get device entity based on device id
     *
     * @param did Device Id
     * @return Device Entity
     * @throws ArangoException If device id not exist
     */
    public Document<Device> getDeviceById(String did) throws ArangoException {
        return toDocument(this.arango.graphGetVertex(GRAPH_IOT_RELATION, COLLECTION_DEVICES, did, Device.class));
    }

    /**
     * Create new user device relation
     *
     * @param uid      User Id
     * @param did      Device Id
     * @param relation User Device Relation
     * @return Relation WITHOUT UserDevice entity
     * @throws ArangoException If user/device not exist or relation already exist
     */
    public Relation<UserDevice> createUserDeviceRelation(String uid, String did, UserDevice relation) throws ArangoException {
        return toRelation(this.arango.graphCreateEdge(GRAPH_IOT_RELATION, EDGE_USER_DEVICE,
                userDeviceRelationId(uid, did),
                keyToHandle(COLLECTION_USERS, uid),
                keyToHandle(COLLECTION_DEVICES, did),
                relation, false));
    }

    /**
     * Get user device relation
     *
     * @param uid User Id
     * @param did Device Id
     * @return Relation with UserDevice entity
     * @throws ArangoException If relation not exist
     */
    public Relation<UserDevice> getUserDeviceRelation(String uid, String did) throws ArangoException {
        return toRelation(this.arango.graphGetEdge(GRAPH_IOT_RELATION, EDGE_USER_DEVICE, userDeviceRelationId(uid, did), UserDevice.class));
    }

    /**
     * Replace user device relation
     *
     * @param uid      User Id
     * @param did      Device Id
     * @param relation User Device Relation
     * @return Relation WITHOUT UserDevice entity
     * @throws ArangoException If relation not exist
     */
    public Relation<UserDevice> replaceUserDeviceRelation(String uid, String did, UserDevice relation) throws ArangoException {
        return toRelation(this.arango.graphReplaceEdge(GRAPH_IOT_RELATION, EDGE_USER_DEVICE, userDeviceRelationId(uid, did), relation));
    }

    /**
     * Delete user device relation
     *
     * @param uid User Id
     * @param did Device Id
     * @return Deleted?
     * @throws ArangoException If relation not exist
     */
    public boolean deleteUserDeviceRelation(String uid, String did) throws ArangoException {
        return this.arango.graphDeleteEdge(GRAPH_IOT_RELATION, EDGE_USER_DEVICE, userDeviceRelationId(uid, did)).getDeleted();
    }

    /**
     * Get device related user's id
     *
     * @param did Device Id
     * @param min Minimal Relation requirement
     * @param max Maximum Relation requirement
     * @return User Id Set
     */
    public Object getDeviceRelatedUserId(String did, UserDevice min, UserDevice max) throws ArangoException {
        // TODO: using AQL
        return null;
    }
}
