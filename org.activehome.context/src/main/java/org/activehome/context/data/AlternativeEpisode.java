package org.activehome.context.data;

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
