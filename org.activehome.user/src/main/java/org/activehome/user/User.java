package org.activehome.user;

import com.eclipsesource.json.JsonObject;
import org.activehome.com.Message;
import org.activehome.com.Notif;
import org.activehome.com.Request;
import org.activehome.com.Response;
import org.activehome.com.error.ErrorType;
import org.activehome.com.error.Error;
import org.activehome.time.TimeControlled;
import org.kevoree.annotation.*;
import org.kevoree.api.ModelService;
import org.kevoree.api.Port;
import org.kevoree.log.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * The User represents the user as a component in the system.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public class User extends TimeControlled {

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
//        System.out.println(getFullId() + " received: " + msgStr);
        if (json.get("method") != null) {
            message = new Request(JsonObject.readFrom(msgStr));
        } else if (json.get("result") != null) {
            message = new Response(JsonObject.readFrom(msgStr));
        } else {
            message = new Notif(JsonObject.readFrom(msgStr));
        }
        if (message.getSrc().contains("://" + getFullId() + "@")) {
            String[] src = message.getSrc().split("(://)|@|:");
//            System.out.println("size src array: " + src.length);
            if (src.length > 2 && src[1].compareTo(getFullId()) == 0) {
                if (message instanceof Notif) {
                    Notif notif = (Notif) message;
                    // TODO
                } else if (message instanceof Request) {
                    Request request = (Request) message;
                    if (checkRight(request)) {
                        Request req = new Request(getFullId(),
                                request.getDest(), getCurrentTime(),
                                request.getMethod(), request.getParams());
                        req.getEnviElem().putAll(request.getEnviElem());
                        req.getEnviElem().put("api", src[0] + "://" + src[2]);
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
            }
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
     * @param notif  The notif to send
     */
    public final void sendNotif(final Notif notif) {
        if (pushNotif != null && pushNotif.getConnectedBindingsSize() > 0) {
            pushNotif.send(notif.toString(), null);
        }
    }

    protected final void logInfo(String message) {
        Log.info("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

    protected final void logError(String message) {
        Log.error("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

}
