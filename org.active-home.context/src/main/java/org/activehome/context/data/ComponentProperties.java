package org.activehome.context.data;

/*
 * #%L
 * Active Home :: Context
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


import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Description of a Kevoree component to be start.
 *
 * @author Jacky Bourgeois
 */
public class ComponentProperties {

    /**
     * The component instance name.
     */
    private String id;
    /**
     * The name of the component Java class.
     */
    private String componentName;
    /**
     * The additional nodes to search in th Kevoree model for
     * channel to bind with the component.
     */
    private String[] externalNodes;
    /**
     * The map of port to bind to destinations.
     */
    private final HashMap<String, LinkedList<String>> portDestinationMap;
    /**
     * The list of attributes to set in the Kevoree component dictionary.
     */
    private final HashMap<String, String> attributeMap;

    /**
     * @param theComponentName Java class name
     * @param theId instance name
     * @param attributes map of attributes
     * @param theExternalNodes array of nodes to search for binding
     */
    public ComponentProperties(final String theComponentName,
                               final String theId,
                               final HashMap<String, String> attributes,
                               final String[] theExternalNodes) {
        componentName = theComponentName;
        id = theId;
        portDestinationMap = new HashMap<>();
        attributeMap = attributes;
        externalNodes = theExternalNodes;
    }

    /**
     * @param theComponentName Java class name
     * @param theId instance name
     * @param attributes map of attributes
     * @param theExternalNodes array of nodes to search for binding
     */
    public ComponentProperties(final String theComponentName,
                               final String theId,
                               final JsonObject attributes,
                               final String[] theExternalNodes) {
        componentName = theComponentName;
        id = theId;
        portDestinationMap = new HashMap<>();
        attributeMap = new HashMap<>();
        externalNodes = theExternalNodes;
        for (JsonObject.Member member : attributes) {
            attributeMap.put(member.getName(), member.getValue().asString());
        }
    }

    /**
     * @param theComponentName Java class name
     * @param theId instance name
     */
    public ComponentProperties(final String theComponentName,
                               final String theId) {
        this(theComponentName, theId, new JsonObject(), new String[]{});
    }

    /**
     * @param json Json that can be map as ComponentProperties
     */
    public ComponentProperties(final JsonObject json) {
        id = json.get("id").asString();
        componentName = json.get("componentName").asString();
        portDestinationMap = new HashMap<>();
        attributeMap = new HashMap<>();
        JsonValue jsonAttr = json.get("attributeMap");
        if (jsonAttr != null) {
            for (JsonObject.Member member : jsonAttr.asObject()) {
                attributeMap.put(member.getName(),
                        member.getValue().asString());
            }
        }
        JsonArray array = json.get("externalNodes").asArray();
        externalNodes = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            externalNodes[i] = array.get(i).asString();
        }
    }

    /**
     * @return The name of the component's instance
     */
    public final String getId() {
        return id;
    }

    /**
     * @param theId The name of the component's instance
     */
    public final void setId(final String theId) {
        id = theId;
    }

    /**
     * @return The Java class of the component
     */
    public final String getComponentName() {
        return componentName;
    }

    /**
     * @param theComponentName The Java class of the component
     */
    public final void setComponentName(final String theComponentName) {
        componentName = theComponentName;
    }

    /**
     * @return The map of destination ports
     */
    public final HashMap<String, LinkedList<String>> getPortDestinationMap() {
        return portDestinationMap;
    }

    /**
     * @return The map of attributes
     */
    public final HashMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    /**
     * @return The array of external nodes
     */
    public final String[] getExternalNodes() {
        return externalNodes;
    }

    /**
     * Convert the ComponentProperties into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", this.getClass().getName());
        json.add("componentName", componentName);
        json.add("id", id);
        JsonObject attrMap = new JsonObject();
        for (String key : attributeMap.keySet()) {
            attrMap.add(key, attributeMap.get(key));
        }
        json.add("attributeMap", attrMap);

        JsonArray nodeArray = new JsonArray();
        for (String str : externalNodes) {
            nodeArray.add(str);
        }
        json.add("externalNodes", nodeArray);
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
