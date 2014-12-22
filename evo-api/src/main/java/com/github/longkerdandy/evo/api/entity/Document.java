package com.github.longkerdandy.evo.api.entity;

/**
 * Document, Vertex
 */
@SuppressWarnings("unused")
public class Document<T> {

    private String id;      // id
    private long revision;  // revision number
    private T entity;       // entity

    public Document(String id, long revision, T entity) {
        this.id = id;
        this.revision = revision;
        this.entity = entity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
