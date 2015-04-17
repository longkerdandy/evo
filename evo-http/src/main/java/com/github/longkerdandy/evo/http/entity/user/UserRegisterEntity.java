package com.github.longkerdandy.evo.http.entity.user;

import com.github.longkerdandy.evo.http.entity.device.DeviceEntity;

/**
 * User register related entity
 */
@SuppressWarnings("unused")
public class UserRegisterEntity {

    private UserEntity user;        // user
    private DeviceEntity device;    // device

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public DeviceEntity getDevice() {
        return device;
    }

    public void setDevice(DeviceEntity device) {
        this.device = device;
    }
}
