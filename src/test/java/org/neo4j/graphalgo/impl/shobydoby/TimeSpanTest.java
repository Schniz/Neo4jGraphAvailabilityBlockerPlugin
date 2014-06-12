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

public class TimeSpanTest extends TestCase {
    public void testCompare() throws Exception {
        TimeSpan timeSpan1 = new TimeSpan().setFrom(1d).setTo(2d);
        TimeSpan timeSpan2 = new TimeSpan().setFrom(3d).setTo(5d);
        TimeSpan timeSpan3 = new TimeSpan().setFrom(2d).setTo(4d);

        assertTrue(timeSpan1.compareTo(timeSpan2) == -1);
        assertTrue(timeSpan2.compareTo(timeSpan1) == 1);
        assertTrue(timeSpan2.compareTo(timeSpan3) == 0);
    }
}