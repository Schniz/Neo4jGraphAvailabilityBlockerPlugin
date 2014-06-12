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

import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by schniz on 6/11/14.
 */
public class StatefulEdgeAvailabilityPathExpander implements PathExpander<Double> {
    int length;

    public StatefulEdgeAvailabilityPathExpander(int length) {
        this.length = length;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState<Double> state) {

        double currCost = 0;

        if ( path.length() == 0 )
        {
            currCost = state.getState();
        }
        else
        {
            currCost = (state.getState()) + ((Number)(path.lastRelationship().getProperty( "length" ))).doubleValue();

            state.setState( currCost );
        }

        List<Relationship> relationships = new LinkedList<Relationship>();

        for (Relationship objRelation : path.endNode().getRelationships()) {
            double[] availabilities = (double[])(objRelation.getProperty("availability"));

            int startIndex = 0;
            int endIndex = availabilities.length - 1;
            int greatestSmallerIndex = 0;

            if (availabilities.length > 0) {
                greatestSmallerIndex = binarySearch(startIndex, endIndex, availabilities, currCost);
            }

            if ((availabilities.length == 0) ||
                (isLastElementInCouple(greatestSmallerIndex) &&
                 (isLastElementInArray(availabilities, greatestSmallerIndex) ||
                  isNextElementNotInterferingThisOne(currCost, availabilities[greatestSmallerIndex + 1]))))
            {
                relationships.add(objRelation);
            }
        }

        return relationships;
    }

    private boolean isNextElementNotInterferingThisOne(double currCost, double i) {
        return (currCost + length < i);
    }

    private boolean isLastElementInCouple(int greatestSmallerNumberIndex) {
        return (Math.abs(greatestSmallerNumberIndex % 2) == 1);
    }

    private boolean isLastElementInArray(double[] arrAvailability, int greatestSmallerNumberIndex) {
        return (greatestSmallerNumberIndex + 1 == arrAvailability.length);
    }

    @Override
    public PathExpander<Double> reverse() {
        return this;
    }

    public int binarySearch(int startIndex, int endIndex, double[] arrAv, double cost)
    {
        int middle = (startIndex + endIndex) / 2;

        if (arrAv[middle] == cost) {
            return middle;
        } else if (arrAv[middle] < cost) {
            if (endIndex == startIndex) {
                return (startIndex);
            }

            return (binarySearch(Math.min(middle + 1, endIndex), endIndex, arrAv, cost));
        } else {
            if (endIndex == startIndex) {
                return (startIndex - 1);
            }

            return (binarySearch(startIndex, Math.max(startIndex, middle - 1), arrAv, cost));
        }
    }
}
