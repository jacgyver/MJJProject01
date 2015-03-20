/*
 * ConnectionManager.java
 *
 * Copyright (C) 2002, 2003, 2004, 2005, 2006 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */


package org.executequery.datasource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.sql.DataSource;
import org.executequery.JDBCProperties;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.util.SystemProperties;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.util.Log;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Manages all data source connections across multiple
 * sources and associated connection pools.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/08/11 12:34:25 $
 */
public class ConnectionManager {
    
    /** the connection 'container' */
    private static Map<DatabaseConnection,ConnectionPool> connectionPools;
    
    /** private constructor to prevent instantiation */
    private ConnectionManager() {}

    /** 
     * Creates a stored data source for the specified database
     * connection properties object.
     *
     * @param the database connection properties object
     */
    public static void createDataSource(DatabaseConnection databaseConnection) 
        throws DataSourceException {
        
        // check the connection has a driver
        if (databaseConnection.getJDBCDriver() == null) {
            long driverId = databaseConnection.getDriverId();
            DatabaseDriver driver = JDBCProperties.getDatabaseDriver(driverId);
            if (driver != null) {
                databaseConnection.setJDBCDriver(driver);
            }
            else {
                throw new DataSourceException("No JDBC driver specified");
            }
        }

        ConnectionDataSource dataSource = 
                new ConnectionDataSource(databaseConnection);
        
        // associate the connection pool with the data source
        ConnectionPool pool = new ConnectionPool(dataSource);
        //pool.setPoolScheme(SystemProperties.getIntProperty("connection.scheme"));
        pool.setMinimumConnections(SystemProperties.getIntProperty("user", "connection.initialcount"));
        pool.setMaximumConnections(5);
        pool.ensureCapacity();
        
        // TODO: ?????????????????
        //pool.setMinConns(determineMinimumConnections());

        if (connectionPools == null) {
            connectionPools = new HashMap<DatabaseConnection,ConnectionPool>();
        }
        connectionPools.put(databaseConnection, pool);
        databaseConnection.setConnected(true);
    }

    /**
     * Returns a connection from the pool of the specified type.
     *
     * @param the stored database connection properties object
     * @return the connection itself
     */
    public static synchronized Connection getConnection(
                    DatabaseConnection databaseConnection) throws DataSourceException {

        if (databaseConnection == null) {
            return null;
        }
        
        if (connectionPools == null || 
                !connectionPools.containsKey(databaseConnection)) {
            createDataSource(databaseConnection);
        }
        ConnectionPool pool = connectionPools.get(databaseConnection);
        
        Connection connection = pool.getConnection();

        /*
        // set some additional saved connection properties
        try {
            //connection.setAutoCommit(databaseConnection.isAutoCommit());
            int txLevel = databaseConnection.getTransactionIsolation();
            if (txLevel != -1) {
                connection.setTransactionIsolation(txLevel);
            }
        } 
        catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException e2) {}
            // re-throw
            throw new DataSourceException(e);
        } */
        return connection;
    }

    /**
     * Closes all connections and removes the pool of the specified type.
     *
     * @param the stored database connection properties object
     */
    public static synchronized void closeConnection(
                    DatabaseConnection databaseConnection) throws DataSourceException {

        if (connectionPools.containsKey(databaseConnection)) {
            Log.info("Disconnecting from data source " + 
                    databaseConnection.getName());
            ConnectionPool pool = connectionPools.get(databaseConnection);
            pool.close();
            connectionPools.remove(databaseConnection);
            pool = null;
            databaseConnection.setConnected(false);
        }
    }

    /**
     * Closes all connections and removes the pool of the specified type.
     *
     * @param the stored database connection properties object
     */
    public static void close() throws DataSourceException {
        if (connectionPools == null || connectionPools.isEmpty()) {
            return;
        }

        // iterate and close all the pools
        for (Iterator i = connectionPools.keySet().iterator(); i.hasNext();) {
            DatabaseConnection dc = (DatabaseConnection)i.next();
            ConnectionPool pool = connectionPools.get(dc);
            pool.close();
        }
        connectionPools.clear();
    }

    /**
     * Retrieves the data source objetc of the specified connection.
     * 
     * @return the data source object
     */
    public static DataSource getDataSource(DatabaseConnection databaseConnection) {
        if (connectionPools == null || 
                !connectionPools.containsKey(databaseConnection)) {
            return null;
        }
        return connectionPools.get(databaseConnection).getDataSource();
    }
    
    /**
     * Sets the transaction isolation level to that specified
     * for <i>all</i> connections in the pool of the specified connection.
     *
     * @param the isolation level
     * @see java.sql.Connection for possible values
     */
    public static void setTransactionIsolationLevel(
                    DatabaseConnection databaseConnection, int isolationLevel) 
        throws DataSourceException {
        if (connectionPools == null || 
                connectionPools.containsKey(databaseConnection)) {
            ConnectionPool pool = connectionPools.get(databaseConnection);
            pool.setTransactionIsolationLevel(isolationLevel);
        }
    }

    /**
     * Returns a collection of database connection property 
     * objects that are active (connected).
     *
     * @return a collection of active connections
     */
    public static Vector<DatabaseConnection> getActiveConnections() {
        if (connectionPools == null || connectionPools.isEmpty()) {
            return new Vector<DatabaseConnection>(0);
        }
        Vector<DatabaseConnection> connections = 
                new Vector<DatabaseConnection>(connectionPools.size());
        for (Iterator i = connectionPools.keySet().iterator(); i.hasNext();) {
            connections.add((DatabaseConnection)i.next());
        }
        return connections;
    }
    
    /**
     * Returns the open connection count for the specified connection.
     *
     * @param dc - the connection to be polled
     */
    public static int getOpenConnectionCount(DatabaseConnection dc) {
        ConnectionPool pool = connectionPools.get(dc);
        if (pool != null) {
            return pool.getSize();
        }
        return 0;
    }
    
    /**
     * Returns a connection from the pool of the specified name.
     *
     * @param the name of the stored database connection
     * @return the connection itself
     */
    public static Connection getConnection(String name)  {
        return null;
    }
    
    /**
     * Returns the number of pools currently active.
     *
     * @return number of active pools
     */
    public static int getActiveConnectionPoolCount() {
        if (connectionPools == null) {
            return 0;
        }
        return connectionPools.size();
    }

    /**
     * Closes the connection completely. The specified connection
     * is not returned to the pool.
     *
     * @param the connection be closed
     */
    public static void close(
            DatabaseConnection databaseConnection, Connection connection)
            throws DataSourceException {
        if (connectionPools == null || connectionPools.isEmpty()) {
            return;
        }
        if (connectionPools.containsKey(databaseConnection)) {
            ConnectionPool pool = connectionPools.get(databaseConnection);
            pool.close(connection);
        }
    }
    
    /** 
     * Retrieves the maximum use count for each open connection 
     * before being closed.
     *
     * @return the max connection use count
     */
    public static int getMaxUseCount() {
        return 50;
    }

}



