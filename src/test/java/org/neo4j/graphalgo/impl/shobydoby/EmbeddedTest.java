package org.neo4j.graphalgo.impl.shobydoby;

import junit.framework.TestCase;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.core.TestNeo4j;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 6/22/14.
 */
public class EmbeddedTest extends TestCase {
    GraphDatabaseService db;
    private int startNodeId = 2002;
    private int endNodeId = 9350;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/neo4j-db");

    }

    void printPath(Node startNode, Node endNode, Path path)
    {
        if (path == null)
        {
            System.out.println("no path found");
            return;
        }

        System.out.println("PATH:");
        System.out.println(path);

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
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        try (Transaction tx = db.beginTx())
        {
            Node nodeA;
            Node nodeB;
            nodeA = db.getNodeById(startNodeId);
            nodeB = db.getNodeById(endNodeId);


            for(int i = 0; i < 5; i++)
            {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                Path availablePath = new GraphAvailabilityBlocker(2).tryBlock(0.0, nodeA, nodeB);
                stopWatch.stop();

                System.out.println(i + ": " + stopWatch.getTime() + "ms");
                max = Math.max(max, stopWatch.getTime());
                min = Math.min(min, stopWatch.getTime());
                printPath(nodeA, nodeB, availablePath);
            }


            System.out.println("Minimum: " + min + "ms, max: " + max + "ms");

            assertTrue("minimum time suppose to be below 1000ms. got " + min + "ms", min < 1000);

            tx.failure();
        }
    }
}
