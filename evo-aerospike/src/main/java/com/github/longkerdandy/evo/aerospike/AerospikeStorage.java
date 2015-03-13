package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aerospike Database Access Layer
 */
public class AerospikeStorage {

    /**
     * The AerospikeClient instance is thread-safe and can be used concurrently.
     * Each get/set call is a blocking, synchronous network call to Aerospike.
     * Connections are cached with a connection pool for each server node.
     */
    protected final AerospikeClient ac;
    private final Logger logger = LoggerFactory.getLogger(AerospikeStorage.class);

    /**
     * Constructor
     * <p>
     * The best practice is to specify each node in the cluster when creating the client.
     * The client will iterate through the array of nodes until it successfully connects to a node,
     * then the client will discover the other nodes in the cluster through that node.
     *
     * @param policy Aerospike Client Policy
     * @param hosts  Aerospike Server Seed Nodes
     */
    public AerospikeStorage(ClientPolicy policy, Host[] hosts) {
        this.ac = new AerospikeClient(policy, hosts);
    }

    /**
     * Close & Cleanup
     * <p>
     * When all transactions are finished and the application is ready to have a clean shutdown,
     * call the close() method to remove the resources held by the AerospikeStorage object.
     * The AerospikeStorage object is no longer usable once close has been called.
     */
    public void close() {
        this.ac.close();
    }

    /**
     * Create or Update new user
     * Validate before invoking this method!
     *
     * @param user User
     */
    public void updateUser(User user) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USER, user.getId());
        this.ac.put(null, k, Converter.userToBins(user));
    }

    /**
     * Get user by id
     *
     * @param userId User Id
     * @return User
     */
    public User getUserById(String userId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USER, userId);
        Record r = this.ac.get(null, k);
        return Converter.recordToUser(r);
    }

    /**
     * Is user email already exist?
     *
     * @param email Mail
     * @return True if already exist
     */
    public boolean isUserEmailExist(String email) {
        Statement stmt = new Statement();
        stmt.setNamespace(Scheme.NS_EVO);
        stmt.setSetName(Scheme.SET_USER);
        stmt.setFilters(Filter.equal(Scheme.BIN_USER_EMAIL, email));
        try (RecordSet rs = this.ac.query(null, stmt)) {
            return rs.next();
        }
    }

    /**
     * Is user mobile already exist?
     *
     * @param mobile Mobile
     * @return True if already exist
     */
    public boolean isUserMobileExist(String mobile) {
        Statement stmt = new Statement();
        stmt.setNamespace(Scheme.NS_EVO);
        stmt.setSetName(Scheme.SET_USER);
        stmt.setFilters(Filter.equal(Scheme.BIN_USER_MOBILE, mobile));
        try (RecordSet rs = this.ac.query(null, stmt)) {
            return rs.next();
        }
    }

    /**
     * Is user id and password correct?
     * Password must be encoded
     *
     * @param userId User Id
     * @param password Password (Encoded)
     * @return True if correct
     */
    public boolean isUserPasswordCorrect(String userId, String password) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USER, userId);
        Record r = this.ac.get(null, k, Scheme.BIN_USER_PASSWORD);
        return r != null && password.equals(r.getValue(Scheme.BIN_USER_PASSWORD));
    }
}
