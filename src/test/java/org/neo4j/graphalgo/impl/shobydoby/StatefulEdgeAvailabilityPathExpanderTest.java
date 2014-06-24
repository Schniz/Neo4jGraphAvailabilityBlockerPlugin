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
import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.path.Dijkstra;
import org.neo4j.graphalgo.impl.shobydoby.util.AlgoTestCase;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.traversal.InitialBranchState;

public class StatefulEdgeAvailabilityPathExpanderTest extends AlgoTestCase {
    @Test
    public void AndersonTest1() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );
        Node nodeE = graph.makeNode( "E" );

        createRelation(nodeA, nodeB, "distance", 1, "availability", new double[0]);
        createRelation(nodeB, nodeC, "distance", 100, "availability", new double[0]);
        createRelation(nodeD, nodeC, "distance", 1, "availability", new double[] { 1, 1.9 });
        createRelation(nodeE, nodeD, "distance", 1, "availability", new double[0]);
        createRelation(nodeA, nodeE, "distance", 1, "availability", new double[0]);
        createRelation(nodeE, nodeC, "distance", 1, "availability", new double[]{2, 4});

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2d);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<>( 0d, 0d ),
                CommonEvaluators.doubleCostEvaluator("distance") );

        assertPath( algo.findSinglePath( nodeA, nodeC ), nodeA, nodeE, nodeD, nodeC );
    }

    @Test
    public void AndersonTest2() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );
        Node nodeE = graph.makeNode( "E" );

        createRelation(nodeA, nodeB, "distance", 1, "availability", new double[0]);
        createRelation(nodeB, nodeC, "distance", 3, "availability", new double[0]);
        createRelation(nodeD, nodeC, "distance", 1, "availability", new double[] { 1, 3 });
        createRelation(nodeE, nodeD, "distance", 1, "availability", new double[0]);
        createRelation(nodeA, nodeE, "distance", 1, "availability", new double[0]);
        createRelation(nodeE, nodeC, "distance", 1, "availability", new double[] { 2, 4 });

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2d);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "distance" ) );

        assertPath( algo.findSinglePath( nodeA, nodeC ), nodeA, nodeB, nodeC );
    }

    @Test
    public void AndersonTest3() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );

        createRelation(nodeA, nodeB, "distance", 1, "availability", new double[0]);
        createRelation(nodeD, nodeB, "distance", 1, "availability", new double[] { 0, 2 });
        createRelation(nodeA, nodeC, "distance", 1, "availability", new double[0]);
        createRelation(nodeC, nodeD, "distance", 2, "availability", new double[0]);

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2d);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "distance" ) );

        assertPath( algo.findSinglePath( nodeA, nodeD ), nodeA, nodeC, nodeD );
    }

    @Test
    public void AndersonTest4() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );


        createRelation(nodeA, nodeB, "distance", 1, "availability", new double[0]);
        createRelation(nodeD, nodeB, "distance", 1, "availability", new double[] { 4, 5 });
        createRelation(nodeA, nodeC, "distance", 1, "availability", new double[0]);
        createRelation(nodeC, nodeD, "distance", 2, "availability", new double[0]);

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2.0);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "distance" ) );

        assertPath( algo.findSinglePath( nodeA, nodeD ), nodeA, nodeB, nodeD );
    }

    @Test
    public void AndersonTest5() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );

        createRelation(nodeA, nodeB, "distance", 1, "availability", new double[0]);
        createRelation(nodeD, nodeB, "distance", 1, "availability", new double[] { 3, 5 });
        createRelation(nodeA, nodeC, "distance", 1, "availability", new double[0]);
        createRelation(nodeC, nodeD, "distance", 2, "availability", new double[0]);

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2d);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "distance" ) );

        assertPath( algo.findSinglePath( nodeA, nodeD ), nodeA, nodeC, nodeD );
    }

    @Test
    public void testCannotStartRoadButItWorksWhenItIsBeingDelayed() throws Exception {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );
        
        createRelation(nodeA, nodeB, "distance", 1, "availability", new double[] { 0, 1 });
        createRelation(nodeD, nodeB, "distance", 1, "availability", new double[] { 0, 1, 5, 7 });
        createRelation(nodeA, nodeC, "distance", 1, "availability", new double[] { 0, 1 });
        createRelation(nodeC, nodeD, "distance", 2, "availability", new double[] { 0, 1 });

        int traindistance = 2;
        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander((double)traindistance);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "distance" ) );

        Assert.assertNull( "There should be no path", algo.findSinglePath( nodeA, nodeD ) );

        algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 2.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "distance" ) );

        WeightedPath singlePath = algo.findSinglePath(nodeA, nodeD);
        assertPath(singlePath, nodeA, nodeC, nodeD);
    }

    private void createTree(int size, Node baseNode) {
        Node nodeA = graph.makeNode( "" );
    }
}
