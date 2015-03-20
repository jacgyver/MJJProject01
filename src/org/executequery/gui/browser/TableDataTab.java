/*
 * TableDataTab.java
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


package org.executequery.gui.browser;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.underworldlabs.util.SystemProperties;
import org.executequery.databasemediators.QuerySender;
import org.executequery.GUIUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.SqlStatementResult;
import org.executequery.gui.editor.ResultSetTableModel;
import org.executequery.gui.editor.QueryEditorResultsTable;
import org.executequery.util.Log;
import org.underworldlabs.swing.util.SwingWorker;
import org.underworldlabs.swing.table.TableSorter;
import org.underworldlabs.swing.DisabledField;

// BrowserPanel table data tab
/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.9 $
 * @date     $Date: 2006/08/11 12:34:50 $
 */
public class TableDataTab extends JPanel {
    
    /** Utility to return table results */
    private QuerySender querySender;
    
    /** The results table model */
    private ResultSetTableModel tableModel;
    
    /** The results table */
    private QueryEditorResultsTable tableView;
    
    /** The scroll pane containing the table */
    private JScrollPane scroller;
    
    /** Whether this class is currently executing a query */
    private boolean executing;
    
    /** The <code>String</code>literal 'SELECT * FROM ' */
    private static final String QUERY = "SELECT * FROM ";

    /** <p>Constructs a new instance. */
    public TableDataTab() {
        super(new GridBagLayout());
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /** <p>Initialises the state of this instance. */
    private void jbInit() throws Exception {
        executing = false;
        
        // create the QuerySender
        querySender = new QuerySender();
        scroller = new JScrollPane();

        //JPanel basePanel new JPanel(new GridBagLayout());

        add(scroller, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                GridBagConstraints.SOUTHEAST, 
                                GridBagConstraints.BOTH,
                                new Insets(5, 5, 5, 5), 0, 0));
    }
    
    /** <p>Executes the SQL SELECT query on the specified
     *  table using a <code>SwingWorker</code> thread.
     *
     *  @param the table name
     */
    public void getTableData(final DatabaseConnection dc,
                             final DatabaseObject metaObject) {

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    executing = true;
                    showWaitCursor();
                    return setTableResultsPanel(dc, metaObject);
                }
                catch (Exception e) {
                    GUIUtilities.displayExceptionErrorDialog(
                                        "An error occured retrieving the table data.\n" + 
                                        e.getMessage(), e);
                    return "done";
                }
            }
            public void finished() {
                executing = false;
                querySender.releaseResources();
                showNormalCursor();
            }
        };        
        worker.start();
    }
    
    public void cleanup() {
        if (querySender != null) {
            querySender.releaseResources();
        }
    }
    
    /**
     * Sets the table data from the specified result set.
     *
     * @param rset - the data result set
     */
    /*
    public void setTableData(ResultSet rset) {
        
        try {
            if (rset != null) {

                if (tableModel == null) {
                    tableModel = new ResultSetTableModel(
                                      SystemProperties.getIntProperty(
                                            "user", "browser.max.records"));
                    tableModel.setHoldMetaData(false);
                }
                tableModel.createTable(rset);

                if (tableView == null) {
                    tableView = new QueryEditorResultsTable();
                    setTableProperties();
                }

                TableSorter sorter = new TableSorter(tableModel);
                tableView.setModel(sorter);
                sorter.setTableHeader(tableView.getTableHeader());

                tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);                
                scroller.getViewport().add(tableView);
            }
        }
        catch (SQLException e) {
            GUIUtilities.displayExceptionErrorDialog("Error retrieving table data.", e);
        }
        
        validate();
        repaint();
    }
    */
    
    /**
     * Contsructs and displays the specified <code>ResultSet</code> 
     * object within the results table.
     *
     * @param the <code>ResultSet</code> data object
     * @return the <code>String</code> 'done' when finished
     */
    private Object setTableResultsPanel(DatabaseConnection dc, 
                                        DatabaseObject metaObject) {

        try {
            String tableName = metaObject.getSchemaName() + '.' + 
                                    metaObject.getName();
            
            // retrieve the row data
            querySender.setDatabaseConnection(dc);
            SqlStatementResult  result = 
                    querySender.getResultSet(QUERY + tableName);

            if (result.isResultSet()) {

                if (tableModel == null) {
                    tableModel = new ResultSetTableModel(
                                      SystemProperties.getIntProperty(
                                            "user", "browser.max.records"));
                    tableModel.setHoldMetaData(false);
                }
                
                ResultSet rset = result.getResultSet();
                tableModel.createTable(rset);
                int rowCount = tableModel.getRowCount();

                if (tableView == null) {
                    tableView = new QueryEditorResultsTable();
                    setTableProperties();
                }

                TableSorter sorter = new TableSorter(tableModel);
                tableView.setModel(sorter);
                sorter.setTableHeader(tableView.getTableHeader());

                tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                
                scroller.getViewport().add(tableView);
            }

        }
        catch (SQLException e) {
            GUIUtilities.displayExceptionErrorDialog("Error retrieving table data.", e);
        }
        
        validate();
        repaint();
        return "done";
        
    }
    
    /** <p>Whether a SQL SELECT statement is currently
     *  being executed by this class.
     *
     *  @return <code>true</code> if executing,
     *          <code>false</code> otherwise.
     */
    public boolean isExecuting() {
        return executing;
    }
    
    /** <p>Cancels the currently executing statement. */
    public void cancelStatement() {
        querySender.cancelCurrentStatement();
    }
    
    /** <p>Sets default table display properties. */
    public void setTableProperties() {
        
        if (tableView == null) {
            return;
        }
        
        tableView.setDragEnabled(true);
        
        tableModel.setMaxRecords(SystemProperties.getIntProperty("user", "browser.max.records"));
        
        tableView.setBackground(
        SystemProperties.getColourProperty("user", "results.table.cell.background.colour"));
        
        tableView.setRowHeight(
        SystemProperties.getIntProperty("user", "results.table.column.height"));
        
        int colWidth = SystemProperties.getIntProperty("user", "results.table.column.width");
        TableColumnModel tcm = tableView.getColumnModel();
        
        tableView.setRowSelectionAllowed(
        SystemProperties.getBooleanProperty("user", "results.table.row.select"));
        
        tableView.getTableHeader().setResizingAllowed(
        SystemProperties.getBooleanProperty("user", "results.table.column.resize"));
        
        tableView.getTableHeader().setReorderingAllowed(
        SystemProperties.getBooleanProperty("user", "results.table.column.reorder"));
        
        if (colWidth != 75) {
            TableColumn col = null;
            for (Enumeration i = tcm.getColumns(); i.hasMoreElements();) {
                col = (TableColumn)i.nextElement();
                col.setPreferredWidth(colWidth);
            }
        }
        
    }
    
    /** <p>Sets this panel's cursor to the system normal cursor */
    private void showNormalCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /** <p>Sets this panel's cursor to the system wait cursor */
    private void showWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    public JTable getTable() {
        return tableView;
    }
    
}






