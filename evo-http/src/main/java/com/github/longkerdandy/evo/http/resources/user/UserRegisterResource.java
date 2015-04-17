package com.github.longkerdandy.evo.http.resources.user;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.http.entity.ResponseEntity;
import com.github.longkerdandy.evo.http.resources.AbstractResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User register related resource
 */
@Path("/users/register")
@Produces(MediaType.APPLICATION_JSON)
public class UserRegisterResource extends AbstractResource {

    public UserRegisterResource(AerospikeStorage aerospikeStorage) {
        super(aerospikeStorage);
    }

    @Path("/exist")
    @GET
    public ResponseEntity exist(@QueryParam("mobile") String mobile) {
        // validate mobile format

        // is mobile exist in storage
        boolean b = this.storage.isUserMobileExist(mobile);
        ResponseEntity<Boolean> e = new ResponseEntity<>(ResponseEntity.SUCCESS, b);
        return e;
    }
}
