package org.activehome.core;

import org.activehome.tools.NetworkHelper;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Start;
import org.kevoree.api.Context;
import org.kevoree.api.KevScriptService;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.ModelListener;
import org.kevoree.api.handler.UpdateContext;
import org.kevoree.log.Log;

import java.util.UUID;

/**
 * Load the core of active home.
 */
@ComponentType(description = "Load the core of active home")
public class ActiveHome implements ModelListener {

    /**
     *
     */
    @KevoreeInject
    private KevScriptService kevScriptService;
    /**
     *
     */
    @KevoreeInject
    private ModelService modelService;
    /**
     *
     */
    @KevoreeInject
    private Context context;

    /**
     * Port to web socket server.
     */
    private static String wsPort = "9050";
    /**
     * Path in web socket server.
     */
    private static String wsPath = "/activehome";
    /**
     * Check the first model update.
     */
    private boolean firstStart = true;

    /**
     * Wait till the model update.
     */
    @Start
    public final void start() {
        modelService.registerModelListener(this);
    }

    /**
     * Generate and submit the model for the basic components.
     */
    private void init() {
        String host = NetworkHelper.getHostExternalAddr();

        String script = "add {node}.timekeeper : org.activehome.service.time.TimeKeeper\n"
                + "set {node}.timekeeper.startDate = \"actual\"\n"
                + "\n"
                + "add chan_pushResponse_timekeeper : AsyncBroadcast\n"
                + "bind {node}.timekeeper.pushResponse chan_pushResponse_timekeeper\n"
                + addChannel("chan_pushNotif_timekeeper")
                + "bind {node}.timekeeper.pushNotif chan_pushNotif_timekeeper\n"
                + "\n"
                + "add {node}.http: org.activehome.api.http.HttpAPI\n"
                + "set {node}.http.address = \"" + host + "\"\n"
                + addChannel("chan_time_http")
                + "bind {node}.timekeeper.tic chan_time_http\n"
                + "bind {node}.http.time chan_time_http\n"
                + addChannel("chan_sendOutside_http")
                + "bind {node}.http.sendOutside chan_sendOutside_http\n"
                + addChannel("chan_pushNotif_http")
                + "bind {node}.http.pushNotif chan_pushNotif_http\n"
                + addChannel("chan_pushToUser_http")
                + "bind {node}.http.pushToUser chan_pushToUser_http\n"
                + addChannel("chan_getRequest_http")
                + "bind {node}.http.getRequest chan_getRequest_http\n"
                + addChannel("chan_getResponse_http")
                + "bind {node}.http.getResponse chan_getResponse_http\n"
                + addChannel("chan_pushResponse_http")
                + "bind {node}.http.pushResponse chan_pushResponse_http\n"
                + addChannel("chan_getRequest_timekeeper")
                + "bind {node}.timekeeper.getRequest chan_getRequest_timekeeper\n"
                + "bind {node}.http.pushRequest chan_getRequest_timekeeper\n"
                + "bind {node}.timekeeper.pushRequest chan_getRequest_http\n"
                + "bind {node}.timekeeper.pushResponse chan_getResponse_http\n"
                + "\n"
                + "add {node}.ws : org.activehome.api.ws.WsAPI\n"
                + "set {node}.ws.address = \"" + host + "\"\n"
                + addChannel("chan_time_ws")
                + "bind {node}.timekeeper.tic chan_time_ws\n"
                + "bind {node}.ws.time chan_time_ws\n"
                + addChannel("chan_sendOutside_ws")
                + "bind {node}.ws.sendOutside chan_sendOutside_ws\n"
                + addChannel("chan_pushNotif_ws")
                + "bind {node}.ws.pushNotif chan_pushNotif_ws\n"
                + addChannel("chan_pushToUser_ws")
                + "bind {node}.ws.pushToUser chan_pushToUser_ws\n"
                + addChannel("chan_getRequest_ws")
                + "bind {node}.ws.getRequest chan_getRequest_ws\n"
                + addChannel("chan_getResponse_ws")
                + "bind {node}.ws.getResponse chan_getResponse_ws\n"
                + addChannel("chan_pushResponse_ws")
                + "bind {node}.ws.pushResponse chan_pushResponse_ws\n"
                + "\n"
                + "add chan_pushRequest_ws : NodeDistributorChannel\n"
                + "bind {node}.ws.pushRequest chan_pushRequest_ws\n"
                + "\n"
                + "add {node}.microgrid : org.activehome.energy.microgrid.MicroGrid\n"
                + addChannel("chan_time_microgrid")
                + "bind {node}.timekeeper.tic chan_time_microgrid\n"
                + "bind {node}.microgrid.time chan_time_microgrid\n"
                + addChannel("chan_getRequest_microgrid")
                + "bind {node}.microgrid.getRequest chan_getRequest_microgrid\n"
                + "bind {node}.http.pushRequest chan_getRequest_microgrid\n"
                + "bind {node}.microgrid.pushRequest chan_getRequest_http\n"
                + "bind {node}.microgrid.getResponse chan_pushResponse_http\n"
                + "bind {node}.microgrid.pushResponse chan_sendOutside_http\n"
                + "bind {node}.microgrid.pushRequest chan_getRequest_ws\n"
                + "bind {node}.microgrid.getResponse chan_pushResponse_ws\n"
                + "\n"
                + "add {node}.web : org.activehome.service.www.Website\n"
                + addChannel("chan_time_web")
                + "bind {node}.timekeeper.tic chan_time_web\n"
                + "bind {node}.web.time chan_time_web\n"
                + addChannel("chan_getRequest_web")
                + "bind {node}.web.getRequest chan_getRequest_web\n"
                + "bind {node}.http.pushRequest chan_getRequest_web\n"
                + "bind {node}.web.pushRequest chan_getRequest_http\n"
                + "bind {node}.web.getResponse chan_pushResponse_http\n"
                + "bind {node}.web.pushResponse chan_sendOutside_http\n"
                + "bind {node}.web.pushRequest chan_getRequest_ws\n"
                + "bind {node}.web.getResponse chan_pushResponse_ws\n"
                + "\n"
                + "add {node}.linker : org.activehome.service.linker.Linker\n"
                + addChannel("chan_getRequest_linker")
                + "bind {node}.linker.getRequest chan_getRequest_linker\n"
                + addChannel("chan_pushResponse_linker")
                + "bind {node}.linker.pushResponse chan_pushResponse_linker\n"
                + addChannel("chan_time_linker")
                + "bind {node}.timekeeper.tic chan_time_linker\n"
                + "bind {node}.linker.time chan_time_linker\n"
                + "\n"
                + "add {node}.auth : org.activehome.service.auth.Auth\n"
                + addChannel("chan_time_auth")
                + "bind {node}.timekeeper.tic chan_time_auth\n"
                + "bind {node}.auth.time chan_time_auth\n"
                + addChannel("chan_getRequest_auth")
                + "bind {node}.auth.getRequest chan_getRequest_auth\n"
                + "bind {node}.http.pushRequest chan_getRequest_auth\n"
                + "bind {node}.auth.pushRequest chan_getRequest_http\n"
                + "bind {node}.auth.getResponse chan_pushResponse_http\n"
                + "bind {node}.auth.getRequest chan_pushRequest_ws\n"
                + "bind {node}.auth.pushResponse chan_sendOutside_http\n"
                + "bind {node}.auth.pushResponse chan_getResponse_http\n"
                + "bind {node}.auth.pushResponse chan_getResponse_ws\n"
                + "bind {node}.auth.pushRequest chan_getRequest_linker\n"
                + "bind {node}.auth.getResponse chan_pushResponse_linker";

        script = script.replaceAll("\\{node\\}", context.getNodeName())
                        .replaceAll("\\{ah\\.version\\}", "0.0.1-SNAPSHOT");

        modelService.submitScript(script, applied -> Log.info("Model updated after loading ActiveHome core: " + applied));

    }

    /**
     * @param name The channel name
     * @return script for a channel
     */
    private String addChannel(final String name) {
        return "\nadd " + name + " : RemoteWSChan\n"
                + "set " + name + ".host = \"" + NetworkHelper.getHostExternalAddr() + "\"\n"
                + "set " + name + ".port = \"" + wsPort + "\"\n"
                + "set " + name + ".uuid = \"" + UUID.randomUUID() + "\"\n"
                + "set " + name + ".path = \"" + wsPath + "\"\n";
    }

    @Override
    public final boolean preUpdate(final UpdateContext updateContext) {
        return true;
    }

    @Override
    public final boolean initUpdate(final UpdateContext updateContext) {
        return true;
    }

    @Override
    public final boolean afterLocalUpdate(final UpdateContext updateContext) {
        return true;
    }

    /**
     * Launch Active Home init after first model update.
     */
    @Override
    public final void modelUpdated() {
        if (firstStart) {
            init();
            firstStart = false;
        }
    }

    @Override
    public void preRollback(final UpdateContext updateContext) {

    }

    @Override
    public void postRollback(final UpdateContext updateContext) {

    }
}
