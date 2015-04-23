package com.github.longkerdandy.evo.http.mq;

import com.github.longkerdandy.evo.api.mq.Producer;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer Factory
 */
@SuppressWarnings("unused")
public class ProducerFactory {

    @NotEmpty
    private String hosts;

    @NotEmpty
    private String acks;

    @NotEmpty
    private String blockOnBufferFull;

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getBlockOnBufferFull() {
        return blockOnBufferFull;
    }

    public void setBlockOnBufferFull(String blockOnBufferFull) {
        this.blockOnBufferFull = blockOnBufferFull;
    }

    public Producer build(Environment environment) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.hosts);
        configs.put(ProducerConfig.ACKS_CONFIG, this.acks);
        configs.put(ProducerConfig.BLOCK_ON_BUFFER_FULL_CONFIG, this.blockOnBufferFull);
        final Producer producer = new Producer(configs);
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() {
            }

            @Override
            public void stop() {
                producer.close();
            }
        });
        return producer;
    }
}
