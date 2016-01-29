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
 * The DataPoint is the way to transport data in Active Home,
 * liking a metric, a time-stamp and a value.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class DataPoint {

    /**
     * The metric id.
     */
    private final String metricId;
    /**
     * the time-stamp (milliseconds).
     */
    private final long ts;
    /**
     * the value.
     */
    private final String value;
    /**
     * version (default = '0')
     */
    private final String version;
    /**
     * shift in time, milliseconds (default = 0)
     */
    private final long shift;
    /**
     * How reliable is this datapoint (between 0 and 1).
     */
    private final double confidence;

    /**
     * @param theMetricId Data point's metric id
     * @param theTimestamp Data point's time-stamp (milliseconds)
     * @param theValue Data point's value
     */
    public DataPoint(final String theMetricId,
                     final long theTimestamp,
                     final String theValue) {
        metricId = theMetricId;
        ts = theTimestamp;
        value = theValue;
        version = "0";
        shift = 0;
        confidence = 1;
    }

    /**
     * @param theMetricId Data point's metric id
     * @param theTimestamp Data point's time-stamp (milliseconds)
     * @param theValue Data point's value
     */
    public DataPoint(final String theMetricId,
                     final long theTimestamp,
                     final String theValue,
                     final String theVersion,
                     final long theShift,
                     final double theConfidence) {
        metricId = theMetricId;
        ts = theTimestamp;
        value = theValue;
        version = theVersion;
        shift = theShift;
        confidence = theConfidence;
    }

    /**
     * @param json Json that can be map as DataPoint
     */
    public DataPoint(final JsonObject json) {
        metricId = json.get("metricId").asString();
        ts = json.get("ts").asLong();
        value = json.get("value").asString();
        version = json.getString("version", "0");
        shift = json.getLong("shift", 0);
        confidence = json.getDouble("confidence", 1);
    }

    /**
     * @return the data point time-stamp
     */
    public final long getTS() {
        return ts;
    }

    /**
     * @return The data point metric id
     */
    public final String getMetricId() {
        return metricId;
    }

    /**
     * @return The data point value
     */
    public final String getValue() {
        return value;
    }

    public final double getConfidence() {
        return confidence;
    }

    public String getVersion() {
        return version;
    }

    public long getShift() {
        return shift;
    }

    /**
     * Convert the DataPoint into Json.
     *
     * @return the Json
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", this.getClass().getName());
        json.add("metricId", metricId);
        json.add("ts", ts);
        json.add("value", value);
        json.add("version", version);
        json.add("shift", shift);
        json.add("confidence", confidence);
        return json;
    }

    /**
     * @return The Json as String
     */
    @Override
    public final String toString() {
        return toJson().toString();
    }

}
