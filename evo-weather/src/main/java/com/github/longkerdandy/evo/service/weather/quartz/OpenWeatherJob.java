package com.github.longkerdandy.evo.service.weather.quartz;

import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.message.MessageFactory;
import com.github.longkerdandy.evo.api.message.Trigger;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
import com.github.longkerdandy.evo.api.protocol.OverridePolicy;
import com.github.longkerdandy.evo.api.protocol.ProtocolType;
import com.github.longkerdandy.evo.api.util.JsonUtils;
import com.github.longkerdandy.evo.service.weather.desc.Description;
import com.github.longkerdandy.evo.service.weather.entity.Area;
import com.github.longkerdandy.evo.service.weather.entity.Forecast;
import com.github.longkerdandy.evo.service.weather.entity.ForecastResult;
import com.github.longkerdandy.evo.service.weather.tcp.TCPClientHandler;
import com.github.longkerdandy.evo.service.weather.util.IdUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenWeather Client
 * Fetch weather data from http://openweather.weather.com.cn
 */
public class OpenWeatherJob implements Job {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherJob.class);

    // Open Weather Api Key
    private static final String APP_ID = "7cd2cc51b3087e54";
    private static final String PRIVATE_KEY = "8e6a2b_SmartWeatherAPI_71c071e";

    /**
     * Each (and every) time the scheduler executes the job, it creates a new instance of the class before calling its execute(..) method.
     * When the execution is complete, references to the job class instance are dropped, and the instance is then garbage collected.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // The JobDataMap that is found on the JobExecutionContext during job execution serves as a convenience.
        // It is a merge of the JobDataMap found on the JobDetail and the one found on the trigger,
        // with the value in the latter overriding any same-named values in the former.
        // Storing JobDataMap values on a trigger can be useful in the case where you have a job
        // that is stored in the scheduler for regular/repeated use by multiple triggers,
        // yet with each independent triggering, you want to supply the job with different data inputs.
        // As a best practice, the code within the Job.execute( ) method should generally retrieve values from the JobDataMap
        // on found on the JobExecutionContext, rather than directly from the one on the JobDetail.
        JobDataMap jdm = context.getMergedJobDataMap();

        // Area Id should be passed into
        String areaId = jdm.getString("AreaId");
        if (StringUtils.isBlank(areaId)) {
            return;
        }

        // fetch with http client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // forge url
            String date = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
            String url = getUrl(areaId, date);
            // http get
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    logger.warn("Http error when getting weather information from OpenWeather, status code:{}", statusCode);
                }
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity, "UTF-8");
                if (content.equals("data error")) {
                    logger.warn("Data error when getting weather information from OpenWeather");
                } else {
                    // parse json
                    ForecastResult forecast = JsonUtils.ObjectMapper.readValue(content, ForecastResult.class);
                    // send trigger message
                    Map<String, Object> attr = forgeAttributes(forecast);
                    Message<Trigger> trigger = MessageFactory.newTriggerMessage(
                            ProtocolType.TCP_1_0, DeviceType.DEVICE,
                            IdUtils.getWeatherDeviceId(areaId), null,
                            Description.TRIGGER_FORECAST, OverridePolicy.UPDATE_IF_NEWER, attr);
                    TCPClientHandler.getInstance().sendMessage(trigger);
                }
            } catch (ClientProtocolException e) {
                logger.error("Http error when getting weather information from OpenWeather: {}", ExceptionUtils.getMessage(e));
            } catch (IOException e) {
                logger.error("IO error when dealing with weather information: {}", ExceptionUtils.getMessage(e));
            }

            // delay 1 second to avoid flooding OpenWeather's server
            Thread.sleep(1000);
        } catch (Exception ignore) {
        }
    }

    /**
     * Get Key
     *
     * @param areaId Area Id, single are '101010100', multiple area '101010100|101010200'
     * @param date   Date in 'yyyyMMddHHmm' format
     * @return Public Key
     */
    public String getUrl(String areaId, String date) {
        String key = getKey(areaId, date);
        return "http://open.weather.com.cn/data/?areaid=" + areaId + "&type=forecast_f&date=" + date + "&appid=" + APP_ID.substring(0, 6) + "&key=" + key;
    }

    /**
     * Get Key
     */
    protected String getKey(String areaId, String date) {
        try {
            String publicKey = getPublicKey(areaId, date);
            // hmac sha1
            final SecretKeySpec signingKey = new SecretKeySpec(PRIVATE_KEY.getBytes(), "HmacSHA1");
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(publicKey.getBytes());
            // base64 & url escape
            return URLEncoder.encode(Base64.encodeBase64String(rawHmac), "UTF-8");
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException ignore) {
            // never happens
            throw new IllegalArgumentException();
        }
    }

    /**
     * Get Public Key
     */
    protected String getPublicKey(String areaId, String date) {
        return "http://open.weather.com.cn/data/?areaid=" + areaId + "&type=forecast_f&date=" + date + "&appid=" + APP_ID;
    }

    /**
     * Forge attributes from forecast result
     */
    protected Map<String, Object> forgeAttributes(ForecastResult forecast) {
        Map<String, Object> attr = new HashMap<>();
        // area
        Area area = forecast.getArea();
        attr.put(Description.ATTR_AREA_ID, area.getAreaId());
        attr.put(Description.ATTR_AREA_CN, area.getAreaCn());
        attr.put(Description.ATTR_AREA_EN, area.getAreaEn());
        attr.put(Description.ATTR_CITY_CN, area.getCityCn());
        attr.put(Description.ATTR_CITY_EN, area.getCityEn());
        attr.put(Description.ATTR_STATE_CN, area.getStateCn());
        attr.put(Description.ATTR_STATE_EN, area.getStateEn());
        attr.put(Description.ATTR_COUNTRY_CN, area.getCountryCn());
        attr.put(Description.ATTR_COUNTRY_EN, area.getCountryEn());
        // forecast
        List<Forecast> forecasts = forecast.getForecast3Day().getForecast3d();
        int i = 1;
        for (Forecast f : forecasts) {
            switch (i) {
                case 1:
                    attr.put(Description.ATTR_DAY_1_DAY_WEATHER, f.getDayWeather());
                    attr.put(Description.ATTR_DAY_1_NIGHT_WEATHER, f.getNightWeather());
                    attr.put(Description.ATTR_DAY_1_DAY_TEMPERATURE, f.getDayTemp());
                    attr.put(Description.ATTR_DAY_1_NIGHT_TEMPERATURE, f.getNightTemp());
                    attr.put(Description.ATTR_DAY_1_DAY_WIND_DIRECTION, f.getDayWindDirection());
                    attr.put(Description.ATTR_DAY_1_NIGHT_WIND_DIRECTION, f.getNightWindDirection());
                    attr.put(Description.ATTR_DAY_1_DAY_WIND_FORCE, f.getDayWindForce());
                    attr.put(Description.ATTR_DAY_1_NIGHT_WIND_FORCE, f.getNightWindForce());
                    attr.put(Description.ATTR_DAY_1_SUNRISE, f.getSunrise());
                    break;
                case 2:
                    attr.put(Description.ATTR_DAY_2_DAY_WEATHER, f.getDayWeather());
                    attr.put(Description.ATTR_DAY_2_NIGHT_WEATHER, f.getNightWeather());
                    attr.put(Description.ATTR_DAY_2_DAY_TEMPERATURE, f.getDayTemp());
                    attr.put(Description.ATTR_DAY_2_NIGHT_TEMPERATURE, f.getNightTemp());
                    attr.put(Description.ATTR_DAY_2_DAY_WIND_DIRECTION, f.getDayWindDirection());
                    attr.put(Description.ATTR_DAY_2_NIGHT_WIND_DIRECTION, f.getNightWindDirection());
                    attr.put(Description.ATTR_DAY_2_DAY_WIND_FORCE, f.getDayWindForce());
                    attr.put(Description.ATTR_DAY_2_NIGHT_WIND_FORCE, f.getNightWindForce());
                    attr.put(Description.ATTR_DAY_2_SUNRISE, f.getSunrise());
                    break;
                case 3:
                    attr.put(Description.ATTR_DAY_3_DAY_WEATHER, f.getDayWeather());
                    attr.put(Description.ATTR_DAY_3_NIGHT_WEATHER, f.getNightWeather());
                    attr.put(Description.ATTR_DAY_3_DAY_TEMPERATURE, f.getDayTemp());
                    attr.put(Description.ATTR_DAY_3_NIGHT_TEMPERATURE, f.getNightTemp());
                    attr.put(Description.ATTR_DAY_3_DAY_WIND_DIRECTION, f.getDayWindDirection());
                    attr.put(Description.ATTR_DAY_3_NIGHT_WIND_DIRECTION, f.getNightWindDirection());
                    attr.put(Description.ATTR_DAY_3_DAY_WIND_FORCE, f.getDayWindForce());
                    attr.put(Description.ATTR_DAY_3_NIGHT_WIND_FORCE, f.getNightWindForce());
                    attr.put(Description.ATTR_DAY_3_SUNRISE, f.getSunrise());
                    break;
            }
            i++;
        }
        return attr;
    }
}
