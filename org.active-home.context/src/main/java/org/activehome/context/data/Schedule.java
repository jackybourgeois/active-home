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

/**
 * @author Jacky Bourgeois
 */
public class Schedule {

    /**
     * A name, to differentiate between schedules.
     */
    private String name;
    /**
     * When the Schedule start (UNIX timestamp).
     */
    private long start;
    /**
     * horizon / duration of the schedule (milliseconds).
     */
    private final long horizon;
    /**
     * duration of each steps (milliseconds).
     */
    private final long granularity;
    /**
     * Episode of the schedule.
     */
    private Episode episode;
    /**
     * Map of data: metric name / MetricRecord.
     */
    private final HashMap<String, MetricRecord> metricRecordMap;

    public Schedule(final String theName,
                    final long theStart,
                    final long theHorizon,
                    final long theGranularity) {
        name = theName;
        start = theStart;
        horizon = theHorizon;
        granularity = theGranularity;
        metricRecordMap  = new HashMap<>();
        episode = new Episode();
    }

    public Schedule(final Schedule schedule) {
        name = schedule.getName();
        start = schedule.getStart();
        horizon = schedule.getHorizon();
        granularity = schedule.getGranularity();
        episode = schedule.getEpisode();
        metricRecordMap = schedule.getMetricRecordMap();
    }

    public Schedule(final JsonObject json) {
        name = json.get("name").asString();
        start = json.get("start").asLong();
        horizon = json.get("horizon").asLong();
        granularity = json.get("granularity").asLong();

        if (json.get("episode") != null) {
            episode = new Episode(json.get("episode").asObject());
        }

        metricRecordMap = new HashMap<>();
        for (JsonValue val : json.get("mr").asArray()) {
            MetricRecord mr = new MetricRecord(val.asObject());
            metricRecordMap.put(mr.getMetricId(),
                    new MetricRecord(val.asObject()));
        }
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String theName) {
        name = theName;
    }

    public final void setStart(final long theStart) {
        start = theStart;
    }

    public final long getStart() {
        return start;
    }

    public final long getGranularity() {
        return granularity;
    }

    public final int getNbSlot() {
        return (int) (horizon / granularity);
    }

    public final long getHorizon() {
        return horizon;
    }

    public final Episode getEpisode() {
        return episode;
    }

    public final void setEpisode(final Episode anEpisode) {
        this.episode = anEpisode;
    }

    public final HashMap<String, MetricRecord> getMetricRecordMap() {
        return metricRecordMap;
    }


    public final String[] normalize(final String metricId) {
        if (metricRecordMap.containsKey(metricId)) {
            return normalize(metricRecordMap.get(metricId));
        }
        return new String[0];
    }

    public final double[] normalizeAsDouble(final MetricRecord metricRecord) {
        double[] data = new double[getNbSlot()];
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }

        int indexSlot = 0;
        double value = 0;
        long currentTS;
        LinkedList<Record> records = metricRecord.getRecords();
        for (Record record : records) {
            currentTS = start + record.getTS();
            // before next slot, set the new val
            if (currentTS < start + (indexSlot + 1) * granularity) {
                value = record.getDouble();
                data[indexSlot] = value;
            } else {
                // after the next slot, set prev val
                while (currentTS > start + (indexSlot + 1) * granularity) {
                    data[indexSlot] = value;
                    indexSlot++;
                }
                // finally, set the new val, one time
                value = record.getDouble();
                data[indexSlot] = value;
            }
        }

        for (int i = indexSlot; i < getNbSlot(); i++) {
            data[i] = value;
        }

        return data;
    }

    public final HashMap<String, String> snapshopValuesAt(final long ts) {
        HashMap<String, String> snapshotMap = new HashMap<>();

        return snapshotMap;
    }

    public final String[] normalize(final MetricRecord metricRecord) {
        String[] data = new String[getNbSlot()];
        for (int i = 0; i < data.length; i++) {
            data[i] = "";
        }

        int indexSlot = 0;
        String value = "";
        long currentTS;
        LinkedList<Record> records = metricRecord.getRecords();
        for (Record record : records) {
            currentTS = start + record.getTS();
            // before next slot, set the new val
            if (currentTS < start + (indexSlot + 1) * granularity) {
                value = record.getValue();
                data[indexSlot] = value;
            } else {
                // after the next slot, set prev val
                while (currentTS > start + (indexSlot + 1) * granularity) {
                    data[indexSlot] = value;
                    indexSlot++;
                }
                // finally, set the new val, one time
                value = record.getValue();
                data[indexSlot] = value;
            }
        }

        for (int i = indexSlot; i < getNbSlot(); i++) {
            data[i] = value;
        }

        return data;
    }

    /**
     * Convert the Schedule into Json.
     *
     * @return the Json
     */
    @Override
    public final String toString() {
        return toJson().toString();
    }

    /**
     * @return The Json as String
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.add("type", Schedule.class.getName());
        json.add("name", name);
        json.add("start", start);
        json.add("horizon", horizon);
        json.add("granularity", granularity);
        if (episode != null) {
            json.add("episode", episode.toJson());
        }

        JsonArray mr = new JsonArray();
        for (String key : metricRecordMap.keySet()) {
            mr.add(metricRecordMap.get(key).toJson());
        }
        json.add("mr", mr);

        return json;
    }

}
