package org.neo4j.graphalgo.impl.shobydoby;

import junit.framework.TestCase;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.path.Dijkstra;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.traversal.InitialBranchState;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 6/22/14.
 */
public class EmbeddedTest extends TestCase {
    GraphDatabaseService db;
    private int startNodeId = 274766;
//    private int endNodeId = 9350;
    private int endNodeId = 2002;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder("/tmp/neo4j-db")
                .newGraphDatabase();

        try (Transaction tx = db.beginTx()) {
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

            System.out.println("graph size: " + i);
        }
    }

    void printPath(Node startNode, Node endNode, WeightedPath path)
    {
        if (path == null)
        {
            System.out.println("no path found");
            return;
        }

        System.out.println("PATH:");
        System.out.println(path);
        System.out.println("WEIGHT: " + path.weight());
        System.out.println();

        List<Node> nodesList = new ArrayList<>();

        Node nodeToInsert = null;

        for (PropertyContainer node : path){

            if (node instanceof Node){

                if (node.hasProperty("node_osm_id")){
                    nodeToInsert = (Node) node;
                } else {
                    nodeToInsert = ((Node)node).getSingleRelationship(StatefulEdgeAvailabilityPathExpander.Relations.NODE, Direction.BOTH).getEndNode();
                }

                if (!nodesList.contains(nodeToInsert)){
                    nodesList.add(nodeToInsert);
                }
            }
        }

        System.out.println("id");
        for (Node node : nodesList){
            System.out.println(node.getId());
        }

        System.out.println("--");
        System.out.println("lat");
        for (Node node : nodesList){
            System.out.println(node.getProperty("lat"));
        }

        System.out.println("--");
        System.out.println("lon");

        for (Node node : nodesList){
            System.out.println(node.getProperty("lon"));
        }

        System.out.println("--");

    }

    public void testPerformance() throws Exception
    {
        StopWatch stopWatch = new StopWatch();

        try (Transaction tx = db.beginTx())
        {
            Node nodeA = db.getNodeById(3681);
            Node nodeB = db.getNodeById(13130);
            stopWatch.start();
            WeightedPath availablePath = new GraphAvailabilityBlocker(2).tryBlock(0.0, nodeA, nodeB);
            stopWatch.stop();
            tx.failure();

            int i = 0;
            for (Relationship r : availablePath.relationships()) {
                i++;
            }

            System.out.println(i + " relations");
        }

        long timeTaken = stopWatch.getTime();
        assertTrue("this test should be less than 1000ms!!!! it took " + timeTaken, timeTaken < 1000);
    }
}
