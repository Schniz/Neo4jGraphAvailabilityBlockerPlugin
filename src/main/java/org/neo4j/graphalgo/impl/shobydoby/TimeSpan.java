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

/**
 * Created by Gal Schlezinger on 6/11/14.
 *
 * Represents a time span: (from, to)
 */
public class TimeSpan implements Comparable<TimeSpan>
{
    private double from;
    private double to;

    public double getFrom() {
        return from;
    }

    public TimeSpan setFrom( double from ) {
        this.from = from;
        return this;
    }

    public double getTo() {
        return to;
    }

    public TimeSpan setTo( double to ) {
        this.to = to;
        return this;
    }

    /**
     * @return a double array contains [ from, to ]
     */
    public double[] toDoubleArray()
    {
        return new double[] { from, to };
    }

    /**
     * @param second
     * @return -1 if this time span is before the second time span. 1 if the time span is after the second one.
     *         returns 0 if they collapse in any way.
     */
    @Override
    public int compareTo(TimeSpan second) {
        if ((this.getFrom() < second.getFrom()) && (this.getTo() < second.getFrom()))
        {
            return -1;
        }
        else if ((second.getFrom() < this.getFrom()) && (second.getTo() < this.getFrom()))
        {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimeSpan)
        {
            TimeSpan other = (TimeSpan)obj;
            return other.getFrom() == this.getFrom() && other.getTo() == this.getTo();
        }

        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "TimeSpan{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
