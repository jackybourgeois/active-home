package org.activehome.context.data;

import com.eclipsesource.json.JsonObject;

/**
 * DataPoint extension for discrete value in time
 * (energy, amount of money...).
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class DiscreteDataPoint extends DataPoint {

    /**
     * The time duration of the sample.
     */
    private final long interval;

    /**
     * @param theMetricId  Data point's metric id
     * @param theTimestamp Data point's time-stamp (milliseconds)
     * @param theValue     Data point's value
     * @param theInterval  The time duration of the sample
     */
    public DiscreteDataPoint(final String theMetricId,
                             final long theTimestamp,
                             final String theValue,
                             final long theInterval) {
        super(theMetricId, theTimestamp, theValue);
        interval = theInterval;
    }

    public DiscreteDataPoint(final String theMetricId,
                             final long theTimestamp,
                             final String theValue,
                             final String theVersion,
                             final long theShift,
                             final double theConfidence,
                             final long theInterval) {
        super(theMetricId, theTimestamp, theValue, theVersion, theShift, theConfidence);
        interval = theInterval;
    }

    /**
     * @param json Json that can be map as DiscreteDataPoint
     */
    public DiscreteDataPoint(final JsonObject json) {
        super(json);
        interval = json.getLong("interval", 0);
    }

    /**
     * @return The sample time duration
     */
    public final long getInterval() {
        return interval;
    }

    /**
     * Convert the DiscreteDataPoint into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("interval", interval);
        return json;
    }

}
