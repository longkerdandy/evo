package com.github.longkerdandy.evo.api.message;

/**
 * Offline Message
 * Notify followers when device has disconnected from the Cloud
 */
@SuppressWarnings("unused")
public class OfflineMessage {

    private long offlineTimestamp;     // Timestamp (when device connected)

    public long getOfflineTimestamp() {
        return offlineTimestamp;
    }

    public void setOfflineTimestamp(long offlineTimestamp) {
        this.offlineTimestamp = offlineTimestamp;
    }
}
