package com.github.longkerdandy.evo.service.sms.gateway;

import com.github.longkerdandy.evo.api.util.JsonUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SMS Gateway client for YunPian (http://www.yunpian.com)
 */
public class YunPianClient {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(YunPianClient.class);

    private static String API_KEY = "e54e48cee2d10f87a248e927787d59d5";
    private static String BASE_URI = "http://yunpian.com";
    private static String VERSION = "v1";
    private static String ENCODING = "UTF-8";
    private static String URI_GET_USER_INFO = BASE_URI + "/" + VERSION + "/user/get.json" + "?apikey=" + API_KEY;
    private static String URI_SEND_SMS = BASE_URI + "/" + VERSION + "/sms/send.json";

    private YunPianClient() {
    }

    /**
     * Get user information
     *
     * @return User Information
     */
    public static String getUserInfo() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(URI_GET_USER_INFO);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    logger.warn("Http error when getting user information from YunPian, status code:{}", statusCode);
                }
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, ENCODING);
            }
        } catch (IOException e) {
            logger.error("Http error when getting user information from YunPian: {}", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    /**
     * Send sms to specific mobile
     *
     * @param mobile Mobile Number
     * @param text   Sms Message
     */
    public static void sendSms(String mobile, String text) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(URI_SEND_SMS);

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("apikey", API_KEY));
            nameValuePairs.add(new BasicNameValuePair("text", text));
            nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, ENCODING));
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    logger.warn("Http error when getting user information from YunPian, status code:{}", statusCode);
                }
                HttpEntity entity = response.getEntity();
                YunPianResult result = JsonUtils.ObjectMapper.readValue(EntityUtils.toString(entity, ENCODING), YunPianResult.class);
                if ("0".equals(result.getCode())) {
                    logger.trace("Successful send sms {} to mobile {}", text, mobile);
                } else {
                    logger.debug("Failed to send sms {} to mobile {}, error: {} {}", text, mobile, result.getCode(), result.getMsg());
                }
            }
        } catch (IOException e) {
            logger.warn("Http error when getting user information from YunPian: {}", ExceptionUtils.getMessage(e));
        }
    }
}
