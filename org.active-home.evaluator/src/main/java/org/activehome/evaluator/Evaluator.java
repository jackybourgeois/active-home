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
import org.activehome.service.RequestHandler;
import org.activehome.service.Service;
import org.activehome.tools.Convert;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class Evaluator extends Service {

    /**
     * generate evaluation at fixed interval.
     * (-1 will evaluate only when requested)
     */
    @Param(defaultValue = "1d")
    private String evalInterval;
    /**
     * Default horizon when looking back to data at fixed intervals.
     * 1d means 'evaluate the last day', 2d means 'evaluate the last 2 days'...
     */
    @Param(defaultValue = "1d")
    private String defaultHorizon;
    /**
     * Push any new pushReport
     */
    @Output
    private org.kevoree.api.Port pushReport;

    /**
     * Scheduler for regular evaluation.
     */
    private ScheduledThreadPoolExecutor stpe;

    /**
     * time of the last evaluation
     */
    private long lastEvaluationTS;

    // time life cycle

    @Override
    public void onInit() {
        super.onInit();
        lastEvaluationTS = -1;
    }

    @Override
    public void onStartTime() {
        super.onStartTime();
        if (!evalInterval.equals("-1")) {
            initExecutor();
            long delay = Convert.strDurationToMillisec(evalInterval) / getTic().getZip();
            stpe.scheduleAtFixedRate(this::evaluate, delay, delay,
                    TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onPauseTime() {
        super.onPauseTime();
        if (stpe != null) {
            stpe.shutdownNow();
        }
    }

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
        long midnight = getLocalTime() - getLocalTime() % DAY - HOUR * getTic().getTimezone();
        evaluate(midnight - Convert.strDurationToMillisec(defaultHorizon),
                midnight, new ShowIfErrorCallback());
    }

    public abstract void evaluate(final long startTS,
                                  final long endTS,
                                  final RequestCallback callback);

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

    public String getDefaultHorizon() {
        return defaultHorizon;
    }

    /**
     * Send Notif on pushReport with the given report.
     *
     * @param report the report to publish
     */
    public final void publishReport(final EvaluationReport report) {
        if (pushReport != null && pushReport.getConnectedBindingsSize() > 0) {
            Notif reportNotif = new Notif(getFullId(), "*", getCurrentTime(), report);
            pushReport.send(reportNotif.toString(), null);
        }
    }


}
