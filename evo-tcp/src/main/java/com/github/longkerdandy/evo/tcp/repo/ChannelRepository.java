package com.github.longkerdandy.evo.tcp.repo;

import com.github.longkerdandy.evo.api.message.Message;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
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
     * @param msg      Message to be sent
     */
    public void sendMessage(Message msg) {
        ChannelHandlerContext ctx = this.getConn(msg.getTo());
        if (ctx != null) {
            sendMessage(ctx, msg);
        } else {
            logger.debug("Message {} {} has not been sent from {} to {} because device is not connected",
                    msg.getMsgType(),
                    msg.getMsgId(),
                    msg.getFrom(),
                    msg.getTo());
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
                    logger.debug("Message {} {} has been sent from {} to {} successfully",
                            msg.getMsgType(),
                            msg.getMsgId(),
                            msg.getFrom(),
                            msg.getTo());
                } else {
                    logger.debug("Message {} {} failed to send from {} to {}: {}",
                            msg.getMsgType(),
                            msg.getMsgId(),
                            msg.getFrom(),
                            msg.getTo(),
                            ExceptionUtils.getMessage(future.cause()));
                }
            }
        });
    }
}
