/*
 * PooledConnection.java
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


package org.underworldlabs.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Pooled connection wrapper.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/08/11 12:31:32 $
 */
public class PooledConnection implements Connection {

    /** this connections use count */
    private int useCount;
    
    /** indicates whether this connection is in use */
    private boolean inUse;
    
    /** indicates whether to close this connection */
    private boolean closeOnReturn;
    
    /** the original auto-commit mode from the real connection */
    private boolean originalAutoCommit;
    
    /** the real JDBC connection that this object wraps */
    private Connection realConnection;
    
    /** 
     * Creates a new PooledConnection object with the
     * specified connection as the source.
     * 
     * @param the real java.sql.Connection
     */  
    public PooledConnection(Connection realConnection) {
        this(realConnection, false);
    }

    /** 
     * Creates a new PooledConnection object with the
     * specified connection as the source.
     * 
     * @param the real java.sql.Connection
     */  
    public PooledConnection(Connection realConnection, boolean closeOnReturn) {
        useCount = 0;
        this.realConnection = realConnection;
        this.closeOnReturn = closeOnReturn;
        
        try {
            originalAutoCommit = realConnection.getAutoCommit();
        } catch (SQLException e) {
            // default to true on dump
            originalAutoCommit = true;
        }
    }

    public boolean equals(Object obj) {
        if (realConnection == null) {
            return false;
        } else {
            return realConnection.equals(obj);
        }
    }
    
    public int hashCode() {
        if (realConnection == null) {
            return 0;
        }
        return realConnection.hashCode();
    }    
    
