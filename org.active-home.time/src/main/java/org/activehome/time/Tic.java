package org.activehome.time;

/*
 * #%L
 * Active Home :: Time
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 Active Home Project
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
 * Tics are used to disseminate the time and related
 * parameters through the distributed architecture.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Tic {

    /**
     * The current time-stamp (milliseconds).
     */
    private final long timestamp;
    /**
     * The factor of time compression.
     */
    private final int zip;
    /**
     * The timezone from -12 to 12.
     */
    private final int timezone;
    /**
     * The time status indicate if the time is ticking or idled.
     */
    private final TimeStatus status;
    /**
     * The last time command.
     */
    private final TimeCommand timeCommand;

    /**
     * @param theTimestamp The current time-stamp (milliseconds)
     * @param theZip       The factor of time compression
     * @param theTimezone  The timezone from -12 to 12
     * @param theStatus    The time status
     * @param theCommand   The last time command
     */
    public Tic(final long theTimestamp, final int theZip,
               final int theTimezone, final TimeStatus theStatus,
               final TimeCommand theCommand) {
        timestamp = theTimestamp;
        zip = theZip;
        timezone = theTimezone;
        status = theStatus;
        timeCommand = theCommand;
    }

    /**
     * @param json Json that can be map as Tic
     */
    public Tic(final JsonObject json) {
        timestamp = json.get("ts").asLong();
        zip = json.get("zip").asInt();
        timezone = json.get("timezone").asInt();
        status = TimeStatus.valueOf(json.get("status").asString());
        if (json.get("cmd") != null) {
            timeCommand = TimeCommand.valueOf(json.get("cmd").asString());
        } else {
            timeCommand = null;
        }
    }

    /**
     * @return The Json as String
     */
    public final String toString() {
        return toJson().toString();
    }

    /**
     * Convert the Tic into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("ts", timestamp);
        json.add("zip", zip);
        json.add("timezone", timezone);
        json.add("status", status.name());
        if (timeCommand != null) {
            json.add("cmd", timeCommand.name());
        }
        return json;
    }

    /**
     * @return The time-stamp
     */
    public final long getTS() {
        return timestamp;
    }

    /**
     * @return The factor of time compression
     */
    public final int getZip() {
        return zip;
    }

    /**
     * @return The timezone
     */
    public final int getTimezone() {
        return timezone;
    }

    /**
     * @return The time status
     */
    public final TimeStatus getStatus() {
        return status;
    }

    /**
     * @return The last time command
     */
    public final TimeCommand getTimeCommand() {
        return timeCommand;
    }

}
