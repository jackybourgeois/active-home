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


import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.activehome.com.helper.JsonHelper;

/**
 * A notification is a Message which does not require a response.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Notif extends Message {

    /**
     * The content of the notification.
     */
    private Object content = null;

    /**
     * @param theSrc     The source of the Notif
     * @param theDest    The destination of the Notif
     * @param theTS      The time-stamp of the Notif (milliseconds)
     * @param theContent The content of the Notif
     */
    public Notif(final String theSrc, final String theDest,
                 final long theTS, final Object theContent) {
        super(theSrc, theDest, theTS);
        content = theContent;
    }

    /**
     * @param jsonData Json that can be map as Notif
     */
    public Notif(final JsonObject jsonData) {
        super(jsonData);
        JsonValue json = jsonData.get("content");
        if (!json.isNull()) {
            content = JsonHelper.jsonToObject(json);
        }
    }

    /**
     * @return The content of the Notification
     */
    public final Object getContent() {
        return content;
    }

    /**
     * Convert the Notif into Json.
     *
     * @return the Json
     */
    @Override
    public final JsonObject toJson() {
        JsonObject notif = super.toJson();
        notif.add("content", JsonHelper.objectToJson(content));
        return notif;
    }

}
