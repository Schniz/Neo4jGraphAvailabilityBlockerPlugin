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

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.path.Dijkstra;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.InitialBranchState;

/**
 * Created by Gal Schlezinger on 6/12/14.
 *
 * Blocks a road on the graph if its available.
 */
public class GraphAvailabilityBlocker {
    int trainLength;
    private PathExpander<Double> pathExpander;

    public GraphAvailabilityBlocker(int trainLength) {
        this.trainLength = trainLength;
        pathExpander = new StatefulEdgeAvailabilityPathExpander(trainLength);
    }


    /**
     * Searches and blocks the road in the correct hours
     * @param startRoadHour hour to start the road at
     * @param from Node to start the search
     * @param to Node to end the search
     * @return null if a path cannot be found; otherwise, returns the path
     */
    public WeightedPath tryBlock(double startRoadHour, Node from, Node to) {
        WeightedPath singlePath = getAvailablePath(startRoadHour, from, to);

        if (singlePath != null) {
            blockPath(startRoadHour, singlePath);
        }

        return singlePath;
    }

    public WeightedPath getAvailablePath(double startRoadHour, Node from, Node to) {
        PathFinder<WeightedPath> pathFinder = createPathFinder(startRoadHour);
        return pathFinder.findSinglePath( from, to );
    }

    /**
     * Creates a pathfinder for searching the path
     * @param startRoadHour the initial hour
     * @return a path finder
     */
    private PathFinder<WeightedPath> createPathFinder(double startRoadHour) {
        return new Dijkstra( pathExpander,
                new InitialBranchState.State<>( startRoadHour, 0.0 ),
                CommonEvaluators.doubleCostEvaluator("distance") );
    }

    /**
     * Blocks the path
     * @param startRoadHour
     * @param pathToBlock Path to block
     */
    private void blockPath(double startRoadHour, WeightedPath pathToBlock) {
        double cost = startRoadHour;

        for (Relationship rel : pathToBlock.relationships()) {
            double length = getLength(rel);
            double[] availabilities = getAvailabilities(rel);
            EdgeAvailability edgeAvailability = EdgeAvailability.fromDoubleArray(availabilities);

            TimeSpan timeSpan = new TimeSpan().setFrom(cost).setTo(cost + length);
            edgeAvailability.addBlock(timeSpan);
            rel.setProperty("availability", edgeAvailability.toDoubleArray());

            cost += length;
        }
    }

    private double[] getAvailabilities(Relationship objRelation) {
        Object availabilities = objRelation.getProperty("availability", new double[0]);

        return (double[]) availabilities;
    }

    private double getLength(Relationship rel) {
        return Double.parseDouble(rel.getProperty("distance").toString());
    }

    /**
     * Searches and blocks the road in the correct hours
     * @param startRoadHour hour to start the road at
     * @param from Node to start the search
     * @param to Node to end the search
     * @return the path
     * @throws RuntimeException if a path wasn't found
     */
    public WeightedPath block(double startRoadHour, Node from, Node to) throws RuntimeException {
        WeightedPath road = this.tryBlock(startRoadHour, from, to);

        if (road == null) {
            throw new RuntimeException("Could not find a path");
        }

        return road;
    }
}
