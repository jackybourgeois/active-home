package org.activehome.context;

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
import org.activehome.com.Notif;
import org.activehome.com.Request;
import org.activehome.com.RequestCallback;
import org.activehome.com.ShowIfErrorCallback;
import org.activehome.com.error.Error;
import org.activehome.com.error.ErrorType;
import org.activehome.context.com.ContextRequest;
import org.activehome.context.data.DataPoint;
import org.activehome.context.data.DiscreteDataPoint;
import org.activehome.context.data.MetricRecord;
import org.activehome.context.data.Record;
import org.activehome.context.data.SampledRecord;
import org.activehome.context.data.Schedule;
import org.activehome.context.data.UserInfo;
import org.activehome.context.process.TriggerManager;
import org.activehome.service.RequestHandler;
import org.activehome.service.Service;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.log.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A context stores, triggers, notifies and shares data.
 *
 * @author Jacky Bourgeois
 */
@ComponentType(version = 1, description = "A context stores, "
        + "triggers, notifies and shares data.")
public abstract class Context extends Service {

    /**
     * Start date of the test.
     */
    @Param(defaultValue = "false")
    protected boolean showNotif;

    /**
     * The map of &lt;subscriber, &lt;metric1,metric2...&gt;&gt;.
     */
    private HashMap<String, LinkedList<String>> subscriptionMap;
    /**
     * The map of the most recent data point
     * of each metric &lt;metricId, DataPoint&gt;.
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<Long, DataPoint>>> currentDPMap;
    /**
     * The map of the second most recent data point
     * of each metric &lt;metricId, DataPoint&gt;.
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<Long, DataPoint>>> previousDPMap;
    /**
     * The trigger manager.
     */
    private TriggerManager triggerManager;

    private long nextSequenceNumber;
    private TreeMap<Long, DataPoint> waitingDP;

    /**
     * Port to publish notifications to other Active Home components.
     */
    @Output
    private org.kevoree.api.Port pushDataToSystem;
    /**
     * Port to publish notifications outside Active Home (through APIs).
     */
    @Output
    private org.kevoree.api.Port pushDataOutside;

    /**
     * Save new data point in long term memory.
     *
     * @param dp the new data point
     */
    protected abstract void save(DataPoint dp);

    /**
     * Save new data point in long term memory.
     *
     * @param dpArray the array of new data points
     */
    protected abstract void save(List<DataPoint> dpArray);

    /**
     * Provide the last DataPoint received of the given metrics.
     *
     * @param metricArray the metrics we are looking for
     * @param callback    The callback to reply
     */
    public abstract void getLastData(String[] metricArray,
                                     RequestCallback callback);

    /**
     * Provide the DataPoints of a given metric for a given period of time.
     *
     * @param metric   The metric we are looking at
     * @param start    The start time of the period (milliseconds)
     * @param end      The end time of the period (milliseconds)
     * @param callback The callback to reply
     */
    public abstract void getData(String metric,
                                 long start,
                                 long end,
                                 RequestCallback callback);

    /**
     * Extract DataPoints from the context.
     *
     * @param contextRequest The details of the data to extract
     * @param iteration      How many request should be performed
     * @param shift          Length of the time shift between each request
     * @param callback       The callback to reply
     */
    public abstract void extractSampleData(ContextRequest contextRequest,
                                           int iteration,
                                           long shift,
                                           RequestCallback callback);

    public abstract void extractSchedule(long start,
                                         long duration,
                                         long granularity,
                                         String[] metricArray,
                                         RequestCallback callback);

    public abstract void getAllAvailableMetrics(UserInfo userInfo,
                                                RequestCallback callback);

    /**
     * @return The map of most recent DataPoints
     */
    public final ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<Long, DataPoint>>> getCurrentDPMap() {
        return currentDPMap;
    }

