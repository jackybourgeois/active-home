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


import com.eclipsesource.json.JsonObject;
import org.activehome.context.data.Schedule;

import java.util.HashMap;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class EvaluationReport {

    private HashMap<String, String> reportedMetrics;
    private Schedule schedule;

    public EvaluationReport(final HashMap<String, String> theReportedMetrics,
                            final Schedule theSchedule) {
        reportedMetrics = theReportedMetrics;
        schedule = theSchedule;
    }

    public EvaluationReport(final JsonObject json) {
        reportedMetrics = new HashMap<>();
        for (String name : json.get("reportedMetrics").asObject().names()) {
            reportedMetrics.put(name, json.get("reportedMetrics")
                    .asObject().get(name).asString());
        }
        schedule = new Schedule(json.get("schedule").asObject());
    }

    public HashMap<String, String> getReportedMetrics() {
        return reportedMetrics;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.add("type", EvaluationReport.class.getName());
        json.add("schedule", schedule.toJson());

        JsonObject map = new JsonObject();
        for (String key : reportedMetrics.keySet()) {
            map.add(key, reportedMetrics.get(key));
        }
        json.add("reportedMetrics", map);

        return json;
    }


}
