package org.activehome.io;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Param;

/**
 * An Appliance.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class Appliance extends IO {

    /**
     * This appliance can be paused and resumed
     */
    @Param(defaultValue = "false")
    private boolean isInterruptable;
    /**
     * This appliance can be paused and resumed
     */
    @Param(defaultValue = "false")
    private boolean isReducible;


    /**
     * Threshold to detect if the appliance is active (ON).
     */
    @Param(defaultValue = "0")
    private double onOffThreshold;

    /**
     * @return Threshold to detect if the appliance is active (ON)
     */
    public final double getOnOffThreshold() {
        return onOffThreshold;
    }

}
