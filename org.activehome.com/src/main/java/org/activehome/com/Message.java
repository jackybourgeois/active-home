package org.activehome.com;

import com.eclipsesource.json.JsonObject;

import java.io.Serializable;
import java.util.UUID;

/**
 * The Message is the generic communication in Active Home.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public abstract class Message implements Serializable {

    /**
     * The unique id of the message.
     */
    private final UUID id;
    /**
     * The source of the message.
     */
    private final String src;
    /**
     * The destination of the message.
     */
    private final String dest;
    /**
     * The timestamp of the message, in milliseconds.
     */
    private final long ts;
    /**
     * The sequence that includes this request
     */
    private String sequence = "";
    /**
     * The sequence number
     */
    private long sequenceNumber = -1;

    /**
     * @param theId   the id of the message
     * @param theSrc  the source of the message
     * @param theDest the destination of the message
     * @param theTS   the timestamp of the message
     */
    public Message(final UUID theId,
                   final String theSrc,
                   final String theDest,
                   final long theTS) {
        id = theId;
        src = theSrc;
        dest = theDest;
        ts = theTS;
    }

    /**
     * Constructor with auto-generated id.
     *
     * @param theSrc  the source of the message
     * @param theDest the destination of the message
     * @param theTS   the time-stamp of the message
     */
    public Message(final String theSrc,
                   final String theDest,
                   final long theTS) {
        this(UUID.randomUUID(), theSrc, theDest, theTS);
    }

    /**
     * @param json Json that can be map as Message
     */
    public Message(final JsonObject json) {
        id = UUID.fromString(json.get("id").asString());
        src = json.get("src").asString();
        dest = json.get("dest").asString();
        ts = json.get("ts").asLong();
        sequence = json.getString("sequence", "");
        sequenceNumber = json.getLong("seq", -1);
    }

    /**
     * @return The unique Id of the message
     */
    public final UUID getId() {
        return id;
    }

    /**
     * @return The source of the message
     */
    public final String getSrc() {
        return src;
    }

    /**
     * @return The destination of the message
     */
    public final String getDest() {
        return dest;
    }

    /**
     * @return The time-stamp of the message (milliseconds)
     */
    public final long getTS() {
        return ts;
    }

    /**
     * Convert the Message into Json.
     *
     * @return the Json
     */
    public JsonObject toJson() {
        JsonObject msg = new JsonObject();
        msg.add("id", getId().toString());
        msg.add("src", getSrc());
        msg.add("dest", getDest());
        msg.add("ts", getTS());
        msg.add("sequence", sequence);
        msg.add("seq", getSequenceNumber());
        return msg;
    }

    /**
     * @return The Json as String
     */
    public final String toString() {
        return toJson().toString();
    }


    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(final long theSequenceNumber) {
        sequenceNumber = theSequenceNumber;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(final String theSequence) {
        sequence = theSequence;
    }
}

