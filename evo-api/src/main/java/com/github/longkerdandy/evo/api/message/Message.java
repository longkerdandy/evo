package com.github.longkerdandy.evo.api.message;

import com.github.longkerdandy.evo.api.protocol.DeviceType;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.api.protocol.ProtocolType;
import com.github.longkerdandy.evo.api.protocol.QoS;
import org.apache.commons.lang3.StringUtils;

/**
 * Message
 * Base message & carrier for all other messages
 */
@SuppressWarnings("unused")
public class Message<T extends Validatable> implements Validatable {

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
        }

        if (this.payload != null) {
            this.payload.validate();
        }
    }
}
