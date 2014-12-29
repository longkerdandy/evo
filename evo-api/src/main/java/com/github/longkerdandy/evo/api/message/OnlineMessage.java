package com.github.longkerdandy.evo.api.message;

/**
 * Online Message
 * Notify followers when device has connected to the Cloud
 */
@SuppressWarnings("unused")
public class OnlineMessage {

    private long onlineTimestamp;     // Timestamp (when device connected)

    public long getOnlineTimestamp() {
        return onlineTimestamp;
    }

    public void setOnlineTimestamp(long onlineTimestamp) {
        this.onlineTimestamp = onlineTimestamp;
    }
}
