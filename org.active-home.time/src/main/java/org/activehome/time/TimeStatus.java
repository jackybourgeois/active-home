package org.activehome.time;

/*
 * #%L
 * Active Home :: Time
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
