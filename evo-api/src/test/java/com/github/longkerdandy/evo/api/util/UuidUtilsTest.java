package com.github.longkerdandy.evo.api.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * UuidUtils Test
 */
public class UuidUtilsTest {

    @Test
    public void shortUuidTest() throws URISyntaxException {
        String url = "http://www.meethue.com/bridge?mac=44-39-C4-50-E9-03";
        URI uri = new URI(url);
        String uuid = UuidUtils.shortUuid(uri);
        assert StringUtils.isNotBlank(uuid);
        assert uuid.equals(UuidUtils.shortUuid(uri));
        assert uuid.equals(UuidUtils.shortUuid(uri));
    }
}
