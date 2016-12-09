package org.activehome.com.error;

/*-
 * #%L
 * Active Home :: Com
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

/**
 * The generic type of errors.
 *
 * @author Jacky Bourgeois
 */
public enum ErrorType {

    // general
    /**
     * The request/method did not have the correct parameters.
     */
    WRONG_PARAM(1, "Wrong parameters."),
    /**
     * The required method is unknown.
     */
    NO_SUCH_METHOD(2, "No such method."),
    /**
     * The method failed.
     */
    METHOD_ERROR(3, "Internal method error."),
    /**
     * The method access is illegal (private/protected).
     */
    ILLEGAL_ACCESS(4, "Illegal access."),
    /**
     * The method replied with error.
     */
    RESPONSE_ERROR(5, "Response error."),

    // context
    /**
     * Unable to retrieve the requested path in the context.
     */
    NOT_FOUND(100, "Path not found in the context."),
    /**
     * Enable to save in the context.
     */
    SAVE_ERROR(102, "Save Error"),

    // runtime
    /**
     * The model is null.
     */
    MODEL_NULL(200, "The model is null"),
    /**
     * The component failed to start.
     */
    START_ERROR(201, "Component start error."),
    /**
     * The component failed to stop.
     */
    STOP_ERROR(202, "Component stop error."),
    /**
     * The model failed to update.
     */
    MODEL_UPDATE_FAILED(203, "Model update failed."),

    // services
    /**
     * The current schedule is already optimized.
     */
    NOTHING_TO_SCHEDULE(301, "Nothing to schedule"),

    // Security
    /**
     * The necessary authentication is missing.
     */
    PERMISSION_DENIED(400, "You do not have enough permission "
            + "to perform this request."),

    // Network
    /**
     * The network connection could not be established.
     */
    NO_CONNECTION(500, "No connection");

    /**
     * the error number.
     */
    private final int number;
    /**
     * the error generic message.
     */
    private final String message;

    /**
     * @param theNumber  the error number
     * @param theMessage the error generic message
     */
    ErrorType(final int theNumber, final String theMessage) {
        number = theNumber;
        message = theMessage;
    }

    /**
     * @return The error number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return The generic message
     */
    public String getMessage() {
        return message;
    }

}
