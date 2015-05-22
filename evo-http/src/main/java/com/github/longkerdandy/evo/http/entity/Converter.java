package com.github.longkerdandy.evo.http.entity;

import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.http.entity.user.UserEntity;

/**
 * Converter
 */
public class Converter {

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
