/*
 * ConnectionPool.java
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.executequery.util.Log;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.jdbc.PooledConnection;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/08/11 12:34:25 $
 */
public class ConnectionPool {
    
    /** a variable denoting a fixed number of connections */
    public static final int FIXED_POOL_SCHEME = 1;
    
    /** a variable denoting a dynamic number of connections */
    public static final int DYNAMIC_POOL_SCHEME = 0;
    
    /** the minimum number of connections to open */
    private int minimumConnections;
    
    /** the maximum number of connections to open */
    private int maximumConnections;
    
    /** the maxium use count for a connection */
    private int maximumUseCount;
    
    /** the <code>DataSource</code> object which establishes connections */
    private ConnectionDataSource dataSource;
    
    /** the connection 'container' */
    private List<PooledConnection> pool;
    
    /** the connection default tx isolation level */
    private int defaultTxIsolation;
    
    /** the scheme for this connection pool */
    private int poolScheme;
    
    public ConnectionPool() {
        this(null);
    }
    
    /**
     * Creates a new connection pool using the specified 
     * data source.
     *
     * @param the data source for this pool
     */
    public ConnectionPool(ConnectionDataSource dataSource) {
        defaultTxIsolation = -1;
        maximumUseCount = -1;
        poolScheme = DYNAMIC_POOL_SCHEME;
        pool = new ArrayList<PooledConnection>();
        this.dataSource = dataSource;
    }

    /**
     * Returns the pool connection scheme.
     * 
     * @return pool scheme - static or dynamic
     */
    public int getPoolScheme() {
        return poolScheme;
    }

    /**
     * Sets the pool connection scheme to that specified.
     * 
     * @param the pool scheme - FIXED_POOL_SCHEME | DYNAMIC_POOL_SCHEME
     * @throws IllegalArgumentException if the pool scheme is not 
     *         either of those specified above.
     */
    public void setPoolScheme(int poolScheme) throws IllegalArgumentException {
        if (poolScheme != FIXED_POOL_SCHEME || poolScheme != DYNAMIC_POOL_SCHEME) {
            throw new IllegalArgumentException("Invalid pool scheme specified");
        }
        this.poolScheme = poolScheme;
    }
    
    public void setMaximumConnections(int maximumConnections) 
        throws IllegalArgumentException {
        if (maximumConnections < 1) {
            throw new IllegalArgumentException(
                    "Maximum number of connections must be at least 1");
        }
        this.maximumConnections = maximumConnections;
    }
    
    public int getMaximumConnections() {
        return maximumConnections;
    }
    
    public void setMinimumConnections(int minimumConnections)
        throws IllegalArgumentException {
        if (minimumConnections < 0) {
            throw new IllegalArgumentException(
                    "Minimum number of connections must be at least 0");
        }
        this.minimumConnections = minimumConnections;
    }

    public int getMinimumConnections() {
        return minimumConnections;
    }

    public void setDataSource(ConnectionDataSource cds) {
        dataSource = cds;
    }

    /**
     * Sets the transaction isolation level to that specified
     * for <i>all</i> connections in this pool.
     *
     * @param the isolation level
     * @see java.sql.Connection for possible values
     */
    public void setTransactionIsolationLevel(int isolationLevel) 
        throws DataSourceException {
        
        // check if we reset to default
        if (isolationLevel == -1) {
            isolationLevel = defaultTxIsolation;
        }

        try {
            for (int i = 0, k = pool.size(); i < k; i++) {
                PooledConnection c = pool.get(i);
                if (!c.isClosed()) {
                    c.setTransactionIsolation(isolationLevel);
                }
            }
        } 
        catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }
    
    public ConnectionDataSource getDataSource() {
        return dataSource;
    }

