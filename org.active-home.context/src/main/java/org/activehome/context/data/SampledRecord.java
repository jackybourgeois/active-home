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
