/*
 * QuerySender.java
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

import java.math.BigDecimal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import org.executequery.datasource.ConnectionManager;
import org.executequery.util.Log;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.util.MiscUtils;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * This class handles all database query functions
 * such as the execution of SQL SELECT, INSERT, UPDATE
 * etc statements.
 *
 * <p>This class will typically be used by the Database
 * Browser or Query Editor where all SQL statements to be
 * executed will pass through here. In the case of a Query
 * Editor, a dedicated connection is maintained by this class
 * for the editor's use. This was shown to decrease some overhead
 * associated with constantly retrieving conenctions from the
 * pool. Also, when the commit mode is not set to auto-commit
 * within an editor, a dedicated connection is required
 * so as to maintain the correct rollback segment.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/05/26 01:56:46 $
 */
public class QuerySender {
    
    /** Whether this object is owned by a QueryEditor instance */
    private boolean keepAlive;
    
    /** The connection's commit mode */
    private boolean commitMode;
    
    /** The database connection */
    private Connection conn;
    
    /** The database <code>Statement</code> object */
    private Statement stmnt;
    
    /** The database <code>CallableStatement</code> object */
    private CallableStatement cstmnt;
    
    /** The connection use count */
    private int useCount = 0;
    
    /** The specified maximum connection use count */
    private int maxUseCount;

    /** the query result object */
    private SqlStatementResult statementResult;
    
    /** the database connection properties object */
    private DatabaseConnection databaseConnection;
    
    /** the meta data value retrieval object */
    private MetaDataValues metaData;
    
    // ---------------------------------
    // SQL statement type int constants
    // ---------------------------------
    
    public static final int ALL_UPDATES = 80;
    
    /** An SQL INSERT statement */
    public static final int INSERT = 80;
    /** An SQL UPDATE statement */
    public static final int UPDATE = 81;
    /** An SQL DELETE statement */
    public static final int DELETE = 82;
    /** An SQL SELECT statement */
    public static final int SELECT = 10;
    /** A DESCRIBE statement - table meta data */
    public static final int DESCRIBE = 16;
    /** An SQL EXPLAIN statement */
    public static final int EXPLAIN = 15;
    /** An SQL EXECUTE statement (procedure) */
    public static final int EXECUTE = 11;
    /** An SQL DROP TABLE statement */
    public static final int DROP_TABLE = 20;
    /** An SQL CREATE TABLE statement */
    public static final int CREATE_TABLE = 21;
    /** An SQL ALTER TABLE statement */
    public static final int ALTER_TABLE = 22;
    /** An SQL CREATE SEQUENCE statement */
    public static final int CREATE_SEQUENCE = 23;
    /** An SQL CREATE FUNCTION statement */
    public static final int CREATE_FUNCTION = 26;
    /** An SQL CREATE PROCEDURE statement */
    public static final int CREATE_PROCEDURE = 25;
    /** An SQL GRANT statement */
    public static final int GRANT = 27;
    /** An SQL GRANT statement */
    public static final int CREATE_SYNONYM = 28;
    /** An unknown SQL statement */
    public static final int UNKNOWN = 99;
    /** A commit statement */
    public static final int COMMIT = 12;
    /** A rollback statement */
    public static final int ROLLBACK = 13;
    /** A connect statement */
    public static final int CONNECT = 14;
        
    /** <p>Creates a new instance */
    public QuerySender() {
        this(null, false);
    }

    /** 
     * Creates a new instance with the specified connection
     * properties object as the connection provider and a keep flag 
     * that determines whether connections are retained or closed between
     * requests.
     *
     * @param the connection properties object
     * @param whether the connection should be kept between requests 
     */
    public QuerySender(DatabaseConnection databaseConnection) {
        this(databaseConnection, false);
    }

    /** 
     * Creates a new instance with the specified connection
     * properties object as the connection provider and a keep flag 
     * that determines whether connections are retained or closed between
     * requests.
     *
     * @param the connection properties object
     * @param whether the connection should be kept between requests 
     */
    public QuerySender(DatabaseConnection databaseConnection, boolean keepAlive) {
        this.keepAlive = keepAlive;
        this.databaseConnection = databaseConnection;
        maxUseCount = ConnectionManager.getMaxUseCount();
        statementResult = new SqlStatementResult();
    }
    
