package com.github.longkerdandy.evo.arangodb.entity;

/**
 * Relation, Edge
 */
@SuppressWarnings("unused")
public class Relation<T> {

    private String id;      // id
    private String from;    // from document id
    private String to;      // to document id
    private long revision;  // revision number
    private T entity;       // real entity

    public Relation(String id, String from, String to, long revision, T entity) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.revision = revision;
        this.entity = entity;
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

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
