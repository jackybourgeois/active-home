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


import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * The MetricRecord details the value of a metric over a given periods.
 * It is composed of Records
 *
 * @author Jacky Bourgeois
 */
public class MetricRecord {

    public static final String DEFAULT_MAIN_VERSION = "0";

    /**
     * The metric id.
     */
    private final String metricId;
    /**
     * The period's start time in milliseconds.
     */
    private long startTime = -1;
    /**
     * The duration of the period in milliseconds.
     */
    private long timeFrame;
    /**
     * The status indicating if the MetricRecord is partial (still recording)
     * or complete (either past values or predicted values).
     */
    private boolean recording;
    /**
     * The list of Records.
     */
    private final HashMap<String, LinkedList<Record>> records;

    private String mainVersion;

    /**
     * @param theMetricId  The metric id
     * @param theTimeFrame The duration of the record
     */
    public MetricRecord(final String theMetricId,
                        final long theTimeFrame) {
        metricId = theMetricId;
        timeFrame = theTimeFrame;
        recording = true;
        records = new HashMap<>();
    }

    public MetricRecord(final String aMetricId,
                        final long aStartTime,
                        final long aTimeFrame) {
        metricId = aMetricId;
        startTime = aStartTime;
        timeFrame = aTimeFrame;
        recording = true;
        records = new HashMap<>();
    }

    public MetricRecord(final String aMetricId,
                        final long aTimeFrame,
                        final String aVersion,
                        final long aStartTime,
                        final String initialValue,
                        final double confidence) {
        this(aMetricId, aTimeFrame);
        addRecord(aStartTime, initialValue, aVersion, confidence);
    }

    public MetricRecord(final String aMetricId,
                        final long aTimeFrame,
                        final long aStartTime,
                        final String initialValue,
                        final double confidence) {
        this(aMetricId, aTimeFrame);
        addRecord(aStartTime, initialValue, DEFAULT_MAIN_VERSION, confidence);
    }

    /**
     * Creates a minimal MetricRecord with a metric id only, no time frame.
     *
     * @param theMetricId The metric Id
     */
    public MetricRecord(final String theMetricId) {
        this(theMetricId, -1);
    }

    /**
     * Creates a copy of a MetricRecord and shifts it in time.
     *
     * @param newStartTime The new start time
     * @param metricRecord The MetricRecord to copy
     */
    public MetricRecord(final long newStartTime,
                        final MetricRecord metricRecord) {
        startTime = newStartTime;
        metricId = metricRecord.getMetricId();
        timeFrame = metricRecord.getTimeFrame();
        records = metricRecord.getAllVersionRecords();
        mainVersion = metricRecord.getMainVersion();
        recording = metricRecord.isRecording();
    }

    /**
     * Creates a copy of a MetricRecord for a different metric.
     *
     * @param newMetricId  The id of the new metric
     * @param metricRecord The MetricRecord to copy
     */
    public MetricRecord(final String newMetricId,
                        final MetricRecord metricRecord) {
        metricId = newMetricId;
        startTime = metricRecord.getStartTime();
        timeFrame = metricRecord.getTimeFrame();
        recording = isRecording();
        records = getAllVersionRecords();
    }

    /**
     * @param json Json that can be map as MetricRecord
     */
    public MetricRecord(final JsonObject json) {
        startTime = json.get("start").asLong();
        metricId = json.get("metricId").asString();
        timeFrame = json.get("timeFrame").asLong();
        recording = json.get("recording").asBoolean();
        if (json.get("mainVersion") != null) {
            mainVersion = json.get("mainVersion").asString();
        }

        records = new HashMap<>();
        JsonObject jsonVersion = json.get("records").asObject();
        for (String version : jsonVersion.names()) {
            records.put(version, new LinkedList<>());
            for (JsonValue jsonRec : jsonVersion.get(version).asArray()) {
                if (jsonRec.asObject().get("duration") != null) {
                    records.get(version).add(new SampledRecord(jsonRec.asObject()));
                } else {
                    records.get(version).add(new Record(jsonRec.asObject()));
                }
            }
        }
    }

    /**
     * @return The start time
     */
    public final long getStartTime() {
        return startTime;
    }

    /**
     * @return The id of the recorded metric
     */
    public final String getMetricId() {
        return metricId;
    }

    /**
     * @param version Version to look at
     * @return The list of records
     */
    public final LinkedList<Record> getRecords(final String version) {
        return records.get(version);
    }

    /**
     * @return the list of records of the main version.
     */
    public final LinkedList<Record> getRecords() {
        return getRecords(mainVersion);
    }

    public final HashMap<String, LinkedList<Record>> getAllVersionRecords() {
        return records;
    }

    /**
     * @return true if the record is partial, still recording
     */
    public final boolean isRecording() {
        return recording;
    }

