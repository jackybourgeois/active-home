package org.activehome.user;

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
