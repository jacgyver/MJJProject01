/*
 * TempConnection.java
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


package org.executequery.databasemediators;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Vector;
import org.executequery.datasource.ConnectionDataSource;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Performs a connection test.<br>
 *  A connection is established with a database
 *  and closed to test the entered connection values.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class TempConnection {
    
    private DatabaseConnection tempConn;
    private String errorMessage;
    
    private Connection conn;
    private Statement stmnt;
    private ResultSet rs;
    
    public TempConnection(DatabaseConnection sc) {
        tempConn = sc;
    }
    
    public Vector getColumnData(String name, String schema) {
        try {
            conn = getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            rs = dmd.getColumns(null, schema.toUpperCase(), name, null);
            
            Vector v = new Vector();
            
            while (rs.next())
                v.add(rs.getString(4));
            
            rs.close();
            
            return v;
        } 
        
        catch (SQLException sqlExc) {
            return null;
        }
        
    }
    
    public Vector getSchemaTables(String schema) {
        try {
            conn = getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            rs = dmd.getTables(null, schema.toUpperCase(), null, null);
            
            Vector v = new Vector();
            
            while (rs.next())
                v.add(rs.getString(3));
            
            rs.close();
            rs = null;
            
            return v;
            
        } 
        
        catch (Exception sqlExc) {
            return null;
        } 
        
    }
    
    public Connection getConnection() {
        try {
            if (conn == null) {               
                ConnectionDataSource source = new ConnectionDataSource(tempConn);
                conn = source.getConnection();                
            } 
            return conn;
            
        } catch (SQLException sqlExc) {
            errorMessage = sqlExc.getMessage();
            releaseResources();
            return null;
        } 
    }
    
    public ResultSet getResultSet(String query, int columnCount) {
        try {
            conn = getConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query);
            
            return rs;
            
        } catch (SQLException sqlExc) {
            errorMessage = sqlExc.getMessage();
            sqlExc.printStackTrace();
            return null;
        }
    }
    
    public void setConnectionData(DatabaseConnection s) {
        releaseResources();
        tempConn = s;
    }
    
    public int testConnection() {
        
        try {
            Connection _conn = getConnection();
            
            if (_conn != null) {
                _conn.close();
                return 1;
            }
            else {
                return 0;
            }
            
        } 
        
        catch (SQLException sqlExc) {
            sqlExc.printStackTrace();
            errorMessage = sqlExc.getMessage();
            releaseResources();
            return 0;
        } 
        
    }
    
    public void releaseResources() {
        try {
            
            if (rs != null) {
                rs.close();
                rs = null;
            }
            
            if (stmnt != null) {
                stmnt.close();
                stmnt = null;
            }
            
            if (conn != null) {
                conn.close();
                conn = null;
            }
            
        } catch (SQLException sqlExc) {}
        
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
}







