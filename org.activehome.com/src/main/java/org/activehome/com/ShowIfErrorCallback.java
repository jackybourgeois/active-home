package org.activehome.com;

import org.activehome.com.error.Error;
import org.kevoree.log.Log;

/**
 * A default callback that ignore success and prompt error
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class ShowIfErrorCallback implements RequestCallback {

    @Override
    public void success(Object result) {

    }

    @Override
    public void error(Error error) {
        Log.error("Error (" + error.getErrorType() + "): " + error.getDetails());
    }
}
