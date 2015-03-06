package com.github.longkerdandy.evo.service.weather.tcp;

import com.github.longkerdandy.evo.api.netty.Decoder;
import com.github.longkerdandy.evo.api.netty.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP Client
 */
public class TCPClient implements Runnable {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(TCPClient.class);

    private String host;
    private int port;
    private TCPClientHandler handler;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.handler = new TCPClientHandler();
    }

    @Override
    public void run() {
        // configure the client
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    // Encoder/Decoder
                    p.addLast(new Encoder());
                    p.addLast(new Decoder());
                    // Handler
                    p.addLast(handler);
                }
            });

            // start the client
            ChannelFuture f = b.connect(this.host, this.port).sync();

            // wait until the connection is closed
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(ExceptionUtils.getMessage(e));
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
