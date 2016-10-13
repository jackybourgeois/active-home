package org.activehome.context.test;

import junit.framework.Assert;
import org.activehome.context.data.MetricRecord;
import org.junit.Test;

import java.util.List;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
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
