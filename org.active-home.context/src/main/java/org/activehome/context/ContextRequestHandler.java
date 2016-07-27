package org.activehome.context;

/*
 * #%L
 * Active Home :: Context
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
import com.eclipsesource.json.JsonValue;
import org.activehome.com.Request;
import org.activehome.com.RequestCallback;
import org.activehome.com.error.Error;
import org.activehome.com.error.ErrorType;
import org.activehome.context.com.ContextRequest;
import org.activehome.context.data.Trigger;
import org.activehome.service.RequestHandler;
import org.activehome.tools.file.FileHelper;
import org.activehome.tools.file.TypeMime;
import org.activehome.context.data.UserInfo;

/**
 * Dedicated handler for context request.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class ContextRequestHandler implements RequestHandler {

    /**
     * The request to handle.
     */
    private final Request request;
    /**
     * The service that will handle it.
     */
    private final Context service;

    /**
     * @param theRequest The request to handle
     * @param theService The service that will execute it
     */
    public ContextRequestHandler(final Request theRequest,
                                 final Context theService) {
        request = theRequest;
        service = theService;
    }

    /**
     * @return Json content or Error
     */
    public final Object html() {
        JsonObject wrap = new JsonObject();
        wrap.add("name", "context-view");
        wrap.add("url", service.getId() + "/context-view.html");
        wrap.add("title", "Active Home Context");
        wrap.add("description", "Active Home Context");

        JsonObject json = new JsonObject();
        json.add("wrap", wrap);
        return json;
    }

    /**
     * @param fileName The file name
     * @param callback Content wrapped in Json, or Error
     */
    public final void file(final String fileName,
                           final RequestCallback callback) {
        String content = FileHelper.fileToString(fileName,
                getClass().getClassLoader());
        if (!content.equals("")) {
            if (fileName.endsWith(".html")) {
                content = content.replaceAll("\\$\\{id\\}", service.getId());
            }
            JsonObject json = new JsonObject();
            json.add("content", content);
            json.add("mime", TypeMime.valueOf(
                    fileName.substring(fileName.lastIndexOf(".") + 1,
                            fileName.length())).getDesc());
            callback.success(json);
        } else {
            callback.error(new Error(ErrorType.NOT_FOUND, "File not found."));
        }
    }

    /**
     *
     * @param callback Json containing all the available metrics
     *                 with their values
     */
    public final void getAllAvailableMetrics(final RequestCallback callback) {
        JsonObject json = new JsonObject();
        Object obj = request.getEnviElem().get("userInfo");
        if (obj != null && obj instanceof UserInfo) {
            service.getAllAvailableMetrics((UserInfo) obj, callback);
        }
    }

    /**
     * Provide the last DataPoint received of the given metric.
     *
     * @param metricArray the metric we are looking for
     * @param callback    The callback to reply
     */
    public final void getLastData(final String[] metricArray,
                                  final RequestCallback callback) {
        service.getLastData(metricArray, callback);
    }

    /**
     * Provide the DataPoints of a given metric for a given period of time.
     *
     * @param metric   The metric we are looking at
     * @param start    The start time of the period (milliseconds)
     * @param end      The end time of the period (milliseconds)
     * @param callback The callback to reply
     */
    public final void getData(final String metric,
                              final long start,
                              final long end,
                              final RequestCallback callback) {
        service.getData(metric, start, end, callback);
    }

    /**
     * Extract DataPoints from the context.
     *
     * @param contextRequest The details of the data to extract
     * @param iteration      How many request should be performed
     * @param shift          Length of the time shift between each request
     * @param callback       The callback to reply
     */
    public final void extractSampleData(final ContextRequest contextRequest,
                                        final double iteration,
                                        final double shift,
                                        final RequestCallback callback) {
        service.extractSampleData(contextRequest, (int) iteration, (long) shift, callback);
    }

    public final void extractSchedule(final double start,
                                      final double duration,
                                      final double granularity,
                                      final String[] metricArray,
                                      final RequestCallback callback) {
        service.extractSchedule((long) start, (long) duration, (long) granularity, metricArray, callback);
    }

    public final boolean addTriggers(final Trigger[] triggerArray) {
        for (Trigger trigger : triggerArray) {
            if (trigger.getResultMetric().equals("")) {
                service.getTriggerManager().addCtrlTrigger(trigger);
            } else {
                service.getTriggerManager().addUseTrigger(trigger);
            }
        }
        return true;
    }

    /**
     * Subscribe to context elements.
     *
     * @param metricArray      The list of metric to listen
     * @param subscriptionDest The destination that will receive data update
     */
    public final void subscribe(final String[] metricArray,
                                final String subscriptionDest) {
        String subDest = request.getSrc();
        if (request.getEnviElem().get("api") != null) {
            subDest = request.getEnviElem().get("api") + ":" + subscriptionDest;
        }
        Object obj = request.getEnviElem().get("userInfo");
        if (obj != null && obj instanceof UserInfo) {
            service.subscribe(metricArray, subDest, (UserInfo) obj);
        } else {
            service.subscribe(metricArray, subDest, null);
        }
    }

    /**
     * Unsubscribe from context elements.
     *
     * @param metricArray      contains paths to stop listening to,
     * @param subscriptionDest is used as destination currently use
     */
    public final void unsubscribe(final String[] metricArray,
                                  final String subscriptionDest) {
        service.unsubscribe(metricArray, subscriptionDest);
    }

}
