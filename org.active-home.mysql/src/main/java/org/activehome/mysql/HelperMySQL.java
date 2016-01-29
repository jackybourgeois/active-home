package org.activehome.mysql;

/*
 * #%L
 * Active Home :: MySQL
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


import org.kevoree.log.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class facilitate MySQL access.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public final class HelperMySQL {

    /**
     * Utility class.
     */
    private HelperMySQL() {
    }

    public static Connection connect(final String user,
                                     final String pass,
                                     final String host,
                                     final String dbName) {
        return connect("jdbc:mysql://" + host + ":3306/" + dbName
                + "?user=" + user + "&password=" + pass);
    }

    public static Connection connect(final String url) {
        Connection dbConnect = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnect = java.sql.DriverManager.getConnection(url);
        } catch (Exception exception){
            Log.error("Database connect error", exception);
        }
        return dbConnect;
    }

    public static Connection connect(final Properties properties) {
        return connect(properties.getProperty("db_user"),
                properties.getProperty("db_pass"),
                properties.getProperty("db_host"),
                properties.getProperty("db_name"));
    }

    /**
     * Close a connection.
     *
     * @param connection The connection to close
     */
    public static void closeDbConnection(final Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}