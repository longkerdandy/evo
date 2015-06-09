package com.github.longkerdandy.evo.api.message;

/**
 * Message
 * Base message & carrier for all other messages
 */
@SuppressWarnings("unused")
public class Message<T> {

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
}
