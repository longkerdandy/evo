package com.github.longkerdandy.evo.service.sms.gateway;

/**
 * SMS Template
 */
public class Template {

    private Template() {
    }

    /**
     * Verify code template
     */
    public static String verifyCode(String verifyCode) {
        return "【及时云】您的验证码是" + verifyCode;
    }
}
