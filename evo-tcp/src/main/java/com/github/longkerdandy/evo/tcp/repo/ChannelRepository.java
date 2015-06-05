package com.github.longkerdandy.evo.tcp.repo;

import com.github.longkerdandy.evo.api.message.Message;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Connection Repository
 */
public class ChannelRepository {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(ChannelRepository.class);
    // Thread safe HashMap as Repository (Device Id : ChannelHandlerContext)
    private final Map<String, ChannelHandlerContext> repo = new ConcurrentHashMap<>();

    /**
     * Save device connection
     *
     * @param deviceId Device Id
     * @param ctx      ChannelHandlerContext
     */
    public void saveConn(String deviceId, ChannelHandlerContext ctx) {
        this.repo.put(deviceId, ctx);
    }

    /**
     * Get device connection
     *
     * @param deviceId Device Id
     * @return ChannelHandlerContext
     */
    public ChannelHandlerContext getConn(String deviceId) {
        return this.repo.get(deviceId);
    }

    /**
     * Remove device connection only if currently
     * mapped to the specified ChannelHandlerContext
     *
     * @param deviceId Device Id
     * @param ctx      ChannelHandlerContext
     * @return Removed?
     */
    public boolean removeConn(String deviceId, ChannelHandlerContext ctx) {
        return this.repo.remove(deviceId, ctx);
    }

    /**
     * Send message to specific device
     *
     * @param deviceId Device Id
     * @param msg      Message to be sent
     */
    public void sendMessage(String deviceId, Message msg) {
        ChannelHandlerContext ctx = this.getConn(deviceId);
        if (ctx != null) {
            sendMessage(ctx, msg);
        } else {
            logger.trace("Message {} {} has not been sent because device {} is not connected",
                    msg.getMsgType(),
                    msg.getMsgId(),
                    deviceId);
        }
    }

    /**
     * Send message to specific channel
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message to be sent
     */
    public void sendMessage(ChannelHandlerContext ctx, Message msg) {
        ChannelFuture future = ctx.writeAndFlush(msg);
        future.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.trace("Message {} {} has been sent to device {} successfully",
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
