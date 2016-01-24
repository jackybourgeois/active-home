package org.activehome.tools;

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
