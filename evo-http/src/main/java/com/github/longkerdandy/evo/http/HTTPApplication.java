package com.github.longkerdandy.evo.http;

import com.github.longkerdandy.evo.http.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class HTTPApplication extends Application<HTTPConfiguration> {

    public static void main(String[] args) throws Exception {
        new HTTPApplication().run("server");
    }

    @Override
    public void run(HTTPConfiguration configuration, Environment environment) throws Exception {
        final HelloWorldResource resource = new HelloWorldResource();
        environment.jersey().register(resource);
    }
}
