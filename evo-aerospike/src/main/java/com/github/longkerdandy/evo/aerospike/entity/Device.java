package com.github.longkerdandy.evo.aerospike.entity;

import java.util.List;
import java.util.Map;

/**
 * Device Entity
 */
@SuppressWarnings("unused")
public class Device {

    private String id;                          // id
    private int type;                           // type
    private String descId;                      // description id
    private int protocol;                       // protocol
    private String token;                       // token
    private String connected;                   // connected node
    private List<Map<String, Object>> own;      // own relations
    private String ctrl;                        // control relation
    // private String ctrlToken;                   // control user's token

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

    //    public String getCtrlToken() {
    //        return ctrlToken;
    //    }
    //
    //    public void setCtrlToken(String ctrlToken) {
    //        this.ctrlToken = ctrlToken;
    //    }
}
