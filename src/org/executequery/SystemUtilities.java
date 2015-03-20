/*
 * SystemUtilities.java
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


package org.executequery;

import java.awt.print.PageFormat;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.executequery.databasemediators.ConnectionBuilder;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.datasource.ConnectionManager;
import org.underworldlabs.jdbc.DataSourceException;
import org.executequery.gui.SaveOnExitDialog;
import org.executequery.gui.editor.QueryEditorSettings;

import org.executequery.print.PrintUtilities;
import org.underworldlabs.util.FileUtils;
import org.underworldlabs.util.MiscUtils;

import org.executequery.gui.SaveFunction;
import org.executequery.util.Log;
import org.executequery.util.SystemResources;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.util.SystemProperties;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.10 $
 * @date     $Date: 2006/08/11 12:32:48 $
 */
public class SystemUtilities {

    /** The user history file - ~/.executequery/sql-command.history */
    private static String historyFile;
    
    /** The user recent files file - ~/.executequery/recent.files */
    private static String recentFile;
    
    /** The user defined properties directory - ~/.executequery/conf */
    private static String userPropertiesPath;
    
    /** The user specific log directory - ~/.executequery/logs */
    private static String userLogsPath;
    
    /** An array of saved connections */
    private static DatabaseConnection[] connections;
    
    /** The printing page format */
    private static PageFormat pageFormat;
    
    /**
     * Returns the user log directory usually ~/executequery/build_number.
     */
    public static String getUserPropertiesPath() {
        return userPropertiesPath;
    }
    
    /**
     * Returns the user log directory usually ~/executequery/logs.
     */
    public static String getUserLogsPath() {
        return userLogsPath;
    }
    
    /**
     * Returns the system log absolute file path.
     */
    public static String getSystemLogPath() {
        return getUserLogsPath() +
                SystemProperties.getProperty("system", "eq.output.log");
    }
    
    /**
     * Initializes system logging and config file paths.
     */
    public static void startup() {
        String fileSeparator = System.getProperty("file.separator");
        
        StringBuffer sb = new StringBuffer();
        
        // build the sql command history file path
        sb.append(System.getProperty("user.home")).
           append(fileSeparator).
           append(System.getProperty("executequery.user.home.dir")).
           append(fileSeparator).
           append("sql-command.history");
        historyFile = sb.toString();
        
        sb.setLength(0);

        // build the recent files history file path
        sb.append(System.getProperty("user.home")).
           append(fileSeparator).
           append(System.getProperty("executequery.user.home.dir")).
           append(fileSeparator).
           append("recent.files");
        recentFile = sb.toString();

        sb.setLength(0);

        // build the user properties home path
        sb.append(System.getProperty("user.home")).
           append(fileSeparator).
           append(System.getProperty("executequery.user.home.dir")).
           append(fileSeparator).
           append(System.getProperty("executequery.build")).
           append(fileSeparator);
        userPropertiesPath = sb.toString();

        sb.setLength(0);
        
        // build the logs directory path
        sb.append(System.getProperty("user.home")).
           append(fileSeparator).
           append(System.getProperty("executequery.user.home.dir")).
           append(fileSeparator).
           append("logs").
           append(fileSeparator);
        userLogsPath = sb.toString();

        boolean dirsCreated = SystemResources.createUserHomeDirSettings();

        if (!dirsCreated) {
            System.exit(0);
        }

    }

    /**
     * Returns the recent open files list as an array of strings.
     *
     * @return the recent file paths
     */
    public static final String[] getRecentFilesList() {        
        try {
            String files = FileUtils.loadFile(recentFile);
            
            if (MiscUtils.isNull(files)) {
                return new String[0];
            } else {            
                return MiscUtils.splitSeparatedValues(files, "\n");
            }

        } catch (IOException e) {
            return new String[0];
        }
    }

