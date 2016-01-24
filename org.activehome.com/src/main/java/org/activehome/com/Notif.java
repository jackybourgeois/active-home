package org.activehome.com;

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
