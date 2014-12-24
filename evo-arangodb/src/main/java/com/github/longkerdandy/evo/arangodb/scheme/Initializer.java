package com.github.longkerdandy.evo.arangodb.scheme;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.entity.EdgeDefinitionEntity;
import com.arangodb.entity.GraphEntity;

import java.util.ArrayList;
import java.util.List;

import static com.github.longkerdandy.evo.api.scheme.Scheme.*;

/**
 * Create ArangoDB Scheme (Graph, Vertex, Edge, Index)
 */
@SuppressWarnings("unused")
public class Initializer {

    public static void main(String[] args) throws ArangoException {
        // Initialize configure
        ArangoConfigure configure = new ArangoConfigure();
        configure.init();
        // Create Driver (this instance is thread-safe)
        ArangoDriver arango = new ArangoDriver(configure);
        // Create Graph
        createRelationGraph(arango);
    }

    protected static void createRelationGraph(ArangoDriver arango) throws ArangoException {
        // edge definitions
        List<EdgeDefinitionEntity> edges = new ArrayList<>();

        // edge: user follow device
        // from: users collection
        // to:   devices collection
        EdgeDefinitionEntity ufd = new EdgeDefinitionEntity();
        ufd.setCollection(EDGE_USER_FOLLOW_DEVICE);
        List<String> ufdFrom = new ArrayList<>();
        ufdFrom.add(COLLECTION_USERS);
        ufd.setFrom(ufdFrom);
        List<String> ufdTo = new ArrayList<>();
        ufdTo.add(COLLECTION_DEVICES);
        ufd.setTo(ufdTo);
        edges.add(ufd);

        // edge: device register user
        // from: devices collection
        // to:   users collection
        EdgeDefinitionEntity dru = new EdgeDefinitionEntity();
        dru.setCollection(EDGE_DEVICE_REGISTER_USER);
        List<String> druFrom = new ArrayList<>();
        druFrom.add(COLLECTION_DEVICES);
        dru.setFrom(druFrom);
        List<String> druTo = new ArrayList<>();
        druTo.add(COLLECTION_USERS);
        dru.setTo(druTo);
        edges.add(dru);

        // orphan collections
        List<String> orphans = new ArrayList<>();
        orphans.add(COLLECTION_USER_TOKEN);

        // create relation graph
        GraphEntity graph = arango.createGraph(GRAPH_IOT_RELATION, edges, orphans, true);

        // create index
        // user index
        arango.createHashIndex(COLLECTION_USERS, true, U_EMAIL);
        arango.createHashIndex(COLLECTION_USERS, true, U_MOBILE);
        // user device edge index
        arango.createSkipListIndex(EDGE_USER_FOLLOW_DEVICE, false, U_F_D_PERMISSION);
    }
}
