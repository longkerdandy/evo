package com.github.longkerdandy.evo.service.weather.tcp;

import com.github.longkerdandy.evo.api.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler
 */
public class TCPClientHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

    }
}
