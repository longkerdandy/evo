package com.github.longkerdandy.evo.http.resources.user;

import com.github.longkerdandy.evo.http.entity.Response;
import com.github.longkerdandy.evo.http.entity.user.RegisterEntity;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * User register related resource
 */
@Path("/api/v1.0/{api_key}/user")
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {

    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(@PathParam("api_key") String userId, @Valid RegisterEntity r) {

    }
}
