package com.github.longkerdandy.evo.service.weather.util;

import com.github.longkerdandy.evo.api.util.UuidUtils;
import com.github.longkerdandy.evo.service.weather.desc.Description;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Id Utils Test
 */
public class IdUtilsTest {

    @Test
    public void descIdTest() throws URISyntaxException {
        String url = "https://github.com/longkerdandy/evo/weather/desc?version=1.0";
        URI uri = new URI(url);
        String uuid = UuidUtils.shortUuid(uri);
        assert Description.ID.equals(uuid);
    }
}
