package org.activehome.context.data;

/*
 * #%L
 * Active Home :: Context
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
