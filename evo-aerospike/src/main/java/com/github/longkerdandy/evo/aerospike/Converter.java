package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Record;
import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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
     * @param user   User
     * @param filter Exclude null and empty fields?
     * @return Bin[]
     */
    public static Bin[] userToBins(User user, boolean filter) {
        List<Bin> bins = new ArrayList<>();
        if (!filter || StringUtils.isNotBlank(user.getId()))
            bins.add(new Bin(Scheme.BIN_U_ID, user.getId()));
        if (!filter || StringUtils.isNotBlank(user.getAlias()))
            bins.add(new Bin(Scheme.BIN_U_ALIAS, user.getAlias()));
        if (!filter || StringUtils.isNotBlank(user.getEmail()))
            bins.add(new Bin(Scheme.BIN_U_EMAIL, user.getEmail()));
        if (!filter || StringUtils.isNotBlank(user.getMobile()))
            bins.add(new Bin(Scheme.BIN_U_MOBILE, user.getMobile()));
        if (!filter || StringUtils.isNotBlank(user.getPassword()))
            bins.add(new Bin(Scheme.BIN_U_PASSWORD, user.getPassword()));

        // exclude relation
        // new Bin(Scheme.BIN_U_OWN, Value.get(user.getOwn())),
        // new Bin(Scheme.BIN_U_CTRL, Value.get(user.getCtrl())),

        return bins.toArray(new Bin[bins.size()]);
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
        User u = EntityFactory.newUser(record.getString(Scheme.BIN_U_ID));
        u.setAlias(record.getString(Scheme.BIN_U_ALIAS));
        u.setEmail(record.getString(Scheme.BIN_U_EMAIL));
        u.setMobile(record.getString(Scheme.BIN_U_MOBILE));
        u.setOwn((List<Map<String, Object>>) record.getValue(Scheme.BIN_U_OWN));
        u.setCtrl((List<String>) record.getValue(Scheme.BIN_U_CTRL));
        return u;
    }

    /**
     * Convert device entity to aerospike bins
     *
     * @param device User
     * @param filter Exclude null and empty fields?
     * @return Bin[]
     */
    public static Bin[] deviceToBins(Device device, boolean filter) {
        List<Bin> bins = new ArrayList<>();
        if (!filter || StringUtils.isNotBlank(device.getId()))
            bins.add(new Bin(Scheme.BIN_D_ID, device.getId()));
        if (!filter || device.getType() > 0)
            bins.add(new Bin(Scheme.BIN_D_TYPE, device.getType()));
        if (!filter || StringUtils.isNotBlank(device.getDescId()))
            bins.add(new Bin(Scheme.BIN_D_DESC_ID, device.getDescId()));
        if (!filter || device.getProtocol() > 0)
            bins.add(new Bin(Scheme.BIN_D_PROTOCOL, device.getProtocol()));
        if (!filter || StringUtils.isNotBlank(device.getToken()))
            bins.add(new Bin(Scheme.BIN_D_TOKEN, device.getToken()));
        if (!filter || StringUtils.isNotBlank(device.getConnected()))
            bins.add(new Bin(Scheme.BIN_D_CONN, device.getConnected()));

        // exclude relation
        // new Bin(Scheme.BIN_D_OWN, Value.get(device.getOwn())),
        // new Bin(Scheme.BIN_D_CTRL, device.getCtrl()),

        return bins.toArray(new Bin[bins.size()]);
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
        Device d = EntityFactory.newDevice(record.getString(Scheme.BIN_D_ID));
        d.setType(record.getInt(Scheme.BIN_D_TYPE));
        d.setDescId(record.getString(Scheme.BIN_D_DESC_ID));
        d.setProtocol(record.getInt(Scheme.BIN_D_PROTOCOL));
        d.setConnected(record.getString(Scheme.BIN_D_CONN));
        d.setOwn((List<Map<String, Object>>) record.getValue(Scheme.BIN_D_OWN));
        d.setCtrl(record.getString(Scheme.BIN_D_CTRL));
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
