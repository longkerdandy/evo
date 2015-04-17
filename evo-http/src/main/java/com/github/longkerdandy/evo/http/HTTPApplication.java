package com.github.longkerdandy.evo.http;

import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.http.resources.user.UserRegisterResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class HTTPApplication extends Application<HTTPConfiguration> {

    private static final String STORAGE_HOST = "192.168.253.68";
    private static final int STORAGE_PORT = 3000;

    public static void main(String[] args) throws Exception {
        new HTTPApplication().run("server");
    }

    @Override
    public void run(HTTPConfiguration configuration, Environment environment) throws Exception {
        // storage
        ClientPolicy policy = new ClientPolicy();
        Host[] hosts = new Host[]{
                new Host(STORAGE_HOST, STORAGE_PORT),
        };
        AerospikeStorage storage = new AerospikeStorage(policy, hosts);

        // register resources
        final UserRegisterResource userRegisterResource = new UserRegisterResource(storage);
        environment.jersey().register(userRegisterResource);

        // config jackson
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        environment.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        environment.getObjectMapper().configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
