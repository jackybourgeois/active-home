package org.activehome.context.com;

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
import org.activehome.time.TimeControlled;

/**
 * @author Jacky Bourgeois
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
    private boolean discrete = true;
    /**
     * Start timestamp in milliseconds.
     */
    private long startTS;
    /**
     * Duration of each sample in milliseconds.
     * Default: 1hr
     */
    private long sampleDuration = TimeControlled.HOUR;
    /**
     * Frequency of iteration to extract a sample, in milliseconds.
     * e.g. a frequency of 1 day will extract the sample everyday
     * at the same time (default = 1hr)
     */
    private long frequency = TimeControlled.HOUR;
    /**
     * Number of iteration (default = 1),
     * it provides less if not available.
     */
    private int numOfIteration = 1;
    /**
     * Type of period, select only sample during this type of period.
     * (default = no filter)
     */
    private String periodType = "";
    /**
     * Function to apply across all sample (default = none).
     */
    private String function = "";

    public ContextRequest(final String[] theMetrics,
                          final long theStartTS) {
        metrics = theMetrics;
        startTS = theStartTS;
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

    /**
     * @return The error as Json
     */
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

    /**
     * @return Json as string
     */
    @Override
    public final String toString() {
        return toJson().toString();
    }
}