    /** <p>Retrieves a description of the specified table using
     *  the connection's <code>DatabaseMetaData</code> object
     *  and the method <code>getColumns(...)</code>.
     *
     *  @param  the table name to describe
     *  @return the query result
     */
    public SqlStatementResult getTableDescription(String tableName) throws Exception {

        if (!prepared()) {
            return statementResult;
        }
        
        try {
            
            /* -------------------------------------------------
             * Database meta data values are case-sensitive.
             * search for a match and use as returned from dmd.
             * -------------------------------------------------
             */

            String _tableName = null;
            String _schemaName = null;
            String schemaName = databaseConnection.getUserName();
            
            boolean valueFound = false;
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getSchemas();

            while (rs.next()) {
                _schemaName = rs.getString(1);
                if (_schemaName.equalsIgnoreCase(schemaName)) {
                    valueFound = true;
                    break;
                }
            } 
            rs.close();

            if (!valueFound) {
                _schemaName = null;
            }
            
            valueFound = false;
            rs = dmd.getTables(null, _schemaName, null, null);
            
            while (rs.next()) {
                _tableName = rs.getString(3);
                if (_tableName.equalsIgnoreCase(tableName)) {
                    valueFound = true;
                    break;
                }
            }             
            rs.close();
            
            if (!valueFound) {
                statementResult.setMessage("Invalid table name");
            }
            else {
                rs = dmd.getColumns(null, _schemaName, _tableName, null);
                statementResult.setResultSet(rs);
            }

        }
        catch (SQLException e) {
            statementResult.setSqlException(e);
            if (stmnt != null) {
                stmnt.close();
            }
            closeConnection(conn);
        }
        catch (OutOfMemoryError e) {
            statementResult.setMessage(e.getMessage());
            releaseResources();
        }
        return statementResult;        
    }
    
    private boolean prepared() throws SQLException {

        if (databaseConnection == null || 
                !databaseConnection.isConnected()) {
            statementResult.setMessage("Not Connected");
            return false;
        }
        
        // check the connection is valid
        if (conn == null) {
            try {
                conn = ConnectionManager.getConnection(databaseConnection);
                if (keepAlive) {
                    conn.setAutoCommit(commitMode);
                }
                useCount = 0;
            } catch (DataSourceException e) {
                handleDataSourceException(e);
            }
        }
        // check its still open
        else if (conn.isClosed()) {
            statementResult.setMessage("Connection closed.");
            return false;
        }

        statementResult.reset();
        if (conn != null) { // still null?
            conn.clearWarnings();
        } else {
            statementResult.setMessage("Connection closed.");
            return false;
        }
        return true;
    }
    
    /** <p>Executes the specified query (SELECT) and returns
     *  a <code>ResultSet</code> object from this query.
     *  <p>If an exception occurs, null is returned and
     *  the relevant error message, if available, assigned
     *  to this object for retrieval.
     *
     *  @param  the SQL query to execute
     *  @return the query result
     */
    public SqlStatementResult getResultSet(String query)  throws SQLException {
        
        if (!prepared()) {
            return statementResult;
        }
        
        stmnt = conn.createStatement();
        
        try {
            ResultSet rs = stmnt.executeQuery(query);
            statementResult.setResultSet(rs);
            useCount++;
            return statementResult;
        }
        catch (SQLException e) {
            statementResult.setSqlException(e);
            if (stmnt != null) {
                stmnt.close();
            }
            closeConnection(conn);
            return statementResult;
        }
        
    }

