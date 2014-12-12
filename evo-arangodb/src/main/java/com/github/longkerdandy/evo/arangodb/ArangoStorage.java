package com.github.longkerdandy.evo.arangodb;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.entity.DocumentEntity;
import com.github.longkerdandy.evo.api.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        return this.arango.graphCreateVertex("relation", "users", user, false);
    }

    public DocumentEntity<User> getUserById(String uid) throws ArangoException {
        return this.arango.graphGetVertex("relation", "users", uid, User.class);
    }
}
