package org.activehome.core.server;

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