    /** <p>Executes the specified procedure.
     *
     *  @param  the SQL procedure to execute
     *  @return the query result
     */
    public SqlStatementResult executeProcedure(DatabaseProcedure proc) throws Exception {

        if (!prepared()) {
            return statementResult;
        }

        ProcedureParameter[] param = proc.getParameters();
        Arrays.sort(param, new ParameterSorter());

        String procQuery = null;
        boolean hasOut = false;
        boolean hasParameters = (param != null && param.length > 0);

        List<ProcedureParameter> outs = null;
        List<ProcedureParameter> ins = null;

        if (hasParameters) {
            
            // split the params into ins and outs
            outs = new ArrayList<ProcedureParameter>();
            ins = new ArrayList<ProcedureParameter>();

            int type = -1;
            for (int i = 0; i < param.length; i++) {
                type = param[i].getType();                
                if (type == DatabaseMetaData.procedureColumnIn ||
                      type == DatabaseMetaData.procedureColumnInOut) {
                    
                    // add to the ins list
                    ins.add(param[i]);
                    
                }
                else if (type == DatabaseMetaData.procedureColumnOut ||
                            type == DatabaseMetaData.procedureColumnResult ||
                            type == DatabaseMetaData.procedureColumnReturn ||
                            type == DatabaseMetaData.procedureColumnUnknown ||
                            type == DatabaseMetaData.procedureColumnInOut) {

                    // add to the outs list
                    outs.add(param[i]);

                }
            }

            char QUESTION_MARK = '?';
            String COMMA = ", ";

            // init the string buffer
            StringBuffer sb = new StringBuffer("{ ");

            // build the out params place holders
            for (int i = 0, n = outs.size(); i < n; i++) {
                sb.append(QUESTION_MARK);
                if (i < n - 1) {
                    sb.append(COMMA);
                }
            }

            sb.append(" = call ");
            
            if (proc.getSchema() != null) {
               sb.append(proc.getSchema()).
                  append('.');
            }

            sb.append(proc.getName()).
               append("( ");
            
            // build the ins params place holders
            for (int i = 0, n = ins.size(); i < n; i++) {
                sb.append(QUESTION_MARK);
                if (i < n - 1) {
                    sb.append(COMMA);
                }
            }

            sb.append(" ) }");
            
            // determine if we have out params
            hasOut = !(outs.isEmpty());
            procQuery = sb.toString();
        }
        else {
            StringBuffer sb = new StringBuffer();
            sb.append("{ call ");

            if (proc.getSchema() != null) {
               sb.append(proc.getSchema()).
                  append('.');
            }

            sb.append(proc.getName()).
               append("( ) }");

            procQuery = sb.toString();
        }

        //Log.debug(procQuery);

        // null value literal
        String NULL = "null";
        
        // whether a result set is returned
        boolean isResultSet = false;

        // clear any warnings
        conn.clearWarnings();
        
        Log.info("Executing: " + procQuery);

        try {
            // prepare the statement
            cstmnt = conn.prepareCall(procQuery);
        } catch (SQLException e) {
            statementResult.setSqlException(e);
            return statementResult;
        }

        // check if we are passing parameters
        if (hasParameters) {
            // the parameter index counter
            int index = 1;
            
            // the java.sql.Type value
            int dataType = -1;
            
            // the parameter input value
            String value = null;

            // register the out params
            for (int i = 0, n = outs.size(); i < n; i++) {
                //Log.debug("setting out at index: " + index);
                cstmnt.registerOutParameter(index, outs.get(i).getDataType());
                index++;
            }

            try {
            
                // register the in params
                for (int i = 0, n = ins.size(); i < n; i++) {
                    value = ins.get(i).getValue();
                    dataType = ins.get(i).getDataType();

                    if (MiscUtils.isNull(value) ||
                          value.equalsIgnoreCase(NULL)) {
                        cstmnt.setNull(index, dataType);
                    }
                    else {

                        switch (dataType) {

                            case Types.TINYINT:
                                byte _byte = Byte.valueOf(value).byteValue();
                                cstmnt.setShort(index, _byte);
                                break;

                            case Types.SMALLINT:
                                short _short = Short.valueOf(value).shortValue();
                                cstmnt.setShort(index, _short);
                                break;

                            case Types.CHAR:
                            case Types.VARCHAR:
                            case Types.LONGVARCHAR:
                                cstmnt.setString(index, value);
                                break;

                            case Types.BIT:
                            case Types.BOOLEAN:
                                boolean _boolean = Boolean.valueOf(value).booleanValue();
                                cstmnt.setBoolean(index, _boolean);
                                break;

                            case Types.BIGINT:
                                long _long = Long.valueOf(value).longValue();
                                cstmnt.setLong(index, _long);
                                break;

                            case Types.REAL:
                                float _float = Float.valueOf(value).floatValue();
                                cstmnt.setFloat(index, _float);
                                break;

                            case Types.INTEGER:
                                int _int = Integer.valueOf(value).intValue();
                                cstmnt.setInt(index, _int);
                                break;

                            case Types.DECIMAL:
                            case Types.NUMERIC:
                                cstmnt.setBigDecimal(index, new BigDecimal(value));
                                break;
    /*
                      case Types.DATE:
                      case Types.TIMESTAMP:
                      case Types.TIME:
                        cstmnt.setTimestamp(index, new Timestamp( BigDecimal(value));
    */
                            case Types.FLOAT:
                            case Types.DOUBLE:
                                double _double = Double.valueOf(value).doubleValue();
                                cstmnt.setDouble(index, _double);
                                break;

                        }

                    }

                    // increment the index
                    index++;
                }
            
            }
            // catch formatting exceptions
            catch (Exception e) {
                statementResult.setOtherErrorMessage(
                        e.getClass().getName() + ": " + e.getMessage());
                return statementResult;
            }

        }
        
        try {
            cstmnt.clearWarnings();
            boolean hasResultSet = cstmnt.execute();
            Hashtable results = new Hashtable();

            if (hasOut) {
                // incrementing index
                int index = 1;
                
                // return value from each registered out
                String returnValue = null;

                for (int i = 0; i < param.length; i++) {
                    
                    int type = param[i].getType();
                    int dataType = param[i].getDataType();
                    
                    if (type == DatabaseMetaData.procedureColumnOut ||
                            type == DatabaseMetaData.procedureColumnResult ||
                            type == DatabaseMetaData.procedureColumnReturn ||
                            type == DatabaseMetaData.procedureColumnUnknown ||
                            type == DatabaseMetaData.procedureColumnInOut) {
                        
                        switch (dataType) {

                            case Types.TINYINT:
                                returnValue = Byte.toString(cstmnt.getByte(index));
                                break;

                            case Types.SMALLINT:
                                returnValue = Short.toString(cstmnt.getShort(index));
                                break;

                            case Types.CHAR:
                            case Types.VARCHAR:
                            case Types.LONGVARCHAR:
                                cstmnt.getString(index);
                                break;

                            case Types.BIT:
                            case Types.BOOLEAN:
                                returnValue = Boolean.toString(cstmnt.getBoolean(index));
                                break;

                            case Types.INTEGER:
                                returnValue = Integer.toString(cstmnt.getInt(index));
                                break;

                            case Types.BIGINT:
                                returnValue = Long.toString(cstmnt.getLong(index));
                                break;

                            case Types.REAL:
                                returnValue = Float.toString(cstmnt.getFloat(index));
                                break;

                            case Types.DECIMAL:
                            case Types.NUMERIC:
                                returnValue = cstmnt.getBigDecimal(index).toString();
                                break;

                            case Types.DATE:
                            case Types.TIMESTAMP:
                            case Types.TIME:
                                returnValue = cstmnt.getDate(index).toString();
                                break;

                            case Types.FLOAT:
                            case Types.DOUBLE:
                                returnValue = Double.toString(cstmnt.getDouble(index));
                                break;

                        }

                        if (returnValue == null) {
                            returnValue = "NULL";
                        }
                        results.put(param[i].getName(), returnValue);
                        index++;                        
                    }
                    
                }

            }

            if (!hasResultSet) {
                statementResult.setUpdateCount(cstmnt.getUpdateCount());
            } else {
                statementResult.setResultSet(cstmnt.getResultSet());
            }

            useCount++;
            statementResult.setOtherResult(results);
        }
        catch (SQLException e) {
            e.printStackTrace();
            statementResult.setSqlException(e);
        }
        catch (Exception e) {
            statementResult.setMessage(e.getMessage());
        }
        finally {
            if (cstmnt != null) {
                cstmnt.close();
            }
            cstmnt = null;
            closeConnection(conn);            
        }
        return statementResult;
    }
    
