package com.github.longkerdandy.evo.http.resources.user;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.http.entity.ErrorCode;
import com.github.longkerdandy.evo.http.entity.ErrorEntity;
import com.github.longkerdandy.evo.http.entity.ResultEntity;
import com.github.longkerdandy.evo.http.exception.ValidateException;
import com.github.longkerdandy.evo.http.resources.AbstractResource;
import com.google.common.base.Optional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public ResultEntity<Boolean> exist(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @QueryParam("mobile") Optional<String> mobile,
                                       @QueryParam("email") Optional<String> email) {
        // validate
        if (!mobile.isPresent() && !email.isPresent()) {
            throw new ValidateException(new ErrorEntity(ErrorCode.MISSING_FIELD, lang));
        }

        if (mobile.isPresent()) {
            // validate mobile format
            if (!isMobileValid(mobile.get())) {
                throw new ValidateException(new ErrorEntity(ErrorCode.VALIDATION_FAILED, lang));
            }
            // is mobile exist in storage
            if (!this.storage.isUserMobileExist(mobile.get())) {
                return new ResultEntity<>(false);
            }
        }

        if (email.isPresent()) {
            // validate email format
            if (!isEmailValid(email.get())) {
                throw new ValidateException(new ErrorEntity(ErrorCode.VALIDATION_FAILED, lang));
            }
            // is email exist in storage
            if (!this.storage.isUserEmailExist(email.get())) {
                return new ResultEntity<>(false);
            }
        }

        return new ResultEntity<>(true);
    }
}
