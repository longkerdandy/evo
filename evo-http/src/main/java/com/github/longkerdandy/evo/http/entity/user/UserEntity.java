package com.github.longkerdandy.evo.http.entity.user;

import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.http.entity.ErrorCode;
import com.github.longkerdandy.evo.http.entity.ErrorEntity;
import com.github.longkerdandy.evo.http.exception.ValidateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User related entity
 */
@SuppressWarnings("unused")
public class UserEntity extends User {

    protected String verifyCode;      // verify code

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    /**
     * Is user id or mobile number valid
     */
    public void validateIdOrMobile(String lang) {
        boolean valid = false;
        if (StringUtils.isBlank(this.id) && StringUtils.isBlank(this.mobile)) {
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
        }
        if (StringUtils.isNotBlank(this.mobile))
            validateMobile(lang);
    }

    /**
     * Is user alias valid
     * Alias can have unicode characters and '-', length from 3 ~ 15
     */
    public void validateAlias(String lang) {
        boolean valid = false;
        if (StringUtils.isNotBlank(this.alias)) {
            Pattern p = Pattern.compile("^[\\w-]{3,15}$", Pattern.UNICODE_CHARACTER_CLASS);
            Matcher m = p.matcher(this.alias);
            valid = m.matches();
        }
        if (!valid) throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
    }

    /**
     * Is email address valid
     * Email address should be something like "longkerdandy@gmail.com"
     */
    public void validateEmail(String lang) {
        if (StringUtils.isBlank(this.email) || !EmailValidator.getInstance().isValid(this.email))
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
    }

    /**
     * Is mobile number valid
     * Mobile number should be something like "+86 18616862881"
     */
    public void validateMobile(String lang) {
        boolean valid = false;
        if (StringUtils.isNotBlank(this.mobile)) {
            Pattern p = Pattern.compile("^\\+\\d{1,2}[ ]\\d{10,11}$");
            Matcher m = p.matcher(this.mobile);
            valid = m.matches();
        }
        if (!valid) throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
    }

    /**
     * Is password valid
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[@#$%^&+=])  # a special character must occur at least once
     * (?=\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     */
    public void validatePassword(String lang) {
        boolean valid = false;
        if (StringUtils.isNotBlank(this.password)) {
            Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
            Matcher m = p.matcher(this.password);
            valid = m.matches();
        }
        if (!valid) throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
    }

    /**
     * Is verify code valid
     */
    public void validateVerifyCode(String lang) {
        if (StringUtils.isBlank(this.verifyCode))
            throw new ValidateException(new ErrorEntity(ErrorCode.INVALID, lang));
    }
}
