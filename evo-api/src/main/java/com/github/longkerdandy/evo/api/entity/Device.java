package com.github.longkerdandy.evo.api.entity;

import com.arangodb.annotations.DocumentKey;

import java.util.Map;

/**
 * Device Entity
 */
@SuppressWarnings("unused")
public class Device {

    @DocumentKey
    private String id;                      // id
    private Map<String, Object> attributes; // attributes
    private long updateTime;                // update timestamp

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
