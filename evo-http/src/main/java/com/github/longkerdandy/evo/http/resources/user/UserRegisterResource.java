package com.github.longkerdandy.evo.http.resources.user;

import com.github.longkerdandy.evo.api.sms.SmsMessage;
import com.github.longkerdandy.evo.api.sms.SmsVerifyCode;
import com.github.longkerdandy.evo.http.entity.ErrorCode;
import com.github.longkerdandy.evo.http.entity.ErrorEntity;
import com.github.longkerdandy.evo.http.entity.ResultEntity;
import com.github.longkerdandy.evo.http.entity.user.UserRegisterEntity;
import com.github.longkerdandy.evo.http.exception.ValidateException;
import com.github.longkerdandy.evo.http.mq.ProducerManager;
import com.github.longkerdandy.evo.http.resources.AbstractResource;
import com.github.longkerdandy.evo.http.storage.AerospikeStorageManager;
import com.google.common.base.Optional;
import org.apache.commons.lang3.RandomStringUtils;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * User register related resource
 */
@Path("/users/register")
@Produces(MediaType.APPLICATION_JSON)
public class UserRegisterResource extends AbstractResource {

    protected static final int MOBILE_VERIFY_CODE_LENGTH = 6;
    protected static final int MOBILE_VERIFY_CODE_TTL = 180;    // seconds

    public UserRegisterResource(AerospikeStorageManager storageManager, ProducerManager producerManager) {
        super(storageManager, producerManager);
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
                throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
            }
            // is mobile exist in storage
            if (!storage().isUserMobileExist(mobile.get())) {
                return new ResultEntity<>(false);
            }
        }

        if (email.isPresent()) {
            // validate email format
            if (!isEmailValid(email.get())) {
                throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
            }
            // is email exist in storage
            if (!storage().isUserEmailExist(email.get())) {
                return new ResultEntity<>(false);
            }
        }

        return new ResultEntity<>(true);
    }

    @Path("/verify")
    @POST
    public ResultEntity<String> verify(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @QueryParam("type") @DefaultValue("mobile") String type,
                                       @Valid UserRegisterEntity r) {
        // validate
        if (r == null || r.getUser() == null) {
            throw new ValidateException(new ErrorEntity(ErrorCode.MISSING_FIELD, lang));
        }
        if (!type.equalsIgnoreCase("mobile") && !type.equalsIgnoreCase("email")) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
        }

        // validate alias format
        if (!isAliasValid(r.getUser().getAlias())) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
        }
        // validate password format
        if (!isPasswordValid(r.getUser().getPassword())) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
        }

        if (type.equalsIgnoreCase("mobile")) {
            // validate mobile format
            if (!isMobileValid(r.getUser().getMobile())) {
                throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
            }
            // is mobile exist in storage
            if (storage().isUserMobileExist(r.getUser().getMobile())) {
                throw new ValidateException(new ErrorEntity(ErrorCode.ALREADY_EXISTS, lang));
            }
            // create mobile verify code
            String code = RandomStringUtils.randomNumeric(MOBILE_VERIFY_CODE_LENGTH);
            storage().updateVerify(r.getUser().getMobile(), code, MOBILE_VERIFY_CODE_TTL);
            // send to mq
            producer().sendSmsMessage(new SmsMessage<>(r.getUser().getMobile(), SmsMessage.TYPE_VERIFY_CODE, new SmsVerifyCode(code)));

            return new ResultEntity<>("successful");
        } else {
            throw new ValidateException(new ErrorEntity(ErrorCode.UNSUPPORTED, lang));
        }
    }
}
