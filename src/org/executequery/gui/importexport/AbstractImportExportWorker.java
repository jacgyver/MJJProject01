/*
 * AbstractImportExportWorker.java
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


package org.executequery.gui.importexport;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.executequery.SystemUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.datasource.ConnectionManager;
import org.underworldlabs.jdbc.DataSourceException;
import org.executequery.gui.browser.ColumnData;
import org.executequery.util.Base64;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Abstract import/export worker class.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.10 $
 * @date     $Date: 2006/06/14 15:09:00 $
 * @author Dragan Vasic
 */
public abstract class AbstractImportExportWorker implements ImportExportWorker {

    /** The progress dialog for this process */
    protected ImportExportProgressPanel progress;
    
    /** the parent process controller */
    protected ImportExportProcess parent;
    
    /** the start time of this process */
    protected long startTime;

    /** the finish time of this process */
    protected long finishTime;

    /** The database connection for data retrieval */
    protected Connection conn;
    
    /** The database statement */
    protected Statement stmnt;
    
    /** The database prepared statement */
    protected PreparedStatement prepStmnt;

    /** indicates a cancelled process */
    protected final String CANCELLED = "cancelled";

    /** indicates a failed process */
    protected final String FAILED = "failed";

    /** indicates a failed process */
    protected final String SUCCESS = "success";

    /** the total records processed */
    private int recordCount;
    
    /** the total records processed successfully */
    private int recordCountProcessed;
    
    /** the number of errors */
    private int errorCount;
    
    /** the table count */
    protected int tableCount;
    
    /** the process result */
    private String result;
    
    /** temp output logging buffer */
    protected StringBuffer outputBuffer;
    
    /** Indicates a bound column value */
    protected final String VARIABLE_BOUND = "variableBound";
    
    /** Indicates a not bound column value */
    protected final String VARIABLE_NOT_BOUND = "variableNotBound";

    /** Indicates an ignored column */
    protected final String IGNORED_COLUMN = "ignoredColumn";
    
    /** Indicates an included column */
    protected final String INCLUDED_COLUMN = "includedColumn";
    
    public AbstractImportExportWorker(ImportExportProcess parent,
                                      ImportExportProgressPanel progress) {
        this.parent = parent;
        this.progress = progress;
        outputBuffer = new StringBuffer();
    }
    
    /**
     * Resets the progress panel.
     */
    protected void reset() {
        progress.reset();
    }

