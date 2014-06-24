package org.neo4j.graphalgo.impl.shobydoby.util;

import common.Neo4jAlgoTestCase;
import org.neo4j.graphalgo.impl.shobydoby.StatefulEdgeAvailabilityPathExpander;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Created by Gal Schlezinger on 6/24/14.
 */
public class AlgoTestCase extends Neo4jAlgoTestCase {
    protected void createRelation(Node from, Node to, Object... keyValuePair) {
        Relationship relationshipTo = from.createRelationshipTo(to, StatefulEdgeAvailabilityPathExpander.Relations.NEXT);

        for (int i = 1; i <= keyValuePair.length; i+=2) {
            String key = keyValuePair[i - 1].toString();
            Object value = keyValuePair[i];
            relationshipTo.setProperty(key, value);
        }
    }
}
