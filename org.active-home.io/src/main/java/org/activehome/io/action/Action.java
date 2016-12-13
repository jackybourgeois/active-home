package org.activehome.io.action;

/*
 * #%L
 * Active Home :: IO
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

import java.util.HashMap;
import java.util.UUID;

/**
 * Define an action to execute.
 */
public class Action {
    /**
     * Unique id of the action.
     */
    private final UUID id;
    /**
     * The command to execute to start the action.
     */
    private final Command startCmd;
    /**
     * The command to execute to end the action.
     */
    private final Command endCmd;
    /**
     * Map of impacted metrics and their expected impact.
     */
    private final HashMap<String, Double> impact;
    /**
     * The action duration (between start and end command).
     */
    private long duration;
    /**
     * Date after which it is no longer relevant to execute the action.
     */
    private long expireDate;
    /**
     * Unique id of the related schedule.
     */
    private UUID scheduleID;
    /**
     * Is this action outdated?
     */
    private boolean outDated = false;

    public Action(final Command theStartCmd,
                  final Command theEndCmd,
                  final HashMap<String, Double> theImpact,
                  final long theDuration) {
        id = UUID.randomUUID();
        startCmd = theStartCmd;
        endCmd = theEndCmd;
        impact = theImpact;
        duration = theDuration;
    }

    public Action(final JsonObject json) {
        id = UUID.fromString(json.get("id").asString());
        startCmd = Command.fromString(json.get("startCmd").asString());
        endCmd = Command.fromString(json.get("endCmd").asString());

        impact = new HashMap<>();
        for (String name : json.get("impact").asObject().names()) {
            impact.put(name, json.get("impact")
                    .asObject().get(name).asDouble());
        }

        duration = json.get("duration").asLong();
        expireDate = json.get("expireDate").asLong();
        scheduleID = UUID.fromString(json.get("scheduleId").asString());
        outDated = json.get("isOutdated").asBoolean();
    }

    public final Command getStartCmd() {
        return startCmd;
    }

    public final Command getEndCmd() {
        return endCmd;
    }

    public final HashMap<String, Double> getImpact() {
        return impact;
    }

    public final long getDuration() {
        return duration;
    }

    public final void setDuration(final long aDuration) {
        duration = aDuration;
    }

    public final long getExpireDate() {
        return expireDate;
    }

    public final void setExpireDate(final long anExpireDate) {
        expireDate = anExpireDate;
    }

    public final void setScheduleID(final UUID aScheduleID) {
        scheduleID = aScheduleID;
    }

    public final UUID getId() {
        return id;
    }

    public final UUID getScheduleID() {
        return scheduleID;
    }

    public final boolean isOutDated() {
        return outDated;
    }

    public final void setOutDated() {
        outDated = true;
    }

    /**
     * Convert the Action into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", Action.class.getName());
        json.add("id", getId().toString());
        json.add("startCmd", getStartCmd().name());
        json.add("endCmd", getEndCmd().name());

        JsonObject jsonImpact = new JsonObject();
        for (String key : impact.keySet()) {
            jsonImpact.add(key, impact.get(key));
        }
        json.add("impact", jsonImpact);

        json.add("duration", getDuration());
        json.add("expireDate", getExpireDate());
        json.add("scheduleID", getScheduleID().toString());
        json.add("isOutdated", isOutDated());

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
