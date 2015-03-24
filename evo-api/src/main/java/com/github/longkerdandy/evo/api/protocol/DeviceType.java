package com.github.longkerdandy.evo.api.protocol;

/**
 * Device Type
 */
@SuppressWarnings("unused")
public class DeviceType {

    public static final int PLATFORM = 0;
    public static final int GATEWAY = 1;
    public static final int DEVICE = 10;
    public static final int SENSOR = 50;
    public static final int CONTROLLER_ANDROID_PHONE = 100;
    public static final int CONTROLLER_ANDROID_PAD = 101;
    public static final int CONTROLLER_ANDROID_TV = 102;
    public static final int CONTROLLER_ANDROID_WEARABLE = 103;
    public static final int CONTROLLER_ANDROID_AUTO = 104;
    public static final int CONTROLLER_IOS_PHONE = 110;
    public static final int CONTROLLER_IOS_PAD = 111;
    public static final int CONTROLLER_IOS_TV = 112;
    public static final int CONTROLLER_IOS_WEARABLE = 113;
    public static final int CONTROLLER_IOS_AUTO = 114;
    public static final int CONTROLLER_WIN_PHONE = 120;
    public static final int CONTROLLER_WIN_PAD = 121;
    public static final int CONTROLLER_WIN_TV = 122;
    public static final int CONTROLLER_WIN_WEARABLE = 123;
    public static final int CONTROLLER_WIN_AUTO = 124;

    private DeviceType() {
    }

    /**
     * Is device type a simple device
     *
     * @param deviceType Device Controller
     * @return True if is simple device
     */
    public static boolean isSimpleDevice(int deviceType) {
        return deviceType >= 10 && deviceType < 100;
    }

    /**
     * Is device type a controller
     *
     * @param deviceType Device Controller
     * @return True if is controller
     */
    public static boolean isController(int deviceType) {
        return deviceType >= 100 && deviceType <= 255;
    }
}
