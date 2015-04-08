package com.github.longkerdandy.evo.http.resources.user;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.api.util.UuidUtils;
import com.github.longkerdandy.evo.http.entity.HttpConst;
import com.github.longkerdandy.evo.http.entity.user.UserRegisterEntity;
import com.github.longkerdandy.evo.http.resources.AbstractResource;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * User register related resource
 */
@Path("/api/v1.0/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserRegisterResource extends AbstractResource {

    public UserRegisterResource(AerospikeStorage aerospikeStorage) {
        super(aerospikeStorage);
    }

    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(@QueryParam("appId") String appId, @QueryParam("date") String date, @QueryParam("deviceId") String deviceId, @Valid UserRegisterEntity r) {
        // validate parameters
        String uri = HttpConst.BASE_HTTP_URI + "?appId=" + appId + "&date=" + date + "&deviceId=" + deviceId;
        validateParam(uri, appId, getAppKey(appId));

        // save new user to storage
        User user = EntityFactory.newUser(UuidUtils.shortUuid());
        user.setAlias(r.getAlias());
        user.setMobile(r.getMobile());
        user.setPassword(r.getPassword());
        this.storage.updateUser(user);
    }
}
