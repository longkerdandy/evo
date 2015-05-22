package com.github.longkerdandy.evo.http.resources;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.api.mq.Producer;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract Base Resource
 */
@SuppressWarnings("unused")
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
        Pattern p = Pattern.compile("^\\+\\d{1,2}[ ]\\d{10,11}$");
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
}
