package com.github.longkerdandy.evo.api.entity;

import java.util.Map;

/**
 * Device Entity
 */
@SuppressWarnings("unused")
public class Device {

    private String sn;                      // hardware sn
    private Map<String, Object> attributes; // attributes
    private long updateTime;                // update timestamp

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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
