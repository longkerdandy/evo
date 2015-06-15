package com.github.longkerdandy.evo.service.sms;

import com.github.longkerdandy.evo.api.mq.LegacyConsumer;
import com.github.longkerdandy.evo.api.mq.Topics;
import com.github.longkerdandy.evo.service.sms.mq.SmsConsumerWorker;
import com.github.longkerdandy.evo.service.sms.mq.SmsConsumerWorkerFactory;
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

        // create message queue consumer
        SmsConsumerWorkerFactory factory = new SmsConsumerWorkerFactory();
        Properties props = new Properties();
        props.put("zookeeper.connect", config.getString("mq.zk.hosts"));
        props.put("group.id", Topics.SMS);
        LegacyConsumer<SmsConsumerWorker> consumer = new LegacyConsumer<>(factory, Topics.SMS, props, config.getInt("mq.topic.sms.workerThreads"));
    }
}
