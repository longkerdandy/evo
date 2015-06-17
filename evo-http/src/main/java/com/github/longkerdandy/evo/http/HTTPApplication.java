package com.github.longkerdandy.evo.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.api.mq.Producer;
import com.github.longkerdandy.evo.http.auth.OAuthAuthenticator;
import com.github.longkerdandy.evo.http.resources.StorageHealthCheck;
import com.github.longkerdandy.evo.http.resources.device.DeviceControlResource;
import com.github.longkerdandy.evo.http.resources.user.UserOwnershipResource;
import com.github.longkerdandy.evo.http.resources.user.UserRegisterResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.oauth.OAuthFactory;
import io.dropwizard.setup.Environment;

public class HTTPApplication extends Application<HTTPConfiguration> {

    public static void main(String[] args) throws Exception {
        new HTTPApplication().run(args);
    }

    @Override
    public void run(HTTPConfiguration configuration, Environment environment) throws Exception {
        // create storage
        AerospikeStorage storage = configuration.getAerospikeStorage().build(environment);

        // create message queue producer
        Producer producer = configuration.getKafkaProducer().build(environment);

        // register OAuth2
        environment.jersey().register(AuthFactory.binder(new OAuthFactory<>(new OAuthAuthenticator(storage), "https://github.com/longkerdandy", String.class)));

        // register resources
        environment.jersey().register(new UserRegisterResource(storage, producer));
        environment.jersey().register(new UserOwnershipResource(storage, producer));
        environment.jersey().register(new DeviceControlResource(storage, producer));

        // register health check
        environment.healthChecks().register("storage", new StorageHealthCheck(storage));

        // config jackson
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        environment.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        environment.getObjectMapper().configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
