package com.github.longkerdandy.evo.http.resources;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;

/**
 * Abstract Base Resource
 */
public abstract class AbstractResource {

    protected final AerospikeStorage storage;

    protected AbstractResource(AerospikeStorage storage) {
        this.storage = storage;
    }

    protected String getAppKey(String appId) {
        return null;
    }

    protected void validateParam(String uri, String appId, String appKey) {

    }
}
