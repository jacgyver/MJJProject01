/*
 * QueryAnalyser.java
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


package org.executequery.gui.editor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.executequery.SystemUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.QuerySender;
import org.executequery.databasemediators.SqlStatementResult;
import org.underworldlabs.swing.util.SwingWorker;
import org.executequery.gui.SQLExecutor;
import org.executequery.gui.text.syntax.Token;
import org.executequery.gui.text.syntax.TokenTypes;
import org.executequery.util.Log;
import org.underworldlabs.util.MiscUtils;
import org.executequery.util.OutputLogger;
import org.underworldlabs.swing.GUIUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Determines the type of exeuted query and returns
 *  appropriate results.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.10 $
 * @date     $Date: 2006/08/24 09:38:11 $
 */
public class QueryAnalyser {
    
    /** the parent controller */
    private SQLExecutor panel;
   
    /** thread worker object */
    private SwingWorker worker;
    
    /** the query sender database mediator */
    private QuerySender qs;
    
    /** indicates verbose logging output */
    private boolean verboseLogging;
    
    /** logging on/off flag */
    private boolean logging;
    
    /** Indicates that an execute is in progress */
    private boolean executing;
    
    /** indicates that executed query was a SQL SELECT statement */
    private boolean isSelect;
    
    /** connection commit mode */
    private boolean autoCommitMode;
    
    /** Regex matcher for quote marks */
    private Matcher quoteMatcher;
    
    /** Regex matcher for single-line comments */
    private Matcher singleLineCommentMatcher;

    /** Regex matcher for multi-line comments */
    private Matcher multiLineCommentMatcher;

    /** Regex matcher for final query trim */
    private Matcher trimNewLineMatcher;    
    
    /** The query execute duration time */
    private String duration;
    
    /** indicates that the current execution has been cancelled */
    private boolean statementCancelled;

    // ------------------------------------------------
    // static string constants
    // ------------------------------------------------
    
    private static final String BEGIN = "BEGIN";
    private static final String DECLARE = "DECLARE";
    
    private static final String delim = ";";
    private static final String SUBSTRING = "...";
    private static final String EXECUTING_1 = "Executing: ";
    private static final String EXECUTING_2 = " Executing... ";
    private static final String ERROR_EXECUTING = " Error executing statement";
    private static final String SUCCESS = " Statement executed successfully";
    private static final String DONE = " Done";
    private static final String LOGGER_NAME = "query-editor";
    private static final String COMMITTING_LAST = "Committing last transaction block...";
    private static final String ROLLINGBACK_LAST = "Rolling back last transaction block...";

    // ------------------------------------------------
    

    public QueryAnalyser(SQLExecutor panel) {
        this.panel = panel;
        qs = new QuerySender(null, true);
        setCommitMode(SystemProperties.getBooleanProperty("user", "editor.connection.commit"));
        //quoteMatcher = Pattern.compile("'([^'\r\n])+'|'.*").matcher(Constants.EMPTY);        
        initialiseLogging();
    }

    /**
     * Intialises the regex matchers.
     */
    protected void initMatchers() {
        if (quoteMatcher == null) {
            quoteMatcher = Pattern.compile(
                    "'((?>[^']*+)(?>'''[^']*+)*+)'|'.*").matcher(Constants.EMPTY);            
        }
        
        if (singleLineCommentMatcher == null) {
            singleLineCommentMatcher = Pattern.compile(
                    TokenTypes.SINGLE_LINE_COMMENT_REGEX, Pattern.MULTILINE).
                    matcher(Constants.EMPTY);
        }
        
        if (multiLineCommentMatcher == null) {
            multiLineCommentMatcher = Pattern.compile(
                    "/\\*((?>[^\\*/]*+)*+)\\*/|/\\*.*", Pattern.DOTALL).
                    matcher(Constants.EMPTY);
        }
        
        if (trimNewLineMatcher == null) {
            trimNewLineMatcher = Pattern.compile(
                    //"\\A\\s+|\n{2,}|\\s+\\z", Pattern.DOTALL).
                    "\n{2,}", Pattern.DOTALL).
                    matcher(Constants.EMPTY);            
        }

    }
    
    /**
     * Notification of user preference changes.
     */
    public void preferencesChanged() {
        initialiseLogging();
    }
    
