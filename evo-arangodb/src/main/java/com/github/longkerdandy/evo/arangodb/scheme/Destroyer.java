package com.github.longkerdandy.evo.arangodb.scheme;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;

import static com.github.longkerdandy.evo.api.scheme.Scheme.GRAPH_IOT_RELATION;

/**
 * Drop ArangoDB Scheme (Graph, Vertex, Edge, Index)
 */
@SuppressWarnings("unused")
public class Destroyer {

    public static void main(String[] args) throws ArangoException {
        // Initialize configure
        ArangoConfigure configure = new ArangoConfigure();
        configure.init();
        // Create Driver (this instance is thread-safe)
        ArangoDriver arango = new ArangoDriver(configure);
        // Drop Graph
        arango.deleteGraph(GRAPH_IOT_RELATION, true);
    }
}
