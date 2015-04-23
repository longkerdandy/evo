package com.github.longkerdandy.evo.http.storage;

import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * AerospikeStorage Factory
 */
@SuppressWarnings("unused")
public class AerospikeStorageFactory {

    @NotEmpty
    private String host;

    @Min(1)
    @Max(65535)
    private int port = 5672;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public AerospikeStorage build(Environment environment) {
        final AerospikeStorage storage = new AerospikeStorage(new ClientPolicy(), new Host[]{new Host(this.host, this.port)});
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() {
            }

            @Override
            public void stop() {
                storage.close();
            }
        });
        return storage;
    }
}
