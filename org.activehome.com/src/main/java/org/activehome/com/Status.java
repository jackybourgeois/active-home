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
