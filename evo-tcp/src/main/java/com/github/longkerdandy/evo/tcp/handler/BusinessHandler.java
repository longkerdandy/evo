package com.github.longkerdandy.evo.tcp.handler;

import com.arangodb.ArangoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.entity.DeviceRegisterUser;
import com.github.longkerdandy.evo.api.entity.Relation;
import com.github.longkerdandy.evo.api.message.ConnAckMessage;
import com.github.longkerdandy.evo.api.message.ConnectMessage;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.message.MessageFactory;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.arangodb.ArangoStorage;
import com.github.longkerdandy.evo.tcp.repo.Repository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.longkerdandy.evo.api.util.JsonUtils.OBJECT_MAPPER;

/**
 * BusinessHandler
 */
public class BusinessHandler extends SimpleChannelInboundHandler<Message<JsonNode>> {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(BusinessHandler.class);

    private final Map<String, String> authMap;   // Authorized device - user map in this connection
    private final ArangoStorage storage;         // Storage
    private final Repository repository;         // Connection Repository

    public BusinessHandler(ArangoStorage storage, Repository repository) {
        this.storage = storage;
        this.repository = repository;
        this.authMap = new HashMap<>();
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
        ConnectMessage connect;
        try {
            connect = OBJECT_MAPPER.treeToValue(msg.getPayload(), ConnectMessage.class);
        } catch (JsonProcessingException e) {
            logger.trace("Exception when parse message's payload as ConnectMessage: {}", ExceptionUtils.getMessage(e));
            return;
        }
        String did = msg.getFrom();
        String uid = connect.getUser();
        String token = connect.getToken();
        // auth succeed?
        boolean auth = false;
        // prepare ConnAck message
        ConnAckMessage connAck = new ConnAckMessage();
        connAck.setConnMsg(msg.getMsgId());
        Message<ConnAckMessage> msgConnAck = MessageFactory.newMessage();
        msgConnAck.setMsgType(MessageType.CONNACK);
        msgConnAck.setTo(did);
        msgConnAck.setPayload(connAck);

        // auth as device
        if (StringUtils.isEmpty(connect.getUser())) {
            this.authMap.put(did, null);
            auth = true;
        }

        // auth as user
        else {
            // empty user or token
            if (StringUtils.isBlank(uid) || StringUtils.isBlank(token)) {
                logger.trace("Empty user id or token");
                connAck.setReturnCode(ConnAckMessage.EMPTY_USER_OR_TOKEN);
            }
            // user token incorrect
            else if (!isUserTokenCorrect(uid, did, token)) {
                logger.trace("User id & token incorrect");
                connAck.setReturnCode(ConnAckMessage.USER_TOKEN_INCORRECT);
            }
            // mark as auth
            else {
                auth = true;
            }
        }

        // send connack message back
        this.repository.sendMessage(ctx, msgConnAck);

        if (!auth) {
            return;
        }

        // save connection mapping
        this.authMap.put(did, uid);
        this.repository.saveConn(did, ctx);

        // notify user device online
        // Set<String> controllers = this.storage.getDeviceFollowedControllerId(did)
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (String did : authMap.keySet()) {
            this.repository.removeConn(did, ctx);
        }
        authMap.clear();
        ctx.fireChannelActive();
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
