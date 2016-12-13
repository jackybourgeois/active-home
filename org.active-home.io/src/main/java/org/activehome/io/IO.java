package org.activehome.io;

/*
 * #%L
 * Active Home :: IO
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


import org.activehome.com.Notif;
import org.activehome.com.Request;
import org.activehome.time.TimeControlled;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.ModelListener;
import org.kevoree.api.handler.UpdateContext;
import org.kevoree.log.Log;

/**
 * An IO, for Input/Output, is a provider of raw data or control.
 * It can be connected to a physical device or completely virtual.
 *
 * @author Jacky Bourgeois
 */
@ComponentType(version = 1, description = "An IO, for Input/Output, "
        + "is a provider of raw data or control.")
public abstract class IO extends TimeControlled implements ModelListener {

    /**
     * The necessary bindings.
     */
    @Param(defaultValue = "pushNotif>Context.getNotif,"
            + "ctrl>User.pushCmd")
    private String bindingIO;

    /**
     * Access to Kevoree's model.
     */
    @KevoreeInject
    private ModelService modelService;
    /**
     * To detect first update and start the test.
     */
    private boolean firstModelUpdate = true;

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
     * If override, this method should be called first.
     */
    @Start
    public void start() {
        super.start();
        if (modelService != null) {
            modelService.registerModelListener(this);
        }
    }

    /**
     * Send a notification with current value.
     */
    @Input
    public void forceNotif() {

    }

    /**
     * Run a command.
     *
     * @param command Command as String
     */
    @Input
    public void ctrl(final String command) {

    }

    /**
     * Receive request from the Task Scheduler.
     *
     * @param request The request as string
     */
    @Input
    public void toExecute(final String request) {
    }

    /**
     * Receive a Message from an API.
     *
     * @param msgStr The message as string
     */
    @Input
    public void fromAPI(final String msgStr) {

    }

    /**
     * @return Component id
     */
    public final String getId() {
        if (context != null) {
            return context.getInstanceName();
        }
        return getClass().getSimpleName();
    }

    /**
     * @return Component node
     */
    public final String getNode() {
        if (context != null) {
            return context.getNodeName();
        }
        return  "ah";
    }

    /**
     * @return Component full id (nodeName.componentName)
     */
    public final String getFullId() {
        if (context != null) {
            return context.getNodeName() + "." + context.getInstanceName();
        }
        return  "ah." + getClass().getSimpleName();
    }

    /**
     * @param notif The notif to send
     */
    public final void sendNotif(final Notif notif) {
        if (pushNotif != null && pushNotif.getConnectedBindingsSize() > 0) {
            pushNotif.send(notif.toString(), null);
        }
    }

    /**
     * @param scheduledRequest The ScheduledRequest to send
     */
    public final void sendToTaskScheduler(final Request scheduledRequest) {
        if (toSchedule != null && toSchedule.getConnectedBindingsSize() > 0) {
            toSchedule.send(scheduledRequest.toString(), null);
        }
    }

    protected final void logInfo(final String message) {
        Log.info("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

    protected final void logError(final String message) {
        Log.error("[" + getFullId() + " - " + strLocalTime() + "] " + message);
    }

    @Override
    public boolean preUpdate(final UpdateContext updateContext) {
        return true;
    }

    @Override
    public boolean initUpdate(final UpdateContext updateContext) {
        return true;
    }


    @Override
    public void preRollback(final UpdateContext updateContext) {
    }

    @Override
    public void postRollback(final UpdateContext updateContext) {
    }

    @Override
    public boolean afterLocalUpdate(final UpdateContext updateContext) {
        return true;
    }

    @Override
    public void modelUpdated() {
        if (firstModelUpdate) {
            firstModelUpdate = false;
        }
    }

    protected boolean isFirstModelUpdate() {
        return firstModelUpdate;
    }

    protected ModelService getModelService() {
        return modelService;
    }

}



