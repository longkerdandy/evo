package com.github.longkerdandy.evo.http.exception;

import com.github.longkerdandy.evo.http.entity.ResponseEntity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Validate Exception
 */
@SuppressWarnings("unused")
public class ValidateException extends WebApplicationException {

    /**
     * Create a HTTP 400 (Bad Request) exception.
     */
    public ValidateException() {
        super(400);
    }

    /**
     * Create a HTTP 400 (Bad Request) exception.
     *
     * @param entity the error response entity
     */
    public ValidateException(ResponseEntity entity) {
        super(Response.status(400).entity(entity).type("application/json").build());
    }
}
