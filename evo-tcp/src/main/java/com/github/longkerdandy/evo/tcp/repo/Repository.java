package com.github.longkerdandy.evo.tcp.repo;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Connection Repository
 */
public class Repository {

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
}
