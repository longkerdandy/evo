package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Record;
import com.aerospike.client.Value;
import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;

import java.util.List;
import java.util.Map;

/**
 * Converter
 */
public class Converter {

    private Converter() {
    }

    /**
     * Convert user entity to aerospike bins
     *
     * @param user User
     * @return Bin[]
     */
    public static Bin[] userToBins(User user) {
        return new Bin[]{
                new Bin(Scheme.BIN_U_ID, user.getId()),
                new Bin(Scheme.BIN_U_ALIAS, user.getAlias()),
                new Bin(Scheme.BIN_U_EMAIL, user.getEmail()),
                new Bin(Scheme.BIN_U_MOBILE, user.getMobile()),
                new Bin(Scheme.BIN_U_PASSWORD, user.getPassword()),
                new Bin(Scheme.BIN_U_OWN, Value.getAsList(user.getOwn()))
        };
    }

    /**
     * Convert aerospike record to user entity
     *
     * @param record Record
     * @return User
     */
    @SuppressWarnings("unchecked")
    public static User recordToUser(Record record) {
        if (record == null) return null;
        User u = EntityFactory.newUser((String) record.getValue(Scheme.BIN_U_ID));
        u.setAlias((String) record.getValue(Scheme.BIN_U_ALIAS));
        u.setEmail((String) record.getValue(Scheme.BIN_U_EMAIL));
        u.setMobile((String) record.getValue(Scheme.BIN_U_MOBILE));
        u.setOwn((List<Map<String, Object>>) record.getValue(Scheme.BIN_U_OWN));
        return u;
    }

    /**
     * Convert device entity to aerospike bins
     *
     * @param device User
     * @return Bin[]
     */
    public static Bin[] deviceToBins(Device device) {
        return new Bin[]{
                new Bin(Scheme.BIN_D_ID, device.getId()),
                new Bin(Scheme.BIN_D_TYPE, device.getType()),
                new Bin(Scheme.BIN_D_DESC_ID, device.getDescId()),
                new Bin(Scheme.BIN_D_PV, device.getPv()),
                new Bin(Scheme.BIN_D_CONN, device.getConnected()),
                new Bin(Scheme.BIN_D_OWN, Value.getAsList(device.getOwn()))
        };
    }

    /**
     * Convert aerospike record to device entity
     *
     * @param record Record
     * @return Device
     */
    @SuppressWarnings("unchecked")
    public static Device recordToDevice(Record record) {
        if (record == null) return null;
        Device d = EntityFactory.newDevice((String) record.getValue(Scheme.BIN_D_ID));
        d.setType(record.getInt(Scheme.BIN_D_TYPE));
        d.setDescId((String) record.getValue(Scheme.BIN_D_DESC_ID));
        d.setPv(record.getInt(Scheme.BIN_D_PV));
        d.setConnected((String) record.getValue(Scheme.BIN_D_CONN));
        d.setOwn((List<Map<String, Object>>) record.getValue(Scheme.BIN_D_OWN));
        return d;
    }

    /**
     * Convert map to aerospike bins
     *
     * @param map Map
     * @return Bin[]
     */
    public static Bin[] mapToBins(Map<String, Object> map) {
        Bin[] bins = new Bin[map.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            bins[i] = new Bin(entry.getKey(), entry.getValue());
            i++;
        }
        return bins;
    }

    /**
     * Convert aerospike record to Map
     *
     * @param record Record
     * @return Map
     */
    public static Map<String, Object> recordToMap(Record record) {
        if (record == null) return null;
        return record.bins;
    }
}
