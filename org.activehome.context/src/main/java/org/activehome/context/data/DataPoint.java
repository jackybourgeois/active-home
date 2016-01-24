package org.activehome.context.data;

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
