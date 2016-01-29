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

/**
 * Records composed MetricRecords.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Record {

    /**
     * Record's value.
     */
    private String value;
    /**
     * Time (in milliseconds) since the MetricRecord started.
     */
    private long ts;
    /**
     * How reliable is this record (between 0 and 1).
     */
    private double confidence;

    /**
     * @param val The value of the record
     * @param timestamp The time since the MetricRecord started
     */
    public Record(final String val,
                  final long timestamp) {
        this(val, timestamp, 1);
    }

    public Record(final String val,
                  final long timestamp,
                  final double conf) {
        value = val;
        ts = timestamp;
        confidence = conf;
    }

    /**
     * @param json Json that can be map as Record
     */
    public Record(final JsonObject json) {
        ts = json.get("ts").asLong();
        value = json.get("value").asString();
        confidence = json.getDouble("confidence", 1);
    }

    /**
     * @return The value of the record
     */
    public final String getValue() {
        return value;
    }

    public final double getDouble() {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    /**
     * @param val The value of the record
     */
    public final void setValue(final String val) {
        value = val;
    }

    /**
     * @return The time since the MetricRecord started
     */
    public final long getTS() {
        return ts;
    }

    public double getConfidence() {
        return confidence;
    }

    /**
     * @param theTimestamp The time since the MetricRecord started
     */
    public final void setTS(final long theTimestamp) {
        ts = theTimestamp;
    }

    /**
     * Convert the Record into Json.
     *
     * @return the Json
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("value", value);
        json.add("ts", ts);
        if (confidence!=1) {
            json.add("confidence",confidence);
        }
        return json;
    }

    /**
     * @return The Json as String
     */
    public final String toString() {
        return toJson().toString();
    }
}
