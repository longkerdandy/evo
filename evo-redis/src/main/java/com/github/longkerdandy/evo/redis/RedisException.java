package com.github.longkerdandy.evo.redis;

/**
 * Redis related Exception
 */
@SuppressWarnings("unused")
public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }
}
