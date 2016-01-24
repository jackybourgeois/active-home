package org.activehome.context.com;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import org.activehome.time.TimeControlled;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class ContextRequest {

    /**
     * Array of metrics to extract.
     */
    private String[] metrics;
    /**
     * Discrete or continuous:
     *   if discrete, the sampling is summed,
     *   otherwise the sampling is averaged.
     * Default: true
     */
    private boolean discrete;
    /**
     * Start timestamp in milliseconds.
     */
    private long startTS;
    /**
     * Duration of each sample in milliseconds.
     * Default: 1hr
     */
    private long sampleDuration;
    /**
     * Frequency of iteration to extract a sample, in milliseconds.
     * e.g. a frequency of 1 day will extract the sample everyday
     * at the same time (default = 1hr)
     */
    private long frequency;
    /**
     * Number of iteration (default = 1),
     * it provides less if not available.
     */
    private int numOfIteration;
    /**
     * Type of period, select only sample during this type of period.
     * (default = no filter)
     */
    private String periodType;
    /**
     * Function to apply across all sample (default = none).
     */
    private String function;

    public ContextRequest(final String[] theMetrics,
                          final boolean isDiscrete,
                          final long theStartTS,
                          final long theSampleDuration,
                          final long theFrequency,
                          final int theNumOfIteration,
                          final String thePeriodType,
                          final String theFunction) {
        metrics = theMetrics;
        discrete = isDiscrete;
        startTS = theStartTS;
        sampleDuration = theSampleDuration;
        frequency = theFrequency;
        numOfIteration = theNumOfIteration;
        periodType = thePeriodType;
        function = theFunction;
    }

    public ContextRequest(final String[] theMetrics, final long theStartTS) {
        this(theMetrics, true, theStartTS, TimeControlled.HOUR, TimeControlled.HOUR,
                1, "", "");
    }

    public ContextRequest(final JsonObject json) {
        JsonArray array = json.get("metrics").asArray();
        metrics = new String[array.size()];
        for (int i = 0; i < metrics.length; i++) {
            metrics[i] = array.get(i).asString();
        }
        discrete = json.getBoolean("discrete", true);
        startTS = json.get("startTS").asLong();
        sampleDuration = json.getLong("duration", TimeControlled.HOUR);
        frequency = json.getLong("frequency", TimeControlled.HOUR);
        numOfIteration = json.getInt("iteration", 1);
        periodType = json.getString("periodType", "");
        function = json.getString("function", "");
    }

    public final String[] getMetrics() {
        return metrics;
    }

    public final boolean isDiscrete() {
        return discrete;
    }

    public final long getStartTS() {
        return startTS;
    }

    public final long getSampleDuration() {
        return sampleDuration;
    }

    public final String getPeriodType() {
        return periodType;
    }

    public final long getFrequency() {
        return frequency;
    }

    public final int getNumIteration() {
        return numOfIteration;
    }

    public final String getFunction() {
        return function;
    }

    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", ContextRequest.class.getName());
        JsonArray array = new JsonArray();
        for (int i = 0; i < metrics.length; i++) {
            array.add(metrics[i]);
        }
        json.add("metrics", array);
        json.add("discrete", discrete);
        json.add("startTS", startTS);
        json.add("duration", sampleDuration);
        json.add("frequency", frequency);
        json.add("iteration", numOfIteration);
        json.add("periodType", periodType);
        json.add("function", function);
        return json;
    }

    @Override
    public final String toString() {
        return toJson().toString();
    }
}