    /** <p>Executes the specified procedure and returns
     *  a <code>ResultSet</code> object from this query.
     *  <p>If an exception occurs, null is returned and
     *  the relevant error message, if available, assigned
     *  to this object for retrieval.
     *
     *  @param  the SQL procedure to execute
     *  @return the query result
     */
    public SqlStatementResult executeProcedure(String query) throws Exception {

        if (!prepared()) {
            return statementResult;
        }
        
        //Log.debug("query " + query);
        
        String execString = "EXECUTE ";
        String callString = "CALL ";

        int nameIndex = -1;
        int index = query.toUpperCase().indexOf(execString);

        // check if EXECUTE was entered
        if (index != -1) {
            nameIndex = execString.length();
        } 
        else { // must be CALL
            nameIndex = callString.length();
        }

        // the procedure name
        String procedureName = null;
        
        // check for input brackets
        boolean possibleParams = false;
        index = query.indexOf("(", nameIndex);
        if (index != -1) {
            possibleParams = true;
            procedureName = query.substring(nameIndex, index);
        }
        else {
            procedureName = query.substring(nameIndex);
        }

        //Log.debug("name: " + procedureName);
        
        if (metaData == null) {
            metaData = new MetaDataValues(databaseConnection, false);
        }
        else {
            metaData.setDatabaseConnection(databaseConnection);
        }
        
        DatabaseProcedure procedure = 
                metaData.getProcedureColumns(null, null, procedureName);
        
        if (procedure != null) {
            
            if (possibleParams) {
                String params = query.substring(index + 1, query.indexOf(")"));
                if (!MiscUtils.isNull(params)) {

                    // check that the proc accepts params
                    if (!procedure.hasParameters()) {
                        statementResult.setSqlException(
                                new SQLException("Procedure call was invalid"));
                        return statementResult;
                    }
                    
                    int paramIndex = 0;
                    ProcedureParameter[] parameters = procedure.getParameters();
                    
                    // extract the parameters
                    StringTokenizer st = new StringTokenizer(params, ",");
                    while (st.hasMoreTokens()) {
                        String value = st.nextToken().trim();

                        // check applicable param
                        for (int i = paramIndex; i < parameters.length; i++) {
                            paramIndex++;

                            int type = parameters[i].getType();
                            if (type == DatabaseMetaData.procedureColumnIn ||
                                  type == DatabaseMetaData.procedureColumnInOut) {
                                
                                // check the data type and remove quotes if char
                                int dataType = parameters[i].getDataType();
                                if (dataType == Types.CHAR || 
                                        dataType == Types.VARCHAR || 
                                        dataType == Types.LONGVARCHAR) {

                                    if (value.indexOf("'") != -1) {
                                        // assuming quotes at start and end
                                        value = value.substring(1, value.length() - 1);
                                    }

                                }
                                

                                parameters[i].setValue(value);
                                break;
                            }
                        }

                    }

                }
            }
            
            // execute the procedure
            return executeProcedure(procedure);
        }
        else {
            statementResult.setSqlException(
                    new SQLException("Procedure or Function name specified is invalid"));
            return statementResult;
        }

        /*
        StringBuffer sb = new StringBuffer("{ call ");
        int indexOfExec = query.indexOf(execString);
        
        // check if EXECUTE was entered
        if (indexOfExec == -1) {
            sb.append(query.substring(
                          query.indexOf(callString) + 5, query.length()));
        }
        else {
            sb.append(query.substring(indexOfExec + 8, query.length()));
        }
        
        sb.append(" }");
        cstmnt = conn.prepareCall(sb.toString());
        boolean isResultSet = false;

        try {
            cstmnt.setEscapeProcessing(false);
            cstmnt.clearWarnings();
            
            isResultSet = cstmnt.execute();
            
            if (isResultSet) {
                ResultSet rs = cstmnt.getResultSet();
                statementResult.setResultSet(rs);
            }
            else {
                int result = cstmnt.getUpdateCount();
                if (result == -1) {
                    result = -10000;
                }
                statementResult.setUpdateCount(result);
            }

            useCount++;
            statementResult.setSqlWarning(cstmnt.getWarnings());
            
        }        
        catch (SQLException e) {
            statementResult.setSqlException(e);
        }
        finally {
            if (cstmnt != null) {
                cstmnt.close();
            }
            cstmnt = null;
            closeConnection(conn);
        }
        return statementResult;
         */
    }
    
