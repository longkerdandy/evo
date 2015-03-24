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
import com.github.longkerdandy.evo.api.protocol.Permission;

import java.util.*;
import java.util.stream.Collectors;

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
     * Is description exist?
     *
     * @param descId Description Id
     * @return True if exist
     */
    public boolean isDescriptionExist(String descId) {
        // TODO: add real logic
        return true;
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
     * Try to mark device as disconnect
     *
     * @param deviceId Device Id
     * @param node     Node Device disconnect from
     * @return True if successes
     */
    public boolean updateDeviceDisconnect(String deviceId, String node) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k, Scheme.BIN_D_CONN);
        // mark as disconnect if node name match
        if (r != null && node.equals(r.getString(Scheme.BIN_D_CONN))) {
            WritePolicy p = new WritePolicy();
            p.recordExistsAction = RecordExistsAction.UPDATE;
            this.ac.put(p, k, new Bin(Scheme.BIN_D_CONN, Value.getAsNull()));
            return true;
        } else {
            return false;
        }
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
     * Is User's token correct (on specific controller)
     *
     * @param userId   User Id
     * @param deviceId Device Id (Controller)
     * @param token    Token
     * @return True if token is correct
     */
    public boolean isUserDeviceTokenCorrect(String userId, String deviceId, String token) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k, Scheme.BIN_D_CTRL, Scheme.BIN_D_CTRL_TOKEN);
        return r != null && userId.equals(r.getString(Scheme.BIN_D_CTRL)) && token.equals(r.getString(Scheme.BIN_D_CTRL_TOKEN));
    }

    /**
     * Create or Update device attribute
     * Make sure device exist before invoking this method!
     *
     * @param deviceId        Device Id
     * @param attr            Device Attribute
     * @param checkUpdateTime Check Update Timestamp?
     */
    public void updateDeviceAttr(String deviceId, Map<String, Object> attr, boolean checkUpdateTime) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, deviceId);
        if (!isTimestampValid((Long) attr.get(Scheme.BIN_D_A_UPDATE_TIME))) {
            attr.put(Scheme.BIN_D_A_UPDATE_TIME, System.currentTimeMillis());
        }
        if (checkUpdateTime) {
            Record r = this.ac.get(null, k, Scheme.BIN_D_A_UPDATE_TIME);
            if (r == null || (Long) attr.get(Scheme.BIN_D_A_UPDATE_TIME) >= r.getLong(Scheme.BIN_D_A_UPDATE_TIME)) {
                this.ac.put(p, k, Converter.mapToBins(attr));
            }
        } else {
            this.ac.put(p, k, Converter.mapToBins(attr));
        }
    }

    /**
     * Create or Replace device attribute
     * Make sure device exist before invoking this method!
     *
     * @param deviceId        Device Id
     * @param attr            Device Attribute
     * @param checkUpdateTime Check Update Timestamp?
     */
    public void replaceDeviceAttr(String deviceId, Map<String, Object> attr, boolean checkUpdateTime) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.REPLACE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, deviceId);
        if (!isTimestampValid((Long) attr.get(Scheme.BIN_D_A_UPDATE_TIME))) {
            attr.put(Scheme.BIN_D_A_UPDATE_TIME, System.currentTimeMillis());
        }
        if (checkUpdateTime) {
            Record r = this.ac.get(null, k, Scheme.BIN_D_A_UPDATE_TIME);
            if (r == null || (Long) attr.get(Scheme.BIN_D_A_UPDATE_TIME) >= r.getLong(Scheme.BIN_D_A_UPDATE_TIME)) {
                this.ac.put(p, k, Converter.mapToBins(attr));
            }
        } else {
            this.ac.put(p, k, Converter.mapToBins(attr));
        }
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
        Record rd = this.ac.get(null, kd, Scheme.BIN_D_OWN);

        if (ru != null && rd != null) {
            WritePolicy p = new WritePolicy();
            p.recordExistsAction = RecordExistsAction.UPDATE;
            List<Map<String, Object>> ou = (List<Map<String, Object>>) ru.getValue(Scheme.BIN_U_OWN);
            if (!hasOwn(ou, userId, deviceId, permission, permission)) {
                this.ac.put(p, ku, new Bin(Scheme.BIN_U_OWN, Value.get(updateOwn(ou, userId, deviceId, permission))));
            }
            List<Map<String, Object>> od = (List<Map<String, Object>>) rd.getValue(Scheme.BIN_D_OWN);
            if (!hasOwn(od, userId, deviceId, permission, permission)) {
                this.ac.put(p, kd, new Bin(Scheme.BIN_D_OWN, Value.get(updateOwn(od, userId, deviceId, permission))));
            }
        }
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
        Record rd = this.ac.get(null, kd, Scheme.BIN_D_OWN);
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;

        if (ru != null) {
            List<Map<String, Object>> ou = (List<Map<String, Object>>) ru.getValue(Scheme.BIN_U_OWN);
            if (hasOwn(ou, userId, deviceId, Permission.NONE, Permission.OWNER)) {
                this.ac.put(p, ku, new Bin(Scheme.BIN_U_OWN, Value.get(removeOwn(ou, userId, deviceId))));
            }
        }

        if (rd != null) {
            List<Map<String, Object>> od = (List<Map<String, Object>>) rd.getValue(Scheme.BIN_D_OWN);
            if (hasOwn(od, userId, deviceId, Permission.NONE, Permission.OWNER)) {
                this.ac.put(p, kd, new Bin(Scheme.BIN_D_OWN, Value.get(removeOwn(od, userId, deviceId))));
            }
        }
    }

    /**
     * Is user owns device with at least permission
     *
     * @param userId   User Id
     * @param deviceId Device Id
     * @param min      Minimal Permission
     * @return True if owns
     */
    @SuppressWarnings("unchecked")
    public boolean isUserOwnDevice(String userId, String deviceId, int min) {
        Key kd = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record rd = this.ac.get(null, kd, Scheme.BIN_D_OWN);
        if (rd != null) {
            List<Map<String, Object>> od = (List<Map<String, Object>>) rd.getValue(Scheme.BIN_D_OWN);
            return hasOwn(od, userId, deviceId, min, Permission.OWNER);
        }
        return false;
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
        Record r = this.ac.get(null, k, Scheme.BIN_D_OWN);
        return r != null ? (List<Map<String, Object>>) r.getValue(Scheme.BIN_D_OWN) : null;
    }

    /**
     * Update user control device relation
     * Make sure user and device exist before invoking this method!
     *
     * @param userId   User Id
     * @param deviceId Device Id
     */
    @SuppressWarnings("unchecked")
    public void updateUserControlDevice(String userId, String deviceId) {
        Key ku = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record ru = this.ac.get(null, ku, Scheme.BIN_U_CTRL);
        Key kd = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record rd = this.ac.get(null, kd, Scheme.BIN_D_CTRL);

        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;

        if (ru != null && rd != null) {
            List<String> cu = (List<String>) ru.getValue(Scheme.BIN_U_CTRL);
            if (cu == null) cu = new ArrayList<>();
            if (!cu.contains(deviceId)) {
                cu.add(deviceId);
                this.ac.put(p, ku, new Bin(Scheme.BIN_U_CTRL, Value.get(cu)));
            }

            String cd = rd.getString(Scheme.BIN_D_CTRL);
            // remove old control relation
            if (cd != null && !cd.equals(userId)) removeUserControlDevice(cd, deviceId);
            if (cd == null || !cd.equals(userId)) this.ac.put(p, kd, new Bin(Scheme.BIN_D_CTRL, userId));
        }
    }

    /**
     * Remove user control device relation
     *
     * @param userId   User Id
     * @param deviceId Device Id
     */
    @SuppressWarnings("unchecked")
    public void removeUserControlDevice(String userId, String deviceId) {
        Key ku = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record ru = this.ac.get(null, ku, Scheme.BIN_U_CTRL);
        Key kd = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record rd = this.ac.get(null, kd, Scheme.BIN_D_CTRL);

        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;

        if (ru != null) {
            List<String> cu = (List<String>) ru.getValue(Scheme.BIN_U_CTRL);
            if (cu != null && cu.removeAll(Collections.singleton(deviceId))) {
                this.ac.put(p, ku, new Bin(Scheme.BIN_U_CTRL, Value.get(cu)));
            }
        }

        if (rd != null && userId.equals(rd.getString(Scheme.BIN_D_CTRL))) {
            this.ac.put(p, kd, new Bin(Scheme.BIN_D_CTRL, Value.getAsNull()));
        }
    }

    /**
     * Get user's controlled devices
     *
     * @param userId User Id
     * @return List of Device
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserControllee(String userId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record r = this.ac.get(null, k, Scheme.BIN_U_CTRL);
        return r != null ? (List<String>) r.getValue(Scheme.BIN_U_CTRL) : null;
    }

    /**
     * Get device's owned user's controllee devices
     *
     * @param deviceId Device Id
     * @param min      Minimal Permission
     * @param max      Maximal Permission
     * @return List of Device
     */
    @SuppressWarnings("unchecked")
    public Set<String> getDeviceOwnerControllee(String deviceId, int min, int max) {
        Set<String> set = new HashSet<>();

        Key kd = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, kd, Scheme.BIN_D_OWN);
        if (r == null) {
            return null;
        }

        List<Map<String, Object>> o = (List<Map<String, Object>>) r.getValue(Scheme.BIN_D_OWN);
        if (o != null) {
            for (Map<String, Object> m : o) {
                long p = (long) m.getOrDefault(Scheme.OWN_PERMISSION, 0);
                if (p >= min && p <= max) {
                    String u = (String) m.get(Scheme.OWN_USER);
                    Key ku = new Key(Scheme.NS_EVO, Scheme.SET_USERS, u);
                    Record ru = this.ac.get(null, ku, Scheme.BIN_U_CTRL);
                    if (ru != null) {
                        List<String> cu = (List<String>) ru.getValue(Scheme.BIN_U_CTRL);
                        if (cu != null) {
                            set.addAll(cu.stream().collect(Collectors.toList()));
                        }
                    }
                }
            }
        }

        return set;
    }

    /**
     * Is timestamp valid
     */
    protected boolean isTimestampValid(Long timestamp) {
        return timestamp != null && timestamp > 0;
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
        Iterator<Map<String, Object>> i = own.iterator();
        while (i.hasNext()) {
            Map<String, Object> m = i.next();
            if (m.get(Scheme.OWN_USER).equals(userId) && m.get(Scheme.OWN_DEVICE).equals(deviceId)) {
                i.remove();
            }
        }
        return own;
    }

    /**
     * Has ownership?
     */
    protected boolean hasOwn(List<Map<String, Object>> own, String userId, String deviceId, int min, int max) {
        if (own != null) {
            for (Map<String, Object> m : own) {
                if (m.get(Scheme.OWN_USER).equals(userId) && m.get(Scheme.OWN_DEVICE).equals(deviceId)
                        && (long) m.getOrDefault(Scheme.OWN_PERMISSION, 0) >= min && (long) m.getOrDefault(Scheme.OWN_PERMISSION, 0) <= max) {
                    return true;
                }
            }
        }
        return false;
    }
}
