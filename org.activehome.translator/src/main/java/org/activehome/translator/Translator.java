package org.activehome.translator;

import org.activehome.com.Request;
import org.activehome.service.RequestHandler;
import org.activehome.service.Service;
import org.kevoree.annotation.ComponentType;
import org.kevoree.api.Callback;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class Translator<Type2Translate> extends Service implements RequestHandler {

    public abstract void translate(final Type2Translate object,
                                   final Language[] language,
                                   final Callback callback);

    @Override
    protected RequestHandler getRequestHandler(Request request) {
        return this;
    }
}
