package org.activehome.com;

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
