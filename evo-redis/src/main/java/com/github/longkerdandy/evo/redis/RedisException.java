package com.github.longkerdandy.evo.redis;

/**
 * Redis related Exception
 */
@SuppressWarnings("unused")
public class RedisException extends RuntimeException {

    public RedisException(final String message) {
        super(message);
    }

    public RedisException(final Throwable cause) {
        super(cause);
    }

    public RedisException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
