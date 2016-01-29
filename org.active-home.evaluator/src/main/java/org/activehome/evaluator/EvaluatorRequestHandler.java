package org.activehome.evaluator;

/*
 * #%L
 * Active Home :: Evaluator
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.activehome
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

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class EvaluatorRequestHandler implements RequestHandler {

    /**
     * The request to handle.
     */
    private final Request request;
    /**
     * The service that will handle it.
     */
    private final Evaluator service;

    /**
     * @param theRequest The request to handle
     * @param theService The service that will execute it
     */
    public EvaluatorRequestHandler(final Request theRequest,
                                   final Evaluator theService) {
        request = theRequest;
        service = theService;
    }

    public void evaluate(final double startTS,
                         final double endTS,
                         final RequestCallback callback) {
        service.evaluate((long)startTS, (long)endTS, callback);
    }

}
