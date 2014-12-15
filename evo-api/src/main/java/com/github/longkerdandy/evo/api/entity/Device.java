package com.github.longkerdandy.evo.api.entity;

import com.arangodb.annotations.DocumentKey;

import java.util.Map;

/**
 * Device Entity
 */
@SuppressWarnings("unused")
public class Device {
    @DocumentKey
    private String id;                  // id
    private Map<String, Object> fields; // fields
    private long updateTime;            // update timestamp

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
