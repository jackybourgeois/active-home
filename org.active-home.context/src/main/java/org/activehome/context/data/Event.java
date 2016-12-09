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


import com.eclipsesource.json.JsonObject;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Jacky Bourgeois
 */
public class Event {

    private UUID id;
    private String scheduleName;
    private long startTS;
    private long duration;
    private HashMap<String, MetricRecord> metricRecordMap;
    private HashMap<String, String> propertyMap;
    private boolean isOnlyPart;

    public Event(final long theStartTS,
                 final long theDuration,
                 final HashMap<String, MetricRecord> theMetricRecordMap,
                 final boolean onlyPart) {
        id = UUID.randomUUID();
        startTS = theStartTS;
        duration = theDuration;
        metricRecordMap = theMetricRecordMap;
        propertyMap = new HashMap<>();
        isOnlyPart = onlyPart;
        scheduleName = null;
    }

    public Event(JsonObject json) {
        id = UUID.fromString(json.get("id").asString());
        startTS = json.getLong("startTS", 0);
        duration = json.getLong("duration", 0);
        scheduleName = json.getString("scheduleName", null);

        metricRecordMap = new HashMap<>();
        JsonObject mrJson = json.get("mr").asObject();
        for (String name : mrJson.names()) {
            metricRecordMap.put(name, new MetricRecord(mrJson.get(name).asObject()));
        }

        propertyMap = new HashMap<>();
        for (String name : json.get("prop").asObject().names()) {
            propertyMap.put(name, json.get("prop").asObject().getString(name,""));
        }

        isOnlyPart = json.getBoolean("isOnlyPart", isOnlyPart);
    }

    public UUID getId() {
        return id;
    }

    public long getStartTS() {
        return startTS;
    }

    public long getDuration() {
        return duration;
    }

    public HashMap<String, MetricRecord> getMetricRecordMap() {
        return metricRecordMap;
    }

    public String getProp(String key) {
        return propertyMap.get(key);
    }

    public void setProp(String key, String val) {
        propertyMap.put(key, val);
    }

    public boolean isOnlyPart() {
        return isOnlyPart;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", Event.class.getName());
        json.add("id", id.toString());
        json.add("startTS", startTS);
        json.add("duration", duration);
        if (scheduleName != null) {
            json.add("scheduleName", scheduleName);
        }

        JsonObject mr = new JsonObject();
        for (String key : metricRecordMap.keySet()) {
            mr.add(key, metricRecordMap.get(key).toJson());
        }
        json.add("mr", mr);

        JsonObject prop = new JsonObject();
        for (String key : propertyMap.keySet()) {
            prop.add(key, propertyMap.get(key));
        }
        json.add("prop", prop);

        json.add("isOnlyPart", isOnlyPart);

        return json;
    }

    public String toString() {
        return toJson().toString();
    }

    public void transform(final Transformation transformation) {
        if (transformation.getType().equals(Transformation.TransformationType.SHIFT)) {
            startTS = startTS + (long)transformation.getParam();
            HashMap<String, MetricRecord> mrMap = new HashMap<>();
            for (String key : metricRecordMap.keySet()) {
                mrMap.put(key, new MetricRecord(startTS, metricRecordMap.get(key)));
            }
            metricRecordMap = mrMap;
        }
    }
}