    public final void setRecording(final boolean isRecording) {
        recording = isRecording;
    }

    /**
     * @param ts         Time-stamp of the new record, in milliseconds since 1970
     * @param value      The new value
     * @param version    Version of the record
     * @param confidence confidence of the record
     */
    public final void addRecord(final long ts,
                                final String value,
                                final String version,
                                final double confidence) {
        if (startTime == -1) {
            startTime = ts;
        }
        if (mainVersion == null) {
            mainVersion = version;
        }
        if (!records.containsKey(version)) {
            records.put(version, new LinkedList<>());
        }
        records.get(version).addLast(new Record(value, ts - startTime, confidence));
    }

    public final void addRecord(final long ts,
                                final String value,
                                final double confidence) {
        if (mainVersion == null) {
            mainVersion = DEFAULT_MAIN_VERSION;
        }
        addRecord(ts, value, mainVersion, confidence);
    }

    /**
     * @param ts         Time-stamp of the new record, in milliseconds since 1970
     * @param duration   Duration of the sample
     * @param value      The new value
     * @param version    Version of the record
     * @param confidence confidence of the record
     */
    public final void addRecord(final long ts,
                                final long duration,
                                final String value,
                                final String version,
                                final double confidence) {
        if (startTime == -1) {
            startTime = ts;
        }
        if (mainVersion == null) {
            mainVersion = version;
        }
        if (!records.containsKey(version)) {
            records.put(version, new LinkedList<>());
        }
        records.get(version).addLast(new SampledRecord(value, ts - startTime, duration, confidence));
    }

    public final void addRecord(final long ts,
                                final long duration,
                                final String value,
                                final double confidence) {
        if (mainVersion == null) {
            mainVersion = DEFAULT_MAIN_VERSION;
        }
        addRecord(ts, duration, value, mainVersion, confidence);
    }

    /**
     * @param metricRecord Source of the new records
     */
    public final void addAllRecords(final MetricRecord metricRecord) {
        if (records.size() == 0 && metricRecord.getRecords().size() > 0) {
            startTime = metricRecord.getRecords().getFirst().getTS();
        }
        for (String version : metricRecord.getAllVersionRecords().keySet()) {
            for (Record record : metricRecord.getRecords(version)) {
                records.get(version).addLast(new Record(record.getValue(),
                        record.getTS() + metricRecord.getStartTime() - startTime));
            }
        }
    }

    /**
     * @param version the version to poll
     * @return The first Record (start time shifting to the 'new' first record)
     */
    public final Record pollFirst(final String version) {
        Record record = records.get(version).pollFirst();
        if (records.size() > 0) {
            startTime += records.get(version).getFirst().getTS();
            records.get(version).getFirst().setTS(0);
        }
        return record;
    }

    public final Record pollFirst() {
        return pollFirst(mainVersion);
    }

    /**
     * @return The defined timeframe, otherwise the
     * difference between last time and start time
     */
    public final long getTimeFrame() {
        if (timeFrame != -1) {
            return timeFrame;
        }
        return records.get(mainVersion).getLast().getTS();
    }

    /**
     * @return The time-stamp of the last record (milliseconds since 1970)
     */
    public final long getlastTS() {
        if (records.get(mainVersion) != null && records.get(mainVersion).size() > 0) {
            return startTime + records.get(mainVersion).getLast().getTS();
        }
        return startTime;
    }

    /**
     * @return The last recorded value
     */
    public final String getLastValue() {
        if (records.get(mainVersion) != null && records.get(mainVersion).size() > 0) {
            return records.get(mainVersion).getLast().getValue();
        }
        return "";
    }

    /**
     * @return The Json as String
     */
    @Override
    public final String toString() {
        return toJson().toString();
    }

