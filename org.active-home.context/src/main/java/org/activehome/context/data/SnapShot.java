package org.activehome.context.data;

/*
 * #%L
 * Active Home :: Context
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.active-home
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


import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Snapshot of all the metrics in a schedule at a given time
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class SnapShot {

    private long ts;
    private Schedule schedule;

    /**
     * The map of the current indexes
     */
    private LinkedList<SnapShotItem> snapShotItems;

    public SnapShot(Schedule schedule) {
        this.ts = -1;
        this.schedule = schedule;
        snapShotItems = new LinkedList<>();
        for (MetricRecord mr : schedule.getMetricRecordMap().values()) {
            snapShotItems.addAll(mr.getAllVersionRecords().keySet()
                    .stream().map(version -> new SnapShotItem(mr.getMetricId(), version, mr.getRecords(version)))
                    .collect(Collectors.toList()));
        }
    }

    public boolean next() {
        long nextTS = -1;
        for (SnapShotItem item : snapShotItems) {
            if (item.currentIndex < item.getRecords().size() - 1) {
                if (nextTS == -1 || item.getRecords().get(item.currentIndex + 1).getTS() < nextTS) {
                    nextTS = item.getRecords().get(item.currentIndex + 1).getTS();
                }
            }
        }
        if (nextTS != -1) {
            for (SnapShotItem item : snapShotItems) {
                if (item.currentIndex < item.getRecords().size() - 1) {
                    if (item.getRecords().get(item.currentIndex + 1).getTS() == nextTS) {
                        item.nextRecord();
                    }
                }
            }
            ts = nextTS;
            return true;
        }

        return false;
    }

    public DataPoint getCurrentDP(final String metricId,
                                  final String version) {
        for (SnapShotItem item : snapShotItems) {
            if (item.getMetricId().equals(metricId)
                    && item.getVersion().equals(version) && item.getCurrentIndex() != -1) {
                Record record = item.getRecords().get(item.getCurrentIndex());
                return new DataPoint(metricId, schedule.getStart() + record.getTS(), record.getValue(),
                        version, 0, record.getConfidence());
            }
        }
        return null;
    }

    public DataPoint getPreviousDP(final String metricId,
                                   final String version) {
        for (SnapShotItem item : snapShotItems) {
            if (item.getMetricId().equals(metricId)
                    && item.getVersion().equals(version) && item.getCurrentIndex() != -1) {
                Record record = item.getRecords().get(item.getPreviousIndex());
                return new DataPoint(metricId, schedule.getStart() + record.getTS(), record.getValue(),
                        version, 0, record.getConfidence());
            }
        }
        return null;
    }

    public LinkedList<DataPoint> getCurrentDPMatch(final String regEx,
                                                   final String[] versions) {
        LinkedList<DataPoint> dpCurrentMatch = new LinkedList<>();
        for (String metric : schedule.getMetricRecordMap().keySet()) {
            String selectedVersion = null;
            if (metric.matches(regEx)) {
                for (String vers : versions) {
                    if (selectedVersion == null
                            && schedule.getMetricRecordMap().get(metric).getAllVersionRecords().containsKey(vers)) {
                        selectedVersion = vers;
                        if (selectedVersion != null) {
                            for (SnapShotItem item : snapShotItems) {
                                if (item.getMetricId().equals(metric) && item.getVersion().equals(selectedVersion)) {
                                    Record record = item.getRecords().get(item.getPreviousIndex());
                                    dpCurrentMatch.add(new DataPoint(item.getMetricId(), schedule.getStart() + record.getTS(),
                                            record.getValue(), selectedVersion, 0, record.getConfidence()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return dpCurrentMatch;
    }

    public long getTS() {
        return ts;
    }

    public String toString() {
        String str = "";
        for (SnapShotItem item : snapShotItems) {
            str += item.getMetricId() + "-" + item.getVersion() + ": "
                    + item.getRecords().get(item.getCurrentIndex()) + "\n";
        }
        return str;
    }

    private class SnapShotItem {

        private String metricId;
        private String version;
        private LinkedList<Record> records;
        private int currentIndex;
        private int previousIndex;

        public SnapShotItem(String metricId, String version, LinkedList<Record> records) {
            this.metricId = metricId;
            this.version = version;
            this.records = records;
            currentIndex = -1;
            previousIndex = -1;
        }

        public String getMetricId() {
            return metricId;
        }

        public String getVersion() {
            return version;
        }

        public LinkedList<Record> getRecords() {
            return records;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public int getPreviousIndex() {
            return previousIndex;
        }

        public void nextRecord() {
            currentIndex++;
            previousIndex++;
        }
    }

}
