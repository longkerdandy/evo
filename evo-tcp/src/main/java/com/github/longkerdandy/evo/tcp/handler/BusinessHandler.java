package com.github.longkerdandy.evo.tcp.handler;

import com.arangodb.ArangoException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.entity.DeviceRegisterUser;
import com.github.longkerdandy.evo.api.entity.Relation;
import com.github.longkerdandy.evo.api.message.ConnAckMessage;
import com.github.longkerdandy.evo.api.message.ConnectMessage;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.arangodb.ArangoStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.longkerdandy.evo.api.util.JsonUtils.OBJECT_MAPPER;

/**
 * BusinessHandler
 */
@SuppressWarnings("unused")
public class BusinessHandler extends SimpleChannelInboundHandler<Message<JsonNode>> {

    // Auth State Enum
    public static final int NOT_AUTH = 0;
    public static final int AUTH_AS_DEVICE = 1;
    public static final int AUTH_AS_USER = 2;

    private final Map<String, String> auth;   // Authorized device - user map
    private final ArangoStorage storage;      // Storage

    public BusinessHandler(ArangoStorage storage) {
        this.storage = storage;
        this.auth = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<JsonNode> msg) throws Exception {
        switch (msg.getMsgType()) {
            case MessageType.CONNECT:
                onConnect(ctx, msg);
                break;
        }
    }

    protected void onConnect(ChannelHandlerContext ctx, Message<JsonNode> msg) throws IOException {
        ConnectMessage connMsg = OBJECT_MAPPER.treeToValue(msg.getPayload(), ConnectMessage.class);
        String did = msg.getDevice();
        ConnAckMessage connAck = new ConnAckMessage();
        connAck.setConnMsg(msg.getMsgId());

        // auth as device
        if (StringUtils.isEmpty(connMsg.getUser())) {
            this.auth.put(did, null);
        }

        // auth as user
        else {
            String uid = connMsg.getUser();
            String token = connMsg.getToken();
            // empty user or token
            if (StringUtils.isBlank(uid) || StringUtils.isBlank(token)) {
                connAck.setReturnCode(ConnAckMessage.EMPTY_USER_OR_TOKEN);
            }
            // user token incorrect
            else if (!isUserTokenCorrect(uid, did, token)) {
                connAck.setReturnCode(ConnAckMessage.USER_TOKEN_INCORRECT);
            } else {
                this.auth.put(did, uid);
            }
        }
        // send connack message back
        // notify user device online
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
