package org.activehome.com;

/*-
 * #%L
 * Active Home :: Com
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 Active Home Project
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

import java.io.Serializable;
import java.util.UUID;

/**
 * The Message is the generic communication in Active Home.
 *
 * @author Jacky Bourgeois
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
     * The sequence that includes this request.
     */
    private String sequence = "";
    /**
     * The sequence number.
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


    public final long getSequenceNumber() {
        return sequenceNumber;
    }

    public final void setSequenceNumber(final long theSequenceNumber) {
        sequenceNumber = theSequenceNumber;
    }

    public final String getSequence() {
        return sequence;
    }

    public final void setSequence(final String theSequence) {
        sequence = theSequence;
    }
}

