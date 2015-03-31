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
    public static String forgeVerifyCode(int verifyCode) {
        // TODO: use production ready template
        return "【正泰网络】您的验证码是" + verifyCode;
    }
}