    public final DataPoint getCurrentDP(final String metridID,
                                        final String version,
                                        final long shift) {
        try {
            return currentDPMap.get(metridID).get(version).get(shift);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * @return The map of second most recent DataPoints
     */
    public final ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<Long, DataPoint>>> getPreviousDPMap() {
        return previousDPMap;
    }

    public final DataPoint getPreviousDP(final String metridID,
                                         final String version,
                                         final long shift) {
        try {
            return currentDPMap.get(metridID).get(version).get(shift);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * On component start init attributes.
     */
    @Override
    public void start() {
        super.start();
        initMaps();
    }

    /**
     * On init, request a handler to HttpAPI and set basic triggers.
     */
    @Override
    public void onInit() {
        super.onInit();
        nextSequenceNumber = 0;

        Request req = new Request(getFullId(), "ah.http", getCurrentTime(),
                "addHandler", new Object[]{"/context", getFullId(), true});
        sendRequest(req, null);

        initMaps();
    }

    public TriggerManager getTriggerManager() {
        return triggerManager;
    }

    /**
     * Manage incoming of DataPoint, DataPoint[], MetricRecord, Schedule and Error
     *
     * @param notifStr External notification
     */
    @Input
    public final void getNotif(final String notifStr) {
        if (showNotif) {
            logInfo(notifStr);
        }
        JsonObject jsonNotif = JsonObject.readFrom(notifStr);
        if (jsonNotif.get("dest").asString().equals(getFullId())) {
            Thread.currentThread().setContextClassLoader(
                    this.getClass().getClassLoader());
            Notif notif = new Notif(jsonNotif);
            if (notif.getContent() instanceof DataPoint[]) {
                manageIncoming((DataPoint[]) notif.getContent(), notif);
            } else if (notif.getContent() instanceof Error) {
                manageIncoming((Error) notif.getContent());
            } else if (notif.getContent() instanceof DataPoint) {
                manageIncoming((DataPoint) notif.getContent(), notif);
            } else if (notif.getContent() instanceof MetricRecord) {
                manageIncoming((MetricRecord) notif.getContent(), notif);
            } else if (notif.getContent() instanceof Schedule) {
                manageIncoming((Schedule) notif.getContent(), notif);
            } else {
                Log.warn("Context received notif with unknown content type: "
                        + notif.getContent().getClass() + "\n" + notif.getContent().toString());
            }
        }
    }

    private void manageIncoming(final Error error) {
        if (error.getErrorType().equals(ErrorType.NO_CONNECTION)) {
            for (String key : subscriptionMap.keySet()) {
                LinkedList<String> toRemove =
                        subscriptionMap.get(key).stream()
                                .filter(path -> path.compareTo(error.getDetails()) == 0)
                                .collect(Collectors.toCollection(LinkedList::new));
                subscriptionMap.get(key).removeAll(toRemove);
            }
        }
    }

    private void manageIncoming(final DataPoint dp,
                                final Notif notif) {
        if (notif.getSequenceNumber() == nextSequenceNumber) {
            updateDataPoint(dp, true);
            nextSequenceNumber++;
            while (waitingDP.containsKey(nextSequenceNumber)) {
                updateDataPoint(waitingDP.remove(nextSequenceNumber), true);
                nextSequenceNumber++;
            }
        } else if (notif.getSequenceNumber() == -1) {
            updateDataPoint(dp, true);
        } else {
            waitingDP.put(notif.getSequenceNumber(), dp);
        }
    }

    private void manageIncoming(final DataPoint[] dpArray,
                                final Notif notif) {
        for (DataPoint dp : dpArray) {
            updateDataPoint(dp, true);
        }
        sendNotif(new Notif(getFullId(), notif.getSrc(), getCurrentTime(),
                "Array of data point managed."));
    }

    private void manageIncoming(final MetricRecord mr,
                                final Notif notif) {
        String[] impactedMetricArray = triggerManager.getMetricTriggeredBy(mr.getMetricId());
        for (int i = 0; i < impactedMetricArray.length; i++) {
            impactedMetricArray[i] += "#" + mr.getMainVersion() + ",0";
        }

        extractSchedule(mr.getStartTime(), mr.getTimeFrame(), HOUR, impactedMetricArray, new RequestCallback() {
            @Override
            public void success(Object result) {
                List<MetricRecord> toSave = checkImpactedMR((Schedule) result, mr);
                toSave.add(mr);
                for (MetricRecord mrToSave : toSave) {
                    saveNewMR(mrToSave);
                }
                Notif ack = new Notif(getFullId(), notif.getSrc(), getCurrentTime(), mr.getMetricId());
                sendNotif(ack);
            }

            @Override
            public void error(Error result) {
                logError(result.toString());
            }
        });
    }

    private List<MetricRecord> checkImpactedMR(final Schedule schedule,
                                               final MetricRecord newMR) {
        schedule.getMetricRecordMap().put(newMR.getMetricId(), newMR);
        LinkedList<MetricRecord> changes = new LinkedList<>();
        LinkedList<MetricRecord> toSave = new LinkedList<>();
        changes.add(newMR);
        while (changes.size() > 0) {
            MetricRecord changedMR = changes.poll();
            LinkedList<MetricRecord> newChanges = triggerManager.checkUseTriggers(changedMR, schedule);
            changes.addAll(newChanges);
            toSave.addAll(newChanges);
            for (MetricRecord newChange : newChanges) {
                schedule.getMetricRecordMap().put(newChange.getMetricId(), newChange);
            }
        }
        return toSave;
    }

    private void saveNewMR(final MetricRecord mrToSave) {
        for (String version : mrToSave.getAllVersionRecords().keySet()) {
            LinkedList<DataPoint> dpToSave = new LinkedList<>();
            LinkedList<Record> versionRecords = mrToSave.getRecords(version);
            for (Record versionRecord : versionRecords) {
                if (versionRecord instanceof SampledRecord) {
                    SampledRecord record = (SampledRecord) versionRecord;
                    dpToSave.add(new DiscreteDataPoint(mrToSave.getMetricId(), mrToSave.getStartTime(),
                            record.getValue(), version, record.getTS(), record.getConfidence(), record.getDuration()));
                } else {
                    dpToSave.add(new DataPoint(mrToSave.getMetricId(), mrToSave.getStartTime() + versionRecord.getTS(),
                            versionRecord.getValue(), version, 0, versionRecord.getConfidence()));
                }
            }
            save(dpToSave);
            notify(mrToSave.getMetricId(), version, mrToSave);
        }
    }

    private void manageIncoming(final Schedule schedule,
                                final Notif notif) {
        for (MetricRecord mr : schedule.getMetricRecordMap().values()) {
            manageIncoming(mr, notif);
        }
        notify(schedule.getName(), "0", schedule);
    }

    /**
     * @param request The external request
     * @return context handler
     */
    protected RequestHandler getRequestHandler(final Request request) {
        return new ContextRequestHandler(request, this);
    }

    /**
     * @return The map of &lt;subscriber, &lt;metric1,metric2...&gt;&gt;
     */
    protected HashMap<String, LinkedList<String>> getSubscriptionMap() {
        return subscriptionMap;
    }

    /**
     *
     */
    private void initMaps() {
        waitingDP = new TreeMap<>();
        subscriptionMap = new HashMap<>();
        currentDPMap = new ConcurrentHashMap<>();
        previousDPMap = new ConcurrentHashMap<>();
        triggerManager = new TriggerManager(this);
    }

    /**
     * Run control triggers on the new dp, then check if it exists
     * a current value. update if changed or does not exist, nothing otherwise
     *
     * @return the list of dp which did not changed
     */
    private LinkedList<DataPoint> updateIfChanged(final LinkedList<DataPoint> changes,
                                                  final DataPoint dp) {
        LinkedList<DataPoint> didNotChange = new LinkedList<>();
        for (DataPoint toCtrlDP : changes) {
            DataPoint ctrlDP = triggerManager.checkCtrlTriggers(toCtrlDP);
            if (!insertInCurrentDPMapIfNotExists(ctrlDP, currentDPMap)) {
                DataPoint currentDP = currentDPMap.get(ctrlDP.getMetricId())
                        .get(ctrlDP.getVersion()).get(ctrlDP.getShift());
                if (!currentDP.getValue().equals(ctrlDP.getValue())) {
                    if (!insertInCurrentDPMapIfNotExists(currentDP, previousDPMap)) {
                        previousDPMap.get(ctrlDP.getMetricId()).get(ctrlDP.getVersion())
                                .put(ctrlDP.getShift(), currentDP);
                    }
                    currentDPMap.get(ctrlDP.getMetricId())
                            .get(ctrlDP.getVersion()).put(ctrlDP.getShift(), ctrlDP);
                } else if (!(ctrlDP instanceof DiscreteDataPoint)) {
                    didNotChange.add(toCtrlDP);
                }
            }
        }
        return didNotChange;
    }

    /**
     * Insert the given data point in the map of dp if the combination
     * metric-version-shift does not exist
     *
     * @param dp the data point
     * @return true if the metricId-version-shift did not exist
     */
    private boolean insertInCurrentDPMapIfNotExists(final DataPoint dp,
                                                    final ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<Long, DataPoint>>> map) {
        if (!map.containsKey(dp.getMetricId())) {
            map.put(dp.getMetricId(), new ConcurrentHashMap<>());
        }
        if (!map.get(dp.getMetricId()).containsKey(dp.getVersion())) {
            map.get(dp.getMetricId()).put(dp.getVersion(), new ConcurrentHashMap<>());
        }
        if (!map.get(dp.getMetricId()).get(dp.getVersion()).containsKey(dp.getShift())) {
            map.get(dp.getMetricId()).get(dp.getVersion()).put(dp.getShift(), dp);
            return true;
        }
        return false;
    }


    /**
     * @param dp The new DataPoint
     */
    private void updateDataPoint(final DataPoint dp,
                                 final boolean notifyChanges) {
        LinkedList<DataPoint> dpToSave = new LinkedList<>();
        LinkedList<DataPoint> changes = new LinkedList<>();
        changes.add(dp);
        while (changes.size() > 0) {
            LinkedList<DataPoint> toRemove = updateIfChanged(changes, dp);
            changes.removeAll(toRemove);
            LinkedList<DataPoint> newChanges = new LinkedList<>();
            dpToSave.addAll(changes);
            for (DataPoint changedDP : changes) {
                newChanges.addAll(triggerManager.checkUseTriggers(
                        currentDPMap.get(changedDP.getMetricId())
                                .get(changedDP.getVersion()).get(changedDP.getShift())));
                if (notifyChanges) {
                    notify(changedDP.getMetricId(), changedDP.getVersion(), changedDP);
                }
            }
            changes.clear();
            changes.addAll(newChanges);
        }
        save(dpToSave);
    }

    /**
     * Notify all registered destination.
     *
     * @param metric  The metric that changed, to notify to subscriber
     * @param version The metric's version
     * @param content The new content to notify to subscriber
     */
    public final void notify(final String metric,
                             final String version,
                             final Object content) {
        if (version.equals("0")) {
            // send to subscriptions
            LinkedList<String> destList = new LinkedList<>();
            for (String key : subscriptionMap.keySet()) {
                if (metric.startsWith(key.replace("*", ""))) {
                    destList.addAll(subscriptionMap.get(key));
                }
            }
            for (String dest : destList) {
                sendNotification(new Notif(getFullId(),
                        dest, getCurrentTime(), content));
            }

            // subscription for everything
            if (subscriptionMap.containsKey("*")) {
                destList = subscriptionMap.get("*");
                for (String dest : destList) {
                    logInfo("### sending notification to * subscribers " + dest);
                    sendNotification(new Notif(getFullId(),
                            dest, getCurrentTime(), content));
                }
            }
        }
    }

    /**
     * Dispatch notification to all subscribers.
     *
     * @param notif The notification to dispatch
     */
    private void sendNotification(final Notif notif) {
        if (notif.getDest().contains("://")) {
            if (pushDataOutside != null
                    && pushDataOutside.getConnectedBindingsSize() > 0) {
                pushDataOutside.send(notif.toString(), null);
            }
        } else {
            if (pushDataToSystem != null
                    && pushDataToSystem.getConnectedBindingsSize() > 0) {
                pushDataToSystem.send(notif.toString(), null);
            }
        }
    }

    /**
     * Subscribe to context elements.
     *
     * @param metricArray      The list of metric to listen
     * @param subscriptionDest The destination that will receive data update
     * @param userInfo         The details of the user who subscribe
     */
    public final void subscribe(final String[] metricArray,
                                final String subscriptionDest,
                                final UserInfo userInfo) {
        boolean isAdmin = userInfo != null && userInfo.isAdmin();
        logInfo("Subscription request for " + subscriptionDest);
        for (String metric : metricArray) {
            if (isAdmin) {
                metric = metric.replace(userInfo.getId() + ".*", "*");
            }
            subscriptionMap.computeIfAbsent(metric, k -> new LinkedList<>());
            boolean exists = false;
            for (String dest : subscriptionMap.get(metric)) {
                if (dest.compareTo(subscriptionDest) == 0) {
                    exists = true;
                }
            }
            if (!exists) {
                subscriptionMap.get(metric).add(subscriptionDest);
            }
        }
    }

    /**
     * Unsubscribe from context elements.
     *
     * @param pathArray        contains paths to stop listening to,
     * @param subscriptionDest is used as destination currently use
     */
    public void unsubscribe(final String[] pathArray,
                            final String subscriptionDest) {
        for (String path : pathArray) {
            if (subscriptionMap.containsKey(path)) {
                String toRemove = null;
                for (String dest : subscriptionMap.get(path)) {
                    if (dest.compareTo(subscriptionDest) == 0) {
                        toRemove = dest;
                    }
                }
                if (toRemove != null) {
                    subscriptionMap.get(path).remove(toRemove);
                }
                if (subscriptionMap.get(path).size() == 0) {
                    subscriptionMap.remove(path);
                }
            }
        }
    }

    /**
     * @param request The origin request
     * @param result  The result
     */
    public final void sendResponse(final Request request, final Object result) {
        response(request, result);
    }


    @Override
    public void modelUpdated() {
        if (isFirstModelUpdate()) {
            sendRequest(new Request(getFullId(), getNode() + ".http", getCurrentTime(),
                    "addHandler", new Object[]{"/context", getFullId(), true}), new ShowIfErrorCallback());
        }
        super.modelUpdated();
    }

}
