package org.activehome.io;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Param;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public class InteractiveAppliance extends Appliance {

    /**
     * The usage of the interactive appliance can
     * be negotiated with the user.
     */
    @Param(defaultValue = "false")
    private boolean isNegotiable;

    /**
     * the sequences this appliance takes part of
     * (e.g 'laundry' for washing machine and dryer)
     */
    @Param
    private boolean sequences;

}
