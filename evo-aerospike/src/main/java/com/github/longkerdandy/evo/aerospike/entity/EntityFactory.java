package com.github.longkerdandy.evo.aerospike.entity;

/**
 * Entity Factory
 */
@SuppressWarnings("unused")
public class EntityFactory {

    private EntityFactory() {
    }

    /**
     * Create a new Device
     *
     * @param deviceId Device Id
     * @return Device
     */
    public static Device newDevice(String deviceId) {
        Device device = new Device();
        device.setId(deviceId);
        return device;
    }

    /**
     * Create a new User
     *
     * @param userId User Id
     * @return User
     */
    public static User newUser(String userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }
}
