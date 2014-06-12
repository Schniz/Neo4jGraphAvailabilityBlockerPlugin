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

import common.Neo4jAlgoTestCase;
import common.SimpleGraphBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.path.Dijkstra;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.InitialBranchState;

public class StatefulEdgeAvailabilityPathExpanderTest extends Neo4jAlgoTestCase {
    @Test
    public void AndersonTest1() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );
        Node nodeE = graph.makeNode( "E" );

        graph.makeEdge("A", "B", "length", 1, "availability", new double[0]);
        graph.makeEdge("B", "C", "length", 3, "availability", new double[0]);
        graph.makeEdge("D", "C", "length", 1, "availability", new double[] { 1, 2 });
        graph.makeEdge("E", "D", "length", 1, "availability", new double[0]);
        graph.makeEdge("A", "E", "length", 1, "availability", new double[0]);
        graph.makeEdge("E", "C", "length", 1, "availability", new double[]{2, 4});

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<>( 0d, 0d ),
                CommonEvaluators.doubleCostEvaluator("length") );

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

        graph.makeEdge("A", "B", "length", 1, "availability", new double[0]);
        graph.makeEdge("B", "C", "length", 3, "availability", new double[0]);
        graph.makeEdge("D", "C", "length", 1, "availability", new double[] { 1, 3 });
        graph.makeEdge("E", "D", "length", 1, "availability", new double[0]);
        graph.makeEdge("A", "E", "length", 1, "availability", new double[0]);
        graph.makeEdge("E", "C", "length", 1, "availability", new double[] { 2, 4 });

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "length" ) );

        assertPath( algo.findSinglePath( nodeA, nodeC ), nodeA, nodeB, nodeC );
    }

    @Test
    public void AndersonTest3() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );

        graph.makeEdge("A", "B", "length", 1, "availability", new double[0]);
        graph.makeEdge("D", "B", "length", 1, "availability", new double[] { 0, 2 });
        graph.makeEdge("A", "C", "length", 1, "availability", new double[0]);
        graph.makeEdge("C", "D", "length", 2, "availability", new double[0]);

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "length" ) );

        assertPath( algo.findSinglePath( nodeA, nodeD ), nodeA, nodeC, nodeD );
    }

    @Test
    public void AndersonTest4() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );

        graph.makeEdge("A", "B", "length", 1, "availability", new double[0]);
        graph.makeEdge("D", "B", "length", 1, "availability", new double[] { 4, 5 });
        graph.makeEdge("A", "C", "length", 1, "availability", new double[0]);
        graph.makeEdge("C", "D", "length", 2, "availability", new double[0]);

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "length" ) );

        assertPath( algo.findSinglePath( nodeA, nodeD ), nodeA, nodeB, nodeD );
    }

    @Test
    public void AndersonTest5() throws Exception
    {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );

        graph.makeEdge("A", "B", "length", 1, "availability", new double[0]);
        graph.makeEdge("D", "B", "length", 1, "availability", new double[] { 3, 5 });
        graph.makeEdge("A", "C", "length", 1, "availability", new double[0]);
        graph.makeEdge("C", "D", "length", 2, "availability", new double[0]);

        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(2);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "length" ) );

        assertPath( algo.findSinglePath( nodeA, nodeD ), nodeA, nodeC, nodeD );
    }

    @Test
    public void testCannotStartRoadButItWorksWhenItIsBeingDelayed() throws Exception {
        Node nodeA = graph.makeNode( "A" );
        Node nodeB = graph.makeNode( "B" );
        Node nodeC = graph.makeNode( "C" );
        Node nodeD = graph.makeNode( "D" );

        graph.makeEdge("A", "B", "length", 1, "availability", new double[] { 0, 1 });
        graph.makeEdge("D", "B", "length", 1, "availability", new double[] { 0, 1, 5, 7 });
        graph.makeEdge("A", "C", "length", 1, "availability", new double[] { 0, 1 });
        graph.makeEdge("C", "D", "length", 2, "availability", new double[] { 0, 1 });

        int trainLength = 2;
        PathExpander<Double> myPathExpander = new StatefulEdgeAvailabilityPathExpander(trainLength);

        Dijkstra algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 0.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "length" ) );

        Assert.assertNull( "There should be no path", algo.findSinglePath( nodeA, nodeD ) );

        algo = new Dijkstra( myPathExpander,
                new InitialBranchState.State<Double>( 2.0, 0.0 ),
                CommonEvaluators.doubleCostEvaluator( "length" ) );

        WeightedPath singlePath = algo.findSinglePath(nodeA, nodeD);
        assertPath(singlePath, nodeA, nodeC, nodeD);
    }

    private void createTree(int size, Node baseNode) {
        Node nodeA = graph.makeNode( "" );
    }
}