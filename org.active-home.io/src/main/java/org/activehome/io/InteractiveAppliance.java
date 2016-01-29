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
    @Param(defaultValue = "<path d=\"m12.1 30c0 0 7 6.1 13.3 3.4 6.3-2.7 9.7-0.4 9.7-0.4m-11.3-22.3a1.9 1.3 0 0 1-1.9 1.3 1.9 1.3 0 0 1-1.9-1.3 1.9 1.3 0 0 1 1.9-1.3 1.9 1.3 0 0 1 1.9 1.3zm-6.7 0.5a1.9 1.3 0 0 1-1.9 1.3 1.9 1.3 0 0 1-1.9-1.3 1.9 1.3 0 0 1 1.9-1.3 1.9 1.3 0 0 1 1.9 1.3zm3.6 10.2c-5.8 0.2-14 10.1-4 17 5.4 3.8 16.8 5.9 18.6-6.8 0.8-5.9-0.7-10.8-14.6-10.2zm-8.1-15.5c-7.9 8.8-16.1 30.4-3.7 35.3 12.4 5 38.4 16.7 37.8-9.3-0.6-26-2.8-36.2-15.2-30-12.4 6.2-14.6-0.8-18.9 4z\" style=\"fill:none;stroke-width:0.6;stroke:#000\"/>")
    private String img;
    /**
     *
     */
    @Param(defaultValue = "An interactive appliance requires user interventions or user agreement to be used (washing machine, microwave...).")
    private String description;

}
