package com.github.longkerdandy.evo.api.sms;

/**
 * SMS Message Factory
 */
@SuppressWarnings("unused")
public class SmsMessageFactory {

    private SmsMessageFactory() {
    }

    /**
     * Create a new sms message based on a exist sms message and replace the payload
     *
     * @param sms     Exist SMS Message, fields will be copied
     * @param payload Payload will be replaced
     * @param <T>     Payload Message Class
     * @return SMS Message with replaced Payload
     */
    public static <T> SmsMessage<T> newMessage(SmsMessage sms, T payload) {
        SmsMessage<T> s = new SmsMessage<>();
        s.setMobile(sms.getMobile());
        s.setType(sms.getType());
        s.setPayload(payload);
        return s;
    }

    /**
     * Create a new SmsMessage<VerifyCode>
     *
     * @param mobile Mobile Number
     * @param code   Verify Code
     * @return SmsMessage<VerifyCode>
     */
    public static SmsMessage<VerifyCode> newVerifyCodeMessage(String mobile, String code) {
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode(code);
        SmsMessage<VerifyCode> sms = new SmsMessage<>();
        sms.setMobile(mobile);
        sms.setType(SmsMessage.TYPE_VERIFY_CODE);
        sms.setPayload(verifyCode);
        return sms;
    }
}
