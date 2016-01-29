package org.activehome.core.server;

/*
 * #%L
 * Active Home :: Core
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


import org.activehome.tools.NetworkHelper;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;
import org.kevoree.api.Context;

import java.net.URI;

/**
 * Created by leiko on 18/06/15.
 */
@ComponentType(description = "Broadcast messages from a client to every other connected clients (without echoing to the sender)")
public class WSBroadcaster {

    @KevoreeInject
    private Context context;

    @Param(defaultValue = "9050")
    private int port;

    private fr.braindead.websocket.WSBroadcaster broadcaster;

    @Start
    public void start() throws Exception {
        String host = NetworkHelper.getHostExternalAddr();
        broadcaster = new fr.braindead.websocket.WSBroadcaster(URI.create("ws://" + host + ":" + port));
        broadcaster.start();
    }

    @Stop
    public void stop() {
        if (broadcaster != null) {
            broadcaster.stop();
            broadcaster = null;
        }
    }

    @Update
    public void update() throws Exception {
        stop();
        start();
    }
}