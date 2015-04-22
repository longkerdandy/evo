package com.github.longkerdandy.evo.api.sms;

/**
 * Sms used for Verify Code
 */
@SuppressWarnings("unused")
public class SmsVerifyCode {

    private String code;   // verify code

    protected SmsVerifyCode() {
    }

    public SmsVerifyCode(String code) {
        this.code = code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
