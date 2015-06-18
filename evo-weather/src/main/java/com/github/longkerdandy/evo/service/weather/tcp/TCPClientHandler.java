package com.github.longkerdandy.evo.service.weather.tcp;

import com.github.longkerdandy.evo.api.message.Connect;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.message.MessageFactory;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
import com.github.longkerdandy.evo.api.protocol.Evolution;
import com.github.longkerdandy.evo.api.protocol.OverridePolicy;
import com.github.longkerdandy.evo.api.protocol.ProtocolType;
import com.github.longkerdandy.evo.service.weather.desc.Description;
import com.github.longkerdandy.evo.service.weather.util.IdUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Handler
 */
public class TCPClientHandler extends SimpleChannelInboundHandler<Message> {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(TCPClientHandler.class);

    // AreaIds
    private final Set<String> areaIds;
    // TCP Client
    private final TCPClient client;

    private ChannelHandlerContext ctx;  // Channel Context

    public TCPClientHandler(Set<String> areaIds, TCPClient client) {
        this.areaIds = areaIds;
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // set context
        this.ctx = ctx;

        // send connect
        for (String areaId : areaIds) {
            String deviceId = IdUtils.getWeatherDeviceId(areaId);
            Map<String, Object> attr = new HashMap<>();
            attr.put(Description.ATTR_AREA_ID, areaId);
            Message<Connect> connect = MessageFactory.newConnectMessage(
                    ProtocolType.TCP_1_0, DeviceType.DEVICE, deviceId, Evolution.IGNORE,
                    Description.ID, null, null, OverridePolicy.UPDATE_IF_NEWER, attr);
            sendMessage(connect);
        }

        // pass event
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // unset context
        this.ctx = null;

        // reconnect
        ctx.channel().eventLoop().schedule(() -> this.client.connect(ctx.channel().eventLoop()), 15, TimeUnit.SECONDS);

        // pass event
        ctx.fireChannelInactive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        logger.debug("Received {} message {}", msg.getMsgType(), msg.getMsgId());
    }

    /**
     * Send message to platform
     *
     * @param msg Message to be sent
     */
    public void sendMessage(Message msg) {
        if (this.ctx == null) {
            logger.debug("Not connected to the platform, message {} {} dropped", msg.getMsgType(), msg.getMsgId());
            return;
        }

        ChannelFuture future = this.ctx.writeAndFlush(msg);
        future.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.debug("Message {} {} has been sent from {} to {} successfully",
                            msg.getMsgType(),
                            msg.getMsgId(),
                            msg.getFrom(),
                            StringUtils.defaultIfBlank(msg.getTo(), "<null>"));
                } else {
                    logger.debug("Message {} {} failed to send from {} to {}: {}",
                            msg.getMsgType(),
                            msg.getMsgId(),
                            msg.getFrom(),
                            StringUtils.defaultIfBlank(msg.getTo(), "<null>"),
                            ExceptionUtils.getMessage(future.cause()));
                }
            }
        });
    }
}
