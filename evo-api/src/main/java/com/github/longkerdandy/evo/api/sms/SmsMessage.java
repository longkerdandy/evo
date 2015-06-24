package com.github.longkerdandy.evo.api.sms;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.Validatable;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Represent SMS Message, send through message queue
 */
@SuppressWarnings("unused")
public class SmsMessage<T> implements Validatable {

    public static final int TYPE_VERIFY_CODE = 1;

    private String mobile;      // mobile number
    private int type;           // sms type
    private T payload;          // payload

    /**
     * Parse String to SmsMessage<JsonNode>
     *
     * @param json Json String
     * @return SmsMessage<JsonNode>
     * @throws IOException Json Exception
     */
    public static SmsMessage<JsonNode> parseMessageNode(String json) throws IOException {
        JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(SmsMessage.class, SmsMessage.class, JsonNode.class);
        return ObjectMapper.readValue(json, type);
    }

    /**
     * Parse String to SmsMessage
     *
     * @param json Json String
     * @return SmsMessage
     * @throws IOException Json Exception
     */
    public static SmsMessage parseMessage(String json) throws IOException {
        SmsMessage<JsonNode> msg = parseMessageNode(json);
        return parseMessage(msg);
    }

    /**
     * Parse SmsMessage<JsonNode> to SmsMessage
     *
     * @param msg SmsMessage<JsonNode>
     * @return SmsMessage
     * @throws IOException Unexpected message type
     */
    protected static SmsMessage parseMessage(SmsMessage<JsonNode> msg) throws IOException {
        SmsMessage m;
        switch (msg.getType()) {
            case TYPE_VERIFY_CODE:
                m = SmsMessageFactory.newMessage(msg, ObjectMapper.treeToValue(msg.getPayload(), VerifyCode.class));
                break;
            default:
                throw new IOException("Unexpected message type: " + msg.getType());
        }
        return m;
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
