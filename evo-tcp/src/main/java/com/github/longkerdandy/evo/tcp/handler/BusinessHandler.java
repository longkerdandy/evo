package com.github.longkerdandy.evo.tcp.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.ConnectMessage;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.redis.RedisStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

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

    private int authState;        // Auth state
    private List<String> devices; // Auth devices with this connection
    private String user;          // Auth user

    private RedisStorage redis;   // Redis

    public BusinessHandler(RedisStorage redis) {
        this.redis = redis;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<JsonNode> msg) throws Exception {
        switch (msg.getMsgType()) {
            case "connect":
                onConnect(ctx, msg);
                break;
        }
    }

    protected void onConnect(ChannelHandlerContext ctx, Message<JsonNode> msg) throws IOException {
        ConnectMessage connMsg = OBJECT_MAPPER.treeToValue(msg.getPayload(), ConnectMessage.class);
        String deviceId = msg.getDevice();

        // Auth as Device
        if (StringUtils.isEmpty(connMsg.getUser())) {
            authState = AUTH_AS_DEVICE;
        }

        // Auth as User
        else {
            String userId = connMsg.getUser();
            String token = connMsg.getToken();

            // Empty User or Token
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
//                MqttConnAckMessage connAck = new MqttConnAckMessage(
//                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
//                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            }

            // User Token not correct
            if (!redis.isUserTokenCorrect(userId, deviceId, token)) {
//                MqttConnAckMessage connAck = new MqttConnAckMessage(
//                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
//                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED));
            }
            authState = AUTH_AS_USER;
        }
    }
}
