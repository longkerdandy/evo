package com.github.longkerdandy.evo.http.resources;

import com.codahale.metrics.health.HealthCheck;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;

/**
 * Aerospike Health Check
 */
public class StorageHealthCheck extends HealthCheck {

    private final AerospikeStorage storage;

    public StorageHealthCheck(AerospikeStorage storage) {
        this.storage = storage;
    }

    @Override
    protected Result check() throws Exception {
        if (this.storage.isConnected()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Cannot connect to aerospike storage");
        }
    }
}
