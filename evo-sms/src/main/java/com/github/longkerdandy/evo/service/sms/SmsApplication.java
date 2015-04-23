package com.github.longkerdandy.evo.service.sms;

import com.github.longkerdandy.evo.service.sms.mq.SmsLegacyConsumer;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.Properties;

/**
 * SMS Service
 */
public class SmsApplication {

    public static void main(String args[]) throws Exception {
        // load config
        String f = args.length >= 1 ? args[0] : "config/sms.properties";
        PropertiesConfiguration config = new PropertiesConfiguration(f);

        // start mq consumer
        Properties props = new Properties();
        props.put("zookeeper.connect", config.getString("mq.zk.hosts"));
        props.put("group.id", "sms");
        SmsLegacyConsumer consumer = new SmsLegacyConsumer(props, 1);
    }
}
