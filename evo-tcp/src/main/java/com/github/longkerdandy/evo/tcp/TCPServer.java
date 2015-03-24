package com.github.longkerdandy.evo.tcp;

import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.api.netty.Decoder;
import com.github.longkerdandy.evo.api.netty.Encoder;
import com.github.longkerdandy.evo.tcp.handler.BusinessHandler;
import com.github.longkerdandy.evo.tcp.repo.ChannelRepository;
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

/**
 * TCP Server
 */
public class TCPServer {

    private static final String STORAGE_HOST = "172.16.1.227";
    private static final int STORAGE_PORT = 3000;

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 1883;
    private static final int THREADS = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) throws Exception {
        ClientPolicy policy = new ClientPolicy();
        Host[] hosts = new Host[]{
                new Host(STORAGE_HOST, STORAGE_PORT),
        };
        AerospikeStorage storage = new AerospikeStorage(policy, hosts);
        ChannelRepository repository = new ChannelRepository();

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
                            p.addLast(new DefaultEventExecutorGroup(THREADS),
                                    new BusinessHandler(storage, repository));
                        }
                    });

            // start the server
            ChannelFuture f = b.bind(HOST, PORT).sync();

            // wait until the server socket is closed
            f.channel().closeFuture().sync();
        } finally {
            // shut down all event loops to terminate all threads
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
