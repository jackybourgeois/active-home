package org.activehome.time;

/*
 * #%L
 * Active Home :: Time
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
