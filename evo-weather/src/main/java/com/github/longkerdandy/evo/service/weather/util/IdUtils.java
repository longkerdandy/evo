package com.github.longkerdandy.evo.service.weather.util;

import com.github.longkerdandy.evo.api.util.UuidUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Id Utils based on Evolution Protocol
 */
public class IdUtils {

    private IdUtils() {
    }

    /**
     * Generate Device Id based on Area Id
     *
     * @param areaId Area Id
     * @return Device Id
     */
    public static String getWeatherDeviceId(String areaId) {
        try {
            String url = "https://github.com/longkerdandy/evo/weather?areaId=" + areaId;
            URI uri = new URI(url);
            return UuidUtils.shortUuid(uri);
        } catch (URISyntaxException ignore) {
            // never happens
            return null;
        }
    }
}
