package com.github.longkerdandy.evo.api.protocol;

import com.github.longkerdandy.evo.api.util.UuidUtils;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Protocol Constant Test
 */
public class ConstTest {

    @Test
    public void evoPlatformTest() throws URISyntaxException {
        String url = "https://github.com/longkerdandy/evo";
        URI uri = new URI(url);
        String uuid = UuidUtils.shortUuid(uri);
        assert Const.PLATFORM_ID.equals(uuid);
    }
}
