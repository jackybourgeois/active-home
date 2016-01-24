package org.activehome.context.data;

import com.eclipsesource.json.JsonObject;

import java.util.UUID;

/**
 * Triggers are used by the context to check and compute
 * data based on new DataPoint.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Trigger {

    /**
     * The unique id.
     */
    private final UUID id;
    /**
     * The expression that filter relevant metrics.
     */
    private String triggerRegex;
    /**
     * Tne expression to compute.
     */
    private String infixExpression;
    /**
     * The metric of the computation's result.
     */
    private String resultMetric;

    /**
     * @param theTriggerRegex The expression that filter relevant metrics
     * @param theInfixExpression Tne expression to compute
     * @param theResultMetric The metric of the computation's result
     */
    public Trigger(final String theTriggerRegex,
                   final String theInfixExpression,
                   final String theResultMetric) {
        id = UUID.randomUUID();
        triggerRegex = theTriggerRegex;
        infixExpression = theInfixExpression;
        resultMetric = theResultMetric;
    }

    /**
     * @param json Json that can be map as Trigger
     */
    public Trigger(final JsonObject json) {
        id = UUID.fromString(json.get("id").asString());
        triggerRegex = json.getString("triggerRegex", "");
        infixExpression = json.getString("infixExpression", "");
        resultMetric = json.getString("resultMetric", "");
    }

    /**
     * @return The unique id
     */
    public final UUID getId() {
        return id;
    }

    /**
     * @return The expression that trigger the relevant metrics
     */
    public final String getTriggerRegex() {
        return triggerRegex;
    }

    /**
     * @return The infix expression to be computed
     */
    public final String getInfixExpression() {
        return infixExpression;
    }

    /**
     * @return The computation's result
     */
    public final String getResultMetric() {
        return resultMetric;
    }

    /**
     * Convert the Trigger into Json.
     *
     * @return the Json
     */
    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", Trigger.class.getName());
        json.add("id", id.toString());
        json.add("triggerRegex", triggerRegex);
        json.add("infixExpression", infixExpression);
        json.add("resultMetric", resultMetric);
        return json;
    }

    /**
     * @return The Json as String
     */
    @Override
    public final String toString() {
        return toJson().toString();
    }
}
