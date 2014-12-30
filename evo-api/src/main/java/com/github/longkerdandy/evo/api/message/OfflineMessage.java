package com.github.longkerdandy.evo.api.message;

/**
 * Offline Message
 * Notify followers when device has disconnected from the Cloud
 */
@SuppressWarnings("unused")
public class OfflineMessage {

    private String protocolVersion;         // Protocol Version
    private String description;             // Device Description (File) Id

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
}
