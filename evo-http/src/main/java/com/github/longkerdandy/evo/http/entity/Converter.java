package com.github.longkerdandy.evo.http.entity;

import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.http.entity.device.DeviceEntity;
import com.github.longkerdandy.evo.http.entity.user.UserEntity;

/**
 * Converter
 */
public class Converter {

    /**
     * Http Device Entity to Storage Device
     *
     * @param deviceEntity Http Device Entity
     * @return Storage Device
     */
    public static Device toDevice(DeviceEntity deviceEntity) {
        Device device = EntityFactory.newDevice(deviceEntity.getId());
        device.setType(deviceEntity.getType());
        device.setDescId(deviceEntity.getDescId());
        device.setProtocol(deviceEntity.getProtocol());
        device.setToken(deviceEntity.getToken());
        return device;
    }

    /**
     * Http User Entity to Storage User
     *
     * @param userEntity Http User Entity
     * @return Storage User
     */
    public static User toUser(UserEntity userEntity) {
        User user = EntityFactory.newUser(userEntity.getId());
        user.setAlias(userEntity.getAlias());
        user.setMobile(userEntity.getMobile());
        user.setPassword(userEntity.getPassword());
        return user;
    }
}
