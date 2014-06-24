/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.graphalgo.impl.shobydoby;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphalgo.impl.shobydoby.util.AlgoTestCase;
import org.neo4j.graphdb.Node;

public class GraphAvailabilityBlockerTest extends AlgoTestCase {
    @Test
    public void testTryBlock() throws Exception {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );

        createRelation(nodeA, nodeB, "distance", 1d, "availability", new double[] { 0, 1 });
        createRelation(nodeD, nodeB, "distance", 1d, "availability", new double[] { 0, 1, 5, 7 });
        createRelation(nodeA, nodeC, "distance", 1d, "availability", new double[] { 0, 1 });
        createRelation(nodeC, nodeD, "distance", 2d, "availability", new double[] { 0, 1 });

        GraphAvailabilityBlocker graphBlocker = new GraphAvailabilityBlocker(2d);

        Assert.assertNull("There should be no path", graphBlocker.tryBlock(0d, nodeA, nodeD));
        assertPath(graphBlocker.tryBlock(2d, nodeA, nodeD), nodeA, nodeC, nodeD);
        Assert.assertNull( "There should be no path cause we blocked it", graphBlocker.tryBlock(2d, nodeA, nodeD) );
    }
}