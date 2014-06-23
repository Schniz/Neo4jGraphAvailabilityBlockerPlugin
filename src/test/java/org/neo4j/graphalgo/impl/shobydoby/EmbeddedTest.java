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
//                .setConfig( GraphDatabaseSettings.relationshipstore_mapped_memory_size, "300M" )
//                .setConfig( GraphDatabaseSettings.nodestore_mapped_memory_size, "300M" )
//                .setConfig( GraphDatabaseSettings.nodestore_propertystore_mapped_memory_size, "300M")
//                .setConfig(GraphDatabaseSettings.cache_type, "strong")
                .newGraphDatabase();

        try (Transaction tx = db.beginTx()) {
            Iterable<Node> allNodes = GlobalGraphOperations.at(db).getAllNodes();
            for (Node node : allNodes) {
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
//        Thread.sleep(10000);

        try (Transaction tx = db.beginTx())
        {
            new Dijkstra(new StatefulEdgeAvailabilityPathExpander(2), new InitialBranchState.State<>(0.0, 0.0), CommonEvaluators.doubleCostEvaluator("distance")).findSinglePath(db.getNodeById(3681), db.getNodeById(13130));
            System.out.println("YES");

            Node nodeA;
            Node nodeB;

            for (double i = 0.0; i < 3.0; i += 1.0) {
                System.out.println(i);
//                findAndBlockAvailablePaths(7491, 89888, i);
                findAndBlockAvailablePaths(3681, 455030, i);
                findAndBlockAvailablePaths(3681, 13130, i);
//                findAndBlockAvailablePaths(startNodeId, endNodeId, i);
            }
            tx.failure();
        }

//        System.out.println("click");
//        Thread.sleep(10000);
    }

    private void findAndBlockAvailablePaths(long start, long end, double startRoadHour) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        Node nodeA;
        Node nodeB;
        nodeA = db.getNodeById(start);
        nodeB = db.getNodeById(end);

        for(int i = 0; i < 5; i++)
        {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
//            Dijkstra dijkstra = new Dijkstra(PathExpanders, InitialBranchState.NO_STATE, CommonEvaluators.doubleCostEvaluator("distance"));
//            dijkstra.findSinglePath(nodeA, nodeB);
            WeightedPath availablePath = new GraphAvailabilityBlocker(2).tryBlock(startRoadHour, nodeA, nodeB);
            stopWatch.stop();

            min = Math.min(min, stopWatch.getTime());
            max = Math.max(max, stopWatch.getTime());

            System.out.println(i + ": " + stopWatch.getTime() + "ms. road exists? " + (null != availablePath));
//            printPath(nodeA, nodeB, availablePath);
        }

        System.out.println("Minimum: " + min + "ms, max: " + max + "ms");

    }
}
