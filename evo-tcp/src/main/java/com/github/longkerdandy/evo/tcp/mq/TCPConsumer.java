package com.github.longkerdandy.evo.tcp.mq;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP MQ Consumer
 */
public class TCPConsumer {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(TCPConsumer.class);

    private KafkaConsumer consumer;
}
