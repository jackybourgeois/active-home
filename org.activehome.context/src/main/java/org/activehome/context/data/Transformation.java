package org.activehome.context.data;

import com.eclipsesource.json.JsonObject;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class Transformation {

    private TransformationType type;
    private double param;

    public Transformation(final TransformationType theType,
                          final double theParam) {
        type = theType;
        param = theParam;
    }

    public Transformation(final JsonObject jsonObject) {
        type = TransformationType.valueOf(jsonObject.get("type").asString());
        param = jsonObject.get("param").asDouble();
    }

    public TransformationType getType() {
        return type;
    }

    public void setType(final TransformationType type) {
        this.type = type;
    }

    public double getParam() {
        return param;
    }

    public void setParam(final double param) {
        this.param = param;
    }

    public enum TransformationType {
        SHIFT, SPLIT, REDUCE
    }

    public String toString() {
        return toJson().toString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", type.name());
        json.add("param", param);
        return json;
    }
}
