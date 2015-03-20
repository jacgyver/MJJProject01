/*
 * QueryEditorResultsPanel.java
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.executequery.GUIUtilities;

import org.underworldlabs.util.SystemProperties;
import org.executequery.databasemediators.QuerySender;
import org.underworldlabs.swing.SimpleCloseTabbedPane;
import org.underworldlabs.swing.plaf.TabRollOverListener;
import org.underworldlabs.swing.plaf.TabRolloverEvent;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The Query Editor's results panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.10 $
 * @date     $Date: 2006/09/06 09:30:58 $ 
 */
public class QueryEditorResultsPanel extends SimpleCloseTabbedPane
                                     implements TabRollOverListener,
                                                ChangeListener {

    /** the editor parent */
    private QueryEditor queryEditor;
    
    /** the text output message pane */
    private QueryEditorOutputPane outputTextPane;

    private JViewport outputTextViewport;
    
    private boolean showRowHeader;
    
    /** Whether the current execute is a single */
    private boolean executingSingle;
    
    /** Result table list */
    private List<ResultSetPanel> resultSetPanels;

    /** Result table available temp list */
    private List<ResultSetPanel> resultSetPanelsAvail;

    /** the result tab icon */
    private Icon resultTabIcon;

    /** the text output tab icon */
    private Icon outputTabIcon;

    /** the text output scroll pane */
    private JScrollPane textOutputScroller;

    
    private static final String SUCCESS = " Statement executed successfully";
    private static final String NO_ROWS = "No rows selected";
    private static final String ZERO_ROWS = " 0 rows returned";
    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String ROW_RETURNED = " row returned";
    private static final String ROWS_RETURNED = " rows returned";
    private static final String ONE_LINE = "\n";
    private static final String TWO_LINES = "\n\n";
    
    
    public QueryEditorResultsPanel(QueryEditor queryEditor) {
        this(queryEditor, null);        
    }
    
    public QueryEditorResultsPanel() {
        this(null, null);
    }
    
    public QueryEditorResultsPanel(ResultSet rs) {
        this(null, rs);
    }

    public QueryEditorResultsPanel(QueryEditor queryEditor, ResultSet rs) {
        super(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
        //setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.queryEditor = queryEditor;
        if (queryEditor != null) {
            addTabRollOverListener(this);
        }

        try {
            jbInit();
            /*
            if (rs != null) {
                setTableResultsCompare(rs);
            }
             */
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() {
        outputTextPane = new QueryEditorOutputPane();
        
        outputTextPane.setMargin(new Insets(5, 5, 5, 5));
        outputTextPane.setDisabledTextColor(Color.black);
        
        Color bg = SystemProperties.getColourProperty("user",
                                        "editor.results.background.colour");
        outputTextPane.setBackground(bg);
        
        textOutputScroller = new JScrollPane(
                                    outputTextPane,
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textOutputScroller.setBackground(bg);
        textOutputScroller.setBorder(null);
        outputTextViewport = textOutputScroller.getViewport();
        outputTextViewport.setBackground(bg);
        
        addTextOutputTab();

        //setPreferredSize(QueryEditorConstants.PANEL_SIZE);
        
        if (queryEditor == null) { // editor calls this also
            setTableProperties();
        }
        addChangeListener(this);
    }
    
    public void cleanup() {
        queryEditor = null;
        destroyTable();
        if (resultSetPanels != null) {
            resultSetPanels.clear();
        }
        if (resultSetPanelsAvail != null) {
            resultSetPanelsAvail.clear();
        }
    }
    
    private void addTextOutputTab() {
        if (outputTabIcon == null) {
            outputTabIcon = GUIUtilities.loadIcon("SystemOutput.gif", true);
        }
        addTab("Output", outputTabIcon, textOutputScroller, "Database output");
    }
    
    /**
     * Sets the user defined (preferences) table properties.
     */
    public void setTableProperties() {
        Component[] tabs = getComponents();
        for (int i = 0; i < tabs.length; i++) {
            Component c = tabs[i];
            if (c instanceof ResultSetPanel) {
                ResultSetPanel panel = (ResultSetPanel)c;
                panel.setTableProperties();
            }            
        }
    }
    
    public int getResultSetTabCount() {
        return resultSetPanels == null ? 0 : resultSetPanels.size();
    }

    public boolean hasOutputPane() {
        return getResultSetTabCount() == (getTabCount() - 1);
    }
    
    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e - the event object
     */
    public void stateChanged(ChangeEvent e) {
        int panelIndex = getSelectedIndex() - 1;
        if (panelIndex >= 0 && 
                resultSetPanels != null && 
                resultSetPanels.size() > panelIndex) {
            int rowCount = resultSetPanels.get(panelIndex).getRowCount();
            resetEditorRowCount(rowCount);
        }
    }

    /*
    public void setTableResultsCompare(ResultSet rset)
      throws SQLException {
        if (rset == null) Log.debug("rs null in table");
        RSTableModel rsModel = new RSTableModel(rset);
        int rowCount = rsModel.getRowCount();
        
        if (rowCount == 0) {
            //setErrorMessage("No row discrepancies found");
        }
        
        else {
            hasResultSet = true;            
            sorter = new TableSorter(rsModel);
            
            if (tableView == null) {
                tableView = new QueryEditorResultsTable(sorter);
            } else {
                tableView.setModel(sorter);
            }

            sorter.setTableHeader(tableView.getTableHeader());
            tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            setTableProperties();
            
            resultSetScroller.setRowHeader(jvp);
            resultSetScroller.getViewport().add(tableView);
        }
    }
    */
    
    /**
     * Indicates whether the current model displayed has 
     * retained the ResultSetMetaData.
     *
     * @return true | false
     */
    public boolean hasResultSetMetaData() {
        ResultSetPanel panel = getSelectedResultSetPanel();
        if (panel != null) {
            return panel.hasResultSetMetaData();
        }
        return false;
    }
    
    public void interrupt() {
        Component[] tabs = getComponents();
        for (int i = 0; i < tabs.length; i++) {
            Component c = tabs[i];
            if (c instanceof ResultSetPanel) {
                ResultSetPanel panel = (ResultSetPanel)c;
                panel.interrupt();
            }
        }
    }

    /**
     * Sets the result set object.
     *
     * @param the executed result set
     * @param whether to return the result set row count
     */
    public int setResultSet(ResultSet rset, boolean showRowNumber)
        throws SQLException{
        return setResultSet(rset, showRowNumber, null);
    }

    /**
     * Sets the result set object.
     *
     * @param the executed result set
     * @param whether to return the result set row count
     * @param the executed query of the result set
     */
    public int setResultSet(ResultSet rset, boolean showRowNumber, String query)
        throws SQLException{

        if (resultSetPanels == null) {
            resultSetPanels = new ArrayList<ResultSetPanel>();
        }
        
        int rowCount = 0;
        ResultSetTableModel model = null;
        ResultSetPanel panel = null;

        // if this is a single execute add a new tab
        if (executingSingle) {
            model = new ResultSetTableModel(rset);
            rowCount = getResultSetRowCount(model, showRowNumber);
            if (rowCount == 0) {
                return rowCount;
            }
            panel = new ResultSetPanel();
            panel.setResultSet(model, showRowNumber);
        }
        else { // recycle the panels

            // if the avail cache is empty - add a new one
            if (resultSetPanelsAvail.isEmpty()) {
                model = new ResultSetTableModel(rset);
                panel = new ResultSetPanel();
                panel.setResultSet(model, showRowNumber);
            }
            else {
                // get the first panel in the list
                panel = resultSetPanelsAvail.get(0);

                // remove from the temp list
                resultSetPanelsAvail.remove(panel);
                
                // reset the model
                model = panel.getResultSetTableModel();
                model.createTable(rset);
                panel.tableDataChanged();
            }

            rowCount = getResultSetRowCount(model, showRowNumber);
        }
        
        // check if we have row count > 0
        if (rowCount == 0) {
            // add to the temp list
            resultSetPanelsAvail.add(panel);
            resetEditorRowCount(rowCount);
            return rowCount;
        }
        
        // add to the current display list
        resultSetPanels.add(panel);
        
        if (resultTabIcon == null) {
            resultTabIcon = GUIUtilities.loadIcon("FrameIcon16.gif", true);
        }

        // add to this tab pane and select
        addTab("Result Set " + (resultSetPanels.indexOf(panel) + 1), 
               resultTabIcon,
               panel, 
               query);
        setSelectedComponent(panel);
        
        // setup the editor
        queryEditor.setMetaDataButtonEnabled(true);
        resetEditorRowCount(rowCount);
        queryEditor.setExportButtonEnabled(true);

        return rowCount;
    }
    
    /**
     * Sets the returned rows status text using the specified row count.
     *
     * @param rowCount - the result set row count
     */
    private void resetEditorRowCount(int rowCount) {
        if (rowCount > 1) {
            queryEditor.setLeftStatusText(SPACE + rowCount + ROWS_RETURNED);
        } 
        else if (rowCount == 1) {
            queryEditor.setLeftStatusText(SPACE + rowCount + ROW_RETURNED);
        }
        else {
            queryEditor.setLeftStatusText(ZERO_ROWS);
        }
    }
    
    private int getResultSetRowCount(ResultSetTableModel model, boolean showRowNumber) {
        int rowCount = model.getRowCount();
        if (rowCount == 0) {
            if (showRowNumber) {
                setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE, NO_ROWS);
                resetEditorRowCount(rowCount);
                queryEditor.setMetaDataButtonEnabled(false);
            }
        }
        return rowCount;
    }
    
    public void setResultText(int result, int type) {
        if (getTabCount() == 0) {
            addTextOutputTab();
        }
        setSelectedIndex(0);

        String row = " row ";
        if (result > 1 || result == 0) {
            row = " rows ";
        }
        
        String rText = null;
        switch (type) {
            case QuerySender.INSERT:
                rText = row + "created.";
                break;
            case QuerySender.UPDATE:
                rText = row + "updated.";
                break;
            case QuerySender.DELETE:
                rText = row + "deleted.";
                break;
            case QuerySender.DROP_TABLE:
                rText = "Table dropped.";
                break;
            case QuerySender.CREATE_TABLE:
                rText = "Table created.";
                break;
            case QuerySender.ALTER_TABLE:
                rText = "Table altered.";
                break;
            case QuerySender.CREATE_SEQUENCE:
                rText = "Sequence created.";
                break;
            case QuerySender.CREATE_PROCEDURE:
                rText = "Procedure created.";
                break;
            case QuerySender.CREATE_FUNCTION:
                rText = "Function created.";
                break;
            case QuerySender.GRANT:
                rText = "Grant succeeded.";
                break;
            case QuerySender.CREATE_SYNONYM:
                rText = "Synonym created.";
                break;
            case QuerySender.COMMIT:
                rText = "Commit complete.";
                break;
            case QuerySender.ROLLBACK:
                rText = "Rollback complete.";
                break;
            case QuerySender.UNKNOWN:
            case QuerySender.EXECUTE:
                if (result > -1) {
                    rText = result + row + "affected.\nStatement executed successfully.";
                }
                else {
                    rText = "Statement executed successfully.";
                }
                break;
        }

        StringBuffer sb = new StringBuffer(50);

        if ((result > -1 && type >= QuerySender.ALL_UPDATES) 
                && type != QuerySender.UNKNOWN) {
            sb.append(result);
        }

        sb.append(rText);
        setOutputMessage(QueryEditorConstants.PLAIN_MESSAGE, sb.toString());
        queryEditor.setLeftStatusText(SUCCESS);
    }
    
    public void setResultBackground(Color colour) {
        outputTextPane.setBackground(colour);
        
        Component[] tabs = getComponents();
        for (int i = 0; i < tabs.length; i++) {
            Component c = tabs[i];
            if (c instanceof ResultSetPanel) {
                ResultSetPanel panel = (ResultSetPanel)c;
                panel.setResultBackground(colour);
            }
        }
    }

    public void destroyTable() {
        Component[] tabs = getComponents();
        for (int i = 0; i < tabs.length; i++) {
            Component c = tabs[i];
            if (c instanceof ResultSetPanel) {
                ResultSetPanel panel = (ResultSetPanel)c;
                panel.destroyTable();
            }
        }
    }
    
    private ResultSetPanel getSelectedResultSetPanel() {
        int selectedIndex = getSelectedIndex();
        if (selectedIndex <= 0) {
            return null;
        }

        Component c = getComponentAt(selectedIndex);
        if (c instanceof ResultSetPanel) {
            return (ResultSetPanel)c;
        }
        return null;
    }
    
    /**
     * Returns whether a result set panel is selected and that
     * that panel has a result set row count > 0.
     *
     * @return true | false
     */
    public boolean isResultSetSelected() {
        ResultSetPanel panel = getSelectedResultSetPanel();
        if (panel != null) {
            return panel.getRowCount() > 0;
        }
        return false;
    }
    
    public JTable getResultsTable() {
        ResultSetPanel panel = getSelectedResultSetPanel();
        if (panel != null) {
            return panel.getResultsTable();
        }
        return null;
    }
    
    public ResultSetTableModel getResultSetTableModel() {
        ResultSetPanel panel = getSelectedResultSetPanel();
        if (panel != null) {
            return panel.getResultSetTableModel();
        }
        return null;
    }

    public void setWarningMessage(String s) {
        appendOutput(QueryEditorConstants.WARNING_MESSAGE, s);
    }

    public void setPlainMessage(String s) {
        appendOutput(QueryEditorConstants.PLAIN_MESSAGE, s);
    }

    public void setActionMessage(String s) {
        appendOutput(QueryEditorConstants.ACTION_MESSAGE, s);
    }
    
    public void setErrorMessage(String s) {
        if (getTabCount() == 0) {
            addTextOutputTab();
        }

        setSelectedIndex(0);
        if (!MiscUtils.isNull(s)) {
            appendOutput(QueryEditorConstants.ERROR_MESSAGE, s);
        }
        if (queryEditor != null) {
            queryEditor.setExportButtonEnabled(false);
            queryEditor.setMetaDataButtonEnabled(false);
        }
    }

    public void setOutputMessage(int type, String text) {
        if (getTabCount() == 0) {
            addTextOutputTab();
        }

        setSelectedIndex(0);
        if (!MiscUtils.isNull(text)) {            
            appendOutput(type, text);
        }
        if (queryEditor != null) {
            queryEditor.setExportButtonEnabled(false);
            queryEditor.setMetaDataButtonEnabled(false);
        }
    }
    
    protected void appendOutput(int type, String text) {
        outputTextPane.append(type, text);
        outputTextPane.setCaretPosition(outputTextPane.getDocument().getLength());
        //outputTextViewport.validate();
    }
    
    public void clearOutputPane() {
        outputTextPane.setText(EMPTY);
        outputTextPane.setCaretPosition(0);
    }

    /**
     * Indicates the current execute has completed to
     * clear the temp panel availability cache.
     */
    public void finished() {
        if (resultSetPanelsAvail != null) {
            resultSetPanelsAvail.clear();
        }
    }

    /**
     * Sets to display the result set meta data for the
     * currently selected result set tab.
     */
    public void displayResultSetMetaData() {
        ResultSetPanel panel = getSelectedResultSetPanel();
        if (panel.hasResultSetMetaData()) {
            int index = getSelectedIndex();
            ResultSetMetaDataPanel metaDataPanel = 
                    panel.getResultSetMetaDataPanel();
            
            // check if the meta data is already displayed
            // at the index next to the result panel
            if (index != getTabCount() - 1) {
                Component c = getComponentAt(index + 1);
                if (c == metaDataPanel) {
                    setSelectedIndex(index + 1);
                    return;
                }
            }

            // otherwise add it
            insertTab(ResultSetMetaDataPanel.TITLE,
                      GUIUtilities.loadIcon("RSMetaData16.gif", true),
                      metaDataPanel,
                      getToolTipTextAt(index),
                      index + 1);
            setSelectedIndex(index + 1);
        }
    }

    /**
     * Indicates whether the current execute is a 
     * single statement - usually an execute at cursor.
     *
     * @return true | false
     */
    public boolean isExecutingSingle() {
        return executingSingle;
    }

    /**
     * Sets that the current execute may be a single.
     * 
     * @param true | false
     */
    public void setExecutingSingle(boolean executingSingle) {
        this.executingSingle = executingSingle;
        if (!executingSingle) {
            if (resultSetPanelsAvail == null) {
                resultSetPanelsAvail = new ArrayList<ResultSetPanel>();
            } else {
                resultSetPanelsAvail.clear();
            }
            
            // temp cache the result panels for use in the current query(ies)
            if (resultSetPanels == null || resultSetPanels.isEmpty()) {
                return;
            }
            
            for (int i = 0, k = resultSetPanels.size(); i < k; i++) {
                resultSetPanelsAvail.add(resultSetPanels.get(i));
            }

            // reset the tab pane
            removeAll();

            // clear the current panels
            resultSetPanels.clear();
        }
        
        // make sure the output tab is there
        if (getTabCount() == 0) {
            addTextOutputTab();
        }
        
    }
    
    /**
     * Moves the caret to the beginning of the specified query.
     *
     * @param query - the query to move the cursor to
     */
    public void caretToQuery(String query) {
        queryEditor.caretToQuery(query);
    }
    
    /** the query display popup */
    private static QueryTextPopup queryPopup;
    
    /** last popup rollover index */
    private int lastRolloverIndex = -1;
    
    /**
     * Returns the result set's query at the specified index.
     *
     * @param index - the result set index
     * @return the query string
     */
    public String getQueryTextAt(int index) {
        return getToolTipTextAt(index);
    }
    
    /**
     * Reacts to a tab rollover.
     *
     * @param the associated event
     */
    public void tabRollOver(TabRolloverEvent e) {
        int index = e.getIndex();
        
        // check if we're over the output panel (index 0)
        if (index == 0 && hasOutputPane()) {
            lastRolloverIndex = index;
            if (queryPopup != null && queryPopup.isVisible()) {
                queryPopup.dispose();
            }
            return;
        }
        
        if (queryPopup != null && 
                queryPopup.isVisible() && lastRolloverIndex == index) {
            return;
        }

        if (index != -1) {
            String query = getToolTipTextAt(index);
            if (!MiscUtils.isNull(query)) {
                if (queryPopup == null) {
                    queryPopup = new QueryTextPopup(this);
                }
                lastRolloverIndex = index;
                queryPopup.setQueryText(e.getX(), e.getY(), query, index);
            }
        }
    }
    
    /**
     * Reacts to a tab rollover finishing.
     *
     * @param the associated event
     */
    public void tabRollOverFinished(TabRolloverEvent e) {
        int index = e.getIndex();
        if (index == -1) {
            lastRolloverIndex = index;
            if (queryPopup != null) {
                queryPopup.dispose();
            }
        }
    }
    
}




