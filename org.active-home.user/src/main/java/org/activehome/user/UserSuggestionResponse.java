package org.activehome.user;

/*
 * #%L
 * Active Home :: User
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
