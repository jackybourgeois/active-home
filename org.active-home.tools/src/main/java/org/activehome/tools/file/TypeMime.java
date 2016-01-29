package org.activehome.tools.file;

/*
 * #%L
 * Active Home :: Tools
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


/**
 * List of Type Mine and their description.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public enum TypeMime {

    html("text/html"),
    css("text/css"),
    js("application/javascript"),
    png("image/png"),
    ico("image/ico"),
    jpg("image/jpeg"),
    svg("image/svg+xml"),
    gif("image/gif");

    private final String desc;

    TypeMime(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
