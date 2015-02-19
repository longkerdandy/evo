package com.github.longkerdandy.evo.arangodb.entity;

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
     * Create a new Device
     *
     * @param deviceId   Device Id
     * @param deviceType Device Type
     * @param descId     Description File Id
     * @param pv         Protocol Version
     * @param pt         Protocol Type
     * @return Device
     */
    public static Device newDevice(String deviceId, int deviceType, String descId, int pv, int pt) {
        Device device = new Device();
        device.setId(deviceId);
        device.setType(deviceType);
        device.setDescId(descId);
        device.setPv(pv);
        device.setPt(pt);
        return device;
    }
}
