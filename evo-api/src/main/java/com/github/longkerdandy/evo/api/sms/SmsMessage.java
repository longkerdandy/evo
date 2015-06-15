package com.github.longkerdandy.evo.api.sms;

import com.github.longkerdandy.evo.api.message.Validatable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent SMS Message, send through message queue
 */
@SuppressWarnings("unused")
public class SmsMessage<T> implements Validatable {

    public static int TYPE_VERIFY_CODE = 1;

    private String mobile;      // mobile number
    private int type;           // sms type
    private T payload;          // payload

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public void validate() {
        if (!isMobileValid(this.mobile)) {
            throw new IllegalStateException("Invalid mobile number");
        }

        if (this.payload == null) {
            throw new IllegalStateException("Invalid sms message payload");
        }

        if (this.payload instanceof Validatable) {
            ((Validatable) this.payload).validate();
        }
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
}
