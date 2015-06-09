package com.github.longkerdandy.evo.service.weather.tcp;

import com.github.longkerdandy.evo.api.message.Connect;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.message.MessageFactory;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
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

/**
 * Handler
 */
public class TCPClientHandler extends SimpleChannelInboundHandler<Message> {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(TCPClientHandler.class);
    // Single instance created upon class loading.
    private static final TCPClientHandler handler = new TCPClientHandler();

    private Set<String> areaIds;        // AreaIds
    private ChannelHandlerContext ctx;  // Channel

    private TCPClientHandler() {
    }

    public static TCPClientHandler getInstance() {
        return handler;
    }

    public void setAreaIds(Set<String> areaIds) {
        this.areaIds = areaIds;
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
                    ProtocolType.TCP_1_0, DeviceType.DEVICE, deviceId, null, Description.ID, null, null, OverridePolicy.UPDATE_IF_NEWER, attr);
            sendMessage(connect);
        }

        // pass event
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // unset context
        this.ctx = null;

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
                    logger.debug("Message {} {} has been sent to device {} successfully",
                            msg.getMsgType(),
                            msg.getMsgId(),
                            StringUtils.defaultIfBlank(msg.getTo(), "<default>"));
                } else {
                    logger.debug("Message {} {} failed to send to device {}: {}",
                            msg.getMsgType(),
                            msg.getMsgId(),
                            StringUtils.defaultIfBlank(msg.getTo(), "<default>"),
                            ExceptionUtils.getMessage(future.cause()));
                }
            }
        });
    }
}