    public SqlStatementResult executeQuery(String query) throws Exception {
        return executeQuery(getQueryType(query), query);
    }
    
    public SqlStatementResult executeQuery(int type, String query) throws Exception {
        statementResult.setType(type);

        switch (type) {
            case SELECT:
            case EXPLAIN:
                return getResultSet(query);
            case INSERT:
            case UPDATE:
            case DELETE:
            case DROP_TABLE:
            case CREATE_TABLE:
            case ALTER_TABLE:
            case CREATE_SEQUENCE:
            case CREATE_FUNCTION:
            case CREATE_PROCEDURE:
            case GRANT:
            case CREATE_SYNONYM:
                return updateRecords(query);

            case UNKNOWN:
                return execute(query);

            case DESCRIBE:
                int tableNameIndex = query.indexOf(" ");
                return getTableDescription(query.substring(tableNameIndex + 1));
                
            case EXECUTE:
                return executeProcedure(query);

            case COMMIT:
                return commitLast(true);

            case ROLLBACK:
                return commitLast(false);
            
            /*
            case CONNECT:
                return establishConnection(query.toUpperCase());
             */
        }
        return statementResult;
    }
    
    public SqlStatementResult execute(String query) throws Exception {
        return execute(query, true);
    }

    public SqlStatementResult execute(String query, boolean enableEscapes) 
        throws SQLException {

        if (!prepared()) {
            return statementResult;
        }

        stmnt = conn.createStatement();
        boolean isResultSet = false;
        
        try {
            stmnt.setEscapeProcessing(enableEscapes);
            isResultSet = stmnt.execute(query);
            
            if (isResultSet) {
                ResultSet rs = stmnt.getResultSet();
                statementResult.setResultSet(rs);
            }            
            else {
                int updateCount = stmnt.getUpdateCount();

                if (updateCount == -1)
                    updateCount = -10000;
                
                statementResult.setUpdateCount(updateCount);
            }

            useCount++;
            statementResult.setSqlWarning(stmnt.getWarnings());
            return statementResult;
        }        
        catch (SQLException e) {
            statementResult.setSqlException(e);
        }
        /*
        finally {
            
            if (stmnt != null) {
                stmnt.close();
                stmnt = null;
            }
            closeConnection(conn);
            
        }
        */
        return statementResult;
        
    }
    
