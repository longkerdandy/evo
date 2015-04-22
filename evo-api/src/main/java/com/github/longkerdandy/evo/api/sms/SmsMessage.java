package com.github.longkerdandy.evo.api.sms;

/**
 * Represent SMS Message, send through mq
 */
@SuppressWarnings("unused")
public class SmsMessage<T> {

    public static int TYPE_VERIFY_CODE = 1;

    private String mobile;      // mobile number
    private int type;           // sms type
    private T payload;          // payload

    protected SmsMessage() {
    }

    public SmsMessage(String mobile, int type, T payload) {
        this.mobile = mobile;
        this.type = type;
        this.payload = payload;
    }

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
}
