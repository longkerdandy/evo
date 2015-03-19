package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Create or Update user
     * Validate before invoking this method!
     *
     * @param user User
     */
    public void updateUser(User user) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, user.getId());
        this.ac.put(p, k, Converter.userToBins(user));
    }

    /**
     * Get user by id
     *
     * @param userId User Id
     * @return User
     */
    public User getUserById(String userId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
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
        stmt.setSetName(Scheme.SET_USERS);
        stmt.setFilters(Filter.equal(Scheme.BIN_U_EMAIL, email));
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
        stmt.setSetName(Scheme.SET_USERS);
        stmt.setFilters(Filter.equal(Scheme.BIN_U_MOBILE, mobile));
        try (RecordSet rs = this.ac.query(null, stmt)) {
            return rs.next();
        }
    }

    /**
     * Is user id and password correct?
     * Password must be encoded
     *
     * @param userId   User Id
     * @param password Password (Encoded)
     * @return True if correct
     */
    public boolean isUserPasswordCorrect(String userId, String password) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record r = this.ac.get(null, k, Scheme.BIN_U_PASSWORD);
        return r != null && password.equals(r.getValue(Scheme.BIN_U_PASSWORD));
    }

    /**
     * Is device exist?
     *
     * @param deviceId Device Id
     * @return True if exist
     */
    public boolean isDeviceExist(String deviceId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        return this.ac.exists(null, k);
    }

    /**
     * Create or Update device
     * Validate before invoking this method!
     *
     * @param device Device
     */
    public void updateDevice(Device device) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, device.getId());
        this.ac.put(p, k, Converter.deviceToBins(device));
    }

    /**
     * Get device by id
     *
     * @param deviceId Device Id
     * @return Device
     */
    public Device getDeviceById(String deviceId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k);
        return Converter.recordToDevice(r);
    }

    /**
     * Create or Update device attribute
     *
     * @param deviceId Device Id
     * @param attr     Device Attribute
     */
    public void updateDeviceAttr(String deviceId, Map<String, Object> attr) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, deviceId);
        this.ac.put(p, k, Converter.mapToBins(attr));
    }

    /**
     * Create or Replace device attribute
     *
     * @param deviceId Device Id
     * @param attr     Device Attribute
     */
    public void replaceDeviceAttr(String deviceId, Map<String, Object> attr) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.REPLACE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, deviceId);
        this.ac.put(p, k, Converter.mapToBins(attr));
    }

    /**
     * Get device attributes by id
     *
     * @param deviceId Device Id
     * @return Device Attribute
     */
    public Map<String, Object> getDeviceAttr(String deviceId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, deviceId);
        Record r = this.ac.get(null, k);
        return Converter.recordToMap(r);
    }

    /**
     * Update user own device relation
     * Make sure user and device exist before invoking this method!
     *
     * @param userId     User Id
     * @param deviceId   Device Id
     * @param permission Ownership Permission
     */
    @SuppressWarnings("unchecked")
    public void updateUserOwnDevice(String userId, String deviceId, int permission) {
        Key ku = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record ru = this.ac.get(null, ku, Scheme.BIN_U_OWN);
        Key kd = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record rd = this.ac.get(null, kd);
        if (ru == null || rd == null) {
            logger.debug("User {} or device {} not existed, update failed", userId, deviceId);
            return;
        }
        List<Map<String, Object>> ou = (List<Map<String, Object>>) ru.getValue(Scheme.BIN_U_OWN);
        List<Map<String, Object>> od = (List<Map<String, Object>>) rd.getValue(Scheme.BIN_D_OWN);
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        this.ac.put(p, ku, new Bin(Scheme.BIN_U_OWN, Value.getAsList(updateOwn(ou, userId, deviceId, permission))));
        this.ac.put(p, kd, new Bin(Scheme.BIN_D_OWN, Value.getAsList(updateOwn(od, userId, deviceId, permission))));
    }

    /**
     * Remove user own device relation
     *
     * @param userId   User Id
     * @param deviceId Device Id
     */
    @SuppressWarnings("unchecked")
    public void removeUserOwnDevice(String userId, String deviceId) {
        Key ku = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record ru = this.ac.get(null, ku, Scheme.BIN_U_OWN);
        Key kd = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record rd = this.ac.get(null, kd);
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        if (ru != null) {
            List<Map<String, Object>> ou = (List<Map<String, Object>>) ru.getValue(Scheme.BIN_U_OWN);
            if (ou != null) {
                this.ac.put(p, ku, new Bin(Scheme.BIN_U_OWN, Value.getAsList(removeOwn(ou, userId, deviceId))));
            }
        }
        if (rd != null) {
            List<Map<String, Object>> od = (List<Map<String, Object>>) rd.getValue(Scheme.BIN_D_OWN);
            if (od != null) {
                this.ac.put(p, kd, new Bin(Scheme.BIN_D_OWN, Value.getAsList(removeOwn(od, userId, deviceId))));
            }
        }
    }

    /**
     * Get user's owned devices
     *
     * @param userId User Id
     * @return List of own relation
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserOwnee(String userId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record r = this.ac.get(null, k, Scheme.BIN_U_OWN);
        return r != null ? (List<Map<String, Object>>) r.getValue(Scheme.BIN_U_OWN) : null;
    }

    /**
     * Get device's owners (users)
     *
     * @param deviceId Device Id
     * @return List of own relation
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDeviceOwner(String deviceId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k);
        return r != null ? (List<Map<String, Object>>) r.getValue(Scheme.BIN_D_OWN) : null;
    }

    /**
     * Update ownership
     */
    protected List<Map<String, Object>> updateOwn(List<Map<String, Object>> own, String userId, String deviceId, int permission) {
        if (own == null) {
            own = new ArrayList<>();
        }
        boolean b = false;
        for (Map<String, Object> map : own) {
            if (map.get(Scheme.OWN_USER).equals(userId) && map.get(Scheme.OWN_DEVICE).equals(deviceId)) {
                map.put(Scheme.OWN_PERMISSION, permission);
                b = true;
                break;
            }
        }
        if (!b) {
            Map<String, Object> map = new HashMap<>();
            map.put(Scheme.OWN_USER, userId);
            map.put(Scheme.OWN_DEVICE, deviceId);
            map.put(Scheme.OWN_PERMISSION, permission);
            own.add(map);
        }

        return own;
    }

    /**
     * Remove ownership
     */
    protected List<Map<String, Object>> removeOwn(List<Map<String, Object>> own, String userId, String deviceId) {
        if (own == null) {
            return null;
        }
        for (Map<String, Object> map : own) {
            if (map.get(Scheme.OWN_USER).equals(userId) && map.get(Scheme.OWN_DEVICE).equals(deviceId)) {
                own.remove(map);
                break;
            }
        }
        return own;
    }
}