    /** <p>Executes the specified query and returns 0 if this
     *  executes successfully. If an exception occurs, -1 is
     *  returned and the relevant error message, if available,
     *  assigned to this object for retrieval. This will
     *  typically be called for a CREATE PROCEDURE/FUNCTION
     *  call.
     *
     *  @param  the SQL query to execute
     *  @return the number of rows affected
     */
    public SqlStatementResult createProcedure(String query) throws Exception {

        if (!prepared()) {
            return statementResult;
        }

        stmnt = conn.createStatement();
        
        try {
            stmnt.clearWarnings();
            stmnt.setEscapeProcessing(false);
            boolean isResultSet = stmnt.execute(query);

            if (!isResultSet) {
                int updateCount = stmnt.getUpdateCount();
                
                if (updateCount == -1)
                    updateCount = -10000;
                
                statementResult.setUpdateCount(updateCount);
            }            
            else { // should never be a result set
                ResultSet rs = stmnt.getResultSet();
                statementResult.setResultSet(rs);
            }

            useCount++;
            statementResult.setSqlWarning(stmnt.getWarnings());
        }
        catch (SQLException e) {
            statementResult.setSqlException(e);
        }
        finally {
            if (stmnt != null) {
                stmnt.close();
            }
            closeConnection(conn);
        }

        return statementResult;        
    }
    
    /** <p>Executes the specified query and returns
     *  the number of rows affected by this query.
     *  <p>If an exception occurs, -1 is returned and
     *  the relevant error message, if available, assigned
     *  to this object for retrieval.
     *
     *  @param  the SQL query to execute
     *  @return the number of rows affected
     */
    public SqlStatementResult updateRecords(String query) throws SQLException {

        if (!prepared()) {
            return statementResult;
        }

        stmnt = conn.createStatement();
        
        try {
            int result = stmnt.executeUpdate(query);
            statementResult.setUpdateCount(result);
            useCount++;
        }
        catch (SQLException e) {
            statementResult.setSqlException(e);
        }
        finally {
            if (stmnt != null) {
                stmnt.close();
            }
            closeConnection(conn);            
        }
        
        return statementResult;
        
    }
    
