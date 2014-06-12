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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Gal Schlezinger on 6/11/14.
 *
 * Represents edge availability
 */
public class EdgeAvailability
{
    private SortedSet<TimeSpan> blockedTimeSpans;

    public SortedSet<TimeSpan> getBlockedTimeSpans() {
        return blockedTimeSpans;
    }

    public EdgeAvailability() {
        blockedTimeSpans = new TreeSet<>();
    }

    /**
     * Provides an interface for converting [ start1, finish1, start2, finish2 ] to awesome EdgeAvailability object.
     * @param array in the form of [ start1, finish1, start2, finish2 ]
     * @return an EdgeAvailability object representing the array.
     */
    public static EdgeAvailability fromDoubleArray(double[] array)
    {
        if (array.length % 2 == 1)
        {
            throw new RuntimeException("The array length should be even.");
        }

        TimeSpan[] edgeAvailabilityArray = new TimeSpan[array.length / 2];

        for (int dualIndex = 1; dualIndex <= array.length; dualIndex += 2) {
            double from = array[dualIndex - 1];
            double to = array[dualIndex];
            int placeInArray = (dualIndex - 1) / 2;
            edgeAvailabilityArray[placeInArray] = new TimeSpan().setFrom(from).setTo(to);
        }

        return fromTimeSpanArray(edgeAvailabilityArray);
    }

    /**
     * Takes the EdgeAvailability awesome object and turns it into a double array.
     * @return a double array representing the EdgeAvailability object.
     */
    public double[] toDoubleArray()
    {
        int edgeAvailabilitySize = blockedTimeSpans.size();
        int arrayLength = edgeAvailabilitySize * 2;
        double[] doubleArray = new double[arrayLength];
        Iterator<TimeSpan> timeSpanIterator = blockedTimeSpans.iterator();

        for (int edgeAvailabilityIndex = 0; edgeAvailabilityIndex < edgeAvailabilitySize; edgeAvailabilityIndex++)
        {
            TimeSpan timeSpan = timeSpanIterator.next();
            doubleArray[edgeAvailabilityIndex * 2] = timeSpan.getFrom();
            doubleArray[edgeAvailabilityIndex * 2 + 1] = timeSpan.getTo();
        }

        return doubleArray;
    }

    /**
     * Provides an interface for converting [ timeStamp1, timeStamp2 ] to awesome EdgeAvailability object.
     * @param array in the form of [ timeStamp1, timeStamp2 ]
     * @return an EdgeAvailability object representing the array.
     */
    public static EdgeAvailability fromTimeSpanArray(TimeSpan[] array)
    {
        EdgeAvailability edgeAvailability = new EdgeAvailability();

        for (TimeSpan timeSpan : array)
        {
            edgeAvailability.blockedTimeSpans.add(timeSpan);
        }

        return edgeAvailability;
    }

    /**
     * Takes the EdgeAvailability awesome object and turns it into a TimeStamp array.
     * @return a TimeStamp array representing the EdgeAvailability object.
     */
    public TimeSpan[] toTimeSpanArray()
    {
        TimeSpan[] array = new TimeSpan[blockedTimeSpans.size()];
        blockedTimeSpans.toArray(array);

        return array;
    }

    /**
     * Check whether the edge is available.
     * @param timeSpan
     * @return boolean. true if the edge is avaiable; otherwise, false.
     */
    public boolean isFree(TimeSpan timeSpan)
    {
        return !blockedTimeSpans.contains(timeSpan);
    }

    /**
     * Will add a block in this timestamp
     * @param timeSpan
     * @return this (EdgeAvailability) for method chaining.
     */
    public EdgeAvailability addBlock(TimeSpan timeSpan)
    {
        blockedTimeSpans.add(timeSpan);

        return this;
    }
}
