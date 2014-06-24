package org.neo4j.graphalgo.impl.shobydoby.util;

import org.neo4j.graphalgo.impl.shobydoby.EmbeddedTest;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * Created by schniz on 6/24/14.
 */
public class DbWarmer {
    public static int warmUpDatabase(GraphDatabaseService db) {
        int i = 0;
        Iterable<Node> allNodes = GlobalGraphOperations.at(db).getAllNodes();
        for (Node node : allNodes) {
            if (node.hasProperty("node_osm_id")) {
                i++;
            }

            for (Relationship r : node.getRelationships()) {
                Iterable<String> propertyKeys = r.getPropertyKeys();
                for (String propertyKey : propertyKeys) {
                    r.getProperty(propertyKey);
                }
            }

            for (String propertyKey : node.getPropertyKeys()) {
                node.getProperty(propertyKey);
            }
        }

        return i;
    }
}
