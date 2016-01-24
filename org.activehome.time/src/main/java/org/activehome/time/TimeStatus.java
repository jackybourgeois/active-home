package org.activehome.time;

/**
 * The TimeStatus indicate if the time is ticking or idled.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public enum TimeStatus {

    /**
     * The time is initialized, ready to start.
     */
    INITIALIZED,
    /**
     * The time is ticking.
     */
    RUNNING,
    /**
     * The time is idled.
     */
    IDLE,
    /**
     * The time is stopped, requires going through
     * the init process to start again
     */
    STOPPED,
    /**
     * Status unknown, before receiving the first tic.
     */
    UNKNOWN
}
