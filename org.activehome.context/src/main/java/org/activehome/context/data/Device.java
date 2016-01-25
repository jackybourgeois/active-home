package org.activehome.context.data;

/*
 * #%L
 * Active Home :: Context
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
