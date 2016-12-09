package org.activehome.user;

/*
 * #%L
 * Active Home :: User
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


import com.eclipsesource.json.JsonObject;
import org.activehome.context.data.Event;

import java.util.UUID;

/**
 * @author Jacky Bourgeois
 */
public class UserSuggestionResponse {

    /**
     * Unique Identifier of the original suggestion.
     */
    private UUID suggestionId;
    /**
     * The related event.
     */
    private Event event;
    /**
     * Should this response be automated by the system?
     */
    private boolean automatedBySystem;

    public UserSuggestionResponse(final UUID aSuggestionId,
                                  final Event anEvent,
                                  final boolean anAutomatedBySystem) {
        suggestionId = aSuggestionId;
        event = anEvent;
        automatedBySystem = anAutomatedBySystem;
    }

    public UserSuggestionResponse(final JsonObject jsonObject) {
        suggestionId = UUID.fromString(
                jsonObject.get("suggestionId").asString());
        event = new Event(jsonObject.get("event").asObject());
        automatedBySystem = jsonObject.get("automatedBySystem").asBoolean();
    }

    public final UUID getSuggestionId() {
        return suggestionId;
    }

    public final Event getEvent() {
        return event;
    }

    public final boolean isAutomatedBySystem() {
        return automatedBySystem;
    }

    /**
     * Convert the UserSuggestionResponse into Json.
     *
     * @return the Json
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", UserSuggestionResponse.class.getName());
        json.add("suggestionId", suggestionId.toString());
        json.add("event", event.toJson());
        json.add("automatedBySystem", automatedBySystem);
        return json;
    }

    /**
     * @return The Json as String
     */
    @Override
    public String toString() {
        return toJson().toString();
    }

}
