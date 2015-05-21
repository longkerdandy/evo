package com.github.longkerdandy.evo.http.util;

import com.github.longkerdandy.evo.api.util.UuidUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Token Utils
 */
public class TokenUtils {

    private TokenUtils() {
    }

    /**
     * Generate short version UUID token for user
     *
     * @param userId User Id
     * @return Token
     */
    public static String newToken(String userId) {
        if (StringUtils.isBlank(userId)) return null;

        try {
            URI uri = new URI("https://github.com/longkerdandy/evo/token?userId=" + userId);
            return UuidUtils.shortUuid(uri);
        } catch (URISyntaxException ignore) {
            // never happens
            return null;
        }
    }
}
