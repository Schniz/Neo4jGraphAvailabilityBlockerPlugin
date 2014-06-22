package org.neo4j.graphalgo.impl.shobydoby;

import junit.framework.TestCase;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.core.TestNeo4j;

/**
 * Created by Daniel on 6/22/14.
 */
public class EmbeddedTest extends TestCase {
    GraphDatabaseService db;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = new GraphDatabaseFactory().newEmbeddedDatabase("C:\\neo4j-db");
    }

    public void testPerformance() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Node nodeA = db.getNodeById(2002);
        Node nodeB = db.getNodeById(9350);

        new GraphAvailabilityPlugin().getAvailablePath(nodeA, nodeB, 2, 0.0);

        stopWatch.stop();

        assertTrue(stopWatch.getTime() < 1000);
    }
}
