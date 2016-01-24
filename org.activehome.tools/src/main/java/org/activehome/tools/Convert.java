package org.activehome.tools;

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
