package com.github.longkerdandy.evo.tcp;

import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.api.mq.AdminTools;
import com.github.longkerdandy.evo.api.mq.LegacyConsumer;
import com.github.longkerdandy.evo.api.mq.Producer;
import com.github.longkerdandy.evo.api.mq.Topics;
import com.github.longkerdandy.evo.api.netty.Decoder;
import com.github.longkerdandy.evo.api.netty.Encoder;
import com.github.longkerdandy.evo.tcp.handler.BusinessHandler;
import com.github.longkerdandy.evo.tcp.mq.TCPConsumerWorker;
import com.github.longkerdandy.evo.tcp.mq.TCPConsumerWorkerFactory;
import com.github.longkerdandy.evo.tcp.repo.ChannelRepository;
import com.github.longkerdandy.evo.tcp.util.TCPNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.*;

/**
 * TCP Server
 */
public class TCPServer {

    private static final int THREADS = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) throws Exception {
        // load config
        String f = args.length >= 1 ? args[0] : "config/tcp.properties";
        PropertiesConfiguration config = new PropertiesConfiguration(f);

        // channel repository
        ChannelRepository repository = new ChannelRepository();

        // storage
        ClientPolicy policy = new ClientPolicy();
        List<Host> hosts = new ArrayList<>();
        for (String h : config.getString("storage.hosts").split(",")) {
            hosts.add(new Host(h.split(":")[0], Integer.valueOf(h.split(":")[1])));
        }
        AerospikeStorage storage = new AerospikeStorage(policy, hosts.toArray(new Host[hosts.size()]));

        // message queue
        // create producer
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("mq.producer.hosts"));
        configs.put(ProducerConfig.ACKS_CONFIG, config.getString("mq.producer.acks"));
        configs.put(ProducerConfig.BLOCK_ON_BUFFER_FULL_CONFIG, config.getString("mq.producer.blockOnBufferFull"));
        Producer producer = new Producer(configs);
        // create tcp-out topic
        String topic = Topics.TCP_OUT(TCPNode.id());
        AdminTools admin = new AdminTools(config.getString("mq.zk.hosts"), 10000, 10000);
        if (!admin.isTopicExist(topic)) {
            admin.createTopic(topic, config.getInt("mq.topic.tcpout.partitions"), config.getInt("mq.topic.tcpout.replication"), new Properties());
        }
        admin.close();
        // create consumer
        TCPConsumerWorkerFactory factory = new TCPConsumerWorkerFactory(repository);
        Properties props = new Properties();
        props.put("zookeeper.connect", config.getString("mq.zk.hosts"));
        props.put("group.id", topic);
        LegacyConsumer<TCPConsumerWorker> consumer = new LegacyConsumer<>(factory, topic, props, config.getInt("mq.topic.tcpout.workerThreads"));

        // configure the server
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(THREADS);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // Encoder/Decoder
                            p.addLast(new Encoder());
                            p.addLast(new Decoder());
                            // Business Handler, in separate ExecutorGroup
                            p.addLast(new DefaultEventExecutorGroup(THREADS), new BusinessHandler(storage, repository, producer));
                        }
                    });

            // start the server
            ChannelFuture future = b.bind(config.getString("tcp.host"), config.getInt("tcp.port")).sync();

            // wait until the server socket is closed
            future.channel().closeFuture().sync();
        } finally {
            // shut down all event loops to terminate all threads
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            // close message queue
            producer.close();
            consumer.close();
        }
    }
}
