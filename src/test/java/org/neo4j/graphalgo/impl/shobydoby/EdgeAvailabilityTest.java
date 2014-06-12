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

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;

public class EdgeAvailabilityTest extends TestCase {
    @Test
    public void testFromDoubleArray() throws Exception
    {
        EdgeAvailability edgeAvailability = EdgeAvailability.fromDoubleArray(new double[]{1d, 2d, 3d, 4d});
        SortedSet<TimeSpan> blockedTimeSpans = edgeAvailability.getBlockedTimeSpans();
        assertEquals(blockedTimeSpans.size(), 2);

        Iterator<TimeSpan> iterator = blockedTimeSpans.iterator();

        TimeSpan item = iterator.next();
        assertEquals(item.getFrom(), 1d, 0.1);
        assertEquals(item.getTo(), 2d, 0.1);

        item = iterator.next();
        assertEquals(item.getFrom(), 3d, 0.1);
        assertEquals(item.getTo(), 4d, 0.1);
    }

    @Test
    public void testToDoubleArray() throws Exception
    {
        double[] arr = new double[] { 1d, 2d, 3d, 4d };

        assertTrue(Arrays.equals(EdgeAvailability.fromDoubleArray(arr).toDoubleArray(), arr));
        assertTrue("It should sort the array", Arrays.equals(EdgeAvailability.fromDoubleArray(new double[] { 3d, 4d, 1d, 2d }).toDoubleArray(), arr));
    }

    public void testToTimeSpanArray() throws Exception {
        TimeSpan[] array = new TimeSpan[] {
            new TimeSpan().setFrom(1d).setTo(2d),
            new TimeSpan().setFrom(3d).setTo(4d)
        };
        assertTrue(Arrays.equals(EdgeAvailability.fromTimeSpanArray(array).toTimeSpanArray(), array));

        assertTrue("Should sort the array", Arrays.equals(EdgeAvailability.fromTimeSpanArray(new TimeSpan[] {
            new TimeSpan().setFrom(3d).setTo(4d),
            new TimeSpan().setFrom(1d).setTo(2d)
        }).toTimeSpanArray(), array));
    }

    @Test
    public void testIsFree() throws Exception
    {

        EdgeAvailability edgeAvailability = new EdgeAvailability();
        assertTrue("The list is empty, so it should be free", edgeAvailability.isFree(new TimeSpan().setFrom(1d).setTo(2d)));

        edgeAvailability.addBlock(new TimeSpan().setFrom(3d).setTo(5d));
        assertFalse("should be false since i just added it to the blocked list", edgeAvailability.isFree(new TimeSpan().setFrom(3d).setTo(5d)));
        assertFalse("should be false since they collapse", edgeAvailability.isFree(new TimeSpan().setFrom(1d).setTo(4d)));
        assertTrue("should be true since they don't collapse", edgeAvailability.isFree(new TimeSpan().setFrom(1d).setTo(2d)));
    }

    @Test
    public void testAddBlock() throws Exception
    {
        EdgeAvailability edgeAvailability = new EdgeAvailability();

        int before = edgeAvailability.getBlockedTimeSpans().size();
        edgeAvailability.addBlock( new TimeSpan().setFrom( 2d ).setTo( 4d ) );

        assertEquals( before + 1, edgeAvailability.getBlockedTimeSpans().size() );

        before = edgeAvailability.getBlockedTimeSpans().size();
        edgeAvailability.addBlock( new TimeSpan().setFrom( 1d ).setTo( 3d ));
        assertEquals( "It shouldn't add the block cause it collapses with other time spans", before, edgeAvailability.getBlockedTimeSpans().size() );
    }
}