    /** 
     * <p>Retrieves a connection from the pool.<br>
     *  
     * If one is not available in a fixed connection scheme
     * and the maximum number of allowable open connections has
     * been reached, NULL will be returned and an exception thrown.
     * 
     * If the connection scheme is dynamic, a new connection will
     * be added to the pool and destroyed when returned if the
     * pool's size is greater than the maximum allowable.
     *
     * @return the connection 
     * @throws an DataSourceException if a connection can not be returned.
     */
    public synchronized Connection getConnection() throws DataSourceException {

        if (Log.isDebugEnabled()) {
            Log.debug("Pool size before ensureCapacity(): " + pool.size());
        }

        // first, check we have at least the min connections
        ensureCapacity();

        if (Log.isDebugEnabled()) {
            Log.debug("Pool size after ensureCapacity(): " + pool.size());
        }

        try {

            // retrieve the first available conn from the pool
            for (int i = 0, k = pool.size(); i < k; i++) {
                PooledConnection c = pool.get(i);
                if (c.isAvailable()) {

                    // check the use count if initialised (not -1)
                    if (c.isClosed() ||
                            (maximumUseCount > 0 && 
                               c.getUseCount() >= maximumUseCount)) {
                        Log.debug("Closing retrieved connection and retrying.");
                        close(c);
                        return getConnection();
                    }

                    //Log.debug("pool size after first loop: " + pool.size());

                    c.setInUse(true);
                    return c;
                } 
            } 
        }
        catch (SQLException e) {
            throw new DataSourceException(e);
        }

        if (pool.size() < maximumConnections) {
            try {
                PooledConnection c = new PooledConnection(dataSource.getConnection());
                c.setInUse(true);
                pool.add(c);
                //Log.debug("pool size after single add: " + pool.size());
                return c;
            }
            catch (SQLException e) {
                throw new DataSourceException(e);
            }
        }
        
        // return a temporary connection
        if (poolScheme == DYNAMIC_POOL_SCHEME) {
            try {
                if (Log.isDebugEnabled()) {
                    Log.debug("Dynamic pool scheme detected - " +
                              "creating connection for single-use.");
                }
                return new PooledConnection(dataSource.getConnection(), true);
            }
            catch (SQLException e) {
                throw new DataSourceException(e);
            }
        }
        else {
            throw new DataSourceException(
                    "All open connections are currently in use. "+
                    "Consider creating a larger pool and setting the scheme to dynamic");
        }

    }
    
    /**
     * Ensures that the pool size is at least at the minimum
     * number of connections specified.
     */
    protected void ensureCapacity() throws DataSourceException {
        if (dataSource == null) {
            throw new DataSourceException("Data source not initialised");
        }

        try {
            while (pool.size() < minimumConnections) {
                pool.add(new PooledConnection(dataSource.getConnection()));
            }

            // initialise the default tx level if not done yet
            if (defaultTxIsolation == -1 && pool.size() > 0) {
                PooledConnection conn = pool.get(0);
                defaultTxIsolation = conn.getTransactionIsolation();
            }
            
        }
        catch (SQLException e) {
            throw new DataSourceException(e);
        }
        // mainly here for unexpected dumps noticed with some drivers
        // ie. sun's odbc driver dumps on poorly defined odbc source
        catch (Exception e) {
            throw new DataSourceException(e);
        }
    }
    
    public void close(Connection connection) throws DataSourceException {
        try {
            // check if this is a PooledConnection
            // and if it exists in the pool
            if (connection instanceof PooledConnection) {
                PooledConnection pooledConnection = (PooledConnection)connection;
                int index = pool.indexOf(pooledConnection);
                if (index != -1) {
                    pool.remove(pooledConnection);
                }
                connection = pooledConnection.getRealConnection();
            }
            if (connection != null) {
                connection.close();
            }
        }
        catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }

    /**
     * Closes all connections and destroys the pool.
     */
    public void close() throws DataSourceException {
        try {
            for (int i = 0, k = pool.size(); i < k; i++) {
                PooledConnection c = (PooledConnection)pool.get(i);
                Connection realConnection = c.getRealConnection();
                if (realConnection != null) {
                    realConnection.close();
                }
            }
            pool.clear();
            pool = null;
            dataSource = null;
        }
        catch (SQLException e) {
            throw new DataSourceException(e);
        }
    }
    
    public int getSize() {
        return pool.size();
    }
    
    public int getPoolActiveSize() {
        int size = 0;
        for (int i = 0, k = pool.size(); i < k; i++) {
            PooledConnection c = pool.get(i);
            if (!c.isAvailable()) {
                size++;
            }
        }
        return size;
    }

    public int getMaximumUseCount() {
        return maximumUseCount;
    }

    public void setMaximumUseCount(int maximumUseCount) 
        throws IllegalArgumentException {
        if (maximumUseCount == 0) {
            throw new IllegalArgumentException(
                    "Maximum connection use count can not be zero");
        }
        this.maximumUseCount = maximumUseCount;
    }
    
}


