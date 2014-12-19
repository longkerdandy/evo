package com.github.longkerdandy.evo.api.entity;

/**
 * Entity, Vertex
 */
@SuppressWarnings("unused")
public class Entity<T> {

    private String id; // id
    private T payload; // real payload

    public Entity(String id, T payload) {
        this.id = id;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
