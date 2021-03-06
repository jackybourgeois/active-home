package org.activehome.evaluator;

/*
 * #%L
 * Active Home :: Evaluator
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
import org.activehome.context.data.Schedule;

import java.util.HashMap;

/**
 * @author Jacky Bourgeois
 */
public class EvaluationReport {

    private String evaluatorName;
    private String version;
    private HashMap<String, String> reportedMetrics;
    private Schedule schedule;

    public EvaluationReport(final String evaluatorName,
                            final String version,
                            final HashMap<String, String> theReportedMetrics,
                            final Schedule theSchedule) {
        this.evaluatorName = evaluatorName;
        this.version = version;
        reportedMetrics = theReportedMetrics;
        schedule = theSchedule;
    }

    public EvaluationReport(final JsonObject json) {
        evaluatorName = json.getString("evaluatorName","");
        version = json.getString("version","");
        reportedMetrics = new HashMap<>();
        for (String name : json.get("reportedMetrics").asObject().names()) {
            reportedMetrics.put(name, json.get("reportedMetrics")
                    .asObject().get(name).asString());
        }
        schedule = new Schedule(json.get("schedule").asObject());
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public String getVersion() {
        return version;
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
        json.add("evaluatorName", evaluatorName);
        json.add("version", version);
        json.add("schedule", schedule.toJson());

        JsonObject map = new JsonObject();
        for (String key : reportedMetrics.keySet()) {
            map.add(key, reportedMetrics.get(key));
        }
        json.add("reportedMetrics", map);

        return json;
    }


}
