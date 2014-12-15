package com.github.longkerdandy.evo.arangodb.tools;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.entity.EdgeDefinitionEntity;
import com.arangodb.entity.GraphEntity;

import java.util.ArrayList;
import java.util.List;

import static com.github.longkerdandy.evo.arangodb.Const.*;

/**
 * Create ArangoDB Scheme (Database, Collection, Graph)
 */
public class CreateScheme {

    public static void main(String[] args) throws ArangoException {
        // Initialize configure
        ArangoConfigure configure = new ArangoConfigure();
        configure.init();
        // Create Driver (this instance is thread-safe)
        ArangoDriver arango = new ArangoDriver(configure);

        createRelationGraph(arango);
    }

    protected static void createRelationGraph(ArangoDriver arango) throws ArangoException {
        // edge definitions
        List<EdgeDefinitionEntity> edgeDefs = new ArrayList<>();

        // edge: user -> device
        // from: user collection
        // to:   devices collection
        EdgeDefinitionEntity edgeDef = new EdgeDefinitionEntity();
        edgeDef.setCollection(EDGE_USER_DEVICE);
        List<String> from = new ArrayList<>();
        from.add(COLLECTION_USERS);
        edgeDef.setFrom(from);
        List<String> to = new ArrayList<>();
        to.add(COLLECTION_DEVICES);
        edgeDef.setTo(to);

        edgeDefs.add(edgeDef);

        // orphan collections
        List<String> orphans = new ArrayList<>();

        // create relation graph
        GraphEntity graph = arango.createGraph(GRAPH_IOT_RELATION, edgeDefs, orphans, true);
        assert graph != null;
    }
}
