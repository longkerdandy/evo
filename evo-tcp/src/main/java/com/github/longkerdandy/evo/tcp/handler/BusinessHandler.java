package com.github.longkerdandy.evo.tcp.handler;

import com.arangodb.ArangoException;
import com.github.longkerdandy.evo.api.message.*;
import com.github.longkerdandy.evo.api.protocol.*;
import com.github.longkerdandy.evo.arangodb.ArangoStorage;
import com.github.longkerdandy.evo.arangodb.entity.*;
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
    private final ArangoStorage storage;                    // Storage
    private final ChannelRepository repository;             // Connection Repository

    public BusinessHandler(ArangoStorage storage, ChannelRepository repository) {
        this.storage = storage;
        this.repository = repository;
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
        int pt = msg.getPt();
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
        else if (!isDescriptionRegistered(descId)) {
            logger.trace("Description {} not registered", descId);
            returnCode = ConnAck.DESCRIPTION_NOT_REGISTERED;
        }
        // auth as device
        else if (deviceType == DeviceType.DEVICE) {
            returnCode = ConnAck.RECEIVED;
        }
        // auth as gateway
        else if (deviceType == DeviceType.GATEWAY) {
            returnCode = ConnAck.RECEIVED;
        }
        // auth as controller
        else if (deviceType == DeviceType.CONTROLLER) {
            // empty user or token
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
                logger.trace("Empty user id or token");
                returnCode = ConnAck.EMPTY_USER_OR_TOKEN;
            }
            // user token incorrect
            else if (!isUserTokenCorrect(userId, deviceId, token)) {
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
            returnCode = ConnAck.PROTOCOL_VERSION_UNACCEPTABLE;
        }

        if (returnCode == ConnAck.RECEIVED) {
            // save mapping
            Device d = EntityFactory.newDevice(deviceId);
            d.setType(deviceType);
            d.setDescId(descId);
            d.setPv(pv);
            d.setPt(pt);
            this.authDevices.put(deviceId, d);
            if (StringUtils.isNotBlank(userId)) {
                User u = EntityFactory.newUser(userId);
                this.authUsers.put(deviceId, u);
            }
            this.repository.saveConn(deviceId, ctx);

            // notify users
            Message<Trigger> online = MessageFactory.newTriggerMessage(pv, deviceType, deviceId, null, Const.TRIGGER_ONLINE, connect.getPolicy(), connect.getAttributes());
            notifyUsers(deviceId, Permission.READ, Permission.OWNER, online);

            // update device
            Device device = EntityFactory.newDevice(deviceId);
            device.setType(deviceType);
            device.setDescId(descId);
            device.setConnected(TCPNode.id());
            device.setPv(pv);
            device.setPt(pt);
            device.setAttributes(connect.getAttributes());
            device.setUpdateTime(msg.getTimestamp());
            try {
                if (!this.storage.isDeviceExist(deviceId)) {
                    this.storage.createDevice(device);
                } else if (updateDevice(device, connect.getPolicy())) {
                    returnCode = ConnAck.TIMESTAMP_NOT_SATISFIED;
                }
            } catch (ArangoException e) {
                logger.error("Try to update device {} with exception: {}", deviceId, ExceptionUtils.getMessage(e));
            }
        }

        // send connack
        Message<ConnAck> msgConnAck = MessageFactory.newConnAckMessage(pv, deviceId, msg.getMsgId(), returnCode);
        this.repository.sendMessage(ctx, msgConnAck);
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

        int returnCode;
        if (this.repository.removeConn(deviceId, ctx)) {
            // update device status
            Device device = EntityFactory.newDevice(deviceId);
            device.setConnected("");
            device.setAttributes(disconnect.getAttributes());
            device.setUpdateTime(msg.getTimestamp());
            try {
                returnCode = updateDevice(device, disconnect.getPolicy()) ? DisconnAck.RECEIVED : DisconnAck.TIMESTAMP_NOT_SATISFIED;
            } catch (ArangoException e) {
                logger.error("Try to update device {} with exception: {}", deviceId, ExceptionUtils.getMessage(e));
                return;
            }
        } else {
            returnCode = DisconnAck.TIMESTAMP_NOT_SATISFIED;
        }

        if (returnCode == DisconnAck.RECEIVED) {
            // notify users
            Message<Trigger> offline = MessageFactory.newTriggerMessage(d.getPv(), d.getType(), deviceId, null, Const.TRIGGER_OFFLINE, disconnect.getPolicy(), disconnect.getAttributes());
            notifyUsers(deviceId, Permission.READ, Permission.OWNER, offline);
        }

        // send diconnack
        if (msg.getQos() == QoS.LEAST_ONCE || msg.getQos() == QoS.EXACTLY_ONCE) {
            Message<DisconnAck> disconnAck = MessageFactory.newDisconnAckMessage(d.getPv(), deviceId, msg.getMsgId(), returnCode);
            this.repository.sendMessage(ctx, disconnAck);
        }

        this.authDevices.remove(deviceId);
        this.authUsers.remove(deviceId);
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
        Trigger trigger = msg.getPayload();
        String deviceId = msg.getFrom();
        Device d = this.authDevices.get(deviceId);

        // not auth
        if (d == null) {
            logger.trace("Not authorized device {}, trigger message ignored");
            return;
        }

        // notify users
        notifyUsers(deviceId, Permission.READ, Permission.OWNER, msg);

        int returnCode;
        // update device status
        Device device = EntityFactory.newDevice(deviceId);
        device.setConnected(TCPNode.id());
        device.setAttributes(trigger.getAttributes());
        device.setUpdateTime(msg.getTimestamp());
        try {
            returnCode = updateDevice(device, trigger.getPolicy()) ? TrigAck.RECEIVED : TrigAck.TIMESTAMP_NOT_SATISFIED;
        } catch (ArangoException e) {
            logger.error("Try to update device {} with exception: {}", deviceId, ExceptionUtils.getMessage(e));
            return;
        }

        // send trigack
        if (msg.getQos() == QoS.LEAST_ONCE || msg.getQos() == QoS.EXACTLY_ONCE) {
            Message<TrigAck> trigAck = MessageFactory.newTrigAckMessage(d.getPv(), DeviceType.PLATFORM, Const.PLATFORM_ID, deviceId, msg.getMsgId(), returnCode);
            this.repository.sendMessage(ctx, trigAck);
        }
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
        if (d == null || (d.getType() != DeviceType.CONTROLLER && d.getType() != DeviceType.GATEWAY) || u == null) {
            logger.trace("Not authorized device {}, action message ignored");
            return;
        }

        int returnCode;
        if (isUserHasPermission(u.getId(), deviceId, Permission.READ_WRITE)) {
            // get connected node
            String node = getDeviceConnectedNode(deviceId);
            if (StringUtils.isNotBlank(node)) {
                // send to device
                this.repository.sendMessage(deviceId, msg);
                returnCode = ActAck.RECEIVED;
            } else {
                returnCode = ActAck.RECEIVED_CACHED;
            }
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
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("Received channel in-active event from remote peer {}", getRemoteAddress(ctx));
        this.authDevices.keySet().stream().filter(deviceId -> this.repository.removeConn(deviceId, ctx)).forEach(deviceId -> {
            boolean b;
            // update device status
            Device device = EntityFactory.newDevice(deviceId);
            device.setConnected("");
            device.setUpdateTime(System.currentTimeMillis());
            try {
                b = updateDevice(device, OverridePolicy.UPDATE_IF_NEWER);
            } catch (ArangoException e) {
                logger.error("Try to update device {} with exception: {}", deviceId, ExceptionUtils.getMessage(e));
                return;
            }

            if (b) {
                // notify users
                Device d = this.authDevices.get(deviceId);
                Message<Trigger> offline = MessageFactory.newTriggerMessage(d.getPv(), d.getType(), deviceId, null, Const.TRIGGER_OFFLINE, OverridePolicy.IGNORE, null);
                notifyUsers(deviceId, Permission.READ, Permission.OWNER, offline);
            }
        });
        this.authDevices.clear();
        this.authUsers.clear();
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
     * @param min      Permission Minimum
     * @param max      Permission Maximum
     * @param msg      Message to be sent
     */
    protected void notifyUsers(String deviceId, int min, int max, Message msg) {
        try {
            Set<String> controllers = this.storage.getDeviceFollowedControllerId(deviceId, min, max);
            for (String controller : controllers) {
                this.repository.sendMessage(controller, msg);
            }
        } catch (ArangoException e) {
            logger.error("Try to get device {} followers with exception: {}", deviceId, ExceptionUtils.getMessage(e));
        }
    }

    /**
     * Get Device connected TCP Node
     *
     * @param deviceId Device Id
     * @return Null or Empty if Device not exist or not connected
     */
    protected String getDeviceConnectedNode(String deviceId) {
        try {
            Document<Device> d = this.storage.getDeviceById(deviceId);
            return d.getEntity().getConnected();
        } catch (ArangoException e) {
            logger.trace("Try to get device {} with exception: {}", deviceId, ExceptionUtils.getMessage(e));
            return null;
        }
    }

    /**
     * Determine if User has followed Device with enough permission
     *
     * @param userId   User Id
     * @param deviceId Device Id
     * @param min      Minimal Permission Level
     * @return True if User has permission
     */
    protected boolean isUserHasPermission(String userId, String deviceId, int min) {
        boolean b = false;
        try {
            Relation<UserFollowDevice> r = this.storage.getUserFollowDevice(userId, deviceId);
            if (r.getEntity().getPermission() >= min) b = true;
        } catch (ArangoException e) {
            logger.trace("Try to get user {} device {} relation with exception: {}", userId, deviceId, ExceptionUtils.getMessage(e));
        }
        return b;
    }
}
