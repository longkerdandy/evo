package com.github.longkerdandy.evo.tcp.handler;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.github.longkerdandy.evo.aerospike.entity.Device;
import com.github.longkerdandy.evo.aerospike.entity.EntityFactory;
import com.github.longkerdandy.evo.aerospike.entity.User;
import com.github.longkerdandy.evo.api.message.*;
import com.github.longkerdandy.evo.api.protocol.*;
import com.github.longkerdandy.evo.tcp.mq.TCPProducer;
import com.github.longkerdandy.evo.tcp.repo.ChannelRepository;
import com.github.longkerdandy.evo.tcp.util.TCPNode;
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
    private final Map<String, User> authUsers;              // Authorized users in this connection
    private final AerospikeStorage storage;                 // Storage
    private final ChannelRepository repository;             // Connection Repository
    private final TCPProducer producer;                     // MQ Producer

    public BusinessHandler(AerospikeStorage storage, ChannelRepository repository, TCPProducer producer) {
        this.storage = storage;
        this.repository = repository;
        this.producer = producer;
        this.authDevices = new HashMap<>();
        this.authUsers = new HashMap<>();
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
     * Device -> Platform
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

        int returnCode;
        // protocol version
        if (!isProtocolVersionAcceptable(pv)) {
            logger.trace("Protocol version {} unacceptable", pv);
            returnCode = ConnAck.PROTOCOL_VERSION_UNACCEPTABLE;
        }
        // description
        else if (!this.storage.isDescriptionExist(descId)) {
            logger.trace("Description {} not registered", descId);
            returnCode = ConnAck.DESCRIPTION_NOT_REGISTERED;
        }
        // auth as device
        else if (DeviceType.isSimpleDevice(deviceType)) {
            returnCode = ConnAck.RECEIVED;
        }
        // auth as gateway
        else if (deviceType == DeviceType.GATEWAY) {
            returnCode = ConnAck.RECEIVED;
        }
        // auth as controller
        else if (DeviceType.isController(deviceType)) {
            // empty user or token
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
                logger.trace("Empty user id or token");
                returnCode = ConnAck.EMPTY_USER_OR_TOKEN;
            }
            // user token incorrect
            else if (!this.storage.isUserDeviceTokenCorrect(userId, deviceId, token)) {
                logger.trace("User id & token incorrect");
                returnCode = ConnAck.USER_TOKEN_INCORRECT;
            }
            // mark as auth
            else {
                returnCode = ConnAck.RECEIVED;
            }
        }
        // never happens
        else {
            returnCode = ConnAck.DEVICE_TYPE_UNACCEPTABLE;
        }

        if (returnCode == ConnAck.RECEIVED) {
            // save mapping
            Device d = EntityFactory.newDevice(deviceId);
            d.setType(deviceType);
            d.setDescId(descId);
            d.setPv(pv);
            this.authDevices.put(deviceId, d);
            if (DeviceType.isController(deviceType)) {
                User u = EntityFactory.newUser(userId);
                this.authUsers.put(deviceId, u);
            }
            this.repository.saveConn(deviceId, ctx);

            Message<Trigger> online = MessageFactory.newTriggerMessage(pv, deviceType, deviceId, null, Const.TRIGGER_ONLINE, connect.getPolicy(), connect.getAttributes());

            // notify users
            if (Const.PLATFORM_ID.equals(msg.getTo())) {
                notifyUsers(deviceId, Permission.READ, Permission.OWNER, online);
            }

            // update device
            Device device = EntityFactory.newDevice(deviceId);
            device.setType(deviceType);
            device.setDescId(descId);
            device.setPv(pv);
            device.setConnected(TCPNode.id());
            this.storage.updateDevice(device);

            // update control relation
            this.storage.updateUserControlDevice(userId, deviceId);

            // push to mq
            this.producer.sendMessage(online);
        }

        // send connack
        Message<ConnAck> msgConnAck = MessageFactory.newConnAckMessage(pv, deviceId, msg.getMsgId(), returnCode);
        this.repository.sendMessage(ctx, msgConnAck);
    }

    /**
     * Process Trigger Message
     * Device -> Platform
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<Trigger>
     */
    protected void onTrigger(ChannelHandlerContext ctx, Message<Trigger> msg) {
        logger.debug("Process Trigger message {} from device {}", msg.getMsgId(), msg.getFrom());
        String deviceId = msg.getFrom();
        Device d = this.authDevices.get(deviceId);

        // not auth
        if (d == null) {
            logger.trace("Not authorized device {}, trigger message ignored");
            return;
        }

        msg.setDeviceType(d.getType());
        msg.setDescId(d.getDescId());
        // msg.setPv(d.getPv());

        // notify users
        if (Const.PLATFORM_ID.equals(msg.getTo())) {
            notifyUsers(deviceId, Permission.READ, Permission.OWNER, msg);
        }

        // send trigack
        if (msg.getQos() == QoS.LEAST_ONCE || msg.getQos() == QoS.EXACTLY_ONCE) {
            Message<TrigAck> trigAck = MessageFactory.newTrigAckMessage(d.getPv(), DeviceType.PLATFORM, Const.PLATFORM_ID, deviceId, msg.getMsgId(), TrigAck.RECEIVED);
            this.repository.sendMessage(ctx, trigAck);
        }

        // push to mq
        this.producer.sendMessage(msg);
    }

    /**
     * Process TrigAck Message
     * Controller -> Platform
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<TrigAck>
     */
    protected void onTrigAck(ChannelHandlerContext ctx, Message<TrigAck> msg) {
        logger.debug("Process TrigAck message {} from device {}", msg.getMsgId(), msg.getFrom());
        // TODO: handle cache
    }

    /**
     * Process Action Message
     * Controller -> Platform
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<Action>
     */
    protected void onAction(ChannelHandlerContext ctx, Message<Action> msg) {
        logger.debug("Process Action message {} from device {}", msg.getMsgId(), msg.getFrom());
        String controllerId = msg.getFrom();
        String deviceId = msg.getTo();
        Device d = this.authDevices.get(controllerId);
        User u = this.authUsers.get(controllerId);

        // not auth
        if (d == null || u == null) {
            logger.trace("Not authorized controller {}, action message ignored");
            return;
        }

        msg.setDeviceType(d.getType());
        msg.setDescId(d.getDescId());
        // msg.setPv(d.getPv());
        msg.setUserId(u.getId());

        int returnCode;
        if (this.storage.isUserOwnDevice(u.getId(), deviceId, Permission.READ_WRITE)) {
            returnCode = ActAck.RECEIVED;
            notifyDevice(deviceId, msg);
        } else {
            returnCode = ActAck.PERMISSION_INSUFFICIENT;
        }

        // send actack
        if (msg.getQos() == QoS.LEAST_ONCE || msg.getQos() == QoS.EXACTLY_ONCE) {
            Message<ActAck> actAck = MessageFactory.newActAckMessage(d.getPv(), DeviceType.PLATFORM, Const.PLATFORM_ID, controllerId, msg.getMsgId(), returnCode);
            this.repository.sendMessage(ctx, actAck);
        }
    }

    /**
     * Process ActAck Message
     * Device -> Platform
     *
     * @param ctx ChannelHandlerContext
     * @param msg Message<ActAck>
     */
    protected void onActAck(ChannelHandlerContext ctx, Message<ActAck> msg) {
        logger.debug("Process ActAck message {} from device {}", msg.getMsgId(), msg.getFrom());
        // TODO: handle cache
    }

    /**
     * Process Disconnect Message
     * Device -> Platform
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

        msg.setDeviceType(d.getType());
        msg.setDescId(d.getDescId());
        // msg.setPv(d.getPv());

        // try to remove from local repository
        if (this.repository.removeConn(deviceId, ctx)) {
            this.authDevices.remove(deviceId);
            this.authUsers.remove(deviceId);
            // try to mark device disconnect
            if (this.storage.updateDeviceDisconnect(deviceId, TCPNode.id())) {
                Message<Trigger> offline = MessageFactory.newTriggerMessage(d.getPv(), d.getType(), deviceId, null, Const.TRIGGER_OFFLINE, disconnect.getPolicy(), disconnect.getAttributes());
                offline.setDescId(d.getDescId());

                // notify users
                // at the moment, we don't notify users device offline
                // notifyUsers(deviceId, Permission.READ, Permission.OWNER, offline);

                // push to mq
                this.producer.sendMessage(offline);
            }
        }

        // send diconnack
        if (msg.getQos() == QoS.LEAST_ONCE || msg.getQos() == QoS.EXACTLY_ONCE) {
            Message<DisconnAck> disconnAck = MessageFactory.newDisconnAckMessage(d.getPv(), deviceId, msg.getMsgId(), DisconnAck.RECEIVED);
            this.repository.sendMessage(ctx, disconnAck);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("Received channel in-active event from remote peer {}", getRemoteAddress(ctx));

        handleConnLost(ctx);

        // ctx.close();
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("Received exception caught event from remote peer {}: {}", getRemoteAddress(ctx), ExceptionUtils.getMessage(cause));

        handleConnLost(ctx);

        // ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    /**
     * Handle connection lost event
     *
     * @param ctx ChannelHandlerContext
     */
    protected void handleConnLost(ChannelHandlerContext ctx) {
        // try to remove from local repository
        this.authDevices.keySet().stream().filter(deviceId -> this.repository.removeConn(deviceId, ctx)).forEach(deviceId -> {
            // try to mark device disconnect
            if (this.storage.updateDeviceDisconnect(deviceId, TCPNode.id())) {
                Device d = this.authDevices.get(deviceId);
                Message<Trigger> offline = MessageFactory.newTriggerMessage(d.getPv(), d.getType(), deviceId, null, Const.TRIGGER_OFFLINE, OverridePolicy.IGNORE, null);
                offline.setDescId(d.getDescId());

                // notify users
                // at the moment, we don't notify users device offline
                // notifyUsers(deviceId, Permission.READ, Permission.OWNER, offline);

                // push to mq
                this.producer.sendMessage(offline);
            }
        });

        this.authDevices.clear();
        this.authUsers.clear();
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
     * Update device entity based on override policy
     *
     * @param deviceId Device Id
     * @param attr     Device Attributes
     * @param policy   Override Policy
     */
    protected void updateDeviceAttr(String deviceId, Map<String, Object> attr, int policy) {
        switch (policy) {
            case OverridePolicy.IGNORE:
                break;
            case OverridePolicy.REPLACE:
                this.storage.replaceDeviceAttr(deviceId, attr, false);
                break;
            case OverridePolicy.REPLACE_IF_NEWER:
                this.storage.replaceDeviceAttr(deviceId, attr, true);
                break;
            case OverridePolicy.UPDATE:
                this.storage.updateDeviceAttr(deviceId, attr, false);
                break;
            case OverridePolicy.UPDATE_IF_NEWER:
                this.storage.updateDeviceAttr(deviceId, attr, true);
                break;
        }
    }

    /**
     * Notify Users/Controllers who own the Device
     *
     * @param deviceId Device Id
     * @param min      Permission Minimum
     * @param max      Permission Maximum
     * @param msg      Message to be sent
     */
    protected void notifyUsers(String deviceId, int min, int max, Message msg) {
        Set<String> controllers = this.storage.getDeviceOwnerControllee(deviceId, min, max);
        if (controllers != null) {
            for (String controller : controllers) {
                notifyDevice(controller, msg);
            }
        }
    }

    /**
     * Notify Device
     *
     * @param deviceId Device Id
     * @param msg      Message to be sent
     */
    protected void notifyDevice(String deviceId, Message msg) {
        Device d = this.storage.getDeviceById(deviceId);
        if (d != null) {
            if (StringUtils.isNotBlank(d.getConnected())) {
                if (TCPNode.id().equals(d.getConnected())) {
                    this.repository.sendMessage(deviceId, msg); // send msg directly
                } else {
                    this.producer.sendMessage(msg); // push to mq
                }
            } else {
                // cache or push service
            }
        } else {
            logger.trace("Device {} not exist, msg {} {} not send", deviceId, msg.getMsgType(), msg.getMsgId());
        }
    }
}
