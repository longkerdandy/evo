package com.github.longkerdandy.evo.api.message;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.api.protocol.ProtocolType;
import com.github.longkerdandy.evo.api.protocol.QoS;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Message
 * Base message & carrier for all other messages
 */
@SuppressWarnings("unused")
public class Message<T> implements Validatable {

    // Message Size
    public static final int MAX_BYTES = 8092;

    protected int protocol;           // Protocol
    protected int msgType;            // Message Type (payload)
    protected int qos;                // QoS Level
    protected boolean duplicate;      // Is duplicate?
    protected int deviceType;         // Device Type
    protected String msgId;           // Message ID
    protected String from;            // Device ID (who send this message)
    protected String to;              // Device ID (whom this message send to)
    protected String descId;          // Device Description Id
    protected String userId;          // User ID
    protected long timestamp;         // Timestamp (when message is send)
    protected T payload;              // Payload (sub-message)

    /**
     * Parse InputStream to Message<JsonNode>
     *
     * @param is InputStream
     * @return Message<JsonNode>
     * @throws IOException Json Exception
     */
    public static Message<JsonNode> parseMessageNode(InputStream is) throws IOException {
        JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(Message.class, Message.class, JsonNode.class);
        return ObjectMapper.readValue(is, type);
    }

    /**
     * Parse String to Message<JsonNode>
     *
     * @param json Json String
     * @return Message<JsonNode>
     * @throws IOException Json Exception
     */
    public static Message<JsonNode> parseMessageNode(String json) throws IOException {
        JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(Message.class, Message.class, JsonNode.class);
        return ObjectMapper.readValue(json, type);
    }

    /**
     * Parse InputStream to Message
     *
     * @param is InputStream
     * @return Message
     * @throws IOException Json Exception
     */
    public static Message parseMessage(InputStream is) throws IOException {
        Message<JsonNode> msg = parseMessageNode(is);
        return parseMessage(msg);
    }

    /**
     * Parse String to Message
     *
     * @param json Json String
     * @return Message
     * @throws IOException Json Exception
     */
    public static Message parseMessage(String json) throws IOException {
        Message<JsonNode> msg = parseMessageNode(json);
        return parseMessage(msg);
    }

    /**
     * Parse Message<JsonNode> to Message
     *
     * @param msg Message<JsonNode>
     * @return Message
     * @throws IOException Unexpected message type
     */
    protected static Message parseMessage(Message<JsonNode> msg) throws IOException {
        Message m;
        switch (msg.getMsgType()) {
            case MessageType.CONNECT:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), Connect.class));
                break;
            case MessageType.CONNACK:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), ConnAck.class));
                break;
            case MessageType.DISCONNECT:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), Disconnect.class));
                break;
            case MessageType.DISCONNACK:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), DisconnAck.class));
                break;
            case MessageType.TRIGGER:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), Trigger.class));
                break;
            case MessageType.TRIGACK:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), TrigAck.class));
                break;
            case MessageType.ACTION:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), Action.class));
                break;
            case MessageType.ACTACK:
                m = MessageFactory.clone(msg, msg.getMsgId(), ObjectMapper.treeToValue(msg.getPayload(), ActAck.class));
                break;
            default:
                throw new IOException("Unexpected message type: " + msg.getMsgType());
        }
        return m;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDescId() {
        return descId;
    }

    public void setDescId(String descId) {
        this.descId = descId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public void validate() {
        if (!ProtocolType.isValid(this.protocol)) {
            throw new IllegalStateException("Invalid protocol type");
        }

        if (!MessageType.isValid(this.msgType)) {
            throw new IllegalStateException("Invalid message type");
        }

        if (!QoS.isValid(this.qos)) {
            throw new IllegalStateException("Invalid qos level");
        }

        if (!DeviceType.isValid(this.deviceType)) {
            throw new IllegalStateException("Invalid device type");
        }

        if (StringUtils.isBlank(this.msgId)) {
            throw new IllegalStateException("Invalid message id");
        }

        if (StringUtils.isBlank(this.from)) {
            throw new IllegalStateException("Invalid message from");
        }

        if (StringUtils.isBlank(this.to)) {
            throw new IllegalStateException("Invalid message to");
        }

        // TODO: find a better way to validate time stamp
        if (this.timestamp <= 0) {
            throw new IllegalStateException("Invalid time stamp");
        }

        // disconnect message can have null payload
        if (this.payload == null && this.msgType != MessageType.DISCONNECT) {
            throw new IllegalStateException("Invalid message payload");
        }

        if (this.msgType == MessageType.CONNECT) {
            if (StringUtils.isBlank(this.descId)) {
                throw new IllegalStateException("Invalid descriptor id");
            }
            if (DeviceType.isController(deviceType) && StringUtils.isBlank(((Connect) this.getPayload()).getToken())) {
                throw new IllegalStateException("Invalid token");
            }
        }

        if (this.payload != null && this.payload instanceof Validatable) {
            ((Validatable) this.payload).validate();
        }
    }
}
