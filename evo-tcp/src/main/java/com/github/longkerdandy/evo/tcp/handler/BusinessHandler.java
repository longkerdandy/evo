package com.github.longkerdandy.evo.tcp.handler;

import com.arangodb.ArangoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.arangodb.entity.DeviceRegisterUser;
import com.github.longkerdandy.evo.arangodb.entity.Relation;
import com.github.longkerdandy.evo.api.message.*;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.api.protocol.Permission;
import com.github.longkerdandy.evo.api.protocol.Const;
import com.github.longkerdandy.evo.arangodb.ArangoStorage;
import com.github.longkerdandy.evo.tcp.repo.ChannelRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Business Handler
 */
public class BusinessHandler extends SimpleChannelInboundHandler<Message<JsonNode>> {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(BusinessHandler.class);

    private final Map<String, String> authMap;   // Authorized device - user map in this connection
    private final Map<String, String> descMap;   // Authorized device - description map in this connection
    private final Map<String, Integer> pvMap;     // Authorized device - protocol version map in this connection
    private final ArangoStorage storage;         // Storage
    private final ChannelRepository channelRepository;         // Connection Repository

    public BusinessHandler(ArangoStorage storage, ChannelRepository channelRepository) {
        this.storage = storage;
        this.channelRepository = channelRepository;
        this.authMap = new HashMap<>();
        this.descMap = new HashMap<>();
        this.pvMap = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<JsonNode> msg) throws Exception {
        switch (msg.getMsgType()) {
            case MessageType.CONNECT:
                onConnect(ctx, msg);
                break;
        }
    }

    /**
     * Process Connect Message
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<JsonNode> which payload should be ConnectMessage
     */
    protected void onConnect(ChannelHandlerContext ctx, Message<JsonNode> msg) {
        logger.debug("Process Connect message {} from device {}", msg.getMsgId(), msg.getFrom());
        Connect connect;
        try {
            connect = ObjectMapper.treeToValue(msg.getPayload(), Connect.class);
        } catch (JsonProcessingException e) {
            logger.trace("Exception when parse message's payload as ConnectMessage: {}", ExceptionUtils.getMessage(e));
            return;
        }
        int pv = msg.getPv();
        String deviceId = msg.getFrom();
        String descId = msg.getDescId();
        String userId = msg.getUserId();
        String token = connect.getToken();
        // auth succeed?
        boolean auth = false;
        // prepare ConnAck message
        Message<ConnAck> msgConnAck = MessageFactory.newConnAckMessage(deviceId, msg.getMsgId(), ConnAck.SUCCESS);

        // protocol version
        if (!isProtocolVersionAcceptable(pv)) {
            logger.trace("Protocol version {} unacceptable", pv);
            msgConnAck.getPayload().setReturnCode(ConnAck.PROTOCOL_VERSION_UNACCEPTABLE);
        }
        // description
        else if (!isDescriptionRegistered(descId)) {
            logger.trace("Description {} not registered", descId);
            msgConnAck.getPayload().setReturnCode(ConnAck.DESCRIPTION_NOT_REGISTERED);
        }
        // auth as device
        else if (StringUtils.isEmpty(userId)) {
            auth = true;
        }
        // auth as user
        else {
            // empty user or token
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
                logger.trace("Empty user id or token");
                msgConnAck.getPayload().setReturnCode(ConnAck.EMPTY_USER_OR_TOKEN);
            }
            // user token incorrect
            else if (!isUserTokenCorrect(userId, deviceId, token)) {
                logger.trace("User id & token incorrect");
                msgConnAck.getPayload().setReturnCode(ConnAck.USER_TOKEN_INCORRECT);
            }
            // mark as auth
            else {
                auth = true;
            }
        }

        // send connack message back
        this.channelRepository.sendMessage(ctx, msgConnAck);

        if (!auth) {
            return;
        }

        // save connection mapping
        this.authMap.put(deviceId, userId);
        this.descMap.put(deviceId, descId);
        this.pvMap.put(deviceId, pv);
        this.channelRepository.saveConn(deviceId, ctx);

        // notify user device online
        try {
            Set<String> controllers = this.storage.getDeviceFollowedControllerId(deviceId, Permission.READ, Permission.OWNER);
            for (String controller : controllers) {
                //Message<OnlineMessage> msgOnline = MessageFactory.newOnlineMessage(deviceId, controller, pv, descId, connect.getAttributes());
                //this.channelRepository.sendMessage(controller, msgOnline);
            }
        } catch (ArangoException e) {
            logger.error("Try to get device {} followers with exception: {}", deviceId, ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("Received channel in-active event from remote peer {}", getRemoteAddress(ctx));
        this.authMap.keySet().stream().filter(did -> this.channelRepository.removeConn(did, ctx)).forEach(did -> {
            // notify user device offline
            try {
                Set<String> controllers = this.storage.getDeviceFollowedControllerId(did, Permission.READ, Permission.OWNER);
                for (String controller : controllers) {
                    //Message<OfflineMessage> msgOffline = MessageFactory.newOfflineMessage(did, controller, this.pvMap.get(did), this.descMap.get(did));
                    //this.channelRepository.sendMessage(controller, msgOffline);
                }
            } catch (ArangoException e) {
                logger.error("Try to get device {} followers with exception: {}", did, ExceptionUtils.getMessage(e));
            }
        });
        this.authMap.clear();
        this.descMap.clear();
        this.pvMap.clear();
        // ctx.close();
        ctx.fireChannelInactive();
    }

    /**
     * Get remote address string from ChannelHandlerContext
     *
     * @param ctx ChannelHandlerContext
     * @return Remote Address String
     */
    protected String getRemoteAddress(ChannelHandlerContext ctx) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        return address != null ? address.toString() : "<unknown>";
    }

    /**
     * Is protocol version acceptable?
     *
     * @param pv Protocol Version
     */
    protected boolean isProtocolVersionAcceptable(int pv) {
        return pv == Const.PROTOCOL_VERSION_1_0;
    }

    /**
     * Is protocol version acceptable?
     *
     * @param desc Description Id
     */
    protected boolean isDescriptionRegistered(String desc) {
        // TODO: check description id
        return true;
    }

    /**
     * Is user token correct?
     *
     * @param uid   User Id
     * @param did   Device Id
     * @param token Token
     * @return Correct?
     */
    protected boolean isUserTokenCorrect(String uid, String did, String token) {
        try {
            Relation<DeviceRegisterUser> relation = this.storage.getDeviceRegisterUser(did, uid);
            return token.equals(relation.getEntity().getToken());
        } catch (ArangoException e) {
            return false;
        }
    }
}
