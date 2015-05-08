package com.github.longkerdandy.evo.aerospike;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.async.AsyncClient;
import com.aerospike.client.async.AsyncClientPolicy;
import com.aerospike.client.listener.RecordListener;

import java.util.List;
import java.util.Map;

/**
 * Aerospike Database Asynchronous Access Layer
 */
public class AerospikeAsyncStorage {

    /**
     * The Aerospike Java Client library provides an asynchronous API via the AsyncClient class.
     * Operations executed using the AsyncClient places commands on a queue and returns control to the application,
     * allowing an executor to process the commands in an asynchronous manner.
     * <p>
     * The AsyncClient instance is thread-safe and can be used concurrently.
     * A separate thread uses a pool of non-blocking sockets to send the command,
     * process the reply, and notify the caller with the result.
     * AsyncClient uses less threads and makes more efficient use of threads than the synchronous client.
     * The downside of using AsyncClient is the programming model is difficult to implement, debug and maintain.
     */
    protected final AsyncClient aac;

    /**
     * Constructor
     * <p>
     * Initialize Aerospike asynchronous client with suitable hosts to seed the cluster map.
     * The client policy is used to set defaults and size internal data structures.
     * For each host connection that succeeds, the client will:
     * <p>
     * - Add host to the cluster map <br>
     * - Request host's list of other nodes in cluster <br>
     * - Add these nodes to cluster map <br>
     * <p>
     * In most cases, only one host is necessary to seed the cluster. The remaining hosts
     * are added as future seeds in case of a complete network failure.
     * <p>
     * If one connection succeeds, the client is ready to process database requests.
     * If all connections fail and the policy's failIfNotConnected is true, a connection
     * exception will be thrown. Otherwise, the cluster will remain in a disconnected state
     * until the server is activated.
     *
     * @param policy client configuration parameters, pass in null for defaults
     * @param hosts  array of potential hosts to seed the cluster
     * @throws AerospikeException if all host connections fail
     */
    public AerospikeAsyncStorage(AsyncClientPolicy policy, Host[] hosts) {
        this.aac = new AsyncClient(policy, hosts);
    }

    /**
     * Close & Cleanup
     * <p>
     * When all transactions are finished and the application is ready to have a clean shutdown,
     * call the close() method to remove the resources held by the AerospikeClient object.
     * The AerospikeAsyncStorage object is no longer usable once close has been called.
     */
    public void close() {
        this.aac.close();
    }

    /**
     * Get device by id and deal with the record in listener
     *
     * @param deviceId Device Id
     */
    public void getDeviceById(String deviceId, RecordListener listener) {
        Key kd = new Key(Scheme.NS_EVO, Scheme.SET_DEVICES, deviceId);
        this.aac.get(null, listener, kd);
    }

    /**
     * Has ownership?
     */
    protected boolean hasOwn(List<Map<String, Object>> own, String userId, String deviceId, int min, int max) {
        if (own != null) {
            for (Map<String, Object> m : own) {
                if (m.get(Scheme.OWN_USER).equals(userId) && m.get(Scheme.OWN_DEVICE).equals(deviceId)
                        && (long) m.getOrDefault(Scheme.OWN_PERMISSION, 0) >= min && (long) m.getOrDefault(Scheme.OWN_PERMISSION, 0) <= max) {
                    return true;
                }
            }
        }
        return false;
    }
}
