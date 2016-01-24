package org.activehome.io;

import org.activehome.com.Notif;
import org.activehome.com.Request;
import org.activehome.time.TimeControlled;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.log.Log;

/**
 * An IO, for Input/Output, is a provider of raw data or control.
 * It can be connected to a physical device or completely virtual.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class IO extends TimeControlled {

    /**
     * The necessary bindings.
     */
    @Param(defaultValue = "pushNotif>Context.getNotif,"
            + "ctrl>User.pushCmd")
    private String bindingIO;

    /**
     * Send notification to the context.
     */
    @Output
    private org.kevoree.api.Port pushNotif;
    /**
     * Send a ScheduledRequest to the task scheduler.
     */
    @Output
    private org.kevoree.api.Port toSchedule;

    /**
     * Send a notification with current value.
     */
    @Input
    public void forceNotif() {

    }

    /**
     * Run a command.
     * @param command Command as String
     */
    @Input
    public void ctrl(final String command) {

    }

    /**
     * Receive request from the Task Scheduler.
     * @param request The request as string
     */
    @Input
    public void toExecute(final String request) {}

    /**
     * Receive a Message from an API.
     * @param msgStr The message as string
     */
    @Input
    public void fromAPI(final String msgStr) {

    }

    /**
     * @return Component id
     */
    public final String getId() {
        return context.getInstanceName();
    }

    /**
     * @return Component node
     */
    public final String getNode() {
        return context.getNodeName();
    }

    /**
     * @return Component full id (nodeName.componentName)
     */
    public final String getFullId() {
        return context.getNodeName() + "." + context.getInstanceName();
    }

    /**
     * @param notif  The notif to send
     */
    public final void sendNotif(final Notif notif) {
        if (pushNotif != null && pushNotif.getConnectedBindingsSize() > 0) {
            pushNotif.send(notif.toString(), null);
        }
    }

    /**
     * @param scheduledRequest  The ScheduledRequest to send
     */
    public final void sendToTaskScheduler(final Request scheduledRequest) {
        if (toSchedule != null && toSchedule.getConnectedBindingsSize() > 0) {
            toSchedule.send(scheduledRequest.toString(), null);
        }
    }

    protected final void logInfo(String message) {
        Log.info("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

    protected final void logError(String message) {
        Log.error("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

}



