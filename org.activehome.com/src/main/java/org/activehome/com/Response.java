package org.activehome.com;

/*
 * #%L
 * Active Home :: Com
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
import org.activehome.com.helper.JsonHelper;

import java.util.UUID;

/**
 * A Response is a message in response to a Request with the same ID.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Response extends Message {

    /**
     *
     */
    private final Object result;

    /**
     * @param theId     The unique id of the message (same as origin request).
     * @param theSrc    The source of the Notif
     * @param theDest   The destination of the Notif
     * @param theTS     The time-stamp of the Notif (milliseconds)
     * @param theResult The request's result
     */
    public Response(final UUID theId, final String theSrc, final String theDest,
                    final long theTS, final Object theResult) {
        super(theId, theSrc, theDest, theTS);
        result = theResult;
    }

    /**
     * @param json Json that can be map as Response
     */
    public Response(final JsonObject json) {
        this(json, null);
    }

    /**
     * @param json        Json that can be map as Response
     * @param classLoader The classLoader to look for object classes
     */
    public Response(final JsonObject json, final ClassLoader classLoader) {
        super(json);
        result = JsonHelper.jsonToObject(json.get("result"), classLoader);
    }

    /**
     * @return The response's result.
     */
    public final Object getResult() {
        return result;
    }

    /**
     * Convert the Response into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject response = super.toJson();
        response.add("result", JsonHelper.objectToJson(getResult()));
        return response;
    }
}
