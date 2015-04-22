package com.github.longkerdandy.evo.service.sms;

import com.github.longkerdandy.evo.service.sms.mq.SmsLegacyConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * SMS Service
 */
public class SmsService {

    private static final String ZK_HOST = "192.168.253.68:2181";

    public static void main(String args[]) throws Exception {
        // start mq consumer
        Properties props = new Properties();
        props.put("zookeeper.connect", ZK_HOST);
        props.put("group.id", "sms");
        SmsLegacyConsumer consumer = new SmsLegacyConsumer(props, 1);
    }
}
