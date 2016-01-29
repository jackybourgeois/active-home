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
