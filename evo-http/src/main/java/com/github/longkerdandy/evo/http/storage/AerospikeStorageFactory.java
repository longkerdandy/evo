package com.github.longkerdandy.evo.http.storage;

import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * AerospikeStorage Factory
 */
@SuppressWarnings("unused")
public class AerospikeStorageFactory {

    @NotEmpty
    private String hosts;

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public AerospikeStorage build(Environment environment) {
        // create aerospike storage
        ClientPolicy policy = new ClientPolicy();
        List<Host> hostList = new ArrayList<>();
        for (String h : this.hosts.split(",")) {
            hostList.add(new Host(h.split(":")[0], Integer.valueOf(h.split(":")[1])));
        }
        AerospikeStorage storage = new AerospikeStorage(policy, hostList.toArray(new Host[hostList.size()]));
        // add to dropwizard life cycle
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
