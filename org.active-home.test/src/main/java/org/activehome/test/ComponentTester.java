package org.activehome.test;

/*
 * #%L
 * Active Home :: Test
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
import org.activehome.com.Request;
import org.activehome.com.RequestCallback;
import org.activehome.com.ScheduledRequest;
import org.activehome.com.ShowIfErrorCallback;
import org.activehome.com.error.Error;
import org.activehome.service.RequestHandler;
import org.activehome.service.Service;
import org.activehome.tools.Convert;
import org.activehome.tools.file.FileHelper;
import org.activehome.context.data.UserInfo;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.ModelListener;
import org.kevoree.api.handler.UpdateContext;

/**
 * Mock component to simulate behaviour and test components.
 *
 * @author Jacky Bourgeois
 */
@ComponentType(version = 1, description = "Mock component "
        + "to simulate behaviour and test components")
public abstract class ComponentTester extends Service
        implements RequestHandler, ModelListener {

    /**
     * Start date of the test.
     */
    @Param(optional = false)
    protected String startDate;
    /**
     * Time duration of the test.
     */
    @Param(defaultValue = "1d")
    private String testDuration;
    /**
     * Log file.
     */
    @Param(defaultValue = "test.log")
    private String logFile;

    /**
     * The Kevoree model.
     */
    @KevoreeInject
    private ModelService modelService;
    /**
     * To detect first update and start the test.
     */
    private boolean firstModelUpdate = true;
    /**
     * Test start timestamp
     */
    protected long startTS;


    /**
     * Port to send to the Task Scheduler.
     */
    @Output
    private org.kevoree.api.Port toSchedule;

    /**
     * @param request The request to handle
     * @return The current instance
     */
    @Override
    protected final RequestHandler getRequestHandler(final Request request) {
        return this;
    }

    /**
     * On start, listen to the Kevoree model.
     */
    @Start
    public void start() {
        super.start();
        modelService.registerModelListener(this);
    }

    @Override
    public void onInit() {
        super.onInit();
        logInfo("Test initialized.");
    }

    @Override
    public void onStartTime() {
        super.onStartTime();
        logInfo("Test started.");
        ScheduledRequest sr = new ScheduledRequest(getFullId(),
                getNode() + ".timekeeper", getCurrentTime(),
                "stopTime", startTS + getTestDuration());
        sendToTaskScheduler(sr, new ShowIfErrorCallback());
    }

    @Override
    public void onPauseTime() {
        super.onPauseTime();
        logInfo("Test paused.");
    }

    @Override
    public void onResumeTime() {
        super.onPauseTime();
        logInfo("Test resumed.");
    }

    @Override
    public void onStopTime() {
        super.onStopTime();
        nextTest();
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

    @Override
    public final void modelUpdated() {
        if (firstModelUpdate) {
            firstModelUpdate = false;
            FileHelper.logln(logHeaders(), logFile);
            nextTest();
        }
    }

    private void nextTest() {
        JsonObject params = prepareNextTest();
        if (params != null) {
            initiateTest(params);
        } else {
            logInfo("Tests done.");
        }
    }

    protected abstract String logHeaders();

    protected void logResults(final String results) {
        FileHelper.logln(results, logFile);
    }

    protected abstract JsonObject prepareNextTest();

    private void initiateTest(final JsonObject params) {
        logInfo("Initializing test: " + params);
        Request initReq = new Request(getFullId(), getNode() + ".timekeeper",
                getCurrentTime(), "setProperties", new Object[]{params});
        sendRequest(initReq, new RequestCallback() {
            @Override
            public void success(final Object result) {
                Request startReq = new Request(getFullId(), getNode() + ".timekeeper",
                        getCurrentTime(), "startTime");
                sendRequest(startReq, new RequestCallback() {
                    @Override
                    public void success(final Object result) {
                    }

                    @Override
                    public void error(final org.activehome.com.error.Error error) {
                        logError("Error: " + error.toString());
                    }
                });
            }

            @Override
            public void error(final Error error) {
                logError("Error: " + error.toString());
            }
        });
    }

    @Override
    public void preRollback(final UpdateContext updateContext) {

    }

    @Override
    public void postRollback(final UpdateContext updateContext) {

    }

    /**
     * @param scheduledRequest The ScheduledRequest to send
     */
    public final void sendToTaskScheduler(final Request scheduledRequest,
                                          final RequestCallback callback) {
        if (toSchedule != null && toSchedule.getConnectedBindingsSize() > 0) {
            if (callback != null) {
                getWaitingRequest().put(scheduledRequest.getId(), callback);
            }
            toSchedule.send(scheduledRequest.toString(), null);
        }
    }

    protected final long getTestDuration() {
        return Convert.strDurationToMillisec(testDuration);
    }

    protected final void setTestDuration(final String duration) {
        testDuration = duration;
    }

    protected UserInfo testUser() {
        return new UserInfo("tester", new String[]{"user"},
                "ah", "org.activehome.user.emulator.EUser");
    }

    /**
     * Send a request to the timekeeper to pause the time.
     */
    protected final void pauseTime() {
        Request startReq = new Request(getFullId(), getNode() + ".timekeeper",
                getCurrentTime(), "pauseTime");
        sendRequest(startReq, new ShowIfErrorCallback());
    }

    /**
     * Send a request to the timekeeper to resume the time.
     */
    protected final void resumeTime() {
        Request startReq = new Request(getFullId(), getNode() + ".timekeeper",
                getCurrentTime(), "resumeTime");
        sendRequest(startReq, new ShowIfErrorCallback());
    }

}
