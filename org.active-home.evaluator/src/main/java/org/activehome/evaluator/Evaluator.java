package org.activehome.evaluator;

/*
 * #%L
 * Active Home :: Evaluator
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
import org.activehome.com.RequestCallback;
import org.activehome.com.ShowIfErrorCallback;
import org.activehome.context.data.DiscreteDataPoint;
import org.activehome.context.data.Schedule;
import org.activehome.service.RequestHandler;
import org.activehome.service.Service;
import org.activehome.tools.Convert;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * An evaluator produces evaluation reports for/on the system.
 *
 * @author Jacky Bourgeois
 */
@ComponentType(version = 1, description = "An evaluator produce "
        + "evaluation reports for/on the system.")
public abstract class Evaluator extends Service {

    /**
     * Generate evaluation at fixed interval.
     * (-1 will evaluate only when requested)
     */
    @Param(defaultValue = "1d")
    private String evalInterval;
    /**
     * Default horizon when looking back to data at fixed intervals.
     * 1d means 'evaluate the last day', 2d means 'evaluate the last 2 days'...
     */
    @Param(defaultValue = "1d")
    private String defaultHorizon = "1d";
    /**
     * Push any new pushReport.
     */
    @Output
    private org.kevoree.api.Port pushReport;

    /**
     * Scheduler for regular evaluation.
     */
    private ScheduledThreadPoolExecutor stpe;

    /**
     * Time of the last evaluation.
     */
    private long lastEvaluationTS;

    // time life cycle

    /**
     * If override, this method should be called first.
     */
    @Override
    public void onInit() {
        super.onInit();
        lastEvaluationTS = -1;
    }

    /**
     * If override, this method should be called first.
     */
    @Override
    public void onStartTime() {
        super.onStartTime();
        if (!evalInterval.equals("-1")) {
            initExecutor();
            long delay = Convert.strDurationToMillisec(evalInterval)
                    / getTic().getZip();
            stpe.scheduleAtFixedRate(this::evaluate, delay, delay,
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * If override, this method should be called first.
     */
    @Override
    public void onPauseTime() {
        super.onPauseTime();
        if (stpe != null) {
            stpe.shutdownNow();
        }
    }

    /**
     * If override, this method should be called last.
     */
    @Override
    public void onResumeTime() {
        super.onResumeTime();
        if (!evalInterval.equals("-1")) {
            initExecutor();
            long interval = Convert.strDurationToMillisec(evalInterval);
            long initialDelay = 0;
            if (lastEvaluationTS != -1) {
                initialDelay = lastEvaluationTS + interval - getTic().getTS();
            }
            stpe.scheduleAtFixedRate(this::evaluate,
                    initialDelay / getTic().getZip(),
                    interval / getTic().getZip(),
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * If override, this method should be called last.
     */
    @Override
    public void onStopTime() {
        super.onStopTime();
        if (stpe != null) {
            stpe.shutdownNow();
        }
        if (!evalInterval.equals("-1")) {
            evaluate();
        }
    }

    // evaluation

    private void evaluate() {
        long midnight = getLocalTime() - getLocalTime() % DAY
                - HOUR * getTic().getTimezone();
        evaluate(midnight - Convert.strDurationToMillisec(defaultHorizon),
                midnight, new ShowIfErrorCallback());
    }

    public abstract void evaluate(long startTS,
                                  long endTS,
                                  RequestCallback callback);

    public abstract EvaluationReport evaluate(Schedule schedule);

    /**
     * Get the handler that will execute the request.
     *
     * @param request The received request
     * @return The request handler
     */
    @Override
    protected final RequestHandler getRequestHandler(final Request request) {
        return new EvaluatorRequestHandler(request, this);
    }

    private void initExecutor() {
        stpe = new ScheduledThreadPoolExecutor(1, r -> {
            return new Thread(r, getFullId() + "-evaluation-pool");
        });
    }

    public final String getDefaultHorizon() {
        return defaultHorizon;
    }

    /**
     * Send Notif on pushReport with the given report.
     *
     * @param report the report to publish
     */
    public final void publishReport(final EvaluationReport report) {
        if (pushReport != null && pushReport.getConnectedBindingsSize() > 0) {
            Notif reportNotif = new Notif(getFullId(), "*",
                    getCurrentTime(), report);
            pushReport.send(reportNotif.toString(), null);
        }
    }

    public final EvaluationReport evaluateAndPublish(final Schedule schedule) {
        EvaluationReport report = evaluate(schedule);

        for (String metric : report.getReportedMetrics().keySet()) {
            sendEvalToContext(metric.split("#")[0], schedule.getStart(),
                    report.getReportedMetrics().get(metric),
                    schedule.getHorizon(), metric.split("#")[1]);
        }

        publishReport(report);

        return report;
    }

    private void sendEvalToContext(final String metricId,
                                   final long start,
                                   final String val,
                                   final long duration,
                                   final String version) {
        DiscreteDataPoint ddp = new DiscreteDataPoint(metricId, start, val,
                version, 0, 1,
                Convert.strDurationToMillisec(getDefaultHorizon()));
        sendNotif(new Notif(getFullId(), getNode() + ".context",
                getCurrentTime(), ddp));
    }

}
