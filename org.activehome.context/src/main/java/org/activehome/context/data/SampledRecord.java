package org.activehome.context.data;

import com.eclipsesource.json.JsonObject;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class SampledRecord extends Record {

    /**
     * Duration of the sample in milliseconds
     */
    private long duration;

    /**
     * @param value     The value of the record
     * @param timestamp The time since the MetricRecord started
     * @param theDuration  The duration of the sample in milliseconds
     */
    public SampledRecord(final String value,
                         final long timestamp,
                         final long theDuration,
                         final double confidence) {
        super(value, timestamp, confidence);
        duration = theDuration;
    }

    public SampledRecord(final String value,
                         final long timestamp,
                         final long theDuration) {
        super(value, timestamp);
        duration = theDuration;
    }

    /**
     * @param json Json that can be map as Record
     */
    public SampledRecord(final JsonObject json) {
        super(json);
        duration = json.getLong("duration", 0);
    }

    public final long getDuration() {
        return duration;
    }

    /**
     * Convert the SampledRecord into Json.
     *
     * @return the Json
     */
    @Override
    public final JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("duration", duration);
        return json;
    }
}