    /*
    public SqlStatementResult establishConnection(String query) {
        statementResult.reset();
        String connectString = "CONNECT ";
        int index = query.indexOf("CONNECT ") + connectString.length();
        String name = query.substring(index).trim();
        DatabaseConnection dc = ConnectionProperties.getDatabaseConnection(name, true);
        
        if (dc == null) {
            statementResult.setMessage("The connection does not exist");
        }
        
        return statementResult;    
    }
     */
    
    /** <p>Commits or rolls back the last executed
     *  SQL query or queries.
     *
     *  @param true to commit - false to roll back
     */
    public SqlStatementResult commitLast(boolean commit) {
        try {
            statementResult.reset();
            statementResult.setUpdateCount(0);            

            if (commit) {
                conn.commit();
                Log.info("Commit complete.");
                statementResult.setMessage("Commit complete.");
                closeMaxedConn();
            } else {
                conn.rollback();
                Log.info("Rollback complete.");
                statementResult.setMessage("Rollback complete.");
                closeMaxedConn();
            }
            
        } catch (SQLException sqlExc) {
            statementResult.setSqlException(sqlExc);
            sqlExc.printStackTrace();
        }
        return statementResult;
        
    }
    
    /** <p>Closes a connection which has reached its
     *  maximum use count and retrieves a new one from
     *  the <code>DBConnection</code> object.
     */
    private void closeMaxedConn() throws SQLException {
        if (keepAlive && useCount > maxUseCount) {
            destroyConnection();
        }
    }
    
    /** 
     * Destroys the open connection.
     */
    public void destroyConnection() throws SQLException {
        try {
            ConnectionManager.close(databaseConnection, conn);
            conn = null;
//            prepared();
//            useCount = 0;
        } catch (DataSourceException e) {
            handleDataSourceException(e);
        }
    }
    
    /** <p>Sets the connection's commit mode to the
     *  specified value.
     *
     *  @param true for auto-commit, false otherwise
     */
    public void setCommitMode(boolean commitMode) {
        this.commitMode = commitMode;
        //Log.debug("commitMode: " + commitMode);
        try {
            if (keepAlive && (conn != null && !conn.isClosed())) {
                conn.setAutoCommit(commitMode);
            }
        }
        catch (SQLException sqlExc) {
            sqlExc.printStackTrace();
        }
    }
    
