package org.activehome.tools;

/*
 * #%L
 * Active Home :: Tools
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
 * This class facilitate conversions.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public final class Convert {

    /**
     * Utility class.
     */
    private Convert() {
    }

    /**
     * @param val The string value to convert
     * @return Result in milliseconds
     */
    public static long strDurationToMillisec(final String val) {
        if (val.endsWith("d")) return Integer.valueOf(val.replace("d", "")) * 86400000;
        if (val.endsWith("h")) return Integer.valueOf(val.replace("h", "")) * 3600000;
        if (val.endsWith("mn")) return Integer.valueOf(val.replace("mn", "")) * 60000;
        try {
            return Long.valueOf(val);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Convert Watt into kiloWatt-hour
     * @param power power in watt
     * @param duration duration in milliseconds
     * @return
     */
    public static double watt2kWh(final double power, final long duration) {
        return (power / 1000.) * (duration / 3600000.);
    }

}
