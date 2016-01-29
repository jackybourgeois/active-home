package org.activehome.context.process;

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


import org.activehome.com.error.Error;
import org.activehome.context.Context;
import org.activehome.context.data.*;
import org.activehome.context.exception.ContextTriggerException;
import org.activehome.context.process.InfixEvaluator;
import org.kevoree.log.Log;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Manage control and computation that follow new DataPoint entries.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class TriggerManager {

    /**
     * The map of all control trigger, checking new DataPoint correctness.
     */
    private HashMap<String, LinkedList<Trigger>> ctrlTriggerMap;
    /**
     * The map of all usage trigger, computing values based on new DataPoint.
     */
    private HashMap<String, LinkedList<Trigger>> useTriggerMap;
    /**
     * The related context.
     */
    private Context context;

    /**
     * @param theContext The related context
     */
    public TriggerManager(final Context theContext) {
        context = theContext;
        useTriggerMap = new HashMap<>();
        ctrlTriggerMap = new HashMap<>();
    }

    /**
     *
     */
    private DecimalFormat dec = new DecimalFormat("#.########");

    /**
     * Add new control trigger: a check and correction of the new DataPoint.
     */
    public final void addCtrlTrigger(final Trigger trigger) {
        if (!ctrlTriggerMap.containsKey(trigger.getTriggerRegex())) {
            ctrlTriggerMap.put(trigger.getTriggerRegex(), new LinkedList<>());
        }
        ctrlTriggerMap.get(trigger.getTriggerRegex()).add(trigger);
    }

    /**
     * Add new use trigger: a computation following the new DataPoint,
     * generating yet another DataPoint.
     */
    public final void addUseTrigger(final Trigger trigger) {
        if (!useTriggerMap.containsKey(trigger.getTriggerRegex())) {
            useTriggerMap.put(trigger.getTriggerRegex(), new LinkedList<>());
        }
        useTriggerMap.get(trigger.getTriggerRegex()).add(trigger);
    }

    /**
     * @param id The id of the trigger to be removed
     * @return true
     */
    public final boolean removeTrigger(final UUID id) {
        for (String triggerRegex : useTriggerMap.keySet()) {
            useTriggerMap.put(triggerRegex,
                    useTriggerMap.get(triggerRegex).stream()
                            .filter(trigger -> !trigger.getId().equals(id))
                            .collect(Collectors.toCollection(LinkedList::new)));
        }
        for (String triggerRegex : ctrlTriggerMap.keySet()) {
            ctrlTriggerMap.put(triggerRegex,
                    ctrlTriggerMap.get(triggerRegex).stream()
                            .filter(trigger -> !trigger.getId().equals(id))
                            .collect(Collectors.toCollection(LinkedList::new)));
        }
        return true;
    }

    /**
     * Goes through the list of checker and correct the data point.
     *
     * @param dp The new DataPoint
     * @return The corrected data point
     */
    public final DataPoint checkCtrlTriggers(final DataPoint dp) {
        DataPoint resultDP = dp;

        for (String regex : ctrlTriggerMap.keySet()) {
            if (resultDP.getMetricId().matches(regex)) {
                for (Trigger trigger : ctrlTriggerMap.get(regex)) {
                    try {
                        Object result = eval(trigger.getInfixExpression(), dp);
                        if (result instanceof org.activehome.com.error.Error) {
                            Log.error("Trigger error: " + result.toString());
                        } else {
                            resultDP = new DataPoint(dp.getMetricId(),
                                    dp.getTS(), result.toString());
                        }
                    } catch (ContextTriggerException e) {
                        Log.warn("Context trigger exception: "
                                + e.getMessage());
                    }
                }
            }
        }

        return resultDP;
    }

    /**
     * @param dp The new DataPoint
     * @return The list of new DataPoint generated by the triggers
     */
    public final LinkedList<DataPoint> checkUseTriggers(final DataPoint dp) {
        LinkedList<DataPoint> changes = new LinkedList<>();
        useTriggerMap.keySet().stream()
                .filter(triggerRegex -> dp.getMetricId().matches(triggerRegex))
                .forEach(triggerRegex -> {
                    for (Trigger trigger : useTriggerMap.get(triggerRegex)) {
                        String resultMetric = trigger.getResultMetric()
                                .replaceAll("\\$\\{triggerMetric\\}", dp.getMetricId());
                        try {
                            Object result = eval(trigger.getInfixExpression(), dp);
                            if (result instanceof Error) {
                                Log.error("Trigger error: " + result.toString());
                            } else {
                                try {
                                    double val = Double.parseDouble(result.toString());
                                    changes.add(new DataPoint(resultMetric,
                                            dp.getTS(), dec.format(val)));
                                } catch (NumberFormatException e) {
                                    changes.add(new DataPoint(resultMetric,
                                            dp.getTS(), result.toString()));
                                }
                            }
                        } catch (ContextTriggerException e) {
                            Log.warn("Context trigger exception: " + e.getMessage());
                        }
                    }
                });
        return changes;
    }

    /**
     * Check use triggers over a period.
     *
     * @param newMR
     * @param environmentSchedule
     * @return
     */
    public final LinkedList<MetricRecord> checkUseTriggers(final MetricRecord newMR,
                                                           final Schedule environmentSchedule) {
        System.out.println("check use triggers for " + newMR.getMetricId());
        LinkedList<MetricRecord> changes = new LinkedList<>();

        useTriggerMap.keySet().stream()
                .filter(triggerRegex -> newMR.getMetricId().matches(triggerRegex))
                .forEach(triggerRegex -> {
                    for (Trigger trigger : useTriggerMap.get(triggerRegex)) {
                        String resultMetric = trigger.getResultMetric()
                                .replaceAll("\\$\\{triggerMetric\\}", newMR.getMetricId());
                        changes.add(eval(trigger.getInfixExpression(), resultMetric, newMR, environmentSchedule));
                    }
                });

        return changes;
    }

    /**
     * @param expression The infix expression to evaluate
     * @param triggerDP  The new DataPoint which triggered the evaluation
     * @return The result of the evaluation
     * @throws ContextTriggerException Missing value or alternative value
     */
    private Object eval(final String expression, final DataPoint triggerDP)
            throws ContextTriggerException {
        String infix = replacePathInCondition(expression, triggerDP);
        InfixEvaluator infixEvaluator = new InfixEvaluator();
        return infixEvaluator.evalInfix(infix);
    }

    private MetricRecord eval(final String expression,
                              final String resultMetricRecord,
                              final MetricRecord newMR,
                              final Schedule environmentSchedule) {
        System.out.println("trigger eval mr " + newMR.getMetricId() + ", main version: " + newMR.getMainVersion()
                + ", nb records: " + newMR.getRecords().size());
        MetricRecord resultMR = new MetricRecord(resultMetricRecord, newMR.getTimeFrame());
        SnapShot snapShot = new SnapShot(environmentSchedule);
        while (snapShot.next()) {
            DataPoint triggerDP = snapShot.getCurrentDP(newMR.getMetricId(), newMR.getMainVersion());
            String infix = null;
            try {
                infix = replacePathInCondition(expression, triggerDP, snapShot);
                InfixEvaluator infixEvaluator = new InfixEvaluator();
                Object result = infixEvaluator.evalInfix(infix);
                if (result instanceof org.activehome.com.error.Error) {
                    Log.error("Trigger error: " + result.toString());
                } else {
                    if (resultMR.getRecords() == null || !resultMR.getLastValue().equals(result.toString())) {
                        resultMR.addRecord(environmentSchedule.getStart() + snapShot.getTS(),
                                result.toString(), newMR.getMainVersion(), 1);
                    }
                }
            } catch (ContextTriggerException e) {
                Log.warn("Context trigger exception: " + e.getMessage());
            }
        }

        return resultMR;
    }

    /**
     * @param expression The infix expression to evaluate
     * @param triggerDP  The new DataPoint which triggered the evaluation
     * @return Infix ready to be evaluated
     * @throws ContextTriggerException Missing value or alternative value
     */
    private String replacePathInCondition(final String expression,
                                          final DataPoint triggerDP)
            throws ContextTriggerException {
        // replace triggered dp metric and val
        String updatedExpression = expression.replaceAll("\\$\\{triggerMetric\\}", triggerDP.getMetricId())
                .replaceAll("\\$\\{triggerValue\\}", triggerDP.getValue());

        // replace current metric values
        Matcher m = Pattern.compile("\\$\\{([^}]+)\\}").matcher(updatedExpression);
        StringBuffer stringBuffer = new StringBuffer();
        while (m.find()) {
            String[] metricAndAlternative = m.group().replace("${", "").replace("}", "").split(",");
            DataPoint curDP = context.getCurrentDP(metricAndAlternative[0], "0", 0);
            if (curDP != null) {
                m.appendReplacement(stringBuffer, curDP.getValue());
            } else if (metricAndAlternative.length > 1) {
                m.appendReplacement(stringBuffer, metricAndAlternative[1]);
            } else {
                throw new ContextTriggerException("Metric " + metricAndAlternative[0]
                        + " does not exist and no alternative provided.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        // replace current metric ts
        m = Pattern.compile("\\$ts\\{([^}]+)\\}").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            String metricAndAlternative = m.group().replace("$ts{", "").replace("}", "");
            DataPoint curDP = context.getCurrentDP(metricAndAlternative, "0", 0);
            if (curDP != null) {
                m.appendReplacement(stringBuffer, curDP.getTS() + "");
            } else {
                throw new ContextTriggerException("TS for metric "
                        + metricAndAlternative + " does not exist.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        // replace previous metric values
        m = Pattern.compile("\\$-1\\{([^}]+)\\}").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            String[] metricAndAlternative = m.group().replace("$-1{", "").replace("}", "").split(",");
            DataPoint prevDP = context.getPreviousDP(metricAndAlternative[0], "0", 0);
            if (prevDP != null) {
                m.appendReplacement(stringBuffer, prevDP.getValue());
            } else if (metricAndAlternative.length > 1) {
                m.appendReplacement(stringBuffer, metricAndAlternative[1]);
            } else {
                throw new ContextTriggerException("Metric " + metricAndAlternative[0]
                        + " does not exist at t-1 and no alternative provided.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        // replace current metric ts
        m = Pattern.compile("\\$ts-1\\{([^}]+)\\}").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            String metricAndAlternative = m.group().replace("$ts-1{", "").replace("}", "");
            DataPoint prevDP = context.getPreviousDP(metricAndAlternative, "0", 0);
            if (prevDP != null) {
                m.appendReplacement(stringBuffer, prevDP.getTS() + "");
            } else {
                throw new ContextTriggerException("TS for metric " + metricAndAlternative + " does not exist at t-1.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        m = Pattern.compile("sum\\(.*?\\)").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(stringBuffer, sum(m.group().replace("sum(", "").replace(")", "")) + "");
        }
        m.appendTail(stringBuffer);

        return stringBuffer.toString();
    }


    /**
     * @param expression The infix expression to evaluate
     * @param triggerDP  The new DataPoint which triggered the evaluation
     * @return Infix ready to be evaluated
     * @throws ContextTriggerException Missing value or alternative value
     */
    private String replacePathInCondition(final String expression,
                                          final DataPoint triggerDP,
                                          final SnapShot snapshot)
            throws ContextTriggerException {
        // replace triggered dp metric and val
        String updatedExpression = expression.replaceAll("\\$\\{triggerMetric\\}", triggerDP.getMetricId())
                .replaceAll("\\$\\{triggerValue\\}", triggerDP.getValue());

        String[] versions = new String[]{triggerDP.getVersion(), "0"};

        // replace current metric values
        Matcher m = Pattern.compile("\\$\\{([^}]+)\\}").matcher(updatedExpression);
        StringBuffer stringBuffer = new StringBuffer();
        while (m.find()) {
            String[] metricAndAlternative = m.group().replace("${", "").replace("}", "").split(",");
            DataPoint curDP = null;
            int i = 0;
            while (curDP == null && i < versions.length) {
                curDP = snapshot.getPreviousDP(metricAndAlternative[0], versions[i]);
                i++;
            }
            if (curDP != null) {
                m.appendReplacement(stringBuffer, curDP.getValue());
            } else if (metricAndAlternative.length > 1) {
                m.appendReplacement(stringBuffer, metricAndAlternative[1]);
            } else {
                throw new ContextTriggerException("Metric " + metricAndAlternative[0]
                        + " does not exist and no alternative provided.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        // replace current metric ts
        m = Pattern.compile("\\$ts\\{([^}]+)\\}").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            String metricAndAlternative = m.group().replace("$ts{", "").replace("}", "");
            DataPoint curDP = null;
            int i = 0;
            while (curDP == null && i < versions.length) {
                curDP = snapshot.getPreviousDP(metricAndAlternative, versions[i]);
                i++;
            }
            if (curDP != null) {
                m.appendReplacement(stringBuffer, curDP.getTS() + "");
            } else {
                throw new ContextTriggerException("TS for metric "
                        + metricAndAlternative + " does not exist.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        // replace previous metric values
        m = Pattern.compile("\\$-1\\{([^}]+)\\}").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            String[] metricAndAlternative = m.group().replace("$-1{", "").replace("}", "").split(",");
            DataPoint prevDP = null;
            int i = 0;
            while (prevDP == null && i < versions.length) {
                prevDP = snapshot.getPreviousDP(metricAndAlternative[0], versions[i]);
                i++;
            }
            if (prevDP != null) {
                m.appendReplacement(stringBuffer, prevDP.getValue());
            } else if (metricAndAlternative.length > 1) {
                m.appendReplacement(stringBuffer, metricAndAlternative[1]);
            } else {
                throw new ContextTriggerException("Metric " + metricAndAlternative[0]
                        + " does not exist at t-1 and no alternative provided.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        // replace current metric ts
        m = Pattern.compile("\\$ts-1\\{([^}]+)\\}").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            String metricAndAlternative = m.group().replace("$ts-1{", "").replace("}", "");
            DataPoint prevDP = null;
            int i = 0;
            while (prevDP == null && i < versions.length) {
                prevDP = snapshot.getPreviousDP(metricAndAlternative, versions[i]);
                i++;
            }
            if (prevDP != null) {
                m.appendReplacement(stringBuffer, prevDP.getTS() + "");
            } else {
                throw new ContextTriggerException("TS for metric " + metricAndAlternative + " does not exist at t-1.");
            }
        }
        m.appendTail(stringBuffer);
        updatedExpression = stringBuffer.toString();

        m = Pattern.compile("sum\\(.*?\\)").matcher(updatedExpression);
        stringBuffer = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(stringBuffer, sum(m.group().replace("sum(", "").replace(")", ""), versions, snapshot) + "");
        }
        m.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    /**
     * Transform a path (power.cons.*) into an infix (power.cons.wm+power.cons.tv...).
     *
     * @param path The path to transform
     * @return infix expression
     */
    private double sum(final String path) {
        String[] partRegex = path.replaceAll("\\.", "\\\\.").split("\\*");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partRegex.length; i++) {
            sb.append("(").append(partRegex[i]).append(")").append("+(.*?)");
            if (i < partRegex.length - 1) {
                sb.append("+");
            }
        }
        String pathRegex = sb.toString();

        double result = 0;

        Iterator<Map.Entry<String, ConcurrentHashMap<String, ConcurrentHashMap<Long, DataPoint>>>> iterator
                = context.getCurrentDP().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConcurrentHashMap<String, ConcurrentHashMap<Long, DataPoint>>> entry = iterator.next();
            if (entry.getKey().matches(pathRegex)) {
                if (entry.getValue().get("0") != null) {
                    result += toNumber(entry.getValue().get("0").get(0L).getValue());
                }
            }
        }

        return result;
    }

    /**
     * Transform a path (power.cons.*) into an infix (power.cons.wm+power.cons.tv...).
     *
     * @param path The path to transform
     * @return infix expression
     */
    private double sum(final String path,
                       final String[] versions,
                       final SnapShot snapShot) {
        String[] partRegex = path.replaceAll("\\.", "\\\\.").split("\\*");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partRegex.length; i++) {
            sb.append("(").append(partRegex[i]).append(")").append("+(.*?)");
            if (i < partRegex.length - 1) {
                sb.append("+");
            }
        }
        String pathRegex = sb.toString();

        double result = 0;

        for (DataPoint dp : snapShot.getCurrentDPMatch(pathRegex, versions)) {
            result += toNumber(dp.getValue());
        }

        return result;
    }

    /**
     * @param str The string to convert
     * @return the value of str, 0 otherwise
     */
    public final double toNumber(final String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public final String[] getMetricTriggeredBy(String metricId) {
        Set<String> metrics = new HashSet<>();

        LinkedList<String> changes = new LinkedList<>();
        changes.add(metricId);
        while (changes.size() > 0) {
            String change = changes.poll();
            useTriggerMap.keySet().stream()
                    .filter(change::matches)
                    .forEach(triggerRegex -> {
                        for (Trigger trigger : useTriggerMap.get(triggerRegex)) {
                            metrics.addAll(extractVariable(trigger.getInfixExpression()));
                            changes.add(trigger.getResultMetric());
                        }
                    });
        }
        return metrics.toArray(new String[metrics.size()]);
    }

    public final Set<String> extractVariable(String expression) {
        Set<String> metrics = new HashSet<>();
        Matcher m = Pattern.compile("\\$\\{([^}]+)\\}").matcher(expression);
        while (m.find()) {
            metrics.add(m.group().replace("${", "").replace("}", "").split(",")[0]);
        }

        // replace current metric ts
        m = Pattern.compile("\\$ts\\{([^}]+)\\}").matcher(expression);
        while (m.find()) {
            metrics.add(m.group().replace("$ts{", "").replace("}", ""));
        }

        // replace previous metric values
        m = Pattern.compile("\\$-1\\{([^}]+)\\}").matcher(expression);
        while (m.find()) {
            metrics.add(m.group().replace("$-1{", "").replace("}", "").split(",")[0]);
        }

        // replace previous metric ts
        m = Pattern.compile("\\$ts-1\\{([^}]+)\\}").matcher(expression);
        while (m.find()) {
            metrics.add(m.group().replace("$ts-1{", "").replace("}", ""));
        }

        m = Pattern.compile("sum\\(.*?\\)").matcher(expression);
        while (m.find()) {
            metrics.add(m.group().replace("sum(", "").replace(")", ""));
        }

        return metrics;
    }

//    /**
//     * Set the default triggers in actual mode.
//     */
//    public final void setTriggers() {
//        addUseTrigger("(^power\\.gen\\.)+(.*?)", "sum(power.gen.*)", "power.gen");
//        addUseTrigger("^(power\\.import|power\\.export|power\\.gen)$", "${power.import,0}+${power.gen,0}-${power.export,0}", "power.cons");
//
//        addUseTrigger("(^power\\.cons$)|((^power\\.gen\\.)+(.*?))", "${power.cons,0}-sum(power.gen.*)", "power.balance");
//
//        addUseTrigger("^power\\.cons$", "($-1{power.cons}/1000)*(($ts{power.cons}-$ts-1{power.cons})/3600000)", "energy.cons");
//        addUseTrigger("^power\\.gen$", "($-1{power.gen}/1000)*(($ts{power.gen}-$ts-1{power.gen})/3600000)", "energy.gen");
//        addUseTrigger("^power\\.import$", "($-1{power.import}/1000)*(($ts{power.import}-$ts-1{power.import})/3600000)", "energy.import");
//        addUseTrigger("^power\\.export$", "($-1{power.export}/1000)*(($ts{power.export}-$ts-1{power.export})/3600000)", "energy.export");
//
//        addUseTrigger("^energy\\.importCost$", "${energy.importCost,0}", "energy.cost");
//        addUseTrigger("^energy\\.exportBenefits$", "-1*${energy.exportBenefits,0}", "energy.cost");
//        addUseTrigger("^energy\\.generationBenefits$", "-1*${energy.generationBenefits,0}", "energy.cost");
//
//        addCtrlTrigger("^power\\.gen\\.solar", "(${time.dayTime,true}==true)?${triggerValue}:0");
//        addCtrlTrigger("^power\\.export", "(${triggerValue}<${power.gen,0}==true)?${triggerValue}:${power.gen,0}");
//    }
//
//    /**
//     * Set the default triggers in simulation mode.
//     */
//    public final void setTriggersSim() {
//        addUseTrigger("(^power\\.cons\\.inter\\.)+(.*?)", "sum(power.cons.inter.*)", "power.cons.inter");
//        addUseTrigger("(^power\\.cons\\.bg\\.)+(.*?)", "sum(power.cons.bg.*)", "power.cons.bg");
//        addUseTrigger("(^power\\.cons\\.unctrl\\.)+(.*?)", "sum(power.cons.unctrl.*)", "power.cons.unctrl");
//        addUseTrigger("(^power\\.cons\\.storage\\.)+(.*?)", "sum(power.cons.storage.*)", "power.cons.storage");
//        addUseTrigger("(^power\\.gen\\.)+(.*?)", "sum(power.gen.*)", "power.gen");
//
//        addUseTrigger("^power\\.cons\\.(unctrl|inter|bg|storage|baseload)$", "${power.cons.baseload,0}+${power.cons.inter,0}+${power.cons.bg,0}+${power.cons.unctrl,0}+${power.cons.storage,0}", "power.cons");
//        addUseTrigger("(^power\\.cons$)|((^power\\.gen\\.)+(.*?))", "${power.cons,0}-sum(power.gen.*)", "power.balance");
//
//        addUseTrigger("^power\\.balance$", "(${power.balance}>0)?${power.balance}:0", "power.import");
//        addUseTrigger("^power\\.balance$", "(${power.balance}<0)?(-1*${power.balance}):0", "power.export");
//
//        addUseTrigger("^power\\.cons$", "($-1{power.cons}/1000)*(($ts{power.cons}-$ts-1{power.cons})/3600000)", "energy.cons");
//        addUseTrigger("^power\\.cons\\.baseload$", "($-1{power.cons.baseload}/1000)*(($ts{power.cons.baseload}-$ts-1{power.cons.baseload})/3600000)", "energy.cons.baseload");
//        addUseTrigger("^power\\.cons\\.inter$", "($-1{power.cons.inter}/1000)*(($ts{power.cons.inter}-$ts-1{power.cons.inter})/3600000)", "energy.cons.inter");
//        addUseTrigger("^power\\.cons\\.bg$", "($-1{power.cons.bg}/1000)*(($ts{power.cons.bg}-$ts-1{power.cons.bg})/3600000)", "energy.cons.bg");
//        addUseTrigger("^power\\.cons\\.unctrl$", "($-1{power.cons.unctrl}/1000)*(($ts{power.cons.unctrl}-$ts-1{power.cons.unctrl})/3600000)", "energy.cons.unctrl");
//        addUseTrigger("^power\\.cons\\.storage$", "($-1{power.cons.storage}/1000)*(($ts{power.cons.storage}-$ts-1{power.cons.storage})/3600000)", "energy.cons.storage");
//        addUseTrigger("^power\\.gen$", "($-1{power.gen}/1000)*(($ts{power.gen}-$ts-1{power.gen})/3600000)", "energy.gen");
//        addUseTrigger("^power\\.import$", "($-1{power.import}/1000)*(($ts{power.import}-$ts-1{power.import})/3600000)", "energy.import");
//        addUseTrigger("^power\\.export$", "($-1{power.export}/1000)*(($ts{power.export}-$ts-1{power.export})/3600000)", "energy.export");
//
//        addUseTrigger("^energy\\.import$", "${energy.import}*${grid.carbonIntensity,0}", "energy.co2Emission");
//        addUseTrigger("^energy\\.import$", "${energy.import}*${tariff.elec.import,0}", "energy.importCost");
//        addUseTrigger("^energy\\.export$", "${energy.export}*${tariff.elec.export,0}", "energy.exportBenefits");
//        addUseTrigger("^energy\\.gen$", "${energy.gen}*${tariff.elec.generation,0}", "energy.generationBenefits");
//
//        addUseTrigger("^energy\\.importCost$", "${energy.importCost,0}", "energy.cost");
//        addUseTrigger("^energy\\.exportBenefits$", "-1*${energy.exportBenefits,0}", "energy.cost");
//        addUseTrigger("^energy\\.generationBenefits$", "-1*${energy.generationBenefits,0}", "energy.cost");
//
//        addUseTrigger("^power\\.origin\\.import$", "($-1{power.origin.import}/1000)*(($ts{power.origin.import}-$ts-1{power.origin.import})/3600000)", "energy.origin.import");
//        addUseTrigger("^power\\.origin\\.export$", "($-1{power.origin.export}/1000)*(($ts{power.origin.export}-$ts-1{power.origin.export})/3600000)", "energy.origin.export");
//
//        addCtrlTrigger("^power\\.gen\\.solar\\.", "(${time.dayTime,true}==true)?${triggerValue}:0");
//    }

}
