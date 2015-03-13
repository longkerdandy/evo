package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.Bin;
import com.aerospike.client.Record;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;

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
                new Bin(Scheme.BIN_USER_ID, user.getId()),
                new Bin(Scheme.BIN_USER_ALIAS, user.getAlias()),
                new Bin(Scheme.BIN_USER_EMAIL, user.getEmail()),
                new Bin(Scheme.BIN_USER_MOBILE, user.getMobile()),
                new Bin(Scheme.BIN_USER_PASSWORD, user.getPassword()),
        };
    }

    /**
     * Convert aerospike record to user entity
     *
     * @param record Query Record
     * @return User
     */
    public static User recordToUser(Record record) {
        if (record == null) return null;
        User u = EntityFactory.newUser((String) record.getValue(Scheme.BIN_USER_ID));
        u.setAlias((String) record.getValue(Scheme.BIN_USER_ALIAS));
        u.setEmail((String) record.getValue(Scheme.BIN_USER_EMAIL));
        u.setMobile((String) record.getValue(Scheme.BIN_USER_MOBILE));
        return u;
    }
}
