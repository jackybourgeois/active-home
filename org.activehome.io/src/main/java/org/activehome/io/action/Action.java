package org.activehome.io.action;

import com.eclipsesource.json.JsonObject;

import java.util.HashMap;
import java.util.UUID;

public class Action {

    private final UUID id;
    private final Command startCmd;
    private final Command endCmd;
    private final HashMap<String, Double> impact;
    private long duration;
    private long expireDate;
    private UUID scheduleID;
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
            impact.put(name, json.get("impact").asObject().get(name).asDouble());
        }

        duration = json.get("duration").asLong();
        expireDate = json.get("expireDate").asLong();
        scheduleID = UUID.fromString(json.get("scheduleId").asString());
        outDated = json.get("isOutdated").asBoolean();
    }

    public Command getStartCmd() {
        return startCmd;
    }

    public Command getEndCmd() {
        return endCmd;
    }

    public HashMap<String, Double> getImpact() {
        return impact;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public void setScheduleID(UUID scheduleID) {
        this.scheduleID = scheduleID;
    }

    public UUID getId() {
        return id;
    }

    public UUID getScheduleID() {
        return scheduleID;
    }

    public boolean isOutDated() {
        return outDated;
    }

    public void setOutDated() {
        outDated = true;
    }

    public JsonObject toJson() {
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

    public String toString() {
        return toJson().toString();
    }

}
