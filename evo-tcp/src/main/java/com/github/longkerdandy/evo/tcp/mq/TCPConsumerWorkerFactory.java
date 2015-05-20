package com.github.longkerdandy.evo.tcp.mq;

import com.github.longkerdandy.evo.api.mq.LegacyConsumerWorkerFactory;
import com.github.longkerdandy.evo.tcp.repo.ChannelRepository;
import kafka.consumer.KafkaStream;

/**
 * TCPConsumerWorker Factory
 */
public class TCPConsumerWorkerFactory implements LegacyConsumerWorkerFactory<TCPConsumerWorker> {

    private final ChannelRepository repository;

    public TCPConsumerWorkerFactory(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    public TCPConsumerWorker createWorker(KafkaStream stream) {
        return new TCPConsumerWorker(stream, this.repository);
    }
}
