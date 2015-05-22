package com.github.longkerdandy.evo.http.resources.user;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.aerospike.util.EncryptionUtils;
import com.github.longkerdandy.evo.api.mq.Producer;
import com.github.longkerdandy.evo.api.sms.SmsMessage;
import com.github.longkerdandy.evo.api.sms.SmsVerifyCode;
import com.github.longkerdandy.evo.api.util.UuidUtils;
import com.github.longkerdandy.evo.http.entity.Converter;
import com.github.longkerdandy.evo.http.entity.ErrorCode;
import com.github.longkerdandy.evo.http.entity.ErrorEntity;
import com.github.longkerdandy.evo.http.entity.ResultEntity;
import com.github.longkerdandy.evo.http.entity.user.UserEntity;
import com.github.longkerdandy.evo.http.exception.AuthorizeException;
import com.github.longkerdandy.evo.http.exception.ValidateException;
import com.github.longkerdandy.evo.http.resources.AbstractResource;
import com.github.longkerdandy.evo.http.util.TokenUtils;
import com.google.common.base.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterResource.class);

    public UserRegisterResource(AerospikeStorage storage, Producer producer) {
        super(storage, producer);
    }

    /**
     * Check whether user exists?
     *
     * @param mobile User mobile
     * @return True if user exists
     */
    @Path("/exist")
    @GET
    public ResultEntity<Boolean> exist(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @QueryParam("mobile") Optional<String> mobile) {
        logger.debug("Process exist request with params: mobile {}", mobile.get());

        // validate
        if (!mobile.isPresent()) {
            throw new ValidateException(new ErrorEntity(ErrorCode.MISSING_FIELD, lang));
        }

        // validate mobile format
        if (!isMobileValid(mobile.get())) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
        }
        // is mobile exist in storage
        if (!this.storage.isUserMobileExist(mobile.get())) {
            return new ResultEntity<>(false);
        }

        return new ResultEntity<>(true);
    }

    /**
     * Require verify code
     * Create a new verify code in storage, and send to user's mobile
     *
     * @param mobile User mobile
     * @return Verify code id
     */
    @Path("/verify")
    @POST
    public ResultEntity<String> verify(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @QueryParam("mobile") Optional<String> mobile) {
        logger.debug("Process verify request with params: mobile {}", mobile.get());

        // validate
        if (!mobile.isPresent()) {
            throw new ValidateException(new ErrorEntity(ErrorCode.MISSING_FIELD, lang));
        }

        // validate mobile format
        if (!isMobileValid(mobile.get())) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
        }
        // is mobile exist in storage
        if (this.storage.isUserMobileExist(mobile.get())) {
            throw new ValidateException(new ErrorEntity(ErrorCode.ALREADY_EXISTS, lang));
        }

        // create mobile verify code
        String code = RandomStringUtils.randomNumeric(MOBILE_VERIFY_CODE_LENGTH);
        this.storage.updateVerify(mobile.get(), code, MOBILE_VERIFY_CODE_TTL);
        logger.trace("Created a verify code for mobile {}", mobile.get());

        // send to message queue
        this.producer.sendSmsMessage(new SmsMessage<>(mobile.get(), SmsMessage.TYPE_VERIFY_CODE, new SmsVerifyCode(code)));

        return new ResultEntity<>("successful");
    }

    /**
     * New user sign up
     *
     * @param userEntity New user information
     * @return Token
     */
    @Path("/signup")
    @POST
    public ResultEntity<String> signUp(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @Valid UserEntity userEntity) {
        if (userEntity == null) {
            throw new ValidateException(new ErrorEntity(ErrorCode.MISSING_FIELD, lang));
        }

        logger.debug("Process signUp request with params: alias {} mobile {}", userEntity.getAlias(), userEntity.getMobile());

        // validate
        userEntity.validateAlias(lang);
        userEntity.validatePassword(lang);
        userEntity.validateVerifyCode(lang);
        userEntity.validateMobile(lang);

        // is mobile exist in storage
        if (this.storage.isUserMobileExist(userEntity.getMobile())) {
            throw new ValidateException(new ErrorEntity(ErrorCode.ALREADY_EXISTS, lang));
        }
        // is mobile verify code correct
        if (!this.storage.isVerifyCodeCorrect(userEntity.getMobile(), userEntity.getVerifyCode())) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INCORRECT, lang));
        }

        // create new user
        User u = Converter.toUser(userEntity);
        u.setId(UuidUtils.shortUuid()); // generate random user id
        u.setPassword(EncryptionUtils.encryptPassword(u.getPassword())); // encode password
        logger.trace("Created a new user {} {}", u.getId(), u.getAlias());

        // create user token
        String t = TokenUtils.newToken(u.getId());
        this.storage.updateUserToken(u.getId(), t);
        logger.trace("Create token for user {}", u.getId());

        return new ResultEntity<>(t);
    }

    /**
     * User sign in
     *
     * @param userEntity User information
     * @return Token
     */
    @Path("/signin")
    @POST
    public ResultEntity<String> signIn(@HeaderParam("Accept-Language") @DefaultValue("zh") String lang,
                                       @Valid UserEntity userEntity) {
        if (userEntity == null) {
            throw new ValidateException(new ErrorEntity(ErrorCode.MISSING_FIELD, lang));
        }

        logger.trace("Process signIn request with params: id {} mobile {}", userEntity.getId(), userEntity.getMobile());

        // validate
        userEntity.validateIdOrMobile(lang);
        userEntity.validatePassword(lang);

        User u;
        if (StringUtils.isBlank(userEntity.getId())) {
            // get user record by mobile
            u = this.storage.getUserByMobile(userEntity.getMobile());
            if (u == null) throw new AuthorizeException(new ErrorEntity(ErrorCode.UNAUTHORIZED, lang));
        } else {
            u = Converter.toUser(userEntity);
        }

        // is user password correct
        if (this.storage.isUserPasswordCorrect(u.getId(), EncryptionUtils.encryptPassword(u.getPassword()))) {
            throw new AuthorizeException(new ErrorEntity(ErrorCode.UNAUTHORIZED, lang));
        }

        // update token
        String t = TokenUtils.newToken(u.getId());
        this.storage.updateUserToken(u.getId(), t);
        logger.trace("Update token for user {}", u.getId());

        return new ResultEntity<>(t);
    }
}
