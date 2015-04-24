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

    public static Device toDevice(DeviceEntity deviceEntity) {
        Device device = EntityFactory.newDevice(deviceEntity.getId());
        device.setType(deviceEntity.getType());
        device.setDescId(deviceEntity.getDescId());
        device.setProtocol(deviceEntity.getProtocol());
        device.setToken(deviceEntity.getToken());
        return device;
    }

    public static User toUser(UserEntity userEntity) {
        User user = EntityFactory.newUser(userEntity.getId());
        user.setAlias(userEntity.getAlias());
        user.setMobile(userEntity.getMobile());
        user.setPassword(userEntity.getPassword());
        return user;
    }
}
