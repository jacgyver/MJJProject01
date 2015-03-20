/*
 * ConnectionBuilder.java
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

import javax.swing.SwingUtilities;
import org.executequery.datasource.ConnectionManager;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.util.SwingWorker;

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
 * @date     $Date: 2006/06/07 16:01:36 $
 */
public class ConnectionBuilder implements ConnectionProcess {
    
    /** The worker thread to establish the connection */
    private SwingWorker worker;
    
    /** The connection progress dialog */
    private ConnectionProgressDialog progressDialog;
    
    /** Indicates whether the process was cancelled */
    private boolean cancelled;

    /** Indicates whether the process was successful */
    private boolean connected;

    /** The database connection object */
    private DatabaseConnection databaseConnection;
    
    /** The exception on error */
    private DataSourceException dataSourceException;
    
    public ConnectionBuilder() {
        connected = false;
        cancelled = false;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public boolean isTestConnection() {
        return false;
    }
    
    public String getConnectionName() {
        return databaseConnection.getName();
    }
    
    public void interrupt() {
        worker.interrupt();
    }
    
    public DataSourceException getException() {
        return dataSourceException;
    }
    
    public String getErrorMessage() {
        return dataSourceException.getMessage();
    }
    
    public void finished() {}
    
    public String getURL() {
        return null;// dbConnection.getURL();
    }
    
    public String getUser() {
        return null;//dbConnection.getUser();
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void establishConnection(DatabaseConnection dc) {
        databaseConnection = dc;
        progressDialog = new ConnectionProgressDialog(this);

        worker = new SwingWorker() {
            public Object construct() {
                return connect();
            }
            public void finished() {
                if (!cancelled) {
                    if (progressDialog != null) {
                        progressDialog.dispose();
                    }                    
                }                
            }
        };
        worker.start();
        progressDialog.run();
    }
    
    private Object connect() {
        try {
            ConnectionManager.createDataSource(databaseConnection);
            connected = true;
            return FINISHED;  
        } 
        catch (DataSourceException e) {
            dataSourceException = e;
            connected = false;
            return FAIL;
        }
    }
    
}