    /**
     * Convert the MetricRecord into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", MetricRecord.class.getName());
        json.add("start", startTime);
        json.add("metricId", metricId);
        json.add("timeFrame", timeFrame);
        json.add("recording", recording);
        if (mainVersion != null) {
            json.add("mainVersion", mainVersion);
        }

        JsonObject jsonVersions = new JsonObject();
        for (String version : records.keySet()) {
            JsonArray recordJsonArray = new JsonArray();
            for (Record record : records.get(version)) {
                recordJsonArray.add(record.toJson());
            }
            jsonVersions.add(version, recordJsonArray);
        }
        json.add("records", jsonVersions);

        return json;
    }

    /**
     * Merge 1 version of an MR into the current MR,
     * adding (sum=true) or subtracting the new values.
     *
     * @param srcMR       The MetricRecord to merge
     * @param srcVersion  The incoming version
     * @param destVersion The destination version
     * @param sum         Merge by summing, otherwise merge by subtracting
     */
    public final void mergeRecords(final MetricRecord srcMR,
                                   final String srcVersion,
                                   final String destVersion,
                                   final boolean sum) {

        int sign = sum ? 1 : -1;
        int i = 0;
        int j = 0;
        double prevInitial = 0;
        double prevUpdated;

        if (!records.containsKey(destVersion)) {
            records.put(destVersion, new LinkedList<>());
        }

        if (srcMR.getRecords(srcVersion).size() > 0) {
            long startTime2 = srcMR.getStartTime();
            LinkedList<Record> records2 = srcMR.getRecords(srcVersion);
            while (i < records.get(destVersion).size()) {
                Record record = records.get(destVersion).get(i);
                while (j < records2.size()
                        && records2.get(j).getTS() + startTime2 < record.getTS() + startTime) {
                    long time = records2.get(j).getTS() + startTime2 - startTime;
                    double val = prevInitial + sign * Double.valueOf(records2.get(j).getValue());
                    Record newRecord = new Record(val + "", time);
                    records.get(destVersion).add(i, newRecord);
                    j++;
                }

                if (j >= records2.size()) {
                    break;
                }
                if (records2.get(j).getTS() + startTime2
                        == record.getTS() + startTime) {
                    prevInitial = Double.valueOf(record.getValue());
                    prevUpdated = prevInitial + sign * Double.valueOf(records2.get(j).getValue());
                    record.setValue(prevUpdated + "");
                    j++;
                } else if (records2.get(j).getTS() + startTime2
                        > record.getTS() + startTime) {
                    if (j > 0) {
                        double val = Double.valueOf(record.getValue())
                                + sign * Double.valueOf(records2.get(j - 1).getValue());
                        record.setValue(val + "");
                    }
                }
                i++;
            }

            while (j < srcMR.getRecords(srcVersion).size()) {
                double val = prevInitial + sign * Double.valueOf(records2.get(j).getValue());
                long ts = records2.get(j).getTS() + startTime2 - startTime;
                Record newRecord = new Record(val + "", ts);
                records.get(destVersion).addLast(newRecord);
                j++;
            }

            if (records.get(destVersion).getLast().getTS() > timeFrame) {
                timeFrame = records.get(destVersion).getLast().getTS();
            }
        }
    }

    public final void mergeRecords(final MetricRecord metricRecord,
                                   final boolean sum) {
        mergeRecords(metricRecord, mainVersion, mainVersion, sum);
    }


    public double sum() {
        double sum = 0;
        if (getRecords() != null) {
            for (Record record : getRecords()) {
                sum += Double.valueOf(record.getValue());
            }
        }
        return sum;
    }

    public HashMap<String, String> getValuesAt(final long ts) {
        HashMap<String, String> valueMap = new HashMap<>();
        for (String version : getAllVersionRecords().keySet()) {
            LinkedList<Record> records = getAllVersionRecords().get(version);
            int i = 0;
            while (i < records.size() - 1 && records.get(i + 1).getTS() < ts) {
                i++;
            }
            if (records.size() > 0) {
                valueMap.put(version, records.get(i).getValue());
            }
        }
        return valueMap;
    }

    public String getMainVersion() {
        return mainVersion;
    }

    public void setMainVersion(String mainVersion) {
        this.mainVersion = mainVersion;
    }


    public static LinkedList<MetricRecord> sortByStartDate(
            final LinkedList<MetricRecord> loadList) {
        LinkedList<MetricRecord> sortedLoadList = new LinkedList<>();
        for (MetricRecord load : loadList) {
            if (sortedLoadList.size() == 0) {
                sortedLoadList.add(load);
            } else {
                int i = 1;
                while (i < sortedLoadList.size()
                        && sortedLoadList.get(i).getStartTime() < load.getStartTime()) {
                    i++;
                }
                sortedLoadList.add(i - 1, load);
            }

        }

        return sortedLoadList;
    }

    /**
     * Compute the number of data point per hour of
     * the main version for the entire time frame.
     *
     * @param granularity of the result
     * @return frequency (DP/granularity)
     */
    public List<Integer> computeFrequencyDatapointPerHour(
            final long granularity) {
        return computeFrequencyDatapointPerHour(mainVersion, granularity);
    }

    /**
     * Compute the number of data point per hour of
     * the specified version for the entire time frame.
     *
     * @param version     to analyse
     * @param granularity of the result
     * @return list of frequencies (DP/granularity) for each hour of the time frame
     */
    public List<Integer> computeFrequencyDatapointPerHour(
            final String version,
            final long granularity) {
        LinkedList<Integer> result = new LinkedList<>();
        long endSlot = -1;
        int nbRecords = 0;
        for (Record record : getRecords(version)) {
            if (endSlot < record.getTS()) {
                if (endSlot != -1) {
                    result.add(nbRecords);
                }
                nbRecords = 0;
                endSlot += granularity;
            }
            nbRecords++;
        }
        result.add(nbRecords);
        return result;
    }
}