    /**
     * Adds the specified file to the recently opened files list.
     * 
     * @param file - the file to be added
     */
    public static final boolean addRecentOpenFile(String file) {
        try {
            String[] files = getRecentFilesList();

            Vector vFiles = new Vector(files.length);
            for (int i = 0; i < files.length; i++) {
                vFiles.add(files[i]);
            }

            // if it already exists in the list,
            // move it to the top of the list
            if (MiscUtils.containsValue(files, file)) {
                vFiles.add(0, file);
                for (int i = 1, k = vFiles.size(); i < k; i++) {
                    String _file = vFiles.elementAt(i).toString();
                    if (_file.equals(file)) {
                        vFiles.remove(i);
                        break;
                    }
                }
            }
            else {
                int maxFiles = SystemProperties.getIntProperty("user", "recent.files.count");
                if (files.length >= maxFiles) {
                    vFiles.remove(files.length - 1);
                }
                vFiles.add(0, file);   
            }

            StringBuffer sb = new StringBuffer();
            for (int i = 0, k = vFiles.size(); i < k; i++) {
                sb.append(vFiles.elementAt(i));
                if (i != k - 1) {
                    sb.append(Constants.NEW_LINE_STRING);
                }
            }
            FileUtils.writeFile(recentFile, sb.toString());
            return true;
        } catch (IOException ioExc) {
            GUIUtilities.displayErrorMessage(
                    "An IO error occurred writing to the recent open files list:\n" +
                    ioExc.getMessage());
            return false;
        }        
    }
    
    /**
     * Returns the SQL command history executed within the 
     * query editor and saved to file.
     *
     * @return a Vector containing previously executed queries
     */
    public static final Vector getSqlCommandHistory() {
        Vector history = null;
        try {
            File file = new File(historyFile);
            if (!file.exists()) {
                history = new Vector();
            }
            else {
                Object object = FileUtils.readObject(historyFile);
                if (object == null || !(object instanceof Vector)) {
                    history = new Vector();
                }
                else {
                    history = (Vector)object;
                }
            }
        } catch (IOException ioExc) {
            GUIUtilities.displayErrorMessage(
                    "An IO error occurred reading previously " +
                    "executed history:\n" + ioExc.getMessage());
            history = new Vector();
        }
        return history;
    }
    
    /**
     * Clears the SQL command history.
     */
    public static final void clearSqlCommandHistory() {
        try {
            FileUtils.writeObject(new Vector(), historyFile);
        } catch (IOException ioExc) {}
    }

