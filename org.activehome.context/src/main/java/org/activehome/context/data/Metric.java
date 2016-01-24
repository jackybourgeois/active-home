package org.activehome.context.data;

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
