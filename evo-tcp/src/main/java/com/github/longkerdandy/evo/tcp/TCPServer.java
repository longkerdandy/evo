package com.github.longkerdandy.evo.tcp;

import com.github.longkerdandy.evo.redis.RedisStorage;
import com.github.longkerdandy.evo.tcp.handler.BusinessHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * TCP Server
 */
public class TCPServer {

    private static final String REDIS_HOST = "0.0.0.0";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = "";

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 1883;
    private static final int THREADS = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) throws Exception {
        // Configure the server.
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
                            // MQTT
                            p.addLast(new MqttEncoder());
                            p.addLast(new MqttDecoder());
                            // Business Handler, in separate ExecutorGroup
                            p.addLast(new DefaultEventExecutorGroup(THREADS),
                                    new BusinessHandler(new RedisStorage(REDIS_HOST, REDIS_PORT, REDIS_PASSWORD)));
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(HOST, PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
