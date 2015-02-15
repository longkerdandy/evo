package com.github.longkerdandy.evo.api.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Message
 * Base message & carrier for all other messages
 */
@SuppressWarnings("unused")
public class Message<T> {

    @JsonIgnore
    private int pv;                 // Protocol Version
    @JsonIgnore
    private int pt;                 // Protocol Type
    private int msgType;            // Message Type (payload)
    private int qos;                // QoS Level
    private boolean duplicate;      // Is duplicate?
    private int deviceType;         // Device Type
    private String msgId;           // Message ID
    private String from;            // Device ID (who send this message)
    private String to;              // Device ID (whom this message send to)
    private String descId;          // Device Description Id
    private String userId;          // User ID (as controller)
    private long timestamp;         // Timestamp (when message is send)
    private T payload;              // Payload (sub-message)

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getPt() {
        return pt;
    }

    public void setPt(int pt) {
        this.pt = pt;
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
