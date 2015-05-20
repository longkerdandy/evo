package com.github.longkerdandy.evo.service.sms.mq;

import com.github.longkerdandy.evo.api.mq.LegacyConsumerWorkerFactory;
import kafka.consumer.KafkaStream;

/**
 * SmsConsumerWorker Factory
 */
public class SmsConsumerWorkerFactory implements LegacyConsumerWorkerFactory<SmsConsumerWorker> {

    @Override
    public SmsConsumerWorker createWorker(KafkaStream stream) {
        return new SmsConsumerWorker(stream);
    }
}
