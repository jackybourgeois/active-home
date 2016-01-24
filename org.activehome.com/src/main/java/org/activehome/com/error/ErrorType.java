package org.activehome.com.error;

/**
 * The generic type of errors.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
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
