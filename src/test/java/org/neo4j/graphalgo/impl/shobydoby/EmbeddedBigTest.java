package org.neo4j.graphalgo.impl.shobydoby;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphalgo.impl.shobydoby.util.DbWarmer;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import static org.junit.Assert.*;

public class EmbeddedBigTest {

    private static GraphDatabaseService db;

    @BeforeClass
    public static void createDatabase() {
        db = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/neo4j-big-db");

        try (Transaction tx = db.beginTx()) {
            int graphSize = DbWarmer.warmUpDatabase(db);
            System.out.println("graph size: " + graphSize);
        }
    }

    @Test public void testTripsFromZeelimToMenfate() {
        long zeelimId = 492656101;
        long menfateId = 1534106444;

        assertTrue(true);
    }
}