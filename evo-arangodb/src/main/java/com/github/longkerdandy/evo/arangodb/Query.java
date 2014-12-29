package com.github.longkerdandy.evo.arangodb;

/**
 * Arango AQL
 */
public class Query {

    public static final String GET_DEVICE_FOLLOWED_USER_ID = "FOR ud IN user_follow_device FILTER ud._to == @to && ud.permission >= @min && ud.permission <= @max RETURN ud._from";
    public static final String GET_USER_FOLLOWING_DEVICE_ID = "FOR ud IN user_follow_device FILTER ud._from == @from && ud.permission >= @min && ud.permission <= @max RETURN ud._to";
    public static final String GET_DEVICE_REGISTER_USER_ID = "FOR du IN device_register_user FILTER du._from == @from LIMIT 1 RETURN du._to";
}
