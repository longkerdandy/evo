package com.github.longkerdandy.evo.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Set;

/**
 * Redis Database Access Layer
 */
@SuppressWarnings("unused")
public class RedisStorage {

    private static final Logger logger = LoggerFactory.getLogger(RedisStorage.class);
    // Redis config
    private final String host;
    private final int port;
    private final String password;
    // Jedis Connection Pool
    private JedisPool jedisPool;

    public RedisStorage(final String host, final int port, final String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    /**
     * Init RedisStorage
     */
    public void init() {
        // init jedis pool
        this.jedisPool = new JedisPool(new JedisPoolConfig(), this.host, this.port);
        // auth
        if (StringUtils.isNotEmpty(this.password)) {
            try (final Jedis jedis = this.jedisPool.getResource()) {
                // FIXME: make sure jedis.auth 's return value
                if (StringUtils.isEmpty(jedis.auth(this.password))) {
                    throw new RedisException("Redis authentication failed, password incorrect.");
                }
            } catch (JedisException e) {
                throw new RedisException(e);
            }
        }
        logger.info("RedisStorage init.");
    }

    /**
     * Destroy RedisStorage
     */
    public void destroy() {
        if (this.jedisPool != null) {
            this.jedisPool.destroy();
        }
        logger.info("RedisStorage destroy.");
    }

    /**
     * Set user's token bind with device.
     *
     * @param user   User ID
     * @param device Device ID
     * @param token  Token
     */
    public void setUserToken(final String user, final String device, final String token) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            // TODO: jedis should provide a way to SET with NX
            jedis.set(RedisKeys.userTokenKey(user, device), token);
            jedis.expire(RedisKeys.userTokenKey(user, device), 31536000);
        } catch (JedisException e) {
            throw new RedisException(e);
        }
    }

    /**
     * Is user's token correct ? (bind with device)
     *
     * @param user   User ID
     * @param device Device ID
     * @param token  Token
     * @return True if token is correct
     */
    public boolean isUserTokenCorrect(final String user, final String device, final String token) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            return token.equals(jedis.get(RedisKeys.userTokenKey(user, device)));
        } catch (JedisException e) {
            throw new RedisException(e);
        }
    }

    /**
     * Set user follow device with privilege
     *
     * @param user      User ID
     * @param device    Device ID
     * @param privilege Privilege Level
     */
    public void setUserFollowDevice(final String user, final String device, final int privilege) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            final String key1 = RedisKeys.userFollowingDevicesKey(user);
            final String key2 = RedisKeys.deviceFollowerUsersKey(device);
            jedis.eval(RedisLuaScript.SET_USER_FOLLOW_DEVICE, 2, key1, key2, user, device, String.valueOf(privilege));
        } catch (JedisException e) {
            throw new RedisException(e);
        }
    }

    /**
     * Set user un-follow device
     *
     * @param user   User ID
     * @param device Device ID
     */
    public void setUserUnFollowDevice(final String user, final String device) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            final String key1 = RedisKeys.userFollowingDevicesKey(user);
            final String key2 = RedisKeys.deviceFollowerUsersKey(device);
            jedis.eval(RedisLuaScript.SET_USER_UNFOLLOW_DEVICE, 2, key1, key2, user, device);
        } catch (JedisException e) {
            throw new RedisException(e);
        }
    }

    /**
     * Is user follow device with at least privilege
     *
     * @param user      User ID
     * @param device    Device ID
     * @param privilege Privilege Level
     */
    public boolean isUserFollowDevice(final String user, final String device, final int privilege) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            final String key = RedisKeys.deviceFollowerUsersKey(device);
            return (boolean) jedis.eval(RedisLuaScript.IS_USER_FOLLOW_DEVICE, 1, key, user, String.valueOf(privilege));
        } catch (JedisException e) {
            throw new RedisException(e);
        }
    }

    /**
     * Get user following devices within privilege range
     *
     * @param user         User ID
     * @param privilegeMin Min Privilege Level
     * @param privilegeMax Max Privilege Level
     * @return Set of Device ID
     */
    public Set<String> getUserFollowingDevices(final String user, final int privilegeMin, final int privilegeMax) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            final String key = RedisKeys.userFollowingDevicesKey(user);
            return jedis.zrangeByScore(key, privilegeMin, privilegeMax);
        } catch (JedisException e) {
            throw new RedisException(e);
        }
    }

    /**
     * Get device follower users within privilege range
     *
     * @param device       Device ID
     * @param privilegeMin Min Privilege Level
     * @param privilegeMax Max Privilege Level
     * @return Set of User ID
     */
    public Set<String> getDeviceFollowerUsers(final String device, final int privilegeMin, final int privilegeMax) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            final String key = RedisKeys.deviceFollowerUsersKey(device);
            return jedis.zrangeByScore(key, privilegeMin, privilegeMax);
        } catch (JedisException e) {
            throw new RedisException(e);
        }
    }
}
