package com.github.longkerdandy.evo.http.resources.device;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.api.message.Action;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.message.MessageFactory;
import com.github.longkerdandy.evo.api.mq.Producer;
import com.github.longkerdandy.evo.api.mq.Topics;
import com.github.longkerdandy.evo.api.protocol.Evolution;
import com.github.longkerdandy.evo.api.protocol.Permission;
import com.github.longkerdandy.evo.api.protocol.ProtocolType;
import com.github.longkerdandy.evo.http.entity.ErrorCode;
import com.github.longkerdandy.evo.http.entity.ErrorEntity;
import com.github.longkerdandy.evo.http.entity.ResultEntity;
import com.github.longkerdandy.evo.http.entity.message.ActionEntity;
import com.github.longkerdandy.evo.http.exception.AuthorizeException;
import com.github.longkerdandy.evo.http.resources.AbstractResource;
import com.google.common.base.Optional;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Control device related resource
 */
@Path("/devices/control")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceControlResource extends AbstractResource {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(DeviceControlResource.class);

    public DeviceControlResource(AerospikeStorage storage, Producer producer) {
        super(storage, producer);
    }

    @Path("/action")
    @POST
    public ResultEntity<String> action(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @Auth String userId, @QueryParam("from") Optional<String> from, @QueryParam("to") String to, @QueryParam("deviceType") int deviceType,
                                       @Valid ActionEntity actionEntity) {
        logger.debug("Process action request with params: userId {} deviceId {} actionId {}", userId, to, actionEntity.getActionId());

        // validate
        actionEntity.validateActionId(lang);

        // check permission
        if (!this.storage.isUserOwnDevice(userId, to, Permission.READ_WRITE)) {
            throw new AuthorizeException(new ErrorEntity(ErrorCode.FORBIDDEN, lang));
        }

        // check device connection
        String node = this.storage.getDeviceById(to).getConnected();

        // not connected, cache the message
        if (StringUtils.isBlank(node)) {
            return new ResultEntity<>("cached");
        }

        // connected, forge the message and push to message queue
        Message<Action> msg = MessageFactory.newActionMessage(ProtocolType.TCP_1_0, deviceType, from.or(Evolution.ID), to, userId,
                actionEntity.getActionId(), actionEntity.getLifetime(), actionEntity.getAttributes());
        this.producer.sendMessage(Topics.TCP_OUT(node), msg);

        return new ResultEntity<>("successful");
    }
}
