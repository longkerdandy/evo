package com.github.longkerdandy.evo.api.mq;

import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message Queue Legacy Consumer Worker
 */
public abstract class LegacyConsumerWorker implements Runnable {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(LegacyConsumerWorker.class);
    private final KafkaStream stream;

    public LegacyConsumerWorker(KafkaStream stream) {
        this.stream = stream;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        for (MessageAndMetadata<String, String> m : (Iterable<MessageAndMetadata<String, String>>) this.stream) {
            String msg = m.message();
            logger.trace("ConsumerWorker received message {}", msg);

            // handle message
            handleMessage(msg);
        }
    }

    /**
     * Implement this method to handle messages from message queue
     *
     * @param message Message
     */
    public abstract void handleMessage(String message);
}
