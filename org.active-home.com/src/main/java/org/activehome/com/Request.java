package org.activehome.com;

/*
 * #%L
 * Active Home :: Com
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
import org.activehome.com.helper.JsonHelper;

import java.util.HashMap;

/**
 * A request is a message which require a response.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Request extends Message {

    public static final long DEFAULT_TIMEOUT = 15000;

    /**
     * The requested method.
     */
    private final String method;
    /**
     * The method's parameters.
     */
    private final Object[] params;
    /**
     * The time before being expired.
     */
    private long timeOut;
    /**
     * The environment variable.
     */
    private final HashMap<String, Object> enviVarMap;

    /**
     * @param theSrc    The source of the request
     * @param theDest   The destination of the request
     * @param theTS     The time-stamp of the request
     * @param theMethod The method of the request
     * @param theParams The method's parameters
     */
    public Request(final String theSrc, final String theDest, final long theTS,
                   final String theMethod, final Object[] theParams) {
        super(theSrc, theDest, theTS);
        method = theMethod;
        params = theParams;
        enviVarMap = new HashMap<>();
        timeOut = DEFAULT_TIMEOUT;
    }

    /**
     * @param theSrc    The source of the request
     * @param theDest   The destination of the request
     * @param theTS     The time-stamp of the request
     * @param theMethod The method of the request
     */
    public Request(final String theSrc, final String theDest, final long theTS,
                   final String theMethod) {
        this(theSrc, theDest, theTS, theMethod, new Object[]{});
    }

    /**
     * @param req Request to copy
     */
    public Request(final Request req) {
        super(req.getId(), req.getSrc(), req.getDest(), req.getTS());
        method = req.getMethod();
        params = req.getParams();
        enviVarMap = new HashMap<>();
        for (String key : req.getEnviElem().keySet()) {
            enviVarMap.put(key, req.getEnviElem().get(key));
        }
        timeOut = req.getTimeOut();
    }

    /**
     * @param json Json that can be map as Request
     */
    public Request(final JsonObject json) {
        this(json, null);
    }

    /**
     * @param json        Json that can be map as Notif
     * @param classLoader The classLoader to look for object classes
     */
    public Request(final JsonObject json, final ClassLoader classLoader) {
        super(json);
        method = json.get("method").asString();
        JsonArray jsonArray = json.get("params").asArray();
        if (jsonArray != null) {
            int len = jsonArray.size();
            Object[] paramArray = new Object[len];
            for (int i = 0; i < len; i++) {
                paramArray[i] =
                        JsonHelper.jsonToObject(jsonArray.get(i), classLoader);
            }
            params = paramArray;
        } else {
            params = new Object[]{};
        }
        enviVarMap = new HashMap<>();

        if (json.get("env") != null) {
            for (JsonObject.Member member : json.get("env").asObject()) {
                enviVarMap.put(member.getName(),
                        JsonHelper.jsonToObject(member.getValue()));
            }
        }

        timeOut = json.getLong("timeout", DEFAULT_TIMEOUT);
    }

    /**
     * @return The method of the request
     */
    public final String getMethod() {
        return method;
    }

    /**
     * @return The parameters of the request
     */
    public final Object[] getParams() {
        return params;
    }

    /**
     * @return The environment variable of the request
     */
    public final HashMap<String, Object> getEnviElem() {
        return enviVarMap;
    }

    /**
     * @return The time out of the request
     */
    public final long getTimeOut() {
        return timeOut;
    }

    /**
     * @param theTimeOut The time out to set
     */
    public final void setTimeOut(final long theTimeOut) {
        timeOut = theTimeOut;
    }

    /**
     * Convert the Request into Json.
     *
     * @return the Json
     */
    @Override
    public JsonObject toJson() {
        JsonObject request = super.toJson();
        request.add("method", getMethod());

        JsonArray paramsArray = new JsonArray();
        for (Object obj : params) {
            paramsArray.add(JsonHelper.objectToJson(obj));
        }
        request.add("params", paramsArray);

        JsonObject enviJson = new JsonObject();
        for (String key : enviVarMap.keySet()) {
            enviJson.add(key, JsonHelper.objectToJson(enviVarMap.get(key)));
        }
        request.add("env", enviJson);

        request.add("timeOut", timeOut);
        return request;
    }

}
