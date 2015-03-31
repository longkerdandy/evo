package com.github.longkerdandy.evo.service.sms;

import com.github.longkerdandy.evo.service.sms.mq.SmsLegacyConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * SMS Service
 */
public class SmsService {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    private static final String ZK_HOST = "172.16.1.227:2181";

    public static void main(String args[]) throws Exception {
        // start mq consumer
        Properties props = new Properties();
        props.put("zookeeper.connect", ZK_HOST);
        props.put("group.id", "sms");
        SmsLegacyConsumer consumer = new SmsLegacyConsumer(props, 1);
    }
}
