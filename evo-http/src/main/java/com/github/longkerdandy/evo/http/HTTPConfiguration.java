package com.github.longkerdandy.evo.http;

import com.github.longkerdandy.evo.http.mq.ProducerFactory;
import com.github.longkerdandy.evo.http.storage.AerospikeStorageFactory;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Configuration
 */
@SuppressWarnings("unused")
public class HTTPConfiguration extends Configuration {

    @Valid
    @NotNull
    private AerospikeStorageFactory aerospikeStorage;

    @Valid
    @NotNull
    private ProducerFactory kafkaProducer;

    public AerospikeStorageFactory getAerospikeStorage() {
        return aerospikeStorage;
    }

    public void setAerospikeStorage(AerospikeStorageFactory aerospikeStorage) {
        this.aerospikeStorage = aerospikeStorage;
    }

    public ProducerFactory getKafkaProducer() {
        return kafkaProducer;
    }

    public void setKafkaProducer(ProducerFactory kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }
}
