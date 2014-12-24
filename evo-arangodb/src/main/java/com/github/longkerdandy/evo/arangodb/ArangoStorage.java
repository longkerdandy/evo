package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.CursorResultSet;
import com.arangodb.entity.CursorEntity;
import com.arangodb.util.MapBuilder;
import com.github.longkerdandy.evo.api.entity.*;
import com.github.longkerdandy.evo.arangodb.scheme.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.github.longkerdandy.evo.arangodb.converter.Converter.*;
import static com.github.longkerdandy.evo.arangodb.scheme.Scheme.*;

/**
 * Arango Database Access Layer
 */
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
     * Is user token correct
     *
     * @param userToken User Token
     * @return Correct?
     */
    public boolean isUserTokenCorrect(UserToken userToken) throws ArangoException {
        // query
        Map<String, Object> bindVars = new MapBuilder().put(USER_TOKEN_USER, userToken.getUser()).put(USER_TOKEN_DEVICE, userToken.getDevice()).get();
        // query user device edge, return device id set
        CursorEntity<UserToken> r = this.arango.executeQuery(Query.GET_USER_TOKEN, bindVars, UserToken.class, true, 0);
        // deal with result
        return r.getCount() > 0 && userToken.getToken().equals(r.get(0).getToken());
    }

    /**
     * Create new user token (bind with device)
     *
     * @param userToken User Token
     */
    public void createOrReplaceUserToken(UserToken userToken) throws ArangoException {
        // query
        Map<String, Object> bindVars = new MapBuilder().put(USER_TOKEN_USER, userToken.getUser()).put(USER_TOKEN_DEVICE, userToken.getDevice()).get();
        // query user device edge, return device id set
        CursorEntity<UserToken> r = this.arango.executeQuery(Query.GET_USER_TOKEN, bindVars, UserToken.class, true, 0);
        // create or replace
        if (r.getCount() > 0) {
            this.arango.graphReplaceVertex(GRAPH_IOT_RELATION, COLLECTION_USER_TOKEN, r.get(0).getId(), userToken);
        } else {
            this.arango.graphCreateVertex(GRAPH_IOT_RELATION, COLLECTION_USER_TOKEN, userToken, false);
        }
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
     * Create or replace device
     * Device id must provided.
     *
     * @param device Device Entity
     * @return Document WITHOUT Device entity
     */
    public Document<Device> createOrReplaceDevice(Device device) throws ArangoException {
        if (checkExist(COLLECTION_DEVICES, device.getId())) {
            return toDocument(this.arango.graphReplaceVertex(GRAPH_IOT_RELATION, COLLECTION_DEVICES, device.getId(), device));
        } else {
            return toDocument(this.arango.graphCreateVertex(GRAPH_IOT_RELATION, COLLECTION_DEVICES, device, false));
        }
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
     * Create or replace user device relation
     *
     * @param uid      User Id
     * @param did      Device Id
     * @param relation User Follow Device Relation
     * @return Relation WITHOUT UserFollowDevice entity
     */
    public Relation<UserFollowDevice> createOrReplaceUserFollowDevice(String uid, String did, UserFollowDevice relation) throws ArangoException {
        // create or replace
        if (checkExist(EDGE_USER_FOLLOW_DEVICE, userFollowDeviceId(uid, did))) {
            return toRelation(this.arango.graphReplaceEdge(GRAPH_IOT_RELATION, EDGE_USER_FOLLOW_DEVICE,
                    userFollowDeviceId(uid, did),
                    relation));
        } else {
            return toRelation(this.arango.graphCreateEdge(GRAPH_IOT_RELATION, EDGE_USER_FOLLOW_DEVICE,
                    userFollowDeviceId(uid, did),
                    keyToHandle(COLLECTION_USERS, uid),
                    keyToHandle(COLLECTION_DEVICES, did),
                    relation, false));
        }
    }

    /**
     * Get user follow device relation
     *
     * @param uid User Id
     * @param did Device Id
     * @return Relation with UserFollowDevice entity
     * @throws ArangoException If relation not exist
     */
    public Relation<UserFollowDevice> getUserFollowDevice(String uid, String did) throws ArangoException {
        return toRelation(this.arango.graphGetEdge(GRAPH_IOT_RELATION, EDGE_USER_FOLLOW_DEVICE, userFollowDeviceId(uid, did), UserFollowDevice.class));
    }

    /**
     * Delete user device relation
     *
     * @param uid User Id
     * @param did Device Id
     * @return Deleted?
     * @throws ArangoException If relation not exist
     */
    public boolean deleteUserFollowDevice(String uid, String did) throws ArangoException {
        return this.arango.graphDeleteEdge(GRAPH_IOT_RELATION, EDGE_USER_FOLLOW_DEVICE, userFollowDeviceId(uid, did)).getDeleted();
    }

    /**
     * Get user id set which device being followed
     *
     * @param did Device Id
     * @param min Minimal Relation requirement
     * @param max Maximum Relation requirement
     * @return User Id Set
     */
    public Set<String> getDeviceFollowedUserId(String did, UserFollowDevice min, UserFollowDevice max) throws ArangoException {
        Set<String> set = new HashSet<>();
        // query
        Map<String, Object> bindVars = new MapBuilder().put("to", keyToHandle(COLLECTION_DEVICES, did)).put("min", min.getPermission()).put("max", max.getPermission()).get();
        // query user device edge, return user id set
        CursorResultSet<String> rs = this.arango.executeQueryWithResultSet(Query.GET_DEVICE_FOLLOWED_USER_ID, bindVars, String.class, true, 20);
        // deal with result
        for (String uid : rs) {
            set.add(handleToKey(uid));
        }
        return set;
    }

    /**
     * Get device id set which user following
     *
     * @param uid User Id
     * @param min Minimal Relation requirement
     * @param max Maximum Relation requirement
     * @return Device Id Set
     */
    public Set<String> getUserFollowingDeviceId(String uid, UserFollowDevice min, UserFollowDevice max) throws ArangoException {
        Set<String> set = new HashSet<>();
        // query
        Map<String, Object> bindVars = new MapBuilder().put("from", keyToHandle(COLLECTION_USERS, uid)).put("min", min.getPermission()).put("max", max.getPermission()).get();
        // query user device edge, return device id set
        CursorResultSet<String> rs = this.arango.executeQueryWithResultSet(Query.GET_USER_RELATED_DEVICE_ID, bindVars, String.class, false, 0);
        // deal with result
        for (String did : rs) {
            set.add(handleToKey(did));
        }
        return set;
    }

    /**
     * Check whether document exist
     *
     * @param collection Collection Name
     * @param documentKey Document Key
     * @return True if document exist
     */
    protected boolean checkExist(String collection, String documentKey) throws ArangoException {
        try {
            this.arango.checkDocument(collection, documentKey);
            return true;
        } catch (ArangoException e) {
            if (e.getCode() == 0 && e.getErrorNumber() == 404) {
                return false;
            } else {
                throw e;
            }
        }
    }
}
