package com.github.longkerdandy.evo.api.message;

import java.util.Map;

/**
 * Online Message
 * Notify followers when device has connected to the Cloud
 */
@SuppressWarnings("unused")
public class OnlineMessage {

    private String protocolVersion;         // Protocol Version
    private String description;             // Device Description (File) Id
    private Map<String, Object> attributes; // attributes

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
