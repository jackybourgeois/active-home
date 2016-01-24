package org.activehome.com;

/**
 * The status emitted by system's components.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public enum Status {
    /**
     * Used before INITIALIZED, when the status is unknown.
     */
    UNKNOWN,

    /**
     * When a background appliance is switch ON.
     */
    ON,
    /**
     * When a background appliance is switch OFF.
     */
    OFF,
    /**
     * When a background appliance is not active (low consumption mode).
     */
    STANDBY,

    /**
     * When an interactive appliance is set ready to start.
     */
    READY,
    /**
     * When an interactive appliance load is running.
     */
    RUNNING,
    /**
     * When a storage is charging.
     */
    CHARGING,
    /**
     * When a storage is discharging.
     */
    DISCHARGING,
    /**
     * When an storage or interactive appliance is paused.
     */
    IDLE,
    /**
     * When an interactive appliance load is interrupted.
     */
    STOPPED,
    /**
     * When an interactive appliance load is completed.
     */
    DONE,
    /**
     * When a storage is full.
     */
    FULL,
    /**
     * When a storage is empty.
     */
    EMPTY,

    /**
     * When a background appliance cannot be switched OFF.
     */
    LOCKED_ON,

    /**
     * When a widget is installed.
     */
    INSTALLED,
    /**
     * When a widget is deleted.
     */
    DELETED,
    /**
     * When a widget is resized.
     */
    RESIZED,
    /**
     * When a widget is relocated.
     */
    RELOCATED,
    /**
     * When a widget is updated.
     */
    UPDATED
}
