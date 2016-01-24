package org.activehome.evaluator;

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
