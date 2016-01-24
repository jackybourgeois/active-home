package org.activehome.tools;

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
