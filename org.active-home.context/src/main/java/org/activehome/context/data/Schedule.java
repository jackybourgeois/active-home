package org.activehome.context.data;

/*
 * #%L
 * Active Home :: Context
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.activehome
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
 * @version %I%, %G%
 */
public class Schedule {

    private String name;
    private long start;
    private final long horizon;
    private final long granularity;
    private Episode episode;
    private final HashMap<String, MetricRecord> metricRecordMap;

    public Schedule(final String theName,
                    final long theStart,
                    final long theHorizon,
                    final long theGranularity) {
        name = theName;
        start = theStart;
        horizon = theHorizon;
        granularity = theGranularity;
        episode = new Episode();
        metricRecordMap = new HashMap<>();
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

        if (json.get("episode")!=null) {
            episode = new Episode(json.get("episode").asObject());
        }

        metricRecordMap = new HashMap<>();
        for (JsonValue val : json.get("mr").asArray()) {
            MetricRecord mr = new MetricRecord(val.asObject());
            metricRecordMap.put(mr.getMetricId(), new MetricRecord(val.asObject()));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String theName) {
        name = theName;
    }

    public void setStart(final long theStart) {
        start = theStart;
    }

    public long getStart() {
        return start;
    }

    public long getGranularity() {
        return granularity;
    }

    public int getNbSlot() {
        return (int) (horizon / granularity);
    }

    public long getHorizon() {
        return horizon;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(final Episode episode) {
        this.episode = episode;
    }

    public HashMap<String, MetricRecord> getMetricRecordMap() {
        return metricRecordMap;
    }


    public String[] normalize(final String metricId) {
        if (metricRecordMap.containsKey(metricId)) {
            return normalize(metricRecordMap.get(metricId));
        }
        return new String[0];
    }

    public double[] normalizeAsDouble(final MetricRecord metricRecord) {
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
            if (currentTS < start + (indexSlot + 1) * granularity) {   // before next slot, set the new val
                value = record.getDouble();
                data[indexSlot] = value;
            } else {
                while (currentTS > start + (indexSlot + 1) * granularity) {     // after the next slot, set prev val
                    data[indexSlot] = value;
                    indexSlot++;
                }
                value = record.getDouble();          // finally, set the new val, one time
                data[indexSlot] = value;
            }
        }

        for (int i = indexSlot; i < getNbSlot(); i++) {
            data[i] = value;
        }

        return data;
    }

    public HashMap<String, String> snapshopValuesAt(final long ts) {
        HashMap<String, String> snapshotMap = new HashMap<>();

        return snapshotMap;
    }

    public String[] normalize(final MetricRecord metricRecord) {
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
            if (currentTS < start + (indexSlot + 1) * granularity) {   // before next slot, set the new val
                value = record.getValue();
                data[indexSlot] = value;
            } else {
                while (currentTS > start + (indexSlot + 1) * granularity) {     // after the next slot, set prev val
                    data[indexSlot] = value;
                    indexSlot++;
                }
                value = record.getValue();          // finally, set the new val, one time
                data[indexSlot] = value;
            }
        }

        for (int i = indexSlot; i < getNbSlot(); i++) {
            data[i] = value;
        }

        return data;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.add("type", Schedule.class.getName());
        json.add("name", name);
        json.add("start", start);
        json.add("horizon", horizon);
        json.add("granularity", granularity);
        if (episode!=null) {
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
