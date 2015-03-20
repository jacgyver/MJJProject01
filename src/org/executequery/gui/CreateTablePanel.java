/*
 * CreateTablePanel.java
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


package org.executequery.gui;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import java.util.Vector;
import org.executequery.components.BottomButtonPanel;

import org.executequery.databasemediators.*;
import org.executequery.gui.table.*;
import org.executequery.GUIUtilities;
import org.executequery.ActiveComponent;
import org.executequery.EventMediator;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>The Create Panel function */
/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/09/13 14:51:12 $
 */
public class CreateTablePanel extends CreateTableFunctionPanel
                              implements ActionListener,
                                         KeywordListener,
                                         ActiveComponent {
    
    /** This objects title as an internal frame */
    public static final String TITLE = "Create Table";
    
    /** This objects icon as an internal frame */
    public static final String FRAME_ICON = "NewTable16.gif";
    
    /** the parent container */
    private ActionContainer parent;
    
    /** <p> Constructs a new instance. */
    public CreateTablePanel(ActionContainer parent) {
        super();
        this.parent = parent;
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
        setFocusComponent();
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        addButtonsPanel(new BottomButtonPanel(
                                this, "Create", "create-table", parent.isDialog()));
        setPreferredSize(new Dimension(650,480));
        EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
    }
    
    /**
     * Indicates that a [long-running] process has begun or ended
     * as specified. This may trigger the glass pane on or off 
     * or set the cursor appropriately.
     *
     * @param inProcess - true | false
     */
    public void setInProcess(boolean inProcess) {
        if (parent != null) {
            parent.setInProcess(inProcess);
        }
    }

    /**
     * Notification of a new keyword added to the list.
     */
    public void keywordsAdded(KeywordEvent e) {
        sqlText.setSQLKeywords(true);
    }

    /**
     * Notification of a keyword removed from the list.
     */
    public void keywordsRemoved(KeywordEvent e) {
        sqlText.setSQLKeywords(true);
    }

    public Vector<String> getHostedSchemasVector() {
        try {
            return metaData.getHostedSchemasVector();
        }
        catch (DataSourceException e) {
            GUIUtilities.displayExceptionErrorDialog(
                    "Error retrieving the catalog/schema list for the " +
                    "selected connection.\n\nThe system returned:\n" + 
                    e.getExtendedMessage(), e);
            return new Vector<String>(0);
        }
    }
    
    public Vector<String> getSchemaTables(String schemaName) {
        try {
            return metaData.getSchemaTables(schemaName);
        }
        catch (DataSourceException e) {
            GUIUtilities.displayExceptionErrorDialog(
                    "Error retrieving the table list for the " +
                    "selected catalog/schema.\n\nThe system returned:\n" + 
                    e.getExtendedMessage(), e);
            return new Vector<String>(0);
        }
    }

    public Vector<String> getColumnNamesVector(String tableName, String schemaName) {
        try {
            return metaData.getColumnNamesVector(tableName, schemaName);
        }
        catch (DataSourceException e) {
            GUIUtilities.displayExceptionErrorDialog(
                    "Error retrieving the column names for the " +
                    "selected table.\n\nThe system returned:\n" + 
                    e.getExtendedMessage(), e);
            return new Vector<String>(0);
        }
    }

    /**
     * Releases database resources before closing.
     */
    public void cleanup() {
        EventMediator.deregisterListener(EventMediator.KEYWORD_EVENT, this);
        metaData.closeConnection();
    }
    
    /**
     * Action listener implementation.<br>
     * Executes the create table script.
     *
     * @param the event
     */
    public void actionPerformed(ActionEvent e) {

        DatabaseConnection dc = getSelectedConnection();
        if (dc == null) {
            GUIUtilities.displayErrorMessage(
                    "No database connection is available.");
            return;
        }

        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {
                    setInProcess(true);
                    createTable();
                }
                finally {
                    setInProcess(false);
                }
            }
        });

    }

    private void createTable() {
        GUIUtils.startWorker(new Runnable() {
            public void run() {        
                try {
                    String query = getSQLText();
                    if (query.endsWith(";")) {
                        query = query.substring(0, query.length() - 1);
                    }

                    DatabaseConnection dc = getSelectedConnection();
                    QuerySender qs = new QuerySender(dc);
                    SqlStatementResult result = qs.updateRecords(query);

                    if (result.getUpdateCount() >= 0) {
                        GUIUtilities.displayInformationMessage(
                                "Table " + getTableName() + " created.");
                        parent.finished();
                    }
                    else {
                        SQLException exc = result.getSqlException();
                        if (exc != null) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("An error occurred creating the specified table.").
                               append("\n\nThe system returned:\n").
                               append(MiscUtils.formatSQLError(exc));
                            GUIUtilities.displayExceptionErrorDialog(sb.toString(), exc);
                        } else {
                            GUIUtilities.displayErrorMessage(result.getErrorMessage());
                        }
                    }

                }
                catch (Exception exc) {
                    GUIUtilities.displayExceptionErrorDialog("Error:\n" + exc.getMessage(), exc);
                }
            }
        });

    }
    
    public String toString() {
        return TITLE;
    }
    
}







