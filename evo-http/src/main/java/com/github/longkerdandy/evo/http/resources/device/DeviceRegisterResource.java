package com.github.longkerdandy.evo.http.resources.device;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.http.entity.ResponseCode;
import com.github.longkerdandy.evo.http.entity.ResponseEntity;
import com.github.longkerdandy.evo.http.entity.device.DeviceRegisterEntity;
import com.github.longkerdandy.evo.http.exception.ValidateException;
import com.github.longkerdandy.evo.http.resources.AbstractResource;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Device register related resource
 */
@Path("/api/v1.0/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceRegisterResource extends AbstractResource {

    protected DeviceRegisterResource(AerospikeStorage storage) {
        super(storage);
    }

    /**
     * Register new device
     * If the device already exist, exception will be throw
     *
     * @param d DeviceRegisterEntity
     */
    @Path("/register")
    @POST
    public void register(@HeaderParam("Accept-Language") String lang, @Valid DeviceRegisterEntity d) {
        // exist?
        if (this.storage.isDeviceExist(d.getId())) {
            throw new ValidateException(new ResponseEntity(ResponseEntity.ERROR, ResponseCode.DEVICE_ALREADY_EXIST, lang));
        }

        // save new device to storage
        Device device = EntityFactory.newDevice(d.getId());
        d.setType(d.getType());
        d.setDescId(d.getDescId());
        d.setPv(d.getPv());
        d.setToken(d.getToken());
        this.storage.updateDevice(device);
    }
}