    /**
     * Logging initialisation.
     */
    protected void initialiseLogging() {
        verboseLogging = SystemProperties.getBooleanProperty("user", "editor.logging.verbose");
        logging = SystemProperties.getBooleanProperty("user", "editor.logging.enabled");

        if (logging) {
            String layout = "[%d{dd-MM-yy HH:mm:ss}] %m%n";
            String path = SystemProperties.getStringProperty("user", "editor.logging.path");
            int maxBackups = SystemProperties.getIntProperty("user", "editor.logging.backups");

            if (!MiscUtils.isNull(path)) {

                try {
                    OutputLogger.initialiseLogger(LOGGER_NAME, layout, path, maxBackups);
                } catch (IOException e) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("An error occurred initialising the logger.").
                       append("\n\nThe system returned:\n").
                       append(e.getMessage());
                    GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
                }

            }

            if (newLineMatcher == null) {
                newLineMatcher = Pattern.compile("\n").matcher("");
            }

        }

    }
    
    /**
     * Sets the commit mode to that specified.
     *
     * @param the commit mode
     */
    public void setCommitMode(boolean commitMode) {
        autoCommitMode = commitMode;
        qs.setCommitMode(autoCommitMode);
        panel.setRightStatusText(" Auto-Commit: " + autoCommitMode);
    }
    
    /**
     * Propagates the call to close the connection to 
     * the QuerySender object.
     */
    public void closeConnection() {
        try {
            if (qs != null) {
                qs.closeConnection();
            }
        }
        catch (SQLException sqlExc) {}
    }
    
    /**
     * Indicates a connection has been closed.
     * Propagates the call to the query sender object.
     * 
     * @param the connection thats been closed
     */
    public void disconnected(DatabaseConnection dc) {
        qs.disconnected(dc);
    }

    /**
     * Returns the current commit mode.
     *
     * @return the commit mode
     */
    public boolean getCommitMode() {
        return autoCommitMode;
    }
    
    /**
     * Executes the query(ies) as specified. The executeAsBlock flag
     * indicates that the query should be executed in its entirety - 
     * not split up into mulitple queries (where applicable).
     *
     * @param the query string
     * @param true to execute in entirety, false otherwise
     */
    public void executeSQLQuery(String query, boolean executeAsBlock) {
        executeSQLQuery(null, query, executeAsBlock);
    }

    /**
     * Executes the query(ies) as specified on the provided database
     * connection properties object. The executeAsBlock flag
     * indicates that the query should be executed in its entirety - 
     * not split up into mulitple queries (where applicable).
     *
     * @param the connection object
     * @param the query string
     * @param true to execute in entirety, false otherwise
     */
    public void executeSQLQuery(DatabaseConnection dc, 
                                final String query, 
                                final boolean executeAsBlock) {

        if (!SystemUtilities.isConnected()) {
            setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE, "Not Connected");
            panel.setLeftStatusText(ERROR_EXECUTING);
            return;
        }

        if (qs == null) {
            qs = new QuerySender(null, true);
        }

        if (dc != null) {
            qs.setDatabaseConnection(dc);
        }
        
        panel.setStopButtonEnabled(true);
        statementCancelled = false;

        worker = new SwingWorker() {
            public Object construct() {
                return executeSQL(query, executeAsBlock);
            }
            public void finished() {
                panel.setStopButtonEnabled(false);
                panel.setActivityStatusText(duration);
                panel.setExecuting(false);

                if (statementCancelled) {
                    setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE,
                                     "Statement cancelled");
                    panel.setLeftStatusText(" Statement cancelled");
                }
                qs.releaseResources();
                executing = false;
            }
        };
        
        setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE, 
                "---\nUsing connection: " + dc);

        panel.setActivityStatusText(EXECUTING_2);
        panel.setExecuting(true);
        panel.setLeftStatusText(Constants.EMPTY);
        worker.start();
    }
    
    /**
     * Interrupts the statement currently being executed.
     */
    public void interruptStatement() {

        //Log.debug("interruptStatement");
        //Log.debug("executing " + executing);
        
        if (!executing) {
            return;
        }
/*
        if (!isSelect) {
            if (worker != null) {
                worker.interrupt();
            }
        }
        else {
 */
            if (qs != null) {
                //Log.debug("interrupting qs");
                qs.cancelCurrentStatement();
            }
/*
            if (worker != null) {
                worker.interrupt();
            }
 */
   //     }
        executing = false;
        statementCancelled = true;
    }
    
    /**
     * Returns whether a a query is currently being executed.
     *
     * @param true if in an execution is in progress, false otherwise
     */
    public boolean isExecuting() {
        return executing;
    }
    
    /**
     * Executes the query(ies) as specified. This method performs the
     * actual execution following query 'massaging'.The executeAsBlock 
     * flag indicates that the query should be executed in its entirety - 
     * not split up into mulitple queries (where applicable).
     *
     * @param the query string
     * @param true to execute in entirety, false otherwise
     */
    private Object executeSQL(String q, boolean executeAsBlock) {        
        // init the start and end times
        long start = 0;
        long end = 0;
        
        try {
            
            // check we are executing the whole block of sql text
            if (executeAsBlock) {
                start = System.currentTimeMillis();
                
                // print the query
                logExecution(q.trim());

                executing = true;
                SqlStatementResult result = qs.execute(q, true);

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                if (result.isResultSet()) {
                    ResultSet rset = result.getResultSet();

                    if (rset == null) {
                        setOutputMessage(QueryEditorConstants.ERROR_MESSAGE, 
                                result.getErrorMessage());
                        setLeftStatusText(ERROR_EXECUTING);
                    }
                    else {
                        setResultSet(rset, q);
                    }

                }
                else {
                    int updateCount = result.getUpdateCount();
                    if (updateCount == -1) {
                        setOutputMessage(QueryEditorConstants.ERROR_MESSAGE,
                                result.getErrorMessage());
                        setLeftStatusText(ERROR_EXECUTING);
                    }
                    else {
                        panel.setResultText(updateCount, QuerySender.UNKNOWN);
                    }
                }
                end = System.currentTimeMillis();
                panel.addToHistory(q);
                return DONE;
            }
            
            int type = -1;
            isSelect = false;
            executing = true;
            String query = null;
            String tempQuery = q;
            String procQuery = tempQuery.toUpperCase();
            
            start = System.currentTimeMillis();

            // check if its a procedure creation or execution
            if (isCreateProcedureOrFunction(procQuery)) {

                logExecution(tempQuery.trim());
                SqlStatementResult result = qs.createProcedure(procQuery);

                if (result.getUpdateCount() == -1) {
                    setOutputMessage(QueryEditorConstants.ERROR_MESSAGE, result.getErrorMessage());
                    setLeftStatusText(ERROR_EXECUTING);
                }
                else {
                    if (isCreateProcedure(procQuery)) {
                        setResultText(result.getUpdateCount(), QuerySender.CREATE_PROCEDURE);
                    }
                    else if (isCreateFunction(procQuery)) {
                        setResultText(result.getUpdateCount(), QuerySender.CREATE_FUNCTION);
                    }
                }
                
                outputWarnings(result.getSqlWarning());
                panel.addToHistory(tempQuery);
                return DONE;
            }

//            StringTokenizer st = tokenizeQuery(tempQuery);
//            int tokens = st.countTokens();
            
            
            List<String> queries = tokenizeQuery(tempQuery);

            String _query = null;
            String returnQuery = null;

            start = System.currentTimeMillis();

            int count = 0;
            //while(st.hasMoreTokens()) {
            for (int i = 0, n = queries.size(); i < n; i++) {
                returnQuery = originalQueries.get(count);
                count++;
                //query = st.nextToken().trim();
                query = queries.get(i);
                
                _query = query.toUpperCase();
                type = qs.getQueryType(query);
                
                if (type != QuerySender.COMMIT && type != QuerySender.ROLLBACK) {
                    logExecution(query);
                }
                else {
                    if (type == QuerySender.COMMIT) {
                        setOutputMessage(
                                QueryEditorConstants.ACTION_MESSAGE,
                                COMMITTING_LAST);
                    } else if (type == QuerySender.ROLLBACK) {
                        setOutputMessage(
                                QueryEditorConstants.ACTION_MESSAGE,
                                ROLLINGBACK_LAST);                        
                    }
                }

                SqlStatementResult result = qs.executeQuery(type, query);

                if (statementCancelled || Thread.interrupted()) {
                    throw new InterruptedException();
                }

                if (result.isResultSet()) {
                    ResultSet rset = result.getResultSet();

                    if (rset == null) {
                        String message = result.getErrorMessage();
                        if (message == null) {
                            message = result.getMessage();
                            // if still null dump simple message
                            if (message == null) {
                                message = "A NULL result set was returned.";
                            }
                        }
                        setOutputMessage(QueryEditorConstants.ERROR_MESSAGE, 
                                         message);
                        setLeftStatusText(ERROR_EXECUTING);
                    }
                    else {
                        setResultSet(rset, returnQuery);
                    }

                }
                else {
                    
                    // check that we executed a 'normal' statement (not a proc)
                    if (result.getType() != QuerySender.EXECUTE) {
                        int updateCount = result.getUpdateCount();

                        if (updateCount == -1) {
                            setOutputMessage(QueryEditorConstants.ERROR_MESSAGE,
                                    result.getErrorMessage());
                            setLeftStatusText(ERROR_EXECUTING);
                        }
                        else {
                            type = result.getType();
                            setResultText(updateCount, type);

                            if (type == QuerySender.COMMIT || type == QuerySender.ROLLBACK) {
                                setLeftStatusText(" " + result.getMessage());
                            }

                        }
                    }
                    else {
                        Hashtable results = (Hashtable)result.getOtherResult();

                        if (results == null) {
                            setOutputMessage(QueryEditorConstants.ERROR_MESSAGE,
                                             result.getErrorMessage());
                            setLeftStatusText(ERROR_EXECUTING);
                        }
                        else {
                            setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE,
                                             "Call executed successfully.");
                            int updateCount = result.getUpdateCount();

                            if (updateCount > 0) {
                                setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE,
                                        updateCount + 
                                        ((updateCount > 1) ? 
                                            " rows affected." : " row affected."));
                            }

                            String SPACE = " = ";
                            Enumeration keys = results.keys();
                            while (keys.hasMoreElements()) {
                                String key = keys.nextElement().toString();
                                setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE,
                                                 key + SPACE + results.get(key));
                            }

                        }

                    }

                }

                /*
                if (count == tokens) {
                    panel.addToHistory(q);
                }
                */

            }

            panel.addToHistory(q);
            end = System.currentTimeMillis();
        }
        catch (SQLException e) {
            processException(e);
            return "SQLException";
        }
        catch (InterruptedException e) {
            //Log.debug("InterruptedException");
            statementCancelled = true; // make sure its set
            return "Interrupted";
        }
        catch (OutOfMemoryError e) {
            setOutputMessage(QueryEditorConstants.ERROR_MESSAGE, 
                    "Resources exhausted while executing query.\n"+
                    "The query result set was too large to return.");
            panel.setLeftStatusText(ERROR_EXECUTING);
        }
        catch (Exception e) {
            if (!statementCancelled) {
                e.printStackTrace();
                processException(e);
            }
        }
        finally {
            qs.releaseStatements();
            if (end == 0) {
                end = System.currentTimeMillis();
            }
            duration = MiscUtils.formatDuration(end - start);
        }

        return DONE;        
    }

    /**
     * Logs the specified query being executed.
     *
     * @param query - the executed query
     */
    private void logExecution(String query) {
        Log.info(EXECUTING_1 + query);
        if (verboseLogging) {
            setOutputMessage(
                    QueryEditorConstants.ACTION_MESSAGE, EXECUTING_1);
            setOutputMessage(
                    QueryEditorConstants.ACTION_MESSAGE_PREFORMAT, query);
        } else {
            int queryLength = query.length();
            int subIndex = queryLength < 50 ? (queryLength + 1) : 50;
            setOutputMessage(
                    QueryEditorConstants.ACTION_MESSAGE, EXECUTING_1);
            setOutputMessage(
                    QueryEditorConstants.ACTION_MESSAGE_PREFORMAT, 
                    query.substring(0, subIndex-1).trim() + SUBSTRING);
        }
    }
    
    private void processException(Throwable e) {
        setOutputMessage(QueryEditorConstants.ERROR_MESSAGE, e.getMessage());

        if (e instanceof SQLException) {
            SQLException sqlExc = (SQLException)e;
            sqlExc = sqlExc.getNextException();
            if (sqlExc != null) {
                setOutputMessage(QueryEditorConstants.ERROR_MESSAGE, sqlExc.getMessage());
            }
        }
        else {
            setLeftStatusText(ERROR_EXECUTING);
        }
    }
    
    private void setResultText(final int result, final int type) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                panel.setResultText(result, type);
            }
        });
    }
    
    private void setLeftStatusText(final String text) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                panel.setLeftStatusText(text);
            }
        });
    }
    
    private void setOutputMessage(final int type, final String text) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                panel.setOutputMessage(type, text);
                if (text != null) {
                    logOutput(text);
                }
            }
        });
    }

    private void setResultSet(final ResultSet rs, final String query) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    panel.setResultSet(rs, query);
                } catch (SQLException e) {
                    processException(e);
                }
            }
        });
    }
    
    /** matcher to remove new lines from log messages */
    private Matcher newLineMatcher;
    
    /**
     * Logs the specified text to the logger.
     *
     * @param text - the text to log
     */
    private void logOutput(String text) {
        if (logging) {
            newLineMatcher.reset(text);
            OutputLogger.append(LOGGER_NAME, newLineMatcher.replaceAll(" "));
        }
    }

    /**
     * Formats and prints to the output pane the specified warning.
     *
     * @param warning - the warning to be printed
     */
    private void outputWarnings(SQLWarning warning) {
        
        if (warning == null) {
            return;
        }
        
        String dash = " - ";
        // print the first warning
        setOutputMessage(QueryEditorConstants.WARNING_MESSAGE, 
                warning.getErrorCode() + dash + warning.getMessage());

        // retrieve subsequent warnings
        SQLWarning _warning = null;

        int errorCode = -1000;
        int _errorCode = warning.getErrorCode();
        
        while ((_warning = warning.getNextWarning()) != null) {
            errorCode = _warning.getErrorCode();

            if (errorCode == _errorCode) {
                return;
            }
            
            _errorCode = errorCode;
            setOutputMessage(QueryEditorConstants.WARNING_MESSAGE, 
                    _errorCode + dash + _warning.getMessage());
            warning = _warning;            
        }
        
    }

    /** Original single queries sent list */
    private List<String> originalQueries;
    
    /** Stored tokens */
    private List<Token> tokens;
    
    /**
     * Splits (tokenizes) the specified query block into multiple single 
     * queries using the semi-colon (;) character as the effective delimiter.
     * <p>This will store the queries in their original form in addition to
     * returning a list of individual queries formatted for execution by 
     * removing any multi-line and single-line comments from each query.
     *
     * @param query - the query block to be executed
     * @return a list of individual queries derived from the specified block
     */
    public List<String> tokenizeQuery(String query) {
        // init or clear the token cache
        if (tokens == null) {
            tokens = new ArrayList<Token>();
        } else {
            tokens.clear();
        }
        
        // make sure the matchers are initialised
        initMatchers();

        // store the multi-line comments position
        multiLineCommentMatcher.reset(query);
        while (multiLineCommentMatcher.find()) {
            tokens.add(new Token(TokenTypes.COMMENT,
                    multiLineCommentMatcher.start(), 
                    multiLineCommentMatcher.end()));
        }
        
        // store the single-line comments position
        singleLineCommentMatcher.reset(query);
        while (singleLineCommentMatcher.find()) {
            tokens.add(new Token(TokenTypes.SINGLE_LINE_COMMENT, 
                    singleLineCommentMatcher.start(), 
                    singleLineCommentMatcher.end()));
        }

        // store the quoted string positions
        quoteMatcher.reset(query);
        while (quoteMatcher.find()) {
            tokens.add(new Token(TokenTypes.STRING, 
                    quoteMatcher.start(), 
                    quoteMatcher.end()));
        }

        int index = 0;
        
        // store the delim (;) indexes
        List<Integer> delims = new ArrayList<Integer>();
        while ((index = query.indexOf(delim, index + 1)) != -1) {
            delims.add(new Integer(index));
        }

        int delimCount = delims.size();
        int tokenCount = tokens.size();
        if (tokenCount > 0 && delimCount > 0) {
            // loop through and remove the delim indexes that are 
            // either a comment (single or multi -line) or a quoted string
            for (int i = 0; i < delims.size(); i++) {
                index = delims.get(i).intValue();

                for (int j = 0; j < tokenCount; j++) {
                    Token token = tokens.get(j);
                    if (token.contains(index)) {
                        delims.remove(i);
                        i--;
                        break;
                    }
                }

            }
        }

        // -------------------------------------------------
        // first, store the queries in their original form

        // init the original queries list
        if (originalQueries == null) {
            originalQueries = new ArrayList<String>();
        } else {
            originalQueries.clear();
        }

        index = 0;
        delimCount = delims.size();
        if (delimCount > 0) {
            for (int i = 0; i < delimCount; i++) {
                int delimIndex = delims.get(i).intValue();
                originalQueries.add(query.substring(index, delimIndex));
                index = delimIndex + 1;
            }            
        }
        else {
            originalQueries.add(query);
        }
        
        // ---------------------------------------------
        // next, store the queries in execution form
        
        // remove all multi-line comments
        multiLineCommentMatcher.reset();
        String _query = multiLineCommentMatcher.replaceAll(Constants.EMPTY);
        
        // remove all the single-line comments
        singleLineCommentMatcher.reset(_query);
        _query = singleLineCommentMatcher.replaceAll(Constants.EMPTY);
        
        // rescan for quoted text
        tokens.clear();
        quoteMatcher.reset(_query);
        while (quoteMatcher.find()) {
            tokens.add(new Token(quoteMatcher.start(), quoteMatcher.end()));
        }
        
        // rescan for delims in absence of commented strings
        index = 0;
        delims.clear();
        while ((index = _query.indexOf(delim, index + 1)) != -1) {
            delims.add(new Integer(index));
        }

        delimCount = delims.size();
        tokenCount = tokens.size();
        if (tokenCount > 0 && delimCount > 0) {
            // loop through and remove the delim indexes that are 
            // either a comment (single or multi -line) or a quoted string
            for (int i = 0; i < delims.size(); i++) {
                index = delims.get(i).intValue();

                for (int j = 0; j < tokenCount; j++) {
                    Token token = tokens.get(j);
                    if (token.contains(index)) {
                        delims.remove(i);
                        i--;
                        break;
                    }
                }

            }
        }

        index = 0;
        delimCount = delims.size();
        List<String> queries = new ArrayList<String>(delimCount);
        if (delimCount > 0) {
            for (int i = 0; i < delimCount; i++) {
                int delimIndex = delims.get(i).intValue();
                trimNewLineMatcher.reset(_query.substring(index, delimIndex));
                System.out.println(_query);
                queries.add(trimNewLineMatcher.replaceAll(
                        Constants.NEW_LINE_STRING).trim());
                index = delimIndex + 1;
            }
        }
        else {
            trimNewLineMatcher.reset(_query);
            queries.add(trimNewLineMatcher.replaceAll(
                    Constants.NEW_LINE_STRING).trim());
        }

        return queries;
    }

