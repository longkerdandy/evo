package com.github.longkerdandy.evo.arangodb.entity;

import com.arangodb.annotations.DocumentKey;

import java.util.Map;

/**
 * Device Entity
 */
@SuppressWarnings("unused")
public class Device {

    @DocumentKey
    private String id;                      // Id
    private int type;                       // Type
    private String descId;                  // Description Id
    private String connected;               // Connected TCP Node
    private int pv;                         // Protocol Version
    private int pt;                         // Protocol Type
    private Map<String, Object> attributes; // Attributes
    private long updateTime;                // Update Timestamp

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescId() {
        return descId;
    }

    public void setDescId(String descId) {
        this.descId = descId;
    }

    public String getConnected() {
        return connected;
    }

    public void setConnected(String connected) {
        this.connected = connected;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return id.equals(device.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
