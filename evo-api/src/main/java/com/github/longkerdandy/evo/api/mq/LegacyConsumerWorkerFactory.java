package com.github.longkerdandy.evo.api.mq;

import kafka.consumer.KafkaStream;

/**
 * Message Queue Legacy Consumer Worker Factory
 */
public interface LegacyConsumerWorkerFactory<W extends LegacyConsumerWorker> {

    /**
     * Create a new LegacyConsumerWorker which reads from KafkaStream
     *
     * @param stream KafkaStream
     * @return LegacyConsumerWorker
     */
    W createWorker(KafkaStream stream);
}
