package com.github.longkerdandy.evo.api.message;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Message
 */
@SuppressWarnings("unused")
public class Message<T> {

    private String msgId;
    private String msgType;
    private String from;
    private List<String> toDevices;
    private List<String> toUsers;
    private String qos;
    private long timestamp;
    private T payload;

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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getToDevices() {
        return toDevices;
    }

    public void setToDevices(List<String> toDevices) {
        this.toDevices = toDevices;
    }

    public List<String> getToUsers() {
        return toUsers;
    }

    public void setToUsers(List<String> toUsers) {
        this.toUsers = toUsers;
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
