package org.activehome.tools;

/*
 * #%L
 * Active Home :: Tools
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


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * This class facilitate network stuff.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public final class NetworkHelper {

    /**
     * Utility class.
     */
    private NetworkHelper() {
    }

    /**
     * @return The host address
     */
    public static String getHostExternalAddr() {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();

            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (!i.getHostAddress().startsWith("192")
                            && !i.getHostAddress().startsWith("127")) {
                        if (!i.getHostAddress().contains(":")) {
                            return i.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return "127.0.0.1";
    }

}
