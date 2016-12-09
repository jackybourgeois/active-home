package org.activehome.user;

/*
 * #%L
 * Active Home :: User
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
import org.activehome.com.Message;
import org.activehome.com.Notif;
import org.activehome.com.Request;
import org.activehome.com.Response;
import org.activehome.com.error.ErrorType;
import org.activehome.com.error.Error;
import org.activehome.time.TimeControlled;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.api.ModelService;
import org.kevoree.api.Port;
import org.kevoree.log.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * @author Jacky Bourgeois
 */
@ComponentType(version = 1, description = "The hub between "
        + "the physical user and the system.")
public class User extends TimeControlled {

    @Param(defaultValue = "/active-home/tree/master/org.active-home.user")
    private String src;

    /**
     * The necessary bindings.
     */
    @Param(defaultValue = "toAPI>API.sendOutside,"
            + "fromAPI>API.pushToUser,"
            + "pushCmd>IO.ctrl,"
            + "pushRequest>Service.getRequest,"
            + "getResponse>Service.pushResponse")
    private String bindingUser;


    /**
     * Access to Kevoree's model.
     */
    @KevoreeInject
    private ModelService modelService;

    /**
     * Received request waiting a response.
     */
    private HashMap<UUID, Request> waitingRequest;

    protected LinkedList<UserSuggestion> userSuggestions;

    /**
     * Port to send outside through APIs.
     */
    @Output
    private org.kevoree.api.Port toAPI;
    /**
     * Port to push user request to the system,
     * received from the actual user via APIs.
     */
    @Output
    private org.kevoree.api.Port pushRequest;
    /**
     * Port to push user notification to the system,
     * received from the actual user via APIs.
     */
    @Output
    private org.kevoree.api.Port pushNotif;
    /**
     * Port to push user update to subscribers.
     */
    @Output
    private org.kevoree.api.Port toSubscriber;
    /**
     * Port to send command to IO.
     */
    @Output
    protected Port pushCmd;

    /**
     * @param msgStr The Message received as API
     */
    @Input
    public final void fromAPI(final String msgStr) {
        Thread.currentThread().setContextClassLoader(
                this.getClass().getClassLoader());
        JsonObject json = JsonObject.readFrom(msgStr);
        Message message;
        if (json.get("method") != null) {
            message = new Request(JsonObject.readFrom(msgStr));
        } else if (json.get("result") != null) {
            message = new Response(JsonObject.readFrom(msgStr));
        } else {
            message = new Notif(JsonObject.readFrom(msgStr));
        }
        if (message.getSrc().contains("://" + getFullId() + "@")) {
            String[] msgSrc = message.getSrc().split("(://)|@|:");
            if (msgSrc.length > 2 && msgSrc[1].compareTo(getFullId()) == 0) {
                if (message instanceof Notif) {
                    Notif notif = (Notif) message;
                    // TODO
                } else if (message instanceof Request) {
                    manageRequestFromAPI((Request) message,
                            msgSrc[0], msgSrc[2]);
                }
            }
        }

    }

    private void manageRequestFromAPI(final Request request,
                                      final String apiName,
                                      final String destName) {
        if (checkRight(request)) {
            Request req = new Request(getFullId(),
                    request.getDest(), getCurrentTime(),
                    request.getMethod(), request.getParams());
            req.getEnviElem().putAll(request.getEnviElem());
            req.getEnviElem().put("api", apiName + "://" + destName);
            waitingRequest.put(req.getId(), request);
            pushRequest.send(req.toString(), null);
        } else {
            Error error = new Error(ErrorType.PERMISSION_DENIED,
                    "Request " + request.getMethod()
                            + " requires a higher level of permission.");
            toAPI.send(new Response(request.getId(), getFullId(),
                    request.getSrc(), getCurrentTime(),
                    error.toJson()).toString(), null);
        }
    }

    /**
     * @param respStr Received Response
     */
    @Input
    public final void getResponse(final String respStr) {
        Thread.currentThread().setContextClassLoader(
                this.getClass().getClassLoader());
        Response response = new Response(JsonObject.readFrom(respStr));
        if (response.getDest().compareTo(getFullId()) == 0) {
            if (waitingRequest.containsKey(response.getId())) {
                // TODO check availability and send the
                // response on a different API in case the user
                // is not available any more on this API
                Request req = waitingRequest.get(response.getId());
                Response resp = new Response(req.getId(), response.getSrc(),
                        req.getSrc(), getCurrentTime(), response.getResult());
                toAPI.send(resp.toString(), null);
            } else {
                Log.info(getFullId() + "UNKNOWN response for : "
                        + response.getResult().toString());
            }
        }
    }

    /**
     * @param notifStr Received Notif as string
     */
    @Input
    public void getNotif(final String notifStr) {
        logInfo("received notif: " + notifStr);
    }

    /**
     * On start, init attributes.
     */
    @Start
    public void start() {
        super.start();
        waitingRequest = new HashMap<>();
    }

    /**
     * If override, this method should be called first.
     */
    @Override
    public void onInit() {
        super.onInit();
        userSuggestions = new LinkedList<>();
    }

    /**
     * When a new UI appears.
     */
    public final void onAddedUI() {
        toSubscriber.send(new Notif(getFullId(), "", getCurrentTime(),
                new UserAvailability()).toString(), null);
    }

    /**
     * When a UI disappears.
     */
    public final void onRemovedUI() {
        toSubscriber.send(new Notif(getFullId(), "", getCurrentTime(),
                new UserAvailability()).toString(), null);
    }

    /**
     * @return The component id (Kevoree instance name)
     */
    protected final String getId() {
        return context.getInstanceName();
    }

    /**
     * @return The Kevoree node name running the component
     */
    protected final String getNode() {
        return context.getNodeName();
    }

    /**
     * @return The full id nodeName.componentName
     */
    protected final String getFullId() {
        return getNode() + "." + getId();
    }

    /**
     * @return Kevoree model service
     */
    protected final ModelService getModelService() {
        return modelService;
    }

    /**
     * @param request The Request to check
     * @return true if success
     */
    private boolean checkRight(final Request request) {
        return true;
    }

    /**
     * @param notif The notif to send
     */
    public final void sendNotif(final Notif notif) {
        if (pushNotif != null && pushNotif.getConnectedBindingsSize() > 0) {
            pushNotif.send(notif.toString(), null);
        }
    }

    protected final void logInfo(final String message) {
        Log.info("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

    protected final void logError(final String message) {
        Log.error("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

}