    /** 
     * Cancels the current SQL statement being executed.
     */
    public void cancelCurrentStatement() {
        if (stmnt != null) {
            try {
                //Log.debug("cancelCurrentStatement");
                stmnt.cancel();
                stmnt.close();
                stmnt = null;
                closeConnection(conn);
                statementResult.setMessage("Statement cancelled.");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /** 
     * Determines the type of query from the specified query.
     *
     * @param the SQL query to analyse
     * @result the type of SQL query
     */
    public int getQueryType(String query) {
        int type = -1;
        
        query = query.toUpperCase();
        
        if (query.indexOf("CREATE TABLE ") == 0)
            type = CREATE_TABLE;
        
        else if (query.indexOf("CREATE ") == 0 &&
                      (query.indexOf("PROCEDURE ") != -1 || 
                      query.indexOf("PACKAGE ") != -1))
            type = CREATE_PROCEDURE;
        
        else if (query.indexOf("CREATE ") == 0 && 
                      query.indexOf("FUNCTION ") != -1)
            type = CREATE_FUNCTION;
        
        else if (query.indexOf("CONNECT ") == 0)
            type = CONNECT;
        
        else if (query.indexOf("INSERT ") == 0)
            type = INSERT;
        
        else if (query.indexOf("UPDATE ") == 0)
            type = UPDATE;
        
        else if (query.indexOf("DELETE ") == 0)
            type = DELETE;
        
        else if (query.indexOf("DROP TABLE ") == 0)
            type = DROP_TABLE;
        
        else if (query.indexOf("ALTER TABLE ") == 0)
            type = ALTER_TABLE;
        
        else if (query.indexOf("CREATE SEQUENCE ") == 0)
            type = CREATE_SEQUENCE;
        
        else if (query.indexOf("CREATE SYNONYM ") == 0)
            type = CREATE_SYNONYM;
        
        else if (query.indexOf("GRANT ") == 0)
            type = GRANT;
        
        else if (query.indexOf("EXECUTE ") == 0 || query.indexOf("CALL ") == 0)
            type = EXECUTE;
        
        else if (query.indexOf("COMMIT") == 0)
            type = COMMIT;
        
        else if (query.indexOf("ROLLBACK") == 0)
            type = ROLLBACK;
        
        else if(query.indexOf("SELECT ") == 0)
            type = SELECT;
        
        else if(query.indexOf("EXPLAIN ") == 0)
            type = EXPLAIN;
        
        else if(query.indexOf("DESC ") == 0 || query.indexOf("DESCRIBE ") == 0)
            type = DESCRIBE;
        
        else
            type = UNKNOWN;
        
        return type;
    }
    
    /** <p>Closes the specified database connection.
     *  <p>If the specified connection is NULL, the open
     *  connection held by this class is closed.
     *
     *  @param the connection to close
     */
    public void closeConnection(Connection c) throws SQLException {
        try {
            // if this not the connection assigned to this object
            if (c != null && c != conn) {
                c.close();
                c = null;
            } else { // otherwise proceed to close
                closeConnection();
            }
            /*
            if (!keepAlive) {
                c.close();
            }
            else if (c == null) {
                if (conn != null) {
                    conn.close();
                }
                conn = null;
            }
             */
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the database connection of this object.
     * If destroy is true, the connection will be 
     * closed using connection.close(). Otherwise,
     * the value of keepAlive for this instance will
     * be respected.
     *
     * @param whether to call close() on the connection object
     */
    public void closeConnection(boolean destroy) {
        if (destroy) {
            try {
                if (conn != null) {
                    conn.close();
                }
                conn = null;
            }
            catch (SQLException e) {}
        }
    }
    
    /** 
     * Closes the database connection of this object.
     */
    public void closeConnection() throws SQLException {
        // if set to keep the connection open
        // for this instance - return
        if (keepAlive) {
            return;
        }
        // otherwise close it
        closeConnection(true);
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the connection thats been closed
     */
    public void disconnected(DatabaseConnection dc) {
        if (databaseConnection == dc) {
            closeConnection(true);
            databaseConnection = null;
        }
    }

    /**
     * Handles a DataSourceException by rethrowing as a
     * SQLException.
     */
    private void handleDataSourceException(DataSourceException e) 
        throws SQLException {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException)e.getCause();
            } else {
                throw new SQLException(e.getMessage()); 
            }
    }
    
    /** <p>Releases database resources held by this class. */
    public void releaseResources() {
        
        //Log.debug("releaseResources: keepAlive - " + keepAlive);
        
        try {
            if(stmnt != null) {
                stmnt.close();
            }
            if(cstmnt != null) {
                cstmnt.close();
            }
            stmnt = null;
            cstmnt = null;
            
            if (!keepAlive) {
                if (conn != null) {
                    conn.close();
                }
                conn = null;
            }
            
            /*
            if (keepAlive) {
                conn = dbConn.getConnection();
                setCommitMode(commitMode);
            }
            */
        } catch (SQLException exc) {}
    }

    public void releaseStatements() {
        try {
            if(stmnt != null) {
                stmnt.close();
            }
            if(cstmnt != null) {
                cstmnt.close();
            }
            stmnt = null;
            cstmnt = null;
            closeConnection(conn);
        } catch (Exception exc) {}
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public void setDatabaseConnection(DatabaseConnection _databaseConnection) {
        if (databaseConnection != _databaseConnection) {
            try {
                // close the current connection
                if (databaseConnection != null && conn != null) {
                    ConnectionManager.close(databaseConnection, conn);
                    conn = null;
                }
                // reassign the connection
                databaseConnection = _databaseConnection;
                prepared();
                useCount = 0;
            } 
            catch (DataSourceException e) {}
            catch (SQLException e) {}
        }
    }

}


