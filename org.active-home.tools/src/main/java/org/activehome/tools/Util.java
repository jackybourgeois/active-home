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


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Couple of utilities that does not fit anywhere else!
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public final  class Util {

    /**
     * Utility class.
     */
    private Util() {
    }

    /**
     * @param value The value to round
     * @return The value round at the 5th deciaml
     */
    public static double round5(final double value) {
        return Math.round(value * 100000.) / 100000.;
    }

    /**
     * @param fileName The targeted file
     * @return Properties extracted from the file
     */
    public static Properties loadProperties(final String fileName) {
        return loadProperties("", fileName);
    }

    /**
     * @param folderName The targeted folder
     * @param fileName The targeted file
     * @return Properties extracted from the file
     */
    private static Properties loadProperties(final String folderName, final String fileName) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(folderName + "/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

}