/*
insert into dummy_table_2 (column1, column2) --jhdsfhs ; kjdshf
values (
'ABC12', 
'f_issueQC=true;f_articleQC=true;f_pdfQC=true;f_hasPDFs=true');

insert into dummy_table_2 (column1, column2) --jhdsfhs ; kjdshf
values (

/* kkjdhgjhdgj 
jshdkjfs; h * /
'ABC12', 
'f_issueQC=true;f_articleQC=true;f_pdfQC=true;f_hasPDFs=true');
*/

    
    /**
     * Closes the current connection.
     */
    public void destroyConnection() {
        if (qs != null) {
            try {
                qs.destroyConnection();
            } catch (SQLException e) {}
        }
    }
    
    /**
     * Dtermines whether the specified query is attempting
     * to create a SQL PROCEDURE or FUNCTION.
     *
     * @param query - the query to be executed
     * @return true | false
     */
    private boolean isCreateProcedureOrFunction(String query) {
        return isCreateProcedure(query) || isCreateFunction(query);
    }

    /**
     * Dtermines whether the specified query is attempting
     * to create a SQL PROCEDURE.
     *
     * @param query - the query to be executed
     * @return true | false
     */
    private boolean isCreateProcedure(String query) {
        int createIndex = query.indexOf("CREATE");
        int tableIndex = query.indexOf("TABLE");
        int procedureIndex = query.indexOf("PROCEDURE");
        int packageIndex = query.indexOf("PACKAGE");
        return createIndex != -1 && tableIndex == -1 &&
                (procedureIndex > createIndex || packageIndex > createIndex);
    }
    
    /**
     * Dtermines whether the specified query is attempting
     * to create a SQL FUNCTION.
     *
     * @param query - the query to be executed
     * @return true | false
     */
    private boolean isCreateFunction(String query) {
        int createIndex = query.indexOf("CREATE");
        int tableIndex = query.indexOf("TABLE");
        int functionIndex = query.indexOf("FUNCTION");
        return createIndex != -1 && 
               tableIndex == -1 && 
               functionIndex > createIndex;
    }
    
}