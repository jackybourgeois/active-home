package org.activehome.service;

import com.eclipsesource.json.JsonObject;
import org.activehome.com.Notif;
import org.activehome.com.Request;
import org.activehome.com.RequestCallback;
import org.activehome.com.Response;
import org.activehome.com.error.ErrorType;
import org.activehome.com.error.Error;
import org.activehome.time.TimeControlled;
import org.kevoree.annotation.*;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.ModelListener;
import org.kevoree.api.handler.UpdateContext;
import org.kevoree.log.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

/**
 * A Service handle Requests and provide Responses.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class Service extends TimeControlled implements ModelListener {

    /**
     * The necessary bindings.
     */
    @Param(defaultValue = "pushNotif>Context.getNotif,"
            + "pushRequest>API.getRequest,"
            + "getResponse>API.pushResponse,"
            + "pushRequest>Context.getRequest,"
            + "getResponse>Context.pushResponse,"
            + "getRequest>User.pushRequest,"
            + "pushResponse>User.getResponse")
    private String bindingService;

    /**
     * The request received, waiting a response.
     */
    private HashMap<UUID, RequestCallback> waitingRequest;
    /**
     * The timestamp of the last attempt to remove expired requests
     */
    private long lastCheckExpiredRequests = 0;


    @KevoreeInject
    private ModelService modelService;
    /**
     * To detect first update and start the test.
     */
    private boolean firstModelUpdate = true;

    /**
     * The port to send responses.
     */
    @Output
    private org.kevoree.api.Port pushResponse;
    /**
     * The port to send request.
     */
    @Output
    private org.kevoree.api.Port pushRequest;
    /**
     * The port to send notification.
     */
    @Output
    private org.kevoree.api.Port pushNotif;

    /**
     * @param reqStr Received request as string
     */
    @Input
    public final void getRequest(final String reqStr) {
        Thread.currentThread().setContextClassLoader(
                this.getClass().getClassLoader());
        JsonObject reqJson = JsonObject.readFrom(reqStr);
        if (reqJson.get("dest").asString().compareTo(getFullId()) == 0) {
            Request request = new Request(reqJson,
                    this.getClass().getClassLoader());
            Method method = null;
            Class[] classArray = new Class[request.getParams().length];
            Object[] params = null;
            try {
                for (int i = 0; i < classArray.length; i++) {
                    classArray[i] = request.getParams()[i].getClass();
                }
                Object service = getRequestHandler(request);
                String[] methodNameArray = request.getMethod().split("\\.");
                String methodName = methodNameArray[methodNameArray.length - 1];
                method = getCompatibleMethod(service.getClass(),
                        methodName, classArray);
                if (method != null) {     // try without callback param
                    onRequestReady(request);
                    Object result = method.invoke(service, request.getParams());
                    if (result != null) {
                        manageRequestResult(result, request);
                    }
                } else {                // try with callback param
                    params = push(request.getParams(),
                            serviceCallback(request));
                    classArray = new Class[params.length];
                    for (int i = 0; i < classArray.length; i++) {
                        classArray[i] = params[i].getClass();
                    }
                    method = getCompatibleMethod(service.getClass(),
                            methodName, classArray);
                    if (method != null) {
                        onRequestReady(request);
                        method.invoke(service, params);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unknown Method '").append(methodName).append("' => ");
                        for (Class clazz : classArray) {
                            sb.append(clazz.getSimpleName()).append(" ");
                        }
                        response(request, new Error(ErrorType.NO_SUCH_METHOD,
                                sb.toString()).toJson());
                    }
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                logError("in getRequest - " + reqStr);
                logInfo("Method: " + method);
                if (params != null) {
                    for (int i = 0; i < classArray.length; i++) {
                        logInfo("param " + i + ": " + params[i].getClass());
                    }
                } else {
                    logError("Params is null.");
                }
                response(request, new Error(ErrorType.METHOD_ERROR,
                        e.getMessage() + "").toJson());
            } catch (IllegalAccessException e) {
                response(request, new Error(ErrorType.ILLEGAL_ACCESS,
                        e.getMessage() + "").toJson());
            }
        }
    }

    /**
     * @param result  The result of the request
     * @param request The original request
     */
    private void manageRequestResult(final Object result,
                                     final Request request) {
        if (result instanceof Error) {
            response(request, ((Error) result).toJson());
        } else {
            response(request, result);
        }
    }

    /**
     * Provide a callback for methods which miss one.
     *
     * @param request The received request
     * @return The callback
     */
    private RequestCallback serviceCallback(final Request request) {
        return new RequestCallback() {
            public void success(final Object result) {
                response(request, result);
            }

            public void error(final Error result) {
                response(request, result);
            }
        };
    }

    /**
     * @param respStr Received Response as string
     */
    @Input
    public final void getResponse(final String respStr) {
        Thread.currentThread().setContextClassLoader(
                this.getClass().getClassLoader());
        JsonObject respJson = JsonObject.readFrom(respStr);
        if (respJson.get("dest").asString().compareTo(getFullId()) == 0) {
            Response response = new Response(respJson,
                    this.getClass().getClassLoader());
            if (waitingRequest.containsKey(response.getId())) {
                if (response.getResult() instanceof Error) {
                    waitingRequest.get(response.getId())
                            .error((Error) response.getResult());
                } else {
                    waitingRequest.get(response.getId())
                            .success(response.getResult());
                }
            } else {
                Log.warn(getId() + ": Response of unknown request (src: "
                        + response.getSrc() + ").");
            }
        }
    }

    /**
     * Get the handler that will execute the request.
     *
     * @param request The received request
     * @return The request handler
     */
    protected abstract RequestHandler getRequestHandler(final Request request);

    /**
     * On component start init attributes.
     */
    @Start
    public void start() {
        super.start();
        if (modelService!=null) {
            modelService.registerModelListener(this);
        }
        waitingRequest = new HashMap<>();
    }

    /**
     * @return The service id (Kevoree component id)
     */
    public final String getId() {
        return context.getInstanceName();
    }

    /**
     * @return Kevoree node running the component
     */
    public final String getNode() {
        return context.getNodeName();
    }

    /**
     * @return The full id (nodeName.componentName)
     */
    public final String getFullId() {
        return context.getNodeName() + "." + context.getInstanceName();
    }

    /**
     * Do some stuff just before the request is executed.
     *
     * @param request Request analysed, ready to be executed
     */
    protected void onRequestReady(final Request request) {
    }

    /**
     * Do some stuff just before the response is sent.
     *
     * @param request  The original request
     * @param response The response ready to be sent
     */
    protected void onResponseReady(final Request request,
                                   final Response response) {
    }

    /**
     * Prepare and send the response.
     *
     * @param request The original request
     * @param result  The request's result
     */
    protected final void response(final Request request, final Object result) {
        Response response = new Response(request.getId(),
                getFullId(), request.getSrc(), getCurrentTime(), result);
        onResponseReady(request, response);
        sendResponse(response);
    }

    /**
     * Get a compatible constructor to the supplied parameter types.
     *
     * @param clazz          The class which we want to construct
     * @param methodName     The method to invoke
     * @param parameterTypes The types required of the constructor
     * @return a compatible constructor or null if none exists
     */
    private Method getCompatibleMethod(final Class<?> clazz,
                                       final String methodName,
                                       final Class<?>[] parameterTypes) {
        for (Method method : clazz.getMethods()) {
            // if the method name does match we go to the next method
            if (method.getName().compareTo(methodName) != 0) {
                continue;
            }
            int numRequestParameters = 0;
            if (parameterTypes != null) {
                numRequestParameters = parameterTypes.length;
            }
            if (method.getParameterTypes().length == numRequestParameters) {
                // If we have the same number of parameters there is a shot that we have a compatible method
                Class<?>[] constructorTypes = method.getParameterTypes();
                boolean isCompatible = true;
                for (int j = 0; j < (parameterTypes != null ? parameterTypes.length : 0); j++) {
                    if (!constructorTypes[j].isAssignableFrom(parameterTypes[j])) {
                        // The type is not assignment compatible, however
                        // we might be able to coerce from a basic type to a boxed type
                        if (constructorTypes[j].isPrimitive()) {
                            if (!isAssignablePrimitiveToBoxed(constructorTypes[j], parameterTypes[j])) {
                                isCompatible = false;
                                break;
                            }
                        }
                    }
                }
                if (isCompatible) {
                    return method;
                }
            }
        }

        return null;
    }

    /**
     * <p> Checks if a primitive type is assignable with a boxed type.</p>
     *
     * @param primitive a primitive class type
     * @param boxed     a boxed class type
     * @return true if primitive and boxed are assignment compatible
     */
    private static boolean isAssignablePrimitiveToBoxed(final Class<?> primitive, final Class<?> boxed) {
        if (primitive.equals(java.lang.Boolean.TYPE)) {
            return boxed.equals(Boolean.class);
        } else {
            if (primitive.equals(java.lang.Byte.TYPE)) {
                return boxed.equals(Byte.class);
            } else {
                if (primitive.equals(java.lang.Character.TYPE)) {
                    return boxed.equals(Character.class);
                } else {
                    if (primitive.equals(java.lang.Double.TYPE)) {
                        return boxed.equals(Double.class);
                    } else {
                        if (primitive.equals(java.lang.Float.TYPE)) {
                            return boxed.equals(Float.class);
                        } else {
                            if (primitive.equals(java.lang.Integer.TYPE)) {
                                return boxed.equals(Integer.class);
                            } else {
                                if (primitive.equals(java.lang.Long.TYPE)) {
                                    return boxed.equals(Long.class);
                                } else {
                                    if (primitive.equals(java.lang.Short.TYPE)) {
                                        return boxed.equals(Short.class);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return Map of Request waiting for external response
     */
    protected final HashMap<UUID, RequestCallback> getWaitingRequest() {
        return waitingRequest;
    }

    /**
     * @param request  The request to send
     * @param callback The callback
     */
    public final void sendRequest(final Request request, final RequestCallback callback) {
        if (lastCheckExpiredRequests-System.currentTimeMillis()>Request.DEFAULT_TIMEOUT) {
            removeExpiredRequests();
        }
        if (pushRequest != null && pushRequest.getConnectedBindingsSize() > 0) {
            if (callback != null) {
                waitingRequest.put(request.getId(), callback);
            }
            pushRequest.send(request.toString(), null);
        }
    }

    private void removeExpiredRequests() {
        // TODO remove expired request
        lastCheckExpiredRequests = System.currentTimeMillis();
    }

    /**
     * @param response The response to send
     */
    public final void sendResponse(final Response response) {
        if (pushResponse != null && pushResponse.getConnectedBindingsSize() > 0) {
            pushResponse.send(response.toString(), null);
        }
    }

    /**
     * @param notif The notif to send
     */
    public final void sendNotif(final Notif notif) {
        if (pushNotif != null && pushNotif.getConnectedBindingsSize() > 0) {
            pushNotif.send(notif.toString(), null);
        }
    }

    /**
     * Push an object into an object array.
     *
     * @param array The origin array
     * @param obj   The object to push
     * @return The new array (size+1)
     */
    protected final Object[] push(final Object[] array, final Object obj) {
        Object[] out = new Object[array.length + 1];
        System.arraycopy(array, 0, out, 0, array.length);
        out[out.length - 1] = obj;
        return out;
    }

    /**
     * Shift a String array.
     *
     * @param array The array to shift
     * @return The new array shifted
     */
    protected final String[] shift(final String[] array) {
        String[] out = new String[array.length - 1];
        System.arraycopy(array, 1, out, 0, array.length - 1);
        return out;
    }

    protected final void logInfo(String log) {
        Log.info("[" + getFullId() + " - " + strLocalTime() + "] " + log);
    }

    protected final void logError(String log) {
        Log.error("[" + getFullId() + " - " + strLocalTime() + "] " + log);
    }


    @Override
    public boolean preUpdate(final UpdateContext updateContext) {
        return true;
    }

    @Override
    public boolean initUpdate(final UpdateContext updateContext) {
        return true;
    }


    @Override
    public void preRollback(UpdateContext updateContext) { }

    @Override
    public void postRollback(UpdateContext updateContext) { }

    @Override
    public boolean afterLocalUpdate(final UpdateContext updateContext) {
        return true;
    }

    @Override
    public void modelUpdated() {
        if (firstModelUpdate) {
            firstModelUpdate = false;
        }
    }

    protected boolean isFirstModelUpdate() {
        return firstModelUpdate;
    }

    protected ModelService getModelService() {
        return modelService;
    }

}