    /**
     *  <p>Determine if the connection is available
     *
     * @return true if the connection can be used
     */
    public boolean isAvailable() {
        try {            
            if (realConnection != null) {
                if (!inUse && !realConnection.isClosed()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            return false;
        }
        catch (SQLException ex) {
            return false;
        }
    }
    
    public void setInUse(boolean inUse) {
        if (inUse) {
            useCount++;
        }
        this.inUse = inUse;
    }
    
    /**
     * Closes the underlying connection, and close
     * any Statements that were not explicitly closed.
     */
    public void close() throws SQLException {
        inUse = false;
        if (closeOnReturn && realConnection != null) {
            realConnection.close();
            realConnection = null;
        }
        else {
            // reset the original auto-commit mode
            try {
                realConnection.setAutoCommit(originalAutoCommit);
            } 
            // ignore errors and don't want to re-throw
            catch (SQLException e) {}
        }
    }
    
    protected void handleException(SQLException e) throws SQLException {
        throw e;
    }
    
    public Statement createStatement() throws SQLException {
        checkOpen();
        return realConnection.createStatement();
    }
    
    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency) 
        throws SQLException {
        checkOpen();
        return realConnection.createStatement(resultSetType,resultSetConcurrency);
    }
    
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkOpen();
        return realConnection.prepareStatement(sql);
    }
    
    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType,
                                              int resultSetConcurrency) 
        throws SQLException {
        checkOpen();
        return realConnection.prepareStatement(sql,resultSetType,resultSetConcurrency);
    }
    
    public CallableStatement prepareCall(String sql) throws SQLException {
        checkOpen();
        return realConnection.prepareCall(sql);
    }
    
    public CallableStatement prepareCall(String sql,
                                         int resultSetType,
                                         int resultSetConcurrency) 
        throws SQLException {
        checkOpen();
        return realConnection.prepareCall(sql, resultSetType,resultSetConcurrency);
    }
    
    public void clearWarnings() throws SQLException {
        checkOpen(); 
        try { realConnection.clearWarnings(); } catch (SQLException e) { handleException(e); } }
    
    public void commit() throws SQLException {
        checkOpen(); try { realConnection.commit(); } catch (SQLException e) { handleException(e); } }
    
    public boolean getAutoCommit() throws SQLException {
        checkOpen(); try { return realConnection.getAutoCommit(); } catch (SQLException e) { handleException(e); return false; }
    }
    public String getCatalog() throws SQLException {
        checkOpen(); try { return realConnection.getCatalog(); } catch (SQLException e) { handleException(e); return null; } }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        checkOpen(); try { return realConnection.getMetaData(); } catch (SQLException e) { handleException(e); return null; } }
    
    public int getTransactionIsolation() throws SQLException {
        checkOpen(); try { return realConnection.getTransactionIsolation(); } catch (SQLException e) { handleException(e); return -1; } }
    
    public Map getTypeMap() throws SQLException {
        checkOpen(); try { return realConnection.getTypeMap(); } catch (SQLException e) { handleException(e); return null; } }
    
    public SQLWarning getWarnings() throws SQLException {
        checkOpen(); try { return realConnection.getWarnings(); } catch (SQLException e) { handleException(e); return null; } }
    
    public boolean isReadOnly() throws SQLException {
        checkOpen(); try { return realConnection.isReadOnly(); } catch (SQLException e) { handleException(e); return false; } }
    
    public String nativeSQL(String sql) throws SQLException {
        checkOpen(); try { return realConnection.nativeSQL(sql); } catch (SQLException e) { handleException(e); return null; } }
    
    public void rollback() throws SQLException {
        checkOpen(); try {  realConnection.rollback(); } catch (SQLException e) { handleException(e); } }
    
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkOpen(); try { realConnection.setAutoCommit(autoCommit); } catch (SQLException e) { handleException(e); } }
    
    public void setCatalog(String catalog) throws SQLException {
        checkOpen(); try { realConnection.setCatalog(catalog); } catch (SQLException e) { handleException(e); } }
    
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkOpen(); try { realConnection.setReadOnly(readOnly); } catch (SQLException e) { handleException(e); } }
    
    public void setTransactionIsolation(int level) throws SQLException {
        checkOpen(); try { realConnection.setTransactionIsolation(level); } catch (SQLException e) { handleException(e); } }
    
    public void setTypeMap(Map map) throws SQLException {
        checkOpen(); try { realConnection.setTypeMap(map); } catch (SQLException e) { handleException(e); } }
    
    public boolean isClosed() throws SQLException {
        if (realConnection == null) {
            return true;
        }
        return realConnection.isClosed();
    }
    
    protected void checkOpen() throws SQLException {
        if (realConnection != null && realConnection.isClosed()) {
            throw new SQLException("Connection is closed.");
        }
        if(realConnection == null) {
            throw new SQLException("Connection is closed.");
        }
    }
    
    public int getHoldability() throws SQLException {
        checkOpen(); try { return realConnection.getHoldability(); } catch (SQLException e) { handleException(e); return 0; } }

    public void setHoldability(int holdability) throws SQLException {
        checkOpen(); try { realConnection.setHoldability(holdability); } catch (SQLException e) { handleException(e); } }

    public java.sql.Savepoint setSavepoint() throws SQLException {
        checkOpen(); try { return realConnection.setSavepoint(); } catch (SQLException e) { handleException(e); return null; } }

    public java.sql.Savepoint setSavepoint(String name) throws SQLException {
        checkOpen(); try { return realConnection.setSavepoint(name); } catch (SQLException e) { handleException(e); return null; } }

    public void rollback(java.sql.Savepoint savepoint) throws SQLException {
        checkOpen(); try { realConnection.rollback(savepoint); } catch (SQLException e) { handleException(e); } }

    public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {
        checkOpen(); try { realConnection.releaseSavepoint(savepoint); } catch (SQLException e) { handleException(e); } }

    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency,
                                     int resultSetHoldability) throws SQLException {
        checkOpen();
        try {
            return realConnection.createStatement(
                resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        checkOpen();
        try {
            return realConnection.prepareStatement(
                sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        checkOpen();
        try {
            return realConnection.prepareCall(
                sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();
        try {
            return realConnection.prepareStatement(sql, autoGeneratedKeys);
        }
        catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        checkOpen();
        try {
            return realConnection.prepareStatement(sql, columnIndexes);
        }
        catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
        checkOpen();
        try {
            return realConnection.prepareStatement(sql, columnNames);
        }
        catch (SQLException e) {
            handleException(e);
            return null;
        }
    }

    public Connection getRealConnection() {
        return realConnection;
    }
    
    public void setRealConnection(Connection realConnection) {
        this.realConnection = realConnection;
    }

    public boolean isCloseOnReturn() {
        return closeOnReturn;
    }

    public void setCloseOnReturn(boolean closeOnReturn) {
        this.closeOnReturn = closeOnReturn;
    }

    public int getUseCount() {
        return useCount;
    }
    
}



