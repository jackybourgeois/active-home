package org.activehome.com;

import org.activehome.com.error.Error;

/**
 * Define what to do after a request.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public interface RequestCallback {
    /**
     * @param result the result
     */
    void success(Object result);

    /**
     * @param result the error return by the service
     */
    void error(Error result);
}
