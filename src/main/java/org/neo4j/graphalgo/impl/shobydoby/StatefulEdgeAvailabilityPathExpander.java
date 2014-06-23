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
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schniz on 6/11/14.
 */
public class StatefulEdgeAvailabilityPathExpander implements PathExpander<Double> {

    public static enum Relations implements RelationshipType {
        NODE, NEXT
    }

    int length;

    public StatefulEdgeAvailabilityPathExpander(int length) {
        this.length = length;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState<Double> state) {

        double currCost = state.getState();

        if (path.length() != 0) {
            currCost += ((Number) path.lastRelationship().getProperty("distance")).doubleValue();
            state.setState(currCost);
        }

        List<Relationship> relationships = new ArrayList<>();
        Iterable<Relationship> relationshipIterable = path.endNode().getRelationships(Relations.NODE, Relations.NEXT);

        for (Relationship relationship : relationshipIterable) {
            double[] availabilities = getAvailabilities(relationship);
            EdgeAvailability e = EdgeAvailability.fromDoubleArray(availabilities);

            if (e.isFree(new TimeSpan().setFrom(currCost).setTo(currCost + length))) {
                relationships.add(relationship);
            }
        }

        return relationships;
    }

    private double[] getAvailabilities(Relationship objRelation) {
        Object availabilities = objRelation.getProperty("availability", new double[0]);

        return (double[]) availabilities;
    }

    @Override
    public PathExpander<Double> reverse() {
        return this;
    }

    public int binarySearch(int startIndex, int endIndex, double[] arrAv, double cost) {
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
