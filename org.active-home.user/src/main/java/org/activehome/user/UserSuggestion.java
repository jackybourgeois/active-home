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


import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.activehome.context.data.Episode;
import org.activehome.context.data.Event;
import org.activehome.context.data.Transformation;
import org.activehome.io.action.Action;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class UserSuggestion {

    private UUID id;
    private Episode episode;
    private long expireTS;

    public UserSuggestion(final Episode episode) {
        id = UUID.randomUUID();
        this.episode = episode;
        this.expireTS = -1;
    }

    public UserSuggestion(final JsonObject json) {
        id = UUID.fromString(json.get("id").asString());
        this.episode = new Episode(json.get("episode").asObject());
        this.expireTS = json.get("expireTS").asLong();
    }

    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", UserSuggestion.class.getName());
        json.add("id", id.toString());
        json.add("episode", episode.toJson());
        json.add("expireTS", expireTS);
        return json;
    }

    @Override
    public final String toString() {
        return toJson().toString();
    }

    public UUID getId() {
        return id;
    }

    public Episode getEpisode() {
        return episode;
    }

    public long getExpireTS() {
        return expireTS;
    }

    public void setExpireTS(long expireTS) {
        this.expireTS = expireTS;
    }
}
