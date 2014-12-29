package com.github.longkerdandy.evo.api.message;

/**
 * Message
 * Base message & carrier for all other messages
 */
@SuppressWarnings("unused")
public class Message<T> {

    private String msgId;           // Message ID
    private String msgType;         // Message Type (payload)
    private String protocolVersion; // Protocol Version
    private String from;            // Device ID (who send this message)
    private String to;              // Device ID (whom this message send to)
    private int qos;                // QoS Level
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

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
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
