package com.github.longkerdandy.evo.api.mq;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;

import java.util.Properties;

/**
 * Message Queue Admin Tools
 */
@SuppressWarnings("unused")
public class AdminTools {

    private ZkClient zkClient;

    /**
     * Constructor
     *
     * @param zkHosts           ZooKeeper hosts
     * @param sessionTimeout    Session timeout in milliseconds
     * @param connectionTimeout Connection timeout in milliseconds
     */
    public AdminTools(String zkHosts, int sessionTimeout, int connectionTimeout) {
        this.zkClient = new ZkClient(zkHosts, sessionTimeout, connectionTimeout, ZKStringSerializer$.MODULE$);
    }

    /**
     * Is topic exist?
     *
     * @param topicName Topic name
     * @return True if topic exists
     */
    public boolean isTopicExist(String topicName) {
        return AdminUtils.topicExists(this.zkClient, topicName);
    }

    /**
     * Create a new topic
     *
     * @param topicName         Topic name
     * @param numPartitions     Number of partitions
     * @param replicationFactor Replication factor
     * @param topicConfig       Topic config
     */
    public void createTopic(String topicName, int numPartitions, int replicationFactor, Properties topicConfig) {
        AdminUtils.createTopic(this.zkClient, topicName, numPartitions, replicationFactor, topicConfig);
    }

    /**
     * Delete a topic
     *
     * @param topicName Topic name
     */
    public void deleteTopic(String topicName) {
        AdminUtils.deleteTopic(this.zkClient, topicName);
    }

    /**
     * Close admin tools
     *
     */
    public void close() {
        this.zkClient.close();
    }
}
