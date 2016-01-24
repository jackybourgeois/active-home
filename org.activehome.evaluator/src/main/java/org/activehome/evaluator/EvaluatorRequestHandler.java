package org.activehome.evaluator;

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
