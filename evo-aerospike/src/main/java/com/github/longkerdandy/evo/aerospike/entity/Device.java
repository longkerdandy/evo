package com.github.longkerdandy.evo.aerospike.entity;

import java.util.List;
import java.util.Map;

/**
 * Device Entity
 */
@SuppressWarnings("unused")
public class Device {

    protected String id;                          // id
    protected int type;                           // type
    protected String descId;                      // description id
    protected int protocol;                       // protocol
    protected String token;                       // token
    protected String connected;                   // connected node
    protected List<Map<String, Object>> own;      // own relations
    protected String ctrl;                        // control relation

    protected Device() {
    }

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

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getConnected() {
        return connected;
    }

    public void setConnected(String connected) {
        this.connected = connected;
    }

    public List<Map<String, Object>> getOwn() {
        return own;
    }

    public void setOwn(List<Map<String, Object>> own) {
        this.own = own;
    }

    public String getCtrl() {
        return ctrl;
    }

    public void setCtrl(String ctrl) {
        this.ctrl = ctrl;
    }
}
