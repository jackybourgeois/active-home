package org.activehome.com.error;

/*
 * #%L
 * Active Home :: Com
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
 * Active Home standard error.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Error {

    /**
     * The generic type of error.
     */
    private final ErrorType errorType;
    /**
     * Contextual details on the error.
     */
    private final String details;

    /**
     * @param theErrorType The generic error type
     * @param theDetails The contextual error details
     */
    public Error(final ErrorType theErrorType, final String theDetails) {
        this.errorType = theErrorType;
        if (theDetails.compareTo("") != 0) {
            details = theDetails;
        } else {
            details = errorType.getMessage();
        }
    }

    /**
     * @param json Json that can be map as Error
     */
    public Error(final JsonObject json) {
        errorType = ErrorType.valueOf(
                json.asObject().get("errorType").asString());
        details = json.asObject().get("details").asString();
    }

    /**
     * @return The generic error type
     */
    public final ErrorType getErrorType() {
        return errorType;
    }

    /**
     * @return The contextual error details
     */
    public final String getDetails() {
        return details;
    }

    /**
     * @return Json as string
     */
    public final String toString() {
        return toJson().toString();
    }

    /**
     * @return The error as Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", Error.class.getName());
        json.add("errorType", errorType.name());
        json.add("details", details);
        return json;
    }

}
