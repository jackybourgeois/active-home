package org.activehome.context.com;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.activehome.context.data.Schedule;

import java.util.TreeMap;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class ContextResponse {

    private ContextRequest request;
    private TreeMap<Integer, Schedule> resultMap;

    public ContextResponse(final ContextRequest theRequest,
                           final TreeMap<Integer, Schedule> theResultMap) {
        request = theRequest;
        resultMap = theResultMap;
    }

    public ContextResponse(final JsonObject json) {
        request = new ContextRequest(json.get("request").asObject());
        resultMap = new TreeMap<>();
        for (JsonValue jsonValue : json.asObject().get("result").asArray()) {
            Integer key = jsonValue.asObject().get("iteration").asInt();
            Schedule schedule = new Schedule(jsonValue.asObject().get("schedule").asObject());
            resultMap.put(key, schedule);
        }
    }

    public ContextRequest getRequest() {
        return request;
    }

    public TreeMap<Integer, Schedule> getResultMap() {
        return resultMap;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", ContextResponse.class.getName());
        json.add("request", request.toJson());
        JsonArray array = new JsonArray();
        for (int key : resultMap.keySet()) {
            JsonObject entryJson = new JsonObject();
            entryJson.add("iteration", key);
            entryJson.add("schedule", resultMap.get(key).toJson());
            array.add(entryJson);
        }
        json.add("result", array);
        return json;
    }

    public String toString() {
        return toJson().toString();
    }
}
