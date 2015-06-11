package com.github.longkerdandy.evo.api.message;

/**
 * Can be validated
 */
public interface Validatable {

    /**
     * Validate instance
     * Throw IllegalStateException if validation failed
     */
    void validate();
}
