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


import org.activehome.com.Request;
import org.activehome.com.RequestCallback;
import org.activehome.service.RequestHandler;

import java.util.UUID;

/**
 * Dedicated handler for predictor request.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class PredictorRequestHandler implements RequestHandler {

    /**
     * The request to handle.
     */
    private final Request request;
    /**
     * The service that will handle it.
     */
    private final Predictor service;

    /**
     * @param theRequest The request to handle
     * @param theService The service that will execute it
     */
    public PredictorRequestHandler(final Request theRequest,
                                   final Predictor theService) {
        request = theRequest;
        service = theService;
    }

    /**
     * Predict the overall consumption of the given time frame.
     *
     * @param startTS     Start time-stamp of the time frame
     * @param duration    Duration of the time frame
     * @param granularity Duration of each time slot
     * @param callback    where we send the result
     */
    public final void predict(final double startTS,
                              final double duration,
                              final double granularity,
                              final RequestCallback callback) {
        service.predict((long) startTS, (long) duration,
                (long) granularity, callback);
    }

    /**
     * Retrieve actual data and compare with the
     * the prediction value to produce an evaluation.
     *
     * @param predictionId To retrieve the prediction
     *                     in the history
     */
    public final void evaluate(final UUID predictionId) {
        service.evaluate(predictionId);
    }


}