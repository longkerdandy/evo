package com.github.longkerdandy.evo.api.entity;

/**
 * Relation, Edge
 */
@SuppressWarnings("unused")
public class Relation<T> {

    private String id;      // id
    private String from;    // id
    private String to;      // id
    private T payload;      // real payload

    public Relation(String id, String from, String to, T payload) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
