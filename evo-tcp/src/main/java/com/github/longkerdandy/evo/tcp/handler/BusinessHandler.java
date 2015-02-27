package com.github.longkerdandy.evo.tcp.handler;

import com.arangodb.ArangoException;
import com.github.longkerdandy.evo.api.message.*;
import com.github.longkerdandy.evo.api.protocol.*;
import com.github.longkerdandy.evo.arangodb.ArangoStorage;
import com.github.longkerdandy.evo.arangodb.entity.Device;
import com.github.longkerdandy.evo.arangodb.entity.DeviceRegisterUser;
import com.github.longkerdandy.evo.arangodb.entity.EntityFactory;
import com.github.longkerdandy.evo.arangodb.entity.Relation;
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

/**
 * Business Handler
 */
public class BusinessHandler extends SimpleChannelInboundHandler<Message> {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(BusinessHandler.class);

    private final Map<String, Device> authDevices;          // Authorized devices in this connection
    private final ArangoStorage storage;            // Storage
    private final ChannelRepository repository;     // Connection Repository

    public BusinessHandler(ArangoStorage storage, ChannelRepository repository) {
        this.storage = storage;
        this.repository = repository;
        this.authDevices = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        switch (msg.getMsgType()) {
            case MessageType.CONNECT:
                onConnect(ctx, (Message<Connect>) msg);
                break;
            case MessageType.DISCONNECT:
                onDisconnect(ctx, (Message<Disconnect>) msg);
                break;
            case MessageType.TRIGGER:
                onTrigger(ctx, (Message<Trigger>) msg);
                break;
            case MessageType.TRIGACK:
                onTrigAck(ctx, (Message<TrigAck>) msg);
                break;
            case MessageType.ACTION:
                onAction(ctx, (Message<Action>) msg);
                break;
            case MessageType.ACTACK:
                onActAck(ctx, (Message<ActAck>) msg);
                break;
        }
    }

