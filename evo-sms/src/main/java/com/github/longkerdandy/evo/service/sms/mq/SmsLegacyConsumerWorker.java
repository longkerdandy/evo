package com.github.longkerdandy.evo.service.sms.mq;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.sms.SmsMessage;
import com.github.longkerdandy.evo.api.sms.SmsVerifyCode;
import com.github.longkerdandy.evo.service.sms.gateway.Template;
import com.github.longkerdandy.evo.service.sms.gateway.YunPianClient;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Sms Kafka Legacy Consumer Worker
 */
public class SmsLegacyConsumerWorker implements Runnable {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(SmsLegacyConsumerWorker.class);
    private final KafkaStream stream;
    private final int thread;

    public SmsLegacyConsumerWorker(KafkaStream stream, int thread) {
        this.stream = stream;
        this.thread = thread;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        for (MessageAndMetadata<String, String> m : (Iterable<MessageAndMetadata<String, String>>) this.stream) {
            String msg = m.message();
            logger.trace("Thread {} received message {}", this.thread, msg);

            // parse json
            try {
                JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(SmsMessage.class, SmsMessage.class, JsonNode.class);
                SmsMessage<JsonNode> sms = ObjectMapper.readValue(msg, type);
                // validate mobile format
                if (isCNMobileValid(sms.getMobile())) {
                    if (sms.getType() == SmsMessage.TYPE_VERIFY_CODE) {
                        SmsVerifyCode verifyCode = ObjectMapper.treeToValue(sms.getPayload(), SmsVerifyCode.class);
                        YunPianClient.sendSms(getCNMobile(sms.getMobile()), Template.forgeVerifyCode(verifyCode.getCode()));
                    } else {
                        logger.warn("Unsupported sms message type {}", sms.getType());
                    }
                } else {
                    logger.warn("Unsupported sms message mobile {}", sms.getMobile());
                }
            } catch (IOException e) {
                logger.error("Parse SmsMessage with error: {}", ExceptionUtils.getMessage(e));
            }
        }
    }

    /**
     * Is given chinese mobile number valid
     * Mobile number should be something like "+86 18600000000"
     *
     * @param mobile Mobile Number
     * @return True if valid
     */
    protected boolean isCNMobileValid(String mobile) {
        if (mobile == null) return false;
        Pattern p = Pattern.compile("^\\+86[- ]\\d{11}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * Get chinese mobile number without country code
     * Like from "+86 18600000000" to "18600000000"
     *
     * @param mobile Mobile Number
     * @return Chinese Mobile Number
     */
    protected String getCNMobile(String mobile) {
        return mobile.substring(4);
    }
}