    /**
     * Adds the specified query to the SQL command list and 
     * saves this to file.
     */
    public static final void addSqlCommand(String query) {
        final Vector history = getSqlCommandHistory();

        int size = history.size();
        if (size == QueryEditorSettings.getHistoryMax()) {
            history.removeElementAt(size - 1);
        }

        history.add(0, query);

        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {
                    FileUtils.writeObject(history, historyFile);
                } catch (IOException ioExc) {
                    GUIUtilities.displayErrorMessage(
                            "An IO error occurred adding the previously " +
                            "executed query to history:\n" + ioExc.getMessage());
                }
            }
        });
    }
    
    
    /** 
     * Returns whether database resources are available on
     * any connection within any pool.
     *
     * @return whether a connection to a database exists
     */
    public static boolean isConnected() {
        return ConnectionManager.getActiveConnectionPoolCount() > 0;
    }
    
    /**
     * Disconnects the specified connection.
     *
     * @param dc - the connection to be disconnected
     */
    public static final void disconnect(DatabaseConnection dc) 
        throws DataSourceException {
        ConnectionManager.closeConnection(dc);
        EventMediator.fireEvent(
                            new ConnectionEvent(dc), 
                            ConnectionListener.DISCONNECTED);
        updateStatusActiveConnections();
    }
    
    /**
     * Updates the first status bar label with the active connection pool 
     * data source count.
     */
    public static void updateStatusActiveConnections() {
        GUIUtilities.getStatusBar().setFirstLabelText(
                " Active Data Sources: " + ConnectionManager.getActiveConnectionPoolCount());
    }
    
    /**
     * Returns the open connection count for the specified connection.
     *
     * @param dc - the connection to be polled
     */
    public static int getOpenConnectionCount(DatabaseConnection dc) {
        return ConnectionManager.getOpenConnectionCount(dc);
    }

    public static final boolean connect(DatabaseConnection dc) 
        throws DataSourceException {

        ConnectionBuilder builder = null;        
        try {
            builder = new ConnectionBuilder();
            builder.establishConnection(dc);

            if (builder.isCancelled()) {
                return false;
            } 
            
            boolean connected = builder.isConnected();
            if (!connected) {
                DataSourceException e = builder.getException();
                if (e != null) {
                    throw e;
                } else {
                    throw new RuntimeException(
                            "Unknown error establishing connection.");
                }
            }

            EventMediator.fireEvent(new ConnectionEvent(dc), 
                                    ConnectionListener.CONNECTED);

            GUIUtils.scheduleGC();
            return true; 
        }
        finally {
            if (builder != null) {
                builder.finished();
            }
            updateStatusActiveConnections();            
        } 
        
    }
    
    public static final DatabaseConnection[] getSavedConnections() {
        if (connections == null) {
            connections = ConnectionProperties.getConnectionsArray();
            setSavedConnections(connections);
        }
        return connections;
    }
    
    public static void setSavedConnections(DatabaseConnection[] _savedConns) {
        connections = _savedConns;
    }
    
    /**
     * Returns the running Java VM version in full format using
     * <code>System.getProperty("java.version")</code>.
     *
     * @return the Java VM version
     */
    public static final String getVMVersionFull() {
        return System.getProperty("java.version");
    }

    /**
     * Returns the running Java VM version in short format (major versio only) 
     * using <code>System.getProperty("java.version")</code>.
     *
     * @return the Java VM version
     */
    public static final double getVMVersion() {
        return Double.parseDouble(System.getProperty("java.version").substring(0,3));
    }
    
    /**
     * Returns the user defined setting for prompt to save open 
     * documents/files etc before closing.
     *
     * @return true | false
     */
    public static boolean isPromptingToSave() {
        return SystemProperties.getBooleanProperty("user", "general.save.prompt");
    }
    
    /**
     * Program shutdown method.
     * Does some logging and closes connections cleanly.
     */
    public static void exitProgram() {
        
        if (SystemProperties.getBooleanProperty("user", "general.save.prompt") &&
                                GUIUtilities.hasValidSaveFunction()) {
            
            if (GUIUtilities.getOpenSaveFunctionCount() > 0) {
                SaveOnExitDialog exitDialog = new SaveOnExitDialog();

                int result = exitDialog.getResult();
                if (result != SaveFunction.SAVE_COMPLETE ||
                            result != SaveOnExitDialog.DISCARD_OPTION) {
                    exitDialog = null;
                    return;
                } 
            }
            
        } 

        // close open connection pools
        Log.info("Releasing database resources...");
        try {
            ConnectionManager.close();
        } catch (DataSourceException e) {}
        Log.info("Connection pools destroyed");

        GUIUtilities.shuttingDown();
        GUIUtilities.getParentFrame().dispose();
        System.exit(0);
    }
    
    /** 
     * Returns the page format for printing.
     *
     * @return the page format
     */
    public static PageFormat getPageFormat() {
        if (pageFormat == null) {
            pageFormat = PrintUtilities.getPageFormat();
        }
        return pageFormat;
    }
    
    /**
     * Sets the page format to that specified.
     *
     * @param _pageFormat - the page format
     */
    public static void setPageFormat(PageFormat _pageFormat) {
        pageFormat = _pageFormat;
    }

    /**
     * Returns whether user-defined locale settings have been set.
     */
    public static boolean hasLocaleSettings() {
        String language = SystemProperties.getStringProperty("user", "locale.language");
        String country = SystemProperties.getStringProperty("user", "locale.country");
        String timezone = SystemProperties.getStringProperty("user", "locale.timezone");

        //    Log.debug("language: " + language + " country: " + country +
        //                       " timezone: " + timezone);

        return !(MiscUtils.isNull(language)) &&
               !(MiscUtils.isNull(country)) &&
               !(MiscUtils.isNull(timezone));
    }
    
}





