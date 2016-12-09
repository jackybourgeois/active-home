package org.activehome.context.test;

/*
 * #%L
 * Active Home :: Context
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 Active Home Project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import junit.framework.Assert;
import org.activehome.context.data.MetricRecord;
import org.junit.Test;

import java.util.List;

/**
 * @author Jacky Bourgeois
 */
public class TestMetricRecord {

    @Test
    public void testFrequency() {

        MetricRecord mr = new MetricRecord("testRecord");
        mr.addRecord(0, "1", "vTest", 1);
        mr.addRecord(5, "1", "vTest", 1);
        mr.addRecord(10, "1", "vTest", 1);
        mr.addRecord(15, "1", "vTest", 1);
        mr.addRecord(20, "1", "vTest", 1);
        mr.addRecord(25, "1", "vTest", 1);
        mr.addRecord(30, "1", "vTest", 1);

        List<Integer> frequency = mr.computeFrequencyDatapointPerHour("vTest", 18);
        Assert.assertEquals(2, frequency.size());
        Assert.assertEquals(4, frequency.get(0).intValue());
        Assert.assertEquals(3, frequency.get(1).intValue());
    }

}
