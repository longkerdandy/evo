package com.github.longkerdandy.evo.http.resources;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.api.mq.Producer;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract Base Resource
 */
public abstract class AbstractResource {

    protected final AerospikeStorage storage;
    protected final Producer producer;

    protected AbstractResource(AerospikeStorage storage, Producer producer) {
        this.storage = storage;
        this.producer = producer;
    }

    /**
     * Is given mobile number valid
     * Mobile number should be something like "+86 18616862881"
     *
     * @param mobile Mobile Number
     * @return True if valid
     */
    protected boolean isMobileValid(String mobile) {
        if (mobile == null) return false;
        Pattern p = Pattern.compile("^\\+\\d{1,2}[- ]\\d{10,11}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * Is given email address valid
     * Email address should be something like "longkerdandy@gmail.com"
     *
     * @param email Email Address
     * @return True if valid
     */
    protected boolean isEmailValid(String email) {
        return email != null && EmailValidator.getInstance().isValid(email);
    }

    /**
     * Is given user alias valid
     * Alias can have unicode characters and '-', length from 3 ~ 15
     *
     * @param alias User Alias
     * @return True if valid
     */
    protected boolean isAliasValid(String alias) {
        if (alias == null) return false;
        Pattern p = Pattern.compile("^[\\w-]{3,15}$", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = p.matcher(alias);
        return m.matches();
    }

    /**
     * Is given password valid
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[@#$%^&+=])  # a special character must occur at least once
     * (?=\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     *
     * @param password Password
     * @return True if valid
     */
    protected boolean isPasswordValid(String password) {
        if (password == null) return false;
        Pattern p = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
