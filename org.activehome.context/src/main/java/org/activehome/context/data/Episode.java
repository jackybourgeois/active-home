package org.activehome.context.data;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Episode {

    private UUID id;
    private LinkedList<Event> events;
    private HashMap<String, AlternativeEpisode> alternatives;

    public Episode(final LinkedList<Event> events) {
        id = UUID.randomUUID();
        this.events = events;
        alternatives = new HashMap<>();
    }

    public Episode() {
        this(new LinkedList<>());
    }

    public Episode(final JsonObject json) {
        id = UUID.fromString(json.get("id").asString());
        events = new LinkedList<>();
        for (JsonValue jsonEvent : json.get("events").asArray()) {
            events.add(new Event(jsonEvent.asObject()));
        }

        alternatives = new HashMap<>();
        JsonObject jsonImpacts = json.get("alternatives").asObject();
        for (String version : jsonImpacts.names()) {
            alternatives.put(version, new AlternativeEpisode(jsonImpacts.get(version).asObject()));
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.add("type", Episode.class.getName());
        json.add("id", id.toString());

        JsonArray eventArray = new JsonArray();
        for (Event event : events) {
            eventArray.add(event.toJson());
        }
        json.add("events", eventArray);

        JsonObject jsonAlternatives = new JsonObject();
        for (String version : alternatives.keySet()) {
            jsonAlternatives.add(version, alternatives.get(version).toJson());
        }
        json.add("alternatives", jsonAlternatives);

        return json;
    }

    public UUID getId() {
        return id;
    }

    public LinkedList<Event> getEvents() {
        return events;
    }

    public void addAlternative(final String version,
                               final AlternativeEpisode alternative) {
        alternatives.put(version, alternative);
    }

    public HashMap<String, AlternativeEpisode> getAlternatives() {
        return alternatives;
    }

    public String toString() {
        return toJson().toString();
    }

    public Event findEventById(final UUID id) {
        for (Event event : getEvents()) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

}
