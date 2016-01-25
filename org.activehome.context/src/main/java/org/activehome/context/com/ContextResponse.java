package org.activehome.context.com;

/*
 * #%L
 * Active Home :: Context
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
