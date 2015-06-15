package com.github.longkerdandy.evo.api.sms;

import com.github.longkerdandy.evo.api.message.Validatable;
import org.apache.commons.lang3.StringUtils;

/**
 * Sms used for Verify Code
 */
@SuppressWarnings("unused")
public class VerifyCode implements Validatable {

    private String code;   // verify code

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(this.code)) {
            throw new IllegalStateException("Invalid verify code");
        }
    }
}
