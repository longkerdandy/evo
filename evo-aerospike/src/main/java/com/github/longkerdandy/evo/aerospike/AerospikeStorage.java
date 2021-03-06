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
     * Initialize Aerospike client with suitable hosts to seed the cluster map.
     * The client policy is used to set defaults and size internal data structures.
     * For each host connection that succeeds, the client will:
     * <p>
     * - Add host to the cluster map <br>
     * - Request host's list of other nodes in cluster <br>
     * - Add these nodes to cluster map <br>
     * <p>
     * In most cases, only one host is necessary to seed the cluster. The remaining hosts
     * are added as future seeds in case of a complete network failure.
     * <p>
     * If one connection succeeds, the client is ready to process database requests.
     * If all connections fail and the policy's failIfNotConnected is true, a connection
     * exception will be thrown. Otherwise, the cluster will remain in a disconnected state
     * until the server is activated.
     *
     * @param policy client configuration parameters, pass in null for defaults
     * @param hosts  array of potential hosts to seed the cluster
     * @throws AerospikeException if all host connections fail
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
     * Determine if we are ready to talk to the database server cluster
     *
     * @return True if cluster is ready
     */
    public final boolean isConnected() {
        return this.ac.isConnected();
    }

    /**
     * Create or Replace verify code
     * Validate before invoking this method!
     *
     * @param verifyId Mobile or Email
     * @param code     Verify Code
     * @param ttl      Time to live
     */
    public void replaceVerify(String verifyId, String code, int ttl) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.REPLACE;
        p.expiration = ttl;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_VERIFY, verifyId);
        this.ac.put(p, k, new Bin(Scheme.BIN_V_ID, verifyId), new Bin(Scheme.BIN_V_CODE, code));
    }

    /**
     * Is verify code correct?
     *
     * @param verifyId Mobile or email
     * @param code     Verify code
     */
    public boolean isVerifyCodeCorrect(String verifyId, String code) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_VERIFY, verifyId);
        Record r = this.ac.get(null, k, Scheme.BIN_V_CODE);
        return r != null && code.equals(r.getString(Scheme.BIN_V_CODE));
    }

    /**
     * Create or Update user
     * Validate before invoking this method!
     *
     * @param user   User
     * @param filter Exclude null and empty fields?
     */
    public void updateUser(User user, boolean filter) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, user.getId());
        this.ac.put(p, k, Converter.userToBins(user, filter));
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
     * Get user by email
     *
     * @param email Email
     * @return user
     */
    public User getUserByEmail(String email) {
        Statement stmt = new Statement();
        stmt.setNamespace(Scheme.NS_EVO);
        stmt.setSetName(Scheme.SET_USERS);
        stmt.setFilters(Filter.equal(Scheme.BIN_U_EMAIL, email));
        try (RecordSet rs = this.ac.query(null, stmt)) {
            return rs.next() ? Converter.recordToUser(rs.getRecord()) : null;
        }
    }

    /**
     * Get user by mobile
     *
     * @param mobile Mobile
     * @return user
     */
    public User getUserByMobile(String mobile) {
        Statement stmt = new Statement();
        stmt.setNamespace(Scheme.NS_EVO);
        stmt.setSetName(Scheme.SET_USERS);
        stmt.setFilters(Filter.equal(Scheme.BIN_U_MOBILE, mobile));
        try (RecordSet rs = this.ac.query(null, stmt)) {
            return rs.next() ? Converter.recordToUser(rs.getRecord()) : null;
        }
    }

    /**
     * Is user email already exists?
     *
     * @param email Mail
     * @return True if already exists
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
     * Is user mobile already exists?
     *
     * @param mobile Mobile
     * @return True if already exists
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
     * @param password Password (encoded)
     * @return True if correct
     */
    public boolean isUserPasswordCorrect(String userId, String password) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record r = this.ac.get(null, k, Scheme.BIN_U_PASSWORD);
        return r != null && password.equals(r.getString(Scheme.BIN_U_PASSWORD));
    }

    /**
     * Get user id by OAuth token
     *
     * @param token OAuth token
     * @return Null if not present
     */
    public String getUserIdByToken(String token) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_OAUTH_TOKEN, token);
        Record r = this.ac.get(null, k, Scheme.BIN_O_T_USER);
        return r != null ? r.getString(Scheme.BIN_O_T_USER) : null;
    }

    /**
     * Create or Replace user id with OAuth token
     *
     * @param userId User Id
     * @param token  OAuth token
     */
    public void replaceUserToken(String userId, String token) {
        // delete old token
        Statement stmt = new Statement();
        stmt.setNamespace(Scheme.NS_EVO);
        stmt.setSetName(Scheme.SET_OAUTH_TOKEN);
        stmt.setFilters(Filter.equal(Scheme.BIN_O_T_USER, userId));
        try (RecordSet rs = this.ac.query(null, stmt)) {
            while (rs.next()) {
                this.ac.delete(null, rs.getKey());
            }
        }
        // add new token
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.REPLACE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_OAUTH_TOKEN, token);
        this.ac.put(p, k, new Bin(Scheme.BIN_O_T_TOKEN, token), new Bin(Scheme.BIN_O_T_USER, userId));
    }

    /**
     * Is device exist?
     *
     * @param deviceId Device Id
     * @return True if exists
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
     * @param filter Exclude null and empty fields?
     */
    public void updateDevice(Device device, boolean filter) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, device.getId());
        this.ac.put(p, k, Converter.deviceToBins(device, filter));
    }

    /**
     * Try to mark device as disconnect
     *
     * @param deviceId Device Id
     * @param node     Node Device disconnect from
     * @return True if successes
     */
    public boolean setDeviceDisconnect(String deviceId, String node) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k, Scheme.BIN_D_CONN);
        // mark as disconnect if node name match
        // TODO: find a better way to do Check And Set
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

    //    /**
    //     * Is User's token correct (on specific controller)
    //     *
    //     * @param userId   User Id
    //     * @param deviceId Device Id (Controller)
    //     * @param token    Token
    //     * @return True if token is correct
    //     */
    //    public boolean isUserDeviceTokenCorrect(String userId, String deviceId, String token) {
    //        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
    //        Record r = this.ac.get(null, k, Scheme.BIN_D_CTRL, Scheme.BIN_D_CTRL_TOKEN);
    //        return r != null && userId.equals(r.getString(Scheme.BIN_D_CTRL)) && token.equals(r.getString(Scheme.BIN_D_CTRL_TOKEN));
    //    }

    /**
     * Create or Update device attribute
     * Make sure device exist before invoking this method!
     * Make sure attribute contains timestamp before invoking this method!
     *
     * @param deviceId        Device Id
     * @param attr            Device Attribute
     * @param checkUpdateTime Check Update Timestamp?
     */
    public void updateDeviceAttr(String deviceId, Map<String, Object> attr, boolean checkUpdateTime) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.UPDATE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, deviceId);
        // still, fault tolerance
        if (!attr.containsKey(Scheme.BIN_D_A_UPDATE_TIME)) {
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
     * Make sure attribute contains timestamp before invoking this method!
     *
     * @param deviceId        Device Id
     * @param attr            Device Attribute
     * @param checkUpdateTime Check Update Timestamp?
     */
    public void replaceDeviceAttr(String deviceId, Map<String, Object> attr, boolean checkUpdateTime) {
        WritePolicy p = new WritePolicy();
        p.recordExistsAction = RecordExistsAction.REPLACE;
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, deviceId);
        // still, fault tolerance
        if (!attr.containsKey(Scheme.BIN_D_A_UPDATE_TIME)) {
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
    public void addUserOwnDevice(String userId, String deviceId, int permission) {
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
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k, Scheme.BIN_D_OWN, Scheme.BIN_D_CTRL);
        if (r != null) {
            List<Map<String, Object>> o = (List<Map<String, Object>>) r.getValue(Scheme.BIN_D_OWN);
            if (hasOwn(o, userId, deviceId, min, Permission.OWNER)) return true;
            String c = r.getString(Scheme.BIN_D_CTRL);
            if (userId.equals(c)) return true;
        }
        return false;
    }

    /**
     * Get user's owned devices
     *
     * @param userId User Id
     * @param min    Minimal Permission
     * @return List of own relation
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserOwnee(String userId, int min) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_USERS, userId);
        Record r = this.ac.get(null, k, Scheme.BIN_U_OWN, Scheme.BIN_U_CTRL);
        if (r != null) {
            List<Map<String, Object>> o = (List<Map<String, Object>>) r.getValue(Scheme.BIN_D_OWN);
            List<String> c = (List<String>) r.getValue(Scheme.BIN_U_CTRL);
            if (c != null) {
                for (String d : c) {
                    o = updateOwn(o, userId, d, Permission.OWNER);
                }
            }
            return filterOwn(o, min, Permission.OWNER);
        } else {
            return null;
        }
    }

    /**
     * Get device's owners (users)
     *
     * @param deviceId Device Id
     * @param min      Minimal Permission
     * @return List of own relation
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDeviceOwner(String deviceId, int min) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k, Scheme.BIN_D_OWN, Scheme.BIN_D_CTRL);
        if (r != null) {
            List<Map<String, Object>> o = (List<Map<String, Object>>) r.getValue(Scheme.BIN_D_OWN);
            String c = r.getString(Scheme.BIN_D_CTRL);
            if (c != null) o = updateOwn(o, c, deviceId, Permission.OWNER);
            return filterOwn(o, min, Permission.OWNER);
        } else {
            return null;
        }
    }

    /**
     * Update user control device relation
     * Make sure user and device exist before invoking this method!
     *
     * @param userId   User Id
     * @param deviceId Device Id
     */
    @SuppressWarnings("unchecked")
    public void addUserControlDevice(String userId, String deviceId) {
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
            // update new control relation
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
     * Is device one of user's controller
     *
     * @param userId   User Id
     * @param deviceId Device Id
     * @return True if is controller
     */
    public boolean isUserControlDevice(String userId, String deviceId) {
        Key k = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        Record r = this.ac.get(null, k, Scheme.BIN_D_OWN, Scheme.BIN_D_CTRL);
        return r != null && userId.equals(r.getString(Scheme.BIN_D_CTRL));
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
        Record r = this.ac.get(null, kd, Scheme.BIN_D_OWN, Scheme.BIN_D_CTRL);
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

        String c = r.getString(Scheme.BIN_D_CTRL);
        if (c != null) set.add(c);

        return set;
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
     * Filter ownership
     */
    protected List<Map<String, Object>> filterOwn(List<Map<String, Object>> own, int min, int max) {
        if (own != null) {
            for (Iterator<Map<String, Object>> iterator = own.iterator(); iterator.hasNext(); ) {
                Map<String, Object> m = iterator.next();
                int p = Integer.valueOf(String.valueOf(m.getOrDefault(Scheme.OWN_PERMISSION, 0)));
                if (p < min || p > max) {
                    iterator.remove();
                }
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
                int p = Integer.valueOf(String.valueOf(m.getOrDefault(Scheme.OWN_PERMISSION, 0)));
                if (m.get(Scheme.OWN_USER).equals(userId) && m.get(Scheme.OWN_DEVICE).equals(deviceId) && p >= min && p <= max) {
                    return true;
                }
            }
        }
        return false;
    }
}
