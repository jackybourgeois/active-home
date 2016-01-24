package org.activehome.io.action;

/**
 * Command send to IO for control.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public enum Command {

    /**
     * Switch ON background appliances.
     */
    TURN_ON,
    /**
     * Switch OFF background appliances.
     */
    TURN_OFF,

    /**
     * Start interactive appliances.
     */
    START,
    /**
     * Stop interactive appliances.
     */
    STOP,
    /**
     * Pause interactive appliances.
     */
    PAUSE,
    /**
     * Resume interactive appliances.
     */
    RESUME,

    /**
     * Charge storage device.
     */
    CHARGE,
    /**
     * Discharge storage device.
     */
    DISCHARGE,

    /**
     * force appliance to send a Status notification.
     */
    STATUS,
    /**
     * Set interactive appliances ready to start.
     */
    SET,
    /**
     * The Command string was not recognized as existing command.
     */
    UNKNOWN;

    /**
     * @param cmdStr Command as string
     * @return The Command
     */
    public static Command fromString(final String cmdStr) {
        try {
            return Command.valueOf(cmdStr);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

}
