package com.github.longkerdandy.evo.tcp.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.util.JsonUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * TCP MQ Producer
 */
public class TCPProducer {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(TCPProducer.class);
    // Kafka Producer
    private KafkaProducer<String, String> producer;

    /**
     * Constructor
     * A Kafka client that publishes records to the Kafka cluster.
     * The producer is thread safe and should generally be shared among all threads for best performance.
     *
     * @param configs Producer Configs; see http://kafka.apache.org/documentation.html#producerconfigs
     */
    public TCPProducer(Map<String, Object> configs) {
        this.producer = new KafkaProducer<>(configs, new StringSerializer(), new StringSerializer());
    }

    /**
     * The producer manages a single background thread that does I/O
     * as well as a TCP connection to each of the brokers it needs to communicate with.
     * Failure to close the producer after use will leak these resources.
     */
    @SuppressWarnings("unused")
    public void close() {
        if (this.producer != null) this.producer.close();
    }

    /**
     * Send message to mq
     *
     * @param topic MQ Topic
     * @param msg   Message
     */
    public void sendMessage(String topic, Message msg) {
        // A key/value pair to be sent to Kafka. This consists of a topic name to which the record is being sent,
        // an optional partition number, and an optional key and value.
        // If a valid partition number is specified that partition will be used when sending the record.
        // If no partition is specified but a key is present a partition will be chosen using a hash of the key.
        // If neither key nor partition is present a partition will be assigned in a round-robin fashion.
        try {
            String value = JsonUtils.ObjectMapper.writeValueAsString(msg);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
            this.producer.send(record,
                    (metadata, e) -> {
                        if (e != null)
                            logger.error("Send message {} {} to mq failed: {}", msg.getMsgType(), msg.getMsgId(), ExceptionUtils.getMessage(e));
                        else {
                            logger.trace("Successful send message {} {} to mq partition {} offset {}", msg.getMsgType(), msg.getMsgId(), metadata.partition(), metadata.offset());
                        }
                    });
        } catch (JsonProcessingException e) {
            logger.error("Send message {} {} to mq with exception: {}", msg.getMsgType(), msg.getMsgId(), ExceptionUtils.getMessage(e));
        }
    }
}
