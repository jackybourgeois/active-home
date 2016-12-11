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
import java.util.UUID;

/**
 * @author Jacky Bourgeois
 */
public class Episode {

    private UUID id;
    private LinkedList<Event> events;

    public Episode(final LinkedList<Event> events) {
        id = UUID.randomUUID();
        this.events = events;
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

        return json;
    }

    public UUID getId() {
        return id;
    }

    public LinkedList<Event> getEvents() {
        return events;
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
