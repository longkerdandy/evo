package com.github.longkerdandy.evo.api.message;

/**
 * Message
 */
@SuppressWarnings("unused")
public class Message<T> {

    private String msgId;           // Message ID
    private String msgType;         // Message Type (payload)
    private String protocolVersion; // Protocol Version
    private String device;          // Device ID (who send this message)
    private String qos;             // QoS Level
    private long timestamp;         // Timestamp (when message is send)
    private T payload;              // Payload (sub-message)

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getQos() {
        return qos;
    }

    public void setQos(String qos) {
        this.qos = qos;
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
