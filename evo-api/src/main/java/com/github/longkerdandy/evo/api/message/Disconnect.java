package com.github.longkerdandy.evo.api.message;

/**
 * Disconnect Message
 * Device/Gate should try to notify platform when device disconnecting to the Cloud
 */
@SuppressWarnings("unused")
public class Disconnect {

    // Status Codes
    public static final int NORMAL = 100;
    public static final int CONNECTION_LOST = 101;

    private int statusCode;     // Status Code

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
