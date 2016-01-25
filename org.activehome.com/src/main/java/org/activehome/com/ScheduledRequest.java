package org.activehome.com;

/*
 * #%L
 * Active Home :: Com
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.activehome
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
 * Request for the Task Scheduler to be fired at a specific time.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class ScheduledRequest extends Request {

    /**
     * The time to execute the Request (milliseconds since 1970).
     */
    private final long execTime;

    /**
     * @param theSrc      The source of the request
     * @param theDest     The destination of the request
     * @param theTS       The time-stamp of the request
     * @param theMethod   The method of the request
     * @param theParams   The method's array of parameters
     * @param theExecTime The time to execute the Request
     */
    public ScheduledRequest(final String theSrc,
                            final String theDest,
                            final long theTS,
                            final String theMethod,
                            final Object[] theParams,
                            final long theExecTime) {
        super(theSrc, theDest, theTS, theMethod, theParams);
        execTime = theExecTime;
    }

    /**
     * @param theSrc      The source of the request
     * @param theDest     The destination of the request
     * @param theTS       The time-stamp of the request
     * @param theMethod   The method of the request
     * @param theParam    The method's parameter
     * @param theExecTime The time to execute the Request
     */
    public ScheduledRequest(final String theSrc,
                            final String theDest,
                            final long theTS,
                            final String theMethod,
                            final Object theParam,
                            final long theExecTime) {
        super(theSrc, theDest, theTS, theMethod, new Object[]{theParam});
        execTime = theExecTime;
    }

    /**
     * @param theSrc      The source of the request
     * @param theDest     The destination of the request
     * @param theTS       The time-stamp of the request
     * @param theMethod   The method of the request
     * @param theExecTime The time to execute the Request
     */
    public ScheduledRequest(final String theSrc,
                            final String theDest,
                            final long theTS,
                            final String theMethod,
                            final long theExecTime) {
        super(theSrc, theDest, theTS, theMethod);
        execTime = theExecTime;
    }

    /**
     * @param theRequest  The request to copy
     * @param theExecTime The time to execute the Request
     */
    public ScheduledRequest(final Request theRequest,
                            final long theExecTime) {
        super(theRequest);
        execTime = theExecTime;
    }

    /**
     * @param json Json that can be map as ScheduledRequest
     */
    public ScheduledRequest(final JsonObject json) {
        super(json);
        execTime = json.getLong("execTime", 0);
    }

    /**
     * @return The time to execute the Request (milliseconds since 1970).
     */
    public final long getExecTime() {
        return execTime;
    }

    @Override
    public final JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("execTime", execTime);
        return json;
    }
}
