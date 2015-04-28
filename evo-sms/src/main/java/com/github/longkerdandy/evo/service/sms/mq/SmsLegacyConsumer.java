package com.github.longkerdandy.evo.service.sms.mq;

import com.github.longkerdandy.evo.api.mq.Topics;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sms Kafka Legacy Consumer
 */
public class SmsLegacyConsumer {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(SmsLegacyConsumer.class);
    private final ConsumerConnector consumer;   // Consumer
    private final ExecutorService executor;     // Executor

    /**
     * Constructor
     *
     * @param props   Kafka Consumer Configuration
     * @param threads Threads Number
     */
    public SmsLegacyConsumer(Properties props, int threads) {
        ConsumerConfig config = new ConsumerConfig(props);
        this.consumer = Consumer.createJavaConsumerConnector(config);

        // connect to kafka
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(Topics.SMS, threads);
        Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap, new StringDecoder(null), new StringDecoder(null));
        List<KafkaStream<String, String>> streams = consumerMap.get(Topics.SMS);

        // now launch all the threads
        this.executor = Executors.newFixedThreadPool(threads);

        // now create workers to consume the messages
        int i = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new SmsLegacyConsumerWorker(stream, i));
            i++;
        }
    }

    /**
     * Close consumer
     */
    public void close() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                    logger.error("Timed out waiting for consumer threads to shut down, exiting uncleanly");
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted during shutdown, exiting uncleanly");
            }
        }
    }
}
