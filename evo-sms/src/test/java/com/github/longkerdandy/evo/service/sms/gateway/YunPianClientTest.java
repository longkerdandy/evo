package com.github.longkerdandy.evo.service.sms.gateway;

import org.junit.Test;

/**
 * YunPianClient Test
 */
public class YunPianClientTest {

    @Test
    public void getUserInfoTest() {
        String userInfo = YunPianClient.getUserInfo();
        assert userInfo != null;
    }

    @Test
    public void sendSmsTest() {
        YunPianClient.sendSms("18616862881", "【及时云】您的验证码是0000");
    }
}