    /**
     * Process Connect Message
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<Connect>
     */
    protected void onConnect(ChannelHandlerContext ctx, Message<Connect> msg) {
        logger.debug("Process Connect message {} from device {}", msg.getMsgId(), msg.getFrom());
        Connect connect = msg.getPayload();
        int pv = msg.getPv();
        int deviceType = msg.getDeviceType();
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
        else if (deviceType == DeviceType.DEVICE) {
            auth = true;
        }
        // auth as gateway
        else if (deviceType == DeviceType.GATEWAY) {
            auth = true;
        }
        // auth as controller
        else if (deviceType == DeviceType.CONTROLLER) {
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
        this.repository.sendMessage(ctx, msgConnAck);

        if (!auth) {
            return;
        }

        // save connection mapping
        Device d = EntityFactory.newDevice(deviceId, msg.getDeviceType(), msg.getDescId(), msg.getPv(), msg.getPt());
        this.authDevices.put(deviceId, d);
        this.repository.saveConn(deviceId, ctx);

        // notify user device online
        try {
            Set<String> controllers = this.storage.getDeviceFollowedControllerId(deviceId, Permission.READ, Permission.OWNER);
            for (String controller : controllers) {
                Message<Trigger> online = MessageFactory.newTriggerMessage(
                        deviceType, deviceId, controller, Const.TRIGGER_ONLINE, connect.getPolicy(), connect.getAttributes());
                this.repository.sendMessage(controller, online);
            }
        } catch (ArangoException e) {
            logger.error("Try to get device {} followers with exception: {}", deviceId, ExceptionUtils.getMessage(e));
        }

        // update device
        Device device = EntityFactory.newDevice(deviceId, msg.getDeviceType(), msg.getDescId(), msg.getPv(), msg.getPt());
        device.setAttributes(connect.getAttributes());
        device.setUpdateTime(msg.getTimestamp());
        try {
            if (!this.storage.isDeviceExist(deviceId)) {
                this.storage.createDevice(device);
            } else {
                updateDevice(device, connect.getPolicy());
            }
        } catch (ArangoException e) {
            logger.error("Try to update device {} with exception: {}", deviceId, ExceptionUtils.getMessage(e));
        }
    }

    /**
     * Process Disconnect Message
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<Disconnect>
     */
    protected void onDisconnect(ChannelHandlerContext ctx, Message<Disconnect> msg) {
        logger.debug("Process Disconnect message {} from device {}", msg.getMsgId(), msg.getFrom());
        Disconnect disconnect = msg.getPayload();
        String deviceId = msg.getFrom();
        Device d = this.authDevices.get(deviceId);

        // not auth
        if (d == null) {
            logger.trace("Not authorized device {}, disconnect message ignored");
            return;
        }

        // notify users
        Message<Trigger> offline = MessageFactory.newTriggerMessage(
                d.getType(), deviceId, null, Const.TRIGGER_OFFLINE, disconnect.getPolicy(), disconnect.getAttributes());
        notifyUsers(deviceId, Permission.READ, Permission.OWNER, offline);

        int returnCode;
        if (this.repository.removeConn(deviceId, ctx)) {
            // update device status
            Device device = EntityFactory.newDevice(deviceId);
            device.setAttributes(disconnect.getAttributes());
            device.setUpdateTime(msg.getTimestamp());
            try {
                returnCode = updateDevice(device, disconnect.getPolicy()) ?
                        DisconnAck.SUCCESS : DisconnAck.TIMESTAMP_FAIL;
            } catch (ArangoException e) {
                logger.error("Try to update device {} with exception: {}", deviceId, ExceptionUtils.getMessage(e));
                return;
            }
        } else {
            returnCode = DisconnAck.ALREADY_RECONNECTED;
        }

        // send diconnack
        Message<DisconnAck> disconnAck = MessageFactory.newDisconnAckMessage(deviceId, msg.getMsgId(), returnCode);
        this.repository.sendMessage(ctx, disconnAck);
    }

    /**
     * Process Trigger Message
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<Trigger>
     */
    protected void onTrigger(ChannelHandlerContext ctx, Message<Trigger> msg) {

    }

    /**
     * Process TrigAck Message
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<TrigAck>
     */
    protected void onTrigAck(ChannelHandlerContext ctx, Message<TrigAck> msg) {

    }

    /**
     * Process Action Message
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<Action>
     */
    protected void onAction(ChannelHandlerContext ctx, Message<Action> msg) {

    }

    /**
     * Process ActAck Message
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<ActAck>
     */
    protected void onActAck(ChannelHandlerContext ctx, Message<ActAck> msg) {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("Received channel in-active event from remote peer {}", getRemoteAddress(ctx));
        this.authDevices.keySet().stream().filter(deviceId -> this.repository.removeConn(deviceId, ctx)).forEach(deviceId -> {
            // notify users
            try {
                Set<String> controllers = this.storage.getDeviceFollowedControllerId(deviceId, Permission.READ, Permission.OWNER);
                for (String controller : controllers) {
                    Message<Trigger> offline = MessageFactory.newTriggerMessage(
                            this.authDevices.get(deviceId).getType(), deviceId, controller, Const.TRIGGER_OFFLINE, OverridePolicy.IGNORE, null);
                    this.repository.sendMessage(controller, offline);
                }
            } catch (ArangoException e) {
                logger.error("Try to get device {} followers with exception: {}", deviceId, ExceptionUtils.getMessage(e));
            }
        });
        this.authDevices.clear();
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

    /**
     * Update device entity based on override policy
     *
     * @param device Device Entity
     * @param policy Override Policy
     * @return Update succeed?
     * @throws ArangoException If device not exist
     */
    protected boolean updateDevice(Device device, int policy) throws ArangoException {
        boolean result = false;
        switch (policy) {
            case OverridePolicy.IGNORE:
                break;
            case OverridePolicy.REPLACE:
                this.storage.replaceDevice(device, false);
                result = true;
                break;
            case OverridePolicy.REPLACE_IF_NEWER:
                if (this.storage.replaceDevice(device, true).getEntity() == null) result = true;
                break;
            case OverridePolicy.UPDATE:
                this.storage.updateDevice(device, false);
                result = true;
                break;
            case OverridePolicy.UPDATE_IF_NEWER:
                if (this.storage.updateDevice(device, true).getEntity() == null) result = true;
                break;
        }
        return result;
    }

    /**
     * Notify Users/Controllers who followed the Device
     *
     * @param deviceId Device Id
     * @param min Permission Minimum
     * @param max Permission Maximum
     * @param msg Message to be sent
     */
    protected void notifyUsers(String deviceId, int min, int max, Message msg) {
        try {
            Set<String> controllers = this.storage.getDeviceFollowedControllerId(deviceId, min, max);
            for (String controller : controllers) {
                msg.setTo(controller);
                this.repository.sendMessage(controller, msg);
            }
        } catch (ArangoException e) {
            logger.error("Try to get device {} followers with exception: {}", deviceId, ExceptionUtils.getMessage(e));
        }
    }
}
