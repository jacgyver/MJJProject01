/*
 * ConnectionTest.java
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
import java.sql.SQLException;

import org.executequery.databasemediators.ConnectionProgressDialog;
import org.underworldlabs.swing.util.SwingWorker;
import org.executequery.datasource.ConnectionDataSource;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Utility class to test a set of parameters
 *  for a database connection. Simply returns whether
 *  the connection attempt succeeded or failed and
 *  cleans up all resources when done (ie. the connection
 *  if established is not maintained).
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class ConnectionTest implements ConnectionProcess {
    
    /** The worker thread to establish the connection */
    private SwingWorker worker;
    /** The connection progress dialog */
    private ConnectionProgressDialog progressDialog;
    /** Indicates whether the process was cancelled */
    private boolean cancelled;
    /** The database connection object */
    private DatabaseConnection databaseConnection;
    /** Whether the test passed */
    private boolean success;
    /** The error message */
    private String errorMessage;
    
    public ConnectionTest() {}
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        
        if (cancelled) {
            errorMessage = "Connection test aborted by user.";
        }
        
    }
    
    public boolean isTestConnection() {
        return true;
    }
    
    public String getConnectionName() {
        return databaseConnection.getName();
    }
    
    public void interrupt() {
        worker.interrupt();
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void establishConnection(DatabaseConnection dc) throws Exception {
        
        databaseConnection = dc;
        progressDialog = new ConnectionProgressDialog(this);
        
        worker = new SwingWorker() {
            public Object construct() {
                return connect();
            }
            public void finished() {
                
                if (!cancelled) {
                    
                    if (progressDialog != null)
                        progressDialog.dispose();
                    
                } 

            }
        };
        
        worker.start();
        progressDialog.run();
        
    }
    
    private Object connect() {
        ConnectionDataSource source = new ConnectionDataSource(databaseConnection);        
        try {            
            Connection conn = source.getConnection();
            if (conn != null) {
                success = true;
                conn.close();
            }

            conn = null;
            return FINISHED;            
        } 
        catch (SQLException sqlExc) {
            errorMessage = sqlExc.getMessage();
            return FAIL;
        } 
        finally {
            source = null;
        } 
        
    }
    
}



