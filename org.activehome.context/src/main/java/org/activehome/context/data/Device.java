package org.activehome.context.data;

import java.util.HashMap;

/**
 * Wrap of a metric as 'Device' (for terminology).
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Device extends Metric {

    private boolean isInterruptable;
    private boolean isReducible;
    private boolean isNegotiable;

    /**
     * @param id The device id
     * @param description The description
     * @param type The type
     * @param attrMap The map of attributes
     */
    public Device(final String id,
                  final String description,
                  final String type,
                  final HashMap<String, String> attrMap) {
        super(id, description, type, attrMap);
        if (attrMap.containsKey("isInterruptable")) {
            isInterruptable = Boolean.valueOf(attrMap.get("isInterruptable"));
        }
        if (attrMap.containsKey("isReducible")) {
            isReducible = Boolean.valueOf(attrMap.get("isReducible"));
        }
        if (attrMap.containsKey("isNegotiable")) {
            isNegotiable = Boolean.valueOf(attrMap.get("isNegotiable"));
        }
    }

    /**
     * @param id The device id
     */
    public Device(final String id) {
        super(id, "", "", new HashMap<>());
    }

    public void setParams(final boolean interruptable,
                          final boolean reducible,
                          final boolean negotiable) {
        isInterruptable = interruptable;
        isReducible = reducible;
        isNegotiable = negotiable;
    }

    public boolean isInterruptable() {
        return isInterruptable;
    }

    public boolean isReducible() {
        return isReducible;
    }

    public boolean isNegotiable() {
        return isNegotiable;
    }
}
