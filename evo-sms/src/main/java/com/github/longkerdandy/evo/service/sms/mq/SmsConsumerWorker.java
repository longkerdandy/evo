package com.github.longkerdandy.evo.service.sms.mq;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.mq.LegacyConsumerWorker;
import com.github.longkerdandy.evo.api.sms.SmsMessage;
import com.github.longkerdandy.evo.api.sms.SmsMessageFactory;
import com.github.longkerdandy.evo.api.sms.VerifyCode;
import com.github.longkerdandy.evo.service.sms.gateway.Template;
import com.github.longkerdandy.evo.service.sms.gateway.YunPianClient;
import kafka.consumer.KafkaStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Message Queue Legacy Consumer Worker for SMS Topic
 */
public class SmsConsumerWorker extends LegacyConsumerWorker {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(SmsConsumerWorker.class);

    public SmsConsumerWorker(KafkaStream stream) {
        super(stream);
    }

    @Override
    public void handleMessage(String message) {
        try {
            // two step parse json
            JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(SmsMessage.class, SmsMessage.class, JsonNode.class);
            SmsMessage<JsonNode> s = ObjectMapper.readValue(message, type);
            // verify code
            if (s.getType() == SmsMessage.TYPE_VERIFY_CODE) {
                SmsMessage<VerifyCode> sms = SmsMessageFactory.newMessage(s, ObjectMapper.treeToValue(s.getPayload(), VerifyCode.class));
                // validate
                sms.validate();
                // send http request
                YunPianClient.sendSms(getCNMobile(sms.getMobile()), Template.verifyCode(sms.getPayload().getCode()));
            } else {
                logger.error("Unsupported sms message type {}", s.getType());
            }
        } catch (IOException | IllegalStateException e) {
            logger.error("Parse sms message with error: {}", ExceptionUtils.getMessage(e));
        }
    }

    /**
     * Get chinese mobile number without country code
     * Like from "+86 18600000000" to "18600000000"
     *
     * @param mobile Mobile Number
     * @return Chinese Mobile Number
     */
    protected String getCNMobile(String mobile) {
        return mobile.substring(mobile.indexOf(" ") + 1);
    }
}
