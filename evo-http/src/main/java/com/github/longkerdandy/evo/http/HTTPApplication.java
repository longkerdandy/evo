package com.github.longkerdandy.evo.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.longkerdandy.evo.http.mq.ProducerManager;
import com.github.longkerdandy.evo.http.resources.user.UserRegisterResource;
import com.github.longkerdandy.evo.http.storage.AerospikeStorageManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class HTTPApplication extends Application<HTTPConfiguration> {

    private static final String STORAGE_HOST = "192.168.253.68";
    private static final int STORAGE_PORT = 3000;
    private static final String MQ_HOST = "192.168.253.68:9092";

    public static void main(String[] args) throws Exception {
        new HTTPApplication().run("server");
    }

    @Override
    public void run(HTTPConfiguration configuration, Environment environment) throws Exception {
        // storage
        AerospikeStorageManager storageManager = new AerospikeStorageManager(STORAGE_HOST, STORAGE_PORT);
        environment.lifecycle().manage(storageManager);

        // mq
        ProducerManager producerManager = new ProducerManager(MQ_HOST);
        environment.lifecycle().manage(producerManager);

        // register resources
        environment.jersey().register(new UserRegisterResource(storageManager, producerManager));

        // config jackson
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        environment.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        environment.getObjectMapper().configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
