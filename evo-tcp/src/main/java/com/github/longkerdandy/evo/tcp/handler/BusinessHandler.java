package com.github.longkerdandy.evo.tcp.handler;

import com.github.longkerdandy.evo.redis.RedisStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * BusinessHandler
 */
@SuppressWarnings("unused")
public class BusinessHandler extends SimpleChannelInboundHandler<MqttMessage> {

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
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                onConnect(ctx, (MqttConnectMessage) msg);
                break;
            case CONNACK:
                break; // Never happens
            case PUBLISH:
                onPublish(ctx, (MqttPublishMessage) msg);
                break;
            case PUBACK:
                onPubAck(ctx, (MqttPubAckMessage) msg);
                break;
            case PUBREC:
                break; // Not used at the moment
            case PUBREL:
                break; // Not used at the moment
            case PUBCOMP:
                break; // Not used at the moment
            case SUBSCRIBE:
                break; // Not used at the moment
            case SUBACK:
                break; // Not used at the moment
            case UNSUBSCRIBE:
                break; // Not used at the moment
            case UNSUBACK:
                break; // Not used at the moment
            case PINGREQ:
                onPingReq(ctx, msg);
                break;
            case PINGRESP:
                break; // Never happens
            case DISCONNECT:
                onDisconnect(ctx, msg);
                break;
        }
    }

    protected void onConnect(ChannelHandlerContext ctx, MqttConnectMessage msg) {
        String deviceId = msg.payload().clientIdentifier();

        // Auth as Device
        if (!msg.variableHeader().hasUserName()) {
            authState = AUTH_AS_DEVICE;
        }

        // Auth as User
        else {
            String userId = msg.payload().userName();
            String token = msg.payload().password();

            // Empty User or Token
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
                MqttConnAckMessage connAck = new MqttConnAckMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
            }

            // User Token not correct
            if (!redis.isUserTokenCorrect(userId, deviceId, token)) {
                MqttConnAckMessage connAck = new MqttConnAckMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED));
            }
            authState = AUTH_AS_USER;
        }
    }

    protected void onDisconnect(ChannelHandlerContext ctx, MqttMessage msg) {

    }

    protected void onPingReq(ChannelHandlerContext ctx, MqttMessage msg) {

    }

    protected void onPublish(ChannelHandlerContext ctx, MqttPublishMessage msg) {

    }

    protected void onPubAck(ChannelHandlerContext ctx, MqttPubAckMessage msg) {

    }

    protected void sendBackMessage(ChannelHandlerContext ctx, MqttMessage msg) {
        ctx.writeAndFlush(msg);
    }
}
