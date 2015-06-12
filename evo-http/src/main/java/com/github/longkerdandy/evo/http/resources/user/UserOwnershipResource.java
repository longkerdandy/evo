package com.github.longkerdandy.evo.http.resources.user;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.api.mq.Producer;
import com.github.longkerdandy.evo.api.protocol.Permission;
import com.github.longkerdandy.evo.http.entity.ResultEntity;
import com.github.longkerdandy.evo.http.resources.AbstractResource;
import com.google.common.base.Optional;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * User ownership related resource
 */
@Path("/users/ownership")
@Produces(MediaType.APPLICATION_JSON)
public class UserOwnershipResource extends AbstractResource {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(UserOwnershipResource.class);

    public UserOwnershipResource(AerospikeStorage storage, Producer producer) {
        super(storage, producer);
    }

    /**
     * Check whether user has own device?
     *
     * @param userId     User Id
     * @param deviceId   Device Id
     * @param permission Minimal ownership permission
     * @return True if ownership exist
     */
    @Path("/exist")
    @GET
    public ResultEntity<Boolean> exist(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @Auth String userId, @QueryParam("deviceId") String deviceId, @QueryParam("permission") int permission) {
        logger.debug("Process exist request with params: userId {} deviceId {} permission {}", userId, deviceId, permission);

        // check ownership
        boolean b = this.storage.isUserOwnDevice(userId, deviceId, permission);

        return new ResultEntity<>(b);
    }

    /**
     * User require ownership for device
     *
     * @param userId     User Id
     * @param deviceId   Device Id
     * @param permission Optional ownership permission (default to OWNER)
     * @return Result
     */
    @Path("/require")
    @POST
    public ResultEntity<String> require(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                        @Auth String userId, @QueryParam("deviceId") String deviceId, @QueryParam("permission") Optional<Integer> permission) {
        logger.debug("Process require request with params: userId {} deviceId {} permission {}", userId, deviceId, permission.or(Permission.OWNER));

        // already has permission?
        if (this.storage.isUserOwnDevice(userId, deviceId, permission.or(Permission.OWNER))) {
            return new ResultEntity<>("successful");
        }

        // device has owner?
        List<Map<String, Object>> o = this.storage.getDeviceOwner(deviceId, Permission.OWNER);
        if (o != null && o.size() > 0) {
            return new ResultEntity<>("failed");
        } else {
            // update as device owner
            this.storage.addUserOwnDevice(userId, deviceId, Permission.OWNER);

            return new ResultEntity<>("successful");
        }
    }

    /**
     * User release ownership for device
     *
     * @param userId   User Id
     * @param deviceId Device Id
     */
    @Path("/release")
    @POST
    public ResultEntity<String> release(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                        @Auth String userId, @QueryParam("deviceId") String deviceId) {
        logger.debug("Process release request with params: userId {} deviceId {}", userId, deviceId);

        // delete ownership
        this.storage.removeUserOwnDevice(userId, deviceId);

        return new ResultEntity<>("successful");
    }
}
