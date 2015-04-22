package com.github.longkerdandy.evo.http.storage;

import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import io.dropwizard.lifecycle.Managed;

/**
 * Dropwizard Managed interface wrapper of AerospikeStorage
 */
public class AerospikeStorageManager implements Managed {

    private ClientPolicy policy;
    private Host[] hosts;
    private AerospikeStorage storage;

    public AerospikeStorageManager(String host, int port) {
        this.policy = new ClientPolicy();
        this.hosts = new Host[]{
                new Host(host, port),
        };
    }

    public AerospikeStorage getStorage() {
        return storage;
    }

    @Override
    public void start() throws Exception {
        this.storage = new AerospikeStorage(this.policy, this.hosts);
    }

    @Override
    public void stop() throws Exception {
        if (this.storage != null) this.storage.close();
    }
}
