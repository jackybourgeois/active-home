package org.activehome.io;

/*
 * #%L
 * Active Home :: IO
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


import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Param;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
@ComponentType
public abstract class InteractiveAppliance extends Appliance {

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


    /**
     * SVG image path
     */
    @Param(defaultValue = "")
    private String img;
    /**
     *
     */
    @Param(defaultValue = "An interactive appliance requires user interventions or user agreement to be used (washing machine, microwave...).")
    private String description;

}
