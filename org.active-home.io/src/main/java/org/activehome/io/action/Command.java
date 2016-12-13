package org.activehome.io.action;

/*
 * #%L
 * Active Home :: IO
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


/**
 * Command send to IO for control.
 *
 * @author Jacky Bourgeois
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
     * Refresh a status, get latest data.
     */
    REFRESH,
    /**
     * Update the frequency, take one parameter of time interval.
     */
    PACE,
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
