package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.async.AsyncClientPolicy;
import com.aerospike.client.listener.RecordListener;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.api.protocol.Const;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
import com.github.longkerdandy.evo.api.protocol.Permission;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AerospikeAsyncStorage Test
 */
public class AerospikeAsyncStorageTest {

    private static AerospikeStorage storage;
    private static AerospikeAsyncStorage asyncStorage;

    @BeforeClass
    public static void before() {
        Host[] hosts = new Host[]{
                new Host("192.168.253.68", 3000),
        };
        storage = new AerospikeStorage(new ClientPolicy(), hosts);
        asyncStorage = new AerospikeAsyncStorage(new AsyncClientPolicy(), hosts);
    }

    @AfterClass
    public static void after() {
        storage.close();
        asyncStorage.close();
    }

    private boolean completed;

    private synchronized void waitTillComplete() {
        completed = false;
        while (!completed) {
            try {
                super.wait();
            } catch (InterruptedException ignore) {
            }
        }
    }

    private synchronized void notifyCompleted() {
        completed = true;
        super.notify();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void userOwnDeviceTest() {
        // create new user
        User userA = EntityFactory.newUser("u000001");
        userA.setAlias("UserA");
        userA.setEmail("usera@example.com");
        userA.setMobile("18600000000");
        userA.setPassword("passwr0d");
        storage.updateUser(userA);

        // create new device
        Device deviceA = EntityFactory.newDevice("d000001");
        deviceA.setType(DeviceType.DEVICE);
        deviceA.setDescId("Desc1");
        deviceA.setProtocol(Const.PROTOCOL_TCP_1_0);
        deviceA.setConnected("Node1");
        storage.updateDevice(deviceA);

        // update own
        storage.updateUserOwnDevice("u000001", "d000001", Permission.READ);
        userA = storage.getUserById("u000001");
        assert userA.getOwn().get(0).get(Scheme.OWN_DEVICE).equals("d000001");
        assert NumberUtils.toInt(String.valueOf(userA.getOwn().get(0).get(Scheme.OWN_PERMISSION))) == Permission.READ;
        deviceA = storage.getDeviceById("d000001");
        assert deviceA.getOwn().get(0).get(Scheme.OWN_USER).equals("u000001");
        assert NumberUtils.toInt(String.valueOf(deviceA.getOwn().get(0).get(Scheme.OWN_PERMISSION))) == Permission.READ;
        assert storage.isUserOwnDevice("u000001", "d000001", Permission.READ);
        assert !storage.isUserOwnDevice("u000001", "d000001", Permission.READ_WRITE);

        asyncStorage.getDeviceById("d000001", new RecordListener() {
            @Override
            public void onSuccess(Key key, Record record) {
                if (record != null) {
                    List<Map<String, Object>> od = (List<Map<String, Object>>) record.getValue(Scheme.BIN_D_OWN);
                    assert storage.hasOwn(od, "u000001", "d000001", Permission.READ, Permission.OWNER);
                } else {
                    assert false;
                }
                notifyCompleted();
            }

            @Override
            public void onFailure(AerospikeException exception) {
                assert false;
                notifyCompleted();
            }
        });
        waitTillComplete();

        // clear
        storage.ac.delete(null, new Key(Scheme.NS_EVO, Scheme.SET_USERS, "u000001"));
        storage.ac.delete(null, new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, "d000001"));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void userDeviceTokenTest() {
        // create new device
        Device deviceA = EntityFactory.newDevice("d000001");
        deviceA.setType(DeviceType.DEVICE);
        deviceA.setDescId("Desc1");
        deviceA.setProtocol(Const.PROTOCOL_TCP_1_0);
        deviceA.setConnected("Node1");
        List<Map<String, Object>> l = new ArrayList<>();
        l.add(new HashMap<String, Object>() {{
            put(Scheme.OWN_USER, "u000001");
            put(Scheme.OWN_DEVICE, "d000001");
            put(Scheme.OWN_PERMISSION, Permission.READ_WRITE);
        }});
        deviceA.setOwn(l);
        deviceA.setCtrl("u000001");
        deviceA.setCtrlToken("1234567890");
        storage.updateDevice(deviceA);

        // token
        assert storage.isUserDeviceTokenCorrect("u000001", "d000001", "1234567890");
        assert !storage.isUserDeviceTokenCorrect("u000002", "d000001", "1234567890");
        asyncStorage.getDeviceById("d000001", new RecordListener() {
            @Override
            public void onSuccess(Key key, Record record) {
                if (record != null) {
                    String c = record.getString(Scheme.BIN_D_CTRL);
                    String ct = record.getString(Scheme.BIN_D_CTRL_TOKEN);
                    assert "u000001".equals(c) && "1234567890".equals(ct);
                } else {
                    assert false;
                }
                notifyCompleted();
            }

            @Override
            public void onFailure(AerospikeException exception) {
                assert false;
                notifyCompleted();
            }
        });
        waitTillComplete();

        // clear
        storage.ac.delete(null, new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, "d000001"));
        storage.ac.delete(null, new Key(Scheme.NS_EVO, Scheme.SET_DEVICES_ATTR, "d000001"));
    }
}
