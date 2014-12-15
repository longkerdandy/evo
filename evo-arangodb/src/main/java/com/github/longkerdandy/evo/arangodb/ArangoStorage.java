package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.entity.CursorEntity;
import com.arangodb.entity.DocumentEntity;
import com.github.longkerdandy.evo.api.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.github.longkerdandy.evo.arangodb.Const.COLLECTION_USERS;
import static com.github.longkerdandy.evo.arangodb.Const.GRAPH_IOT_RELATION;

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

    public DocumentEntity<User> createUser(User user) throws ArangoException {
        // user exist?
        Map<String, Object> example = new HashMap<>();
        if (StringUtils.isNotBlank(user.getEmail())) example.put("email", user.getEmail());
        if (StringUtils.isNotBlank(user.getEmail())) example.put("mobile", user.getMobile());
        CursorEntity<User> cursor = this.arango.executeSimpleByExample(COLLECTION_USERS, example, 0, 1, User.class);
        if (cursor.getCount() > 0) throw new ArangoException("user's email or mobile already exist");

        return this.arango.graphCreateVertex(GRAPH_IOT_RELATION, COLLECTION_USERS, user, false);
    }

    public DocumentEntity<User> getUserById(String uid) throws ArangoException {
        return this.arango.graphGetVertex(GRAPH_IOT_RELATION, COLLECTION_USERS, uid, User.class);
    }
}
