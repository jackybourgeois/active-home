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


import com.eclipsesource.json.JsonObject;

import java.util.HashMap;

/**
 * A Metric describe a computed or collected value.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Metric {

    /**
     * The metric id.
     */
    private final String id;
    /**
     * The metric description.
     */
    private final String description;
    /**
     * The metric type.
     */
    private final String type;
    /**
     * The attribute map.
     */
    private final HashMap<String, String> attributeMap;

    /**
     * @param theId The metric id
     * @param theDescription The description
     * @param theType The type
     * @param theAttributeMap The attribute map
     */
    protected Metric(final String theId,
                     final String theDescription,
                     final String theType,
                     final HashMap<String, String> theAttributeMap) {
        id = theId;
        description = theDescription;
        type = theType;
        attributeMap = theAttributeMap;
    }

    /**
     * @return The metric id
     */
    public final String getID() {
        return id;
    }

    /**
     * @return The metric description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @return The metric type
     */
    public final String getType() {
        return type;
    }

    /**
     * @return The attribute map
     */
    public final HashMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    /**
     * Convert the Metric into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("id", id);
        json.add("description", description);
        json.add("metricType", type);
        JsonObject jsonAttr = new JsonObject();
        for (String key : attributeMap.keySet()) {
            jsonAttr.add(key, attributeMap.get(key));
        }
        json.add("attributeMap", jsonAttr);
        return json;
    }

    /**
     * @return The Json as String
     */
    @Override
    public final String toString() {
        return toJson().toString();
    }
}
