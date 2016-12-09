package org.activehome.predictor;

/*
 * #%L
 * Active Home :: Predictor
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


import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;

import java.util.HashMap;
import java.util.UUID;

import org.activehome.com.Request;
import org.activehome.com.RequestCallback;
import org.activehome.com.ScheduledRequest;
import org.activehome.context.data.MetricRecord;
import org.activehome.service.RequestHandler;
import org.activehome.service.Service;

/**
 * An predictor produces prediction values
 * (as DataPoints, MetricRecords or Schedules).
 *
 * @author Jacky Bourgeois
 */
@ComponentType(version = 1, description = "An predictor produces "
        + "prediction values (as DataPoints, MetricRecords or Schedules)")
public abstract class Predictor
        extends Service implements RequestHandler {

    /**
     * Label of the prediction.
     */
    @Param(optional = false)
    private String metric;
    /**
     * Are we manipulating discrete or continuous data?
     */
    @Param(optional = false)
    private boolean discrete;

    /**
     * Trace of the latest prediction to be evaluated
     * when the actual data will be available.
     */
    private HashMap<UUID, MetricRecord> predictionHistory;

    /**
     * Port to send to the Task Scheduler.
     */
    @Output
    private org.kevoree.api.Port toSchedule;

    /**
     * If override, this method should be called first.
     */
    @Start
    public void start() {
        super.start();
        predictionHistory = new HashMap<>();
    }

    /**
     * If override, this method should be called first.
     */
    @Override
    public void onInit() {
        super.onInit();
        predictionHistory = new HashMap<>();
    }

    /**
     * Generate a prediction for the given time frame.
     *
     * @param startTS     Start time-stamp of the time frame
     * @param duration    Duration of the time frame
     * @param granularity Duration of each time slot
     * @param callback    where we send the result
     */
    public abstract void predict(long startTS,
                                 long duration,
                                 long granularity,
                                 RequestCallback callback);

    /**
     * Keep track of the prediction and schedule
     * an evaluation when the prediction expires.
     *
     * @param metricRecord The new prediction
     */
    protected final void addPredictionToHistory(
            final MetricRecord metricRecord) {
        UUID predictionId = UUID.randomUUID();
        predictionHistory.put(predictionId, metricRecord);
        ScheduledRequest sr = new ScheduledRequest(getFullId(), getFullId(),
                getCurrentTime(), "evaluate", new Object[]{predictionId},
                metricRecord.getStartTime() + metricRecord.getTimeFrame());
        sendToTaskScheduler(sr);
    }

    /**
     * @return The predicted metric.
     */
    public final String getMetric() {
        return metric;
    }

    /**
     * @return true if the predicted values are discrete, false otherwise.
     */
    public final boolean isDiscrete() {
        return discrete;
    }

    /**
     * Retrieve actual data and compare with the
     * the prediction value to produce an evaluation.
     *
     * @param predictionId To retrieve the prediction
     *                     in the history
     */
    public final void evaluate(final UUID predictionId) {
//        MetricRecord predictionMR = predictionHistory.get(predictionId);
//        LinkedList<Record> records = predictionMR.getRecords();
//        long granularity = ((SampledRecord) records.get(0)).getDuration();
//        // request to the context to get actual data
//        ContextRequest ctxReq = new ContextRequest(new String[]{actualMetric},
//                isDiscrete(), predictionMR.getStartTime(), granularity,
//                granularity, records.size(), "", "");
//        Request ctxRequest = new Request(getFullId(), getNode() + ".context",
//                getCurrentTime(), "extractSampleData",
//                        new Object[]{ctxReq, 1, 1});
//        sendRequest(ctxRequest, new RequestCallback() {
//            @Override
//            public void success(final Object result) {
//                MetricRecord actualMR = ((ContextResponse) result)
//                        .getResultMap().get(0)
//                        .getMetricRecordMap().getFirst();
//                StringBuilder sb = new StringBuilder();
//                sb.append(strLocalTime(predictionMR.getStartTime()));
//                for (Record record : actualMR.getRecords()) {
//                    sb.append(",").append(record.getValue());
//                }
//                FileHelper.logln(sb.toString(), "solarPrediction.csv");
//                logInfo("Actual: " + sb);
//                double rmsd = rmsd(predictionMR, actualMR);
//                DataPoint dp = new DataPoint("eval." + getId() + ".rmsd",
//                        getCurrentTime(), rmsd + "");
//                logInfo("RMSD: " + rmsd);
//                FileHelper.logln(strLocalTime(predictionMR.getStartTime())
//                            + "," + rmsd, "rmsd.csv");
//                sendNotif(new Notif(getFullId(), getNode() + ".context",
//                        getCurrentTime(), dp));
//            }
//
//            @Override
//            public void error(final Error error) {
//                Log.error("Evaluation cannot be performed, "
//                        + "unable to get actual data: " + error);
//            }
//        });
    }

    /**
     * Get the handler that will execute the request.
     *
     * @param request The received request
     * @return The request handler
     */
    @Override
    protected final RequestHandler getRequestHandler(final Request request) {
        return new PredictorRequestHandler(request, this);
    }

    /**
     * @param scheduledRequest The ScheduledRequest to send
     */
    public final void sendToTaskScheduler(final Request scheduledRequest) {
        if (toSchedule != null && toSchedule.getConnectedBindingsSize() > 0) {
            toSchedule.send(scheduledRequest.toString(), null);
        }
    }

    /**
     * Calculate the Root Mean Square Deviation.
     *
     * @param predicted Predicted values
     * @param actual    Actual values
     * @return deviation
     */
    private double rmsd(final MetricRecord predicted,
                        final MetricRecord actual) {
        double sumSquare = 0;
        for (int i = 0; i < actual.getRecords().size(); i++) {
            double predictedVal = predicted.getRecords().get(i).getDouble();
            double actualVal = actual.getRecords().get(i).getDouble();
            sumSquare += (predictedVal - actualVal)
                    * (predictedVal - actualVal);
        }
        return Math.sqrt(sumSquare / (actual.getRecords().size() - 1));
    }
}

