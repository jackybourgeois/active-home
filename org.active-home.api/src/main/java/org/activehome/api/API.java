package org.activehome.api;

/*
 * #%L
 * Active Home :: API
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


import org.activehome.com.Message;
import org.activehome.com.Notif;
import org.activehome.com.Request;
import org.activehome.com.RequestCallback;
import org.activehome.service.Service;
import org.activehome.service.RequestHandler;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.annotation.*;
import org.kevoree.api.ModelService;

/**
 * An API makes an interaction between the system and the external world.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class API extends Service implements RequestHandler {

    /**
     * The necessary bindings.
     */
    @Param(defaultValue = "sendOutside>User.toAPI,"
            + "pushToUser>User.fromAPI,")
    private String bindingAPI;

    /**
     * Port sending notifications.
     */
    @Output
    private org.kevoree.api.Port pushNotif;
    /**
     * Port sending messages to users.
     */
    @Output
    private org.kevoree.api.Port pushToUser;

    /**
     * @param msgStr The message to send outside
     */
    @Input
    public abstract void sendOutside(String msgStr);

    /**
     * @param notif The notification to send to the system
     */
    public final void sendNotifToSys(final Notif notif) {
        pushNotif.send(notif.toString(), null);
    }

    /**
     * @param message  The message (Request, Response or Notif)
     * @param callback The request callback
     */
    public final void sendToUser(final Message message,
                                 final RequestCallback callback) {
        if (pushToUser != null && pushToUser.getConnectedBindingsSize() > 0) {
            if (message instanceof Request && callback != null) {
                getWaitingRequest().put(message.getId(), callback);
            }
            pushToUser.send(message.toString(), null);
        }
    }

    /**
     * @param id The id of the ComponentInstance to look check
     * @return The ComponentInstance found in the Kevoree model, null otherwise
     */
    public final Object checkComponent(final String id) {
        ContainerRoot model = getModelService().getCurrentModel().getModel();
        if (model != null) {
            ContainerNode node = model.findNodesByID(context.getNodeName());
            if (node != null) {
                return node.findComponentsByID(id);
            }
        }
        return null;
    }

    /**
     * @param request The request received by the service
     * @return The current instance
     */
    protected final RequestHandler getRequestHandler(final Request request) {
        return this;
    }

}



