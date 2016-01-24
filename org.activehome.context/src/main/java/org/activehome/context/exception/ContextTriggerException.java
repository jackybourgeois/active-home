package org.activehome.context.exception;

/**
 * Exception thrown when a trigger cannot be parsed
 * or a default value is missing.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class ContextTriggerException extends Exception {

    /**
     * @param message Exception's message
     */
    public ContextTriggerException(final String message) {
        super(message);
    }
}
