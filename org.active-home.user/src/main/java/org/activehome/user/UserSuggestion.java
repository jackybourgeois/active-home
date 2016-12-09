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
import org.activehome.context.data.Episode;

import java.util.UUID;

/**
 * @author Jacky Bourgeois
 */
public class UserSuggestion {

    /**
     * Unique Id (random).
     */
    private UUID id;
    /**
     * The detail of the suggestion.
     */
    private Episode episode;
    /**
     * When the suggestion will not be expired (UNIX timestamp).
     * Default: -1, no expiration time
     */
    private long expireTS = -1;

    public UserSuggestion(final Episode anEpisode) {
        id = UUID.randomUUID();
        episode = anEpisode;
    }

    public UserSuggestion(final JsonObject json) {
        id = UUID.fromString(json.get("id").asString());
        this.episode = new Episode(json.get("episode").asObject());
        this.expireTS = json.get("expireTS").asLong();
    }

    public final UUID getId() {
        return id;
    }

    public final Episode getEpisode() {
        return episode;
    }

    public final long getExpireTS() {
        return expireTS;
    }

    public final void setExpireTS(final long anExpireTS) {
        expireTS = anExpireTS;
    }

    /**
     * Convert the UserSuggestion into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", UserSuggestion.class.getName());
        json.add("id", id.toString());
        json.add("episode", episode.toJson());
        json.add("expireTS", expireTS);
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
