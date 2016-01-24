package org.activehome.time;

import com.eclipsesource.json.JsonObject;
import org.kevoree.annotation.*;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class TimeControlled {

    /**
     * The necessary bindings.
     */
    @Param(defaultValue = "time>TimeKeeper.tic")
    private String bindingTimeControlled;

    private Tic tic;
    private long ticUpdate;
    private SimpleDateFormat printDate;

    public static final long DAY = 86400000;
    public static final long HALF_DAY = 43200000;
    public static final long HOUR = 3600000;
    public static final long QUARTER = 900000;
    public static final long TEN_MINUTES = 600000;
    public static final long FIVE_MINUTES = 300000;
    public static final long MINUTE = 60000;

    @KevoreeInject
    protected org.kevoree.api.Context context;

    @Start
    public void start() {
        Thread.currentThread().setName(context.getNodeName() + "." + context.getInstanceName());
        printDate = new SimpleDateFormat("dd/MM/YY HH:mm:ss");
        printDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        tic = new Tic(0, 1, 0, TimeStatus.UNKNOWN, null);
    }

    @Input
    public void time(final String jsonTic) {
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        Tic newTic = new Tic(JsonObject.readFrom(jsonTic));
//        Log.debug(context.getInstanceName() + " received tic: " + newTic + " => current tic: " + tic);
        ticUpdate = System.currentTimeMillis();
        if (tic != null && tic.getStatus() != newTic.getStatus()) {
            if (newTic.getTimeCommand().equals(TimeCommand.CARRYON)) {
                switch (newTic.getStatus()) {
                    case INITIALIZED:
                        tic = newTic;
                        onInit();
                        break;
                    case RUNNING:
                        if (tic.getStatus() == TimeStatus.UNKNOWN) {
                            onInit();
                            onStartTime();
                        } else if (tic.getStatus() == TimeStatus.INITIALIZED) {
                            onStartTime();
                        } else if (tic.getStatus() == TimeStatus.IDLE) {
                            onResumeTime();
                        }
                        break;
                    case IDLE:
                        onPauseTime();
                        break;
                    default:
                }
            } else if (newTic.getTimeCommand().equals(TimeCommand.START) && tic.getStatus().equals(TimeStatus.UNKNOWN)) {
                onInit();
            }
        }
        tic = newTic;
        onTic();
        switch (tic.getTimeCommand()) {
            case INIT:
                onInit();
                break;
            case START:
                onStartTime();
                break;
            case STOP:
                onStopTime();
                break;
            case PAUSE:
                onPauseTime();
                break;
            case RESUME:
                onResumeTime();
                break;
            default:
        }
    }

    public long getCurrentTime() {
        if (tic != null) {
            switch (tic.getStatus()) {
                case UNKNOWN:
                    return 0;
                case RUNNING:
                    return tic.getTS() + (System.currentTimeMillis() - ticUpdate) * tic.getZip();
                default: // INITIALIZED, IDLED or STOPPED
                    return tic.getTS();
            }
        }
        return 0;
    }

    public long getLocalTime() {
        return getCurrentTime() + tic.getTimezone() * HOUR;
    }

    public String strLocalTime() {
        return printDate.format(getLocalTime());
    }

    public String strLocalTime(long tsUTC) {
        return printDate.format(tsUTC + tic.getTimezone() * HOUR);
    }

    public Tic getTic() {
        return tic;
    }

    protected void onTic() {
    }

    protected void onInit() {
    }

    protected void onStartTime() {
    }

    protected void onStopTime() {
    }

    protected void onPauseTime() {
    }

    protected void onResumeTime() {
    }

}



