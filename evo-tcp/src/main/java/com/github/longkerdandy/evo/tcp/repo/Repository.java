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
public class Repository {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(Repository.class);
    // Thread safe HashMap as Repository (Device Id : ChannelHandlerContext)
    private final Map<String, ChannelHandlerContext> repo = new ConcurrentHashMap<>();

    /**
     * Save device connection
     *
     * @param did Device Id
     * @param ctx ChannelHandlerContext
     */
    public void saveConn(String did, ChannelHandlerContext ctx) {
        this.repo.put(did, ctx);
    }

    /**
     * Save device connection
     *
     * @param did Device Id
     * @return ChannelHandlerContext
     */
    public ChannelHandlerContext getConn(String did) {
        return this.repo.get(did);
    }

    /**
     * Remove device connection only if currently
     * mapped to the specified ChannelHandlerContext
     *
     * @param did Device Id
     * @param ctx ChannelHandlerContext
     * @return Removed?
     */
    public boolean removeConn(String did, ChannelHandlerContext ctx) {
        return this.repo.remove(did, ctx);
    }

    /**
     * Send message to specific device
     *
     * @param did Device Id
     * @param message Message to be sent
     */
    public void sendMessage(String did, Message message) {
        ChannelHandlerContext ctx = this.getConn(did);
        if (ctx != null) {
            sendMessage(ctx, message);
        } else {
            logger.trace("Message {} {} has not been sent because device {} is not connected",
                    message.getMsgType(),
                    message.getMsgId(),
                    did);
        }
    }

    /**
     * Send message to specific channel
     *
     * @param ctx     ChannelHandlerContext
     * @param message Message to be sent
     */
    public void sendMessage(ChannelHandlerContext ctx, Message message) {
        ChannelFuture future = ctx.writeAndFlush(message);
        future.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.trace("Message {} {} has been sent to device {} successfully",
                            message.getMsgType(),
                            message.getMsgId(),
                            StringUtils.defaultIfBlank(message.getTo(), "<default>"));
                } else {
                    logger.debug("Message {} {} failed to send to device {}: {}",
                            message.getMsgType(),
                            message.getMsgId(),
                            StringUtils.defaultIfBlank(message.getTo(), "<default>"),
                            ExceptionUtils.getMessage(future.cause()));
                }
            }
        });
    }
}
