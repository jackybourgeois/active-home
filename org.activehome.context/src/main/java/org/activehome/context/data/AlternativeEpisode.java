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


import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class AlternativeEpisode {

    private String version;
    private LinkedList<Transformation> transformations;
    private HashMap<String, Double> impacts;

    public AlternativeEpisode(final String version,
                              final LinkedList<Transformation> transformations,
                              final HashMap<String, Double> impacts) {
        this.version = version;
        this.transformations = transformations;
        this.impacts = impacts;
    }

    public AlternativeEpisode(final JsonObject json) {
        version = json.get("version").asString();

        transformations = new LinkedList<>();
        for (JsonValue jsonEvent : json.get("transformations").asArray()) {
            transformations.add(new Transformation(jsonEvent.asObject()));
        }

        impacts = new HashMap<>();
        JsonObject jsonImpacts = json.get("impacts").asObject();
        for (String objectiveName : jsonImpacts.names()) {
            impacts.put(objectiveName, jsonImpacts.get(objectiveName).asDouble());
        }

    }

    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", AlternativeEpisode.class.getName());
        json.add("version", version);

        JsonArray transArray = new JsonArray();
        for (Transformation trans : transformations) {
            transArray.add(trans.toJson());
        }
        json.add("transformations", transArray);

        JsonObject jsonImpact = new JsonObject();
        for (String objectiveName : impacts.keySet()) {
            jsonImpact.add(objectiveName, impacts.get(objectiveName));
        }
        json.add("impacts", jsonImpact);

        return json;
    }

    public String getVersion() {
        return version;
    }

    public LinkedList<Transformation> getTransformations() {
        return transformations;
    }

    public HashMap<String, Double> getImpacts() {
        return impacts;
    }

    @Override
    public final String toString() {
        return toJson().toString();
    }

}
