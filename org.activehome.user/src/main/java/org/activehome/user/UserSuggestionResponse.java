package org.activehome.user;

import com.eclipsesource.json.JsonObject;
import org.activehome.context.data.Event;
import org.activehome.io.action.Action;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class UserSuggestionResponse {

    private UUID suggestionId;
    private Event event;
    private boolean automatedBySystem;

    public UserSuggestionResponse(final UUID suggestionId,
                                  final Event event,
                                  final boolean automatedBySystem) {
        this.suggestionId = suggestionId;
        this.event = event;
        this.automatedBySystem = automatedBySystem;
    }

    public UserSuggestionResponse(JsonObject jsonObject) {
        this.suggestionId = UUID.fromString(jsonObject.get("suggestionId").asString());
        this.event = new Event(jsonObject.get("event").asObject());
        this.automatedBySystem = jsonObject.get("automatedBySystem").asBoolean();
    }

    public UUID getSuggestionId() {
        return suggestionId;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isAutomatedBySystem() {
        return automatedBySystem;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", UserSuggestionResponse.class.getName());
        json.add("suggestionId", suggestionId.toString());
        json.add("event", event.toJson());
        json.add("automatedBySystem", automatedBySystem);
        return json;
    }

    public String toString() {
        return toJson().toString();
    }

}
