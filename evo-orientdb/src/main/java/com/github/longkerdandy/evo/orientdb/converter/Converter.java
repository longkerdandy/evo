package com.github.longkerdandy.evo.orientdb.converter;

import com.github.longkerdandy.evo.api.entity.Device;
import com.github.longkerdandy.evo.api.entity.Entity;
import com.github.longkerdandy.evo.api.entity.User;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import static com.github.longkerdandy.evo.orientdb.scheme.Scheme.*;

/**
 * Entity Converter
 */
@SuppressWarnings("unused")
public class Converter {

    public static Entity<User> toUser(OrientVertex uv) {
        if (uv == null) return null;
        User user = new User();
        user.setAlias(uv.getProperty(USER_ALIAS));
        user.setEmail(uv.getProperty(USER_EMAIL));
        user.setMobile(uv.getProperty(USER_MOBILE));
        // user.setPassword(uv.getProperty(USER_PASSWORD)); DO NOT RETURN PASSWORD
        return new Entity<>(String.valueOf(uv.getId()), user);
    }

    public static Entity<Device> toDevice(OrientVertex dv) {
        if (dv == null) return null;
        Device device = new Device();
        device.setSn(dv.getProperty(DEVICE_SN));
        device.setAttributes(dv.getProperty(DEVICE_ATTRIBUTES));
        device.setUpdateTime(dv.getProperty(DEVICE_UPDATE_TIME));
        return new Entity<>(String.valueOf(dv.getId()), device);
    }
}
