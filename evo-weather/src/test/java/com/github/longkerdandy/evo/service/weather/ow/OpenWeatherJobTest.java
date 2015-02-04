package com.github.longkerdandy.evo.service.weather.ow;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * OpenWeatherJob Test
 */
public class OpenWeatherJobTest {

    @Test
    public void urlTest() {
        OpenWeatherJob ow = new OpenWeatherJob();
        // single area
        String date = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String url = ow.getUrl("101010100", date);
        assert StringUtils.isNotBlank(url);
        // multiple area
        date = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        url = ow.getUrl("101010100|101020100", date);
        assert StringUtils.isNotBlank(url);
    }
}