    protected int getTableRecordCount(String tableName) 
        throws DataSourceException,
               SQLException {

        ResultSet rs = null;
        try {
            StringBuffer query = new StringBuffer();
            query.append("SELECT COUNT(*) FROM ");

            String schema = parent.getSchemaName();
            if (!MiscUtils.isNull(schema)) {
                query.append(schema).append('.');
            }
            query.append(tableName);

            conn = getConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery(query.toString());
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
        finally {
            if (rs != null) {
                rs.close();
            }
            if (stmnt != null) {
                stmnt.close();
            }
        }            
    }
    
    /**
     * Returns a data result set for the specified table.
     *
     * @param table - the database table
     */
    protected ResultSet getResultSet(String table) 
        throws DataSourceException,
               SQLException {
        return getResultSet(table, null);
    }
    
    /**
     * Returns a data result set for the specified table and the 
     * specified columns. If the columns collection is null, 
     * this will assume a multi-table export and expor all the 
     * database columns of the table.
     *
     * @param table - the database table name
     * @param columns - the columns to select from the table
     */
    protected ResultSet getResultSet(String table, Vector<?> columns) 
        throws DataSourceException,
               SQLException {
        
        // check the columns and retrieve if null
        if (columns == null) {
            columns = getColumns(table);
        }
        
        // build the SQL statement
        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        
        int columnCount = columns.size();
        for (int i = 0, n = columnCount - 1; i < columnCount; i++) {
            query.append(columns.get(i).toString());
            if (i != n) {
                query.append(',');
            }
        }
        query.append(" FROM ");
        
        String schema = parent.getSchemaName();
        if (!MiscUtils.isNull(schema)) {
            query.append(schema).append('.');
        }
        query.append(table);

        if (stmnt != null) {
            try {
                stmnt.close();
            } catch (SQLException e) {}
        }
        
        conn = getConnection();
        stmnt = conn.createStatement();
        return stmnt.executeQuery(query.toString());
    }
    
    /**
     * Prepares the statement for an import process.
     *
     * @param table - the database table name
     * @param columns - the columns to select from the table
     */
    protected void prepareStatement(String table, Vector<?> columns) 
        throws DataSourceException,
               SQLException {
        
        // check the columns and retrieve if null
        if (columns == null) {
            columns = getColumns(table);
        }
        
        String schema = parent.getSchemaName();
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO ");

        if (!MiscUtils.isNull(schema)) {
            query.append(schema).append('.');
        }
        query.append(table);
        query.append(" (");
        
        // add the column names to the query
        int columnCount = columns.size();
        for (int i = 0, n = columnCount - 1; i < columnCount; i++) {
            query.append(columns.get(i).toString());
            if (i != n) {
                query.append(',');
            }
        }
        query.append(") VALUES (");

        // add the value place holders
        for (int i = 0, n = columnCount - 1; i < columnCount; i++) {
            query.append('?');
            if (i != n) {
                query.append(',');
            }
        }
        query.append(")");
        
        // make sure it was closed from a possible previous run
        if (prepStmnt != null) {
            try {
                prepStmnt.close();
            } catch (SQLException e) {}
        }

        conn = getConnection();
        conn.setAutoCommit(false);
        prepStmnt = conn.prepareStatement(query.toString());
    }
    
    /**
     * Returns the columns to be exported for the specified table.
     * 
     * @param table - the table to be dumped
     */
    protected Vector<ColumnData> getColumns(String table) 
        throws SQLException {
        Vector<ColumnData> columns = parent.getSelectedColumns();
        if (columns == null) {
            String schema = parent.getSchemaName();
            MetaDataValues metaData = parent.getMetaDataUtility();
            try {
                columns = metaData.getColumnMetaDataVector(table, schema);
            }
            catch (DataSourceException e) {
                if (e.getCause() instanceof SQLException) {
                    throw (SQLException)(e.getCause());
                }
                throw new SQLException(e.getMessage());
            }
        }
        else {
            columns = (Vector<ColumnData>)columns.clone();
        }
        return columns;
    }
    
    /**
     * Returns the connection to be used with this process.
     *
     * @return the connection
     */
    protected Connection getConnection() throws DataSourceException {
        if (conn == null) {
            conn = ConnectionManager.getConnection(parent.getDatabaseConnection());
        }
        return conn;
    }
    
    /**
     * Sets the specified value in the specified position for the
     * specified java.sql.Type within the prepared statement.
     *
     * @param value - the value
     * @param index - the position within the statement
     * @param sqlType - the SQL type
     * @param trim - whether to trim the whitespace from the value
     * @param df - the DataFormat object for date values
     */
    protected void setValue(String value, int index, 
                            int sqlType, boolean trim, DateFormat df) 
        throws Exception {
        
        if (value == null) {
            prepStmnt.setNull(index, sqlType);
        }
        else {

            switch (sqlType) {

                case Types.TINYINT:
                    byte _byte = Byte.valueOf(value).byteValue();
                    prepStmnt.setShort(index, _byte);
                    break;

                case Types.SMALLINT:
                    short _short = Short.valueOf(value).shortValue();
                    prepStmnt.setShort(index, _short);
                    break;

                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    if (trim) {
                        value = value.trim();
                    }
                    prepStmnt.setString(index, value);
                    break;

                case Types.BIT:
                case Types.BOOLEAN:
                    boolean _boolean = Boolean.valueOf(value).booleanValue();
                    prepStmnt.setBoolean(index, _boolean);
                    break;

                case Types.BIGINT:
                    long _long = Long.valueOf(value).longValue();
                    prepStmnt.setLong(index, _long);
                    break;

                case Types.REAL:
                    float _float = Float.valueOf(value).floatValue();
                    prepStmnt.setFloat(index, _float);
                    break;

                case Types.DECIMAL:
                case Types.NUMERIC:
                    prepStmnt.setBigDecimal(index, new BigDecimal(value));
                    break;

                case Types.FLOAT:
                case Types.DOUBLE:
                    prepStmnt.setDouble(index, Double.parseDouble(value));
                    break;

                case Types.INTEGER:
                    prepStmnt.setInt(index, Integer.parseInt(value));
                    break;

                case Types.DATE:
                case Types.TIMESTAMP:
                case Types.TIME:
                    // if the date format is null, insert as a char value
                    if (df != null) { 
                        java.util.Date j_datetime = df.parse(value);
                        prepStmnt.setDate(index,
                              new java.sql.Date(j_datetime.getTime()));
                    }
                    else {
                        try {
                            prepStmnt.setObject(index,value,sqlType);
                            /*
                            if (sqlType == Types.TIMESTAMP) {
                                prepStmnt.setTimestamp(index, 
                                        java.sql.Timestamp.valueOf(value));
                            }
                            else if (sqlType == Types.TIME) {
                                prepStmnt.setTime(index, 
                                        java.sql.Time.valueOf(value));
                            }
                            else {
                                prepStmnt.setDate(index, 
                                        java.sql.Date.valueOf(value));
                            }
                             */
                        }
                        // want a more useful message here than what will likely
                        // be returned due to internal driver code on formatting 
                        // a SQL date value from string 
                        // (ie. could be parsing error, number format etc...)
                        catch (Exception e) {
                            throw new IllegalArgumentException(
                                    "[ " + MiscUtils.getExceptionName(e) + " ] " +
                                    "Error converting date/time value - " +
                                    "invalid SQL date/time escape format.");
                        }
                            
                    }
                    break;

                case Types.BLOB:
                case Types.CLOB:
                case Types.BINARY:
                case Types.LONGVARBINARY:
                    prepStmnt.setBytes(index, Base64.decode(value));
                    break;

                default:
                    prepStmnt.setObject(index, value);
                    break;
            }
        }
    }
    
    /** 
     * Displays an error dialog with the specified message.
     */
    protected void displayErrorDialog(String message) {
        GUIUtilities.displayErrorMessage(message);
    }

    /** 
     * Displays an input dialog to enter the date format mask.
     *
     * @return the mask entered
     */
    protected String displayDateFormatDialog() {
        StringBuffer sb = new StringBuffer();
        sb.append("At least one column in the current table is a Date/Time ");
        sb.append("SQL datatype.\nPlease enter the date format for this ");
        sb.append("column as displayed in the import file in the field below.");
        return GUIUtilities.displayInputMessage("Date Field", sb.toString());
    }
    
    /**
     * Verifies the date format where applicable.
     *
     * @return the date format
     */
    protected String verifyDate() {
        String format = displayDateFormatDialog();
        
        if (format == null || format.length() == 0) {
            
            int yesNo = GUIUtilities.displayConfirmDialog(
                    "This will cancel the current process.\nDo you wish to proceed?");
            
            if (yesNo == JOptionPane.YES_OPTION) {
                cancelTransfer();
                return null;
            }
            else {
                format = displayDateFormatDialog();
            }
            
        }
        
        return format;        
    }

    /**
     * Appends the specified text to the output pane as normal
     * fixed width text.
     *
     * @param text - the text to be appended
     */
    protected void appendProgressText(String text) {
        progress.appendProgressText(text);
    }

    /**
     * Appends the specified text to the output pane as normal
     * fixed width text.
     *
     * @param text - the text to be appended
     */
    protected void appendProgressErrorText(String text) {
        progress.appendProgressErrorText(text);
    }

    /**
     * Appends the specified buffer to the output pane as normal
     * fixed width text.
     *
     * @param text - the text to be appended
     */
    protected void appendProgressText(StringBuffer text) {
        progress.appendProgressText(text.toString());
        text.setLength(0);
    }

    /**
     * Appends the specified buffer to the output pane as error
     * fixed width text.
     *
     * @param text - the text to be appended
     */
    protected void appendProgressErrorText(StringBuffer text) {
        progress.appendProgressErrorText(text.toString());
        text.setLength(0);
    }

    /**
     * Appends the specified buffer to the output pane as warning
     * fixed width text.
     *
     * @param text - the text to be appended
     */
    protected void appendProgressWarningText(StringBuffer text) {
        progress.appendProgressWarningText(text.toString());
        text.setLength(0);
    }

    /**
     * Sets the progress bar as indeterminate as specified
     */
    protected void setIndeterminateProgress(boolean indeterminate) {
        progress.setIndeterminate(indeterminate);
    }
    
    /**
     * Sets the maximum value for the progess bar.
     *
     * @param value - the max value
     */
    protected void setProgressBarMaximum(int value) {
        progress.setMaximum(value);
    }
    
    /** 
     * Sets the progress bar's position during the process.
     *
     * @param the new process status
     */
    public void setProgressStatus(int status) {
        progress.setProgressStatus(status);
    }

    /**
     * Releases all held database resources.
     */
    protected void releaseResources(DatabaseConnection dc) {
        try {
            
            if (stmnt != null) {
                stmnt.close();
                stmnt = null;
            }
            
            if (prepStmnt != null) {
                prepStmnt.close();
                prepStmnt = null;
            }

            if (conn != null) {
                conn.setAutoCommit(true);
                ConnectionManager.close(dc, conn);
                conn = null;
            }

        } 
        catch (DataSourceException e) {
            System.err.println(
                    "Exception releasing resources at: " + e.getMessage());            
        }
        catch (SQLException e) {
            System.err.println(
                    "Exception releasing resources at: " + e.getMessage());
        }
    }

    protected void outputExceptionError(String message, Throwable e) {
        if (message != null) {
            outputBuffer.append(message);
        }
        outputBuffer.append("\n[ ");
        outputBuffer.append(MiscUtils.getExceptionName(e));
        outputBuffer.append(" ] ");
        
        if (e instanceof DataSourceException) {
            outputBuffer.append(e.getMessage());
            outputBuffer.append(((DataSourceException)e).getExtendedMessage());
        }
        else if (e instanceof SQLException) {
            outputBuffer.append(e.getMessage());
            SQLException _e = (SQLException)e;
            outputBuffer.append("\nError Code: " + _e.getErrorCode());

            String state = _e.getSQLState();
            if (state != null) {
                outputBuffer.append("\nSQL State Code: " + state);
            }

        }
        else {
            outputBuffer.append(e.getMessage());
        }

        appendProgressErrorText(outputBuffer);
    }
    
    /**
     * Appends the final process results to the output pane.
     */
    protected void printResults() {
        StringBuffer sb = new StringBuffer();
        sb.append("---------------------------\n");

        if (result == SUCCESS) {
            sb.append("Process completed successfully\n");
        }
        else if (result == CANCELLED) {
            sb.append("Process cancelled.\n");
        }
        else if (result == FAILED) {
            sb.append("Process completed with errors.\n");
        }

        sb.append("\nTotal duration: ");
        sb.append(getFormattedDuration());
        sb.append("\nTotal tables processed: ");
        sb.append(tableCount);
        sb.append("\nTotal records processed: ");
        sb.append(recordCount);
        sb.append("\nTotal records transferred: ");
        sb.append(recordCountProcessed);
        sb.append("\nErrors: ");
        sb.append(errorCount);

        appendProgressText(sb.toString());
        
        // log the output to file
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                logOutputToFile();
            }
        });
        
    }
    
    /**
     * Returns the controlling parent process object.
     */
    protected ImportExportProcess getParent() {
        return parent;
    }
    
    /** 
     * Cancels the current data transfer process. 
     */
    public abstract void cancelTransfer();
    
    /**
     * Indicates a data transfer process has completed
     * and clean-up can be performed.
     */
    public abstract void finished();
    
    /**
     * Logs the start time of this process.
     */
    protected void start() {
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Logs the finish time of this process.
     */
    protected void finish() {
        finishTime = System.currentTimeMillis();
        progress.setStopButtonEnabled(false);
    }
    
    /**
     * Logs the contents of the output pane to file.
     */
    protected void logOutputToFile() {
        PrintWriter writer = null;
        try {
            String logHeader = null;
            String path = SystemUtilities.getUserLogsPath();
            
            int transferType = parent.getTransferType();
            if (transferType == ImportExportProcess.EXPORT) {
                logHeader = "[ Data Export Process - ";
                path += SystemProperties.getProperty("system", "eq.export.log");
            } else {
                logHeader = "[ Data Import Process - ";
                path += SystemProperties.getProperty("system", "eq.import.log");
            }

            // add a header for this process
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            StringBuffer sb = new StringBuffer();
            sb.append(logHeader);
            sb.append(df.format(new Date(startTime)));
            sb.append(" ]\n\n");
            sb.append(progress.getText());
            sb.append("\n\n");

            writer = new PrintWriter(new FileWriter(path, true), true);
            writer.println(sb.toString());
            sb = null;
        } 
        catch (IOException io) {}
        finally {
            if (writer != null) {
                writer.close();
            }
            writer = null;
        }
    }
    
    /**
     * Returns the start time of the import/export process.
     *
     * @return the process start time
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Returns the start time of the import/export process.
     *
     * @return the process finish time
     */
    public long getFinishTime() {
        return finishTime;
    }

    /**
     * Returns a formatted string of the duration of the process.
     *
     * @return the process duration formatted as hh:mm:ss
     */
    public String getFormattedDuration() {
        return MiscUtils.formatDuration(finishTime - startTime);
    }

    /**
     * Sets the start time to that specified.
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the finish time to that specified.
     */
    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    
    /**
     * Returns the total record count.
     */
    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getRecordCountProcessed() {
        return recordCountProcessed;
    }

    public void setRecordCountProcessed(int recordCountProcessed) {
        this.recordCountProcessed = recordCountProcessed;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getTableCount() {
        return tableCount;
    }

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}



