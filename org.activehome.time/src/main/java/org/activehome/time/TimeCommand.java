package org.activehome.time;

/**
 * The TimeCommand disseminated in Tics.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public enum TimeCommand {

    /**
     * No change from the previous Tic.
     */
    CARRYON,
    /**
     * Init the time.
     */
    INIT,
    /**
     * start time.
     */
    START,
    /**
     * stop the time (will not restart without INIT).
     */
    STOP,
    /**
     * pause the time.
     */
    PAUSE,
    /**
     * resume time.
     */
    RESUME

}
