package com.github.longkerdandy.evo.http.mq;

import com.github.longkerdandy.evo.api.mq.Producer;
import io.dropwizard.lifecycle.Managed;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Dropwizard Managed interface wrapper of Producer
 */
public class ProducerManager implements Managed {

    private Map<String, Object> configs;
    private Producer producer;

    public ProducerManager(String host) {
        this.configs = new HashMap<>();
        this.configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        this.configs.put(ProducerConfig.ACKS_CONFIG, "1");
        this.configs.put(ProducerConfig.BLOCK_ON_BUFFER_FULL_CONFIG, "false");
    }

    public Producer getProducer() {
        return this.producer;
    }

    @Override
    public void start() throws Exception {
        this.producer = new Producer(this.configs);
    }

    @Override
    public void stop() throws Exception {
        if (this.producer != null) this.producer.close();
    }
}
