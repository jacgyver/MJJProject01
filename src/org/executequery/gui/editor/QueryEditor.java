/*
 * QueryEditor.java
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.print.Printable;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.File;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.text.JTextComponent;
import org.executequery.Constants;
import org.executequery.EventMediator;

import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.executequery.SystemUtilities;
import org.executequery.base.DefaultTabView;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.gui.*;
import org.executequery.gui.text.TextEditor;
import org.executequery.gui.text.TextFileWriter;
import org.executequery.databasemediators.DatabaseResourceHolder;
import org.executequery.datasource.ConnectionManager;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;
import org.executequery.print.TablePrinter;
import org.executequery.print.TextPrinter;
import org.underworldlabs.util.MiscUtils;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The Query Editor.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/06/07 15:04:19 $
 */
public class QueryEditor extends DefaultTabView
                         implements ConnectionListener,
                                    TextEditor,
                                    KeywordListener,
                                    DatabaseResourceHolder,
                                    FocusablePanel {

    public static final String TITLE = "Query Editor";
    public static final String FRAME_ICON = "Edit16.gif";

    /** editor open count for title numbering */
    private static int editorCountSequence = 1;
    
    /** The editor's status bar */
    private QueryEditorStatusBar statusBar;
    
    /** The editor's text pan panel */
    private QueryEditorTextPanel editor;
    
    /** The editor's results panel */
    private QueryEditorResultsPanel resultsPanel;
    
    /** Whether a file has been opened in the editor */
    private boolean openFile = false;
    
    /** The open file's path */
    private String absolutePath;

    /** The open file's name */
    private String fileName;

    /** the default file */
    private String defaultFileName;
    
    /** flags the content as having being changed */
    private boolean contentChanged;
    
    /** Whether the set printable is text - not the table */
    private boolean printingText;
    
    /** Whether result set meta data is to
     *  be retained for executed queries */
    private boolean holdMetaData;
    
    /** the editor's tool bar */
    private QueryEditorToolBar toolBar;
    
    /** the active connections combo box model */
    private DynamicComboBoxModel connectionsModel;
    
    /** the active connections combo */
    private JComboBox connectionsCombo;
    
    /** the result pane base panel */
    private JPanel resultsBase;
    
    /** the editor split pane */
    private JSplitPane splitPane;
    
    /** 
     * Constructs a new instance. 
     */
    public QueryEditor() {
        this(null, null);
    }

    /**
     * Creates a new query editor with the specified text content.
     *
     * @param the text content to be set
     */
    public QueryEditor(String text) {
        this(text, null);
    }

    /**
     * Creates a new query editor with the specified text content
     * and the specified absolute file path.
     *
     * @param the text content to be set
     * @param the absolute file path;
     */
    public QueryEditor(String text, String absolutePath) {
        super(new GridBagLayout());
        printingText = true;

        // set the open file value
        setAbsoluteFile(absolutePath);

        try  {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        defaultFileName = "script" + (editorCountSequence++) + ".sql";

        if (text != null) {
            loadText(text);
            //setEditorText(text, true);
        }
        contentChanged = false;
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        // construct the two query text area and results panels
        statusBar = new QueryEditorStatusBar();
        statusBar.setBorder(BorderFactory.createEmptyBorder(2,-1,-2,-2));

        editor = new QueryEditorTextPanel(this);
        resultsPanel = new QueryEditorResultsPanel(this);

        JPanel baseEditorPanel = new JPanel(new BorderLayout());
        baseEditorPanel.add(editor, BorderLayout.CENTER);
        baseEditorPanel.add(statusBar, BorderLayout.SOUTH);
        baseEditorPanel.setBorder(BorderFactory.createMatteBorder(
                                1, 1, 1, 1, GUIUtilities.getDefaultBorderColour()));
        
        // add to a base panel - when last tab closed visible set 
        // to false on the tab pane and split collapses - want to avoid this
        resultsBase = new JPanel(new BorderLayout());
        resultsBase.add(resultsPanel, BorderLayout.CENTER);
        
        if (GUIUtilities.getLookAndFeel() < Constants.GTK_LAF) {
            splitPane = new EditorSplitPane(JSplitPane.VERTICAL_SPLIT,
                                            baseEditorPanel, resultsBase);
        }
        else {
            splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                       baseEditorPanel, resultsBase);
        }

        splitPane.setDividerSize(4);
        splitPane.setResizeWeight(0.5);
        
        // ---------------------------------------
        // the tool bar and conn combo
        toolBar = new QueryEditorToolBar();
        Vector connections = ConnectionManager.getActiveConnections();
        connectionsModel = new DynamicComboBoxModel(connections);
        connectionsCombo = new JComboBox(connectionsModel);
        if (connectionsModel.getSize() == 0) {
            connectionsCombo.setEnabled(false);
        }
        //connectionsCombo.setFont(new Font("dialog", Font.BOLD, 11));

        JPanel toolsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy++;
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        toolsPanel.add(toolBar, gbc);
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 5;
        gbc.insets.left = 5;
        gbc.insets.right = 10;
        toolsPanel.add(new JLabel("Connection:"), gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        toolsPanel.add(connectionsCombo, gbc);
        
        splitPane.setBorder(BorderFactory.createEmptyBorder(0,3,3,3));
        
        JPanel base = new JPanel(new BorderLayout());
        base.add(toolsPanel, BorderLayout.NORTH);
        base.add(splitPane, BorderLayout.CENTER);
        
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets.top = 0;
        gbc.insets.bottom = 0;
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        
        add(base, gbc);
        
        // register for connection events
        EventMediator.registerListener(EventMediator.CONNECTION_EVENT, this);
        // register for keyword events
        EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
        
        setEditorPreferences();
        
        statusBar.setCaretPosition(1,1);
        statusBar.setInsertionMode("INS");
    }
    
    /**
     * Notifies this panel of a query execute in progress.
     *
     * @param true if executing, false otherwise
     */
    public void setExecuting(boolean executing) {
        if (executing) {
            statusBar.startProgressBar();
        } else {
            resultsPanel.finished();
            statusBar.stopProgressBar();
        }
    }
    
    /** the last divider location before a output hide */
    private int lastDividerLocation;

    /**
     * Toggles the output pane visible or not.
     */
    public void toggleOutputPaneVisible() {
        if (resultsBase.isVisible()) {
            lastDividerLocation = splitPane.getDividerLocation();
            resultsBase.setVisible(false);
        } else {
            resultsBase.setVisible(true);
            splitPane.setDividerLocation(lastDividerLocation);
        }
    }
    
    /**
     * Enters the specified text at the editor's current 
     * insertion point.
     *
     * @param text - the text to insert
     */
    public void insertTextAtCaret(String text) {
        editor.insertTextAtCaret(text);
    }
    
    /**
     * Returns the default focus component, the query text
     * editor component.
     *
     * @return the editor component
     */
    public Component getDefaultFocusComponent() {
        return editor.getQueryArea();
    }
    
    /**
     * Sets the editor user preferences.
     */
    public void setEditorPreferences() {
        setPanelBackgrounds();
        
        statusBar.setVisible(
                    SystemProperties.getBooleanProperty("user", "editor.display.statusbar"));
        
        editor.showLineNumbers(
                    SystemProperties.getBooleanProperty("user", "editor.display.linenums"));
        
        editor.preferencesChanged();
        
        editor.setCommitMode(
                    SystemProperties.getBooleanProperty("user", "editor.connection.commit"));
        
        holdMetaData = SystemProperties.getBooleanProperty("user", "editor.results.metadata");
        
        resultsPanel.setTableProperties();
    }
    
    /**
     * Called to inform this component of a change/update
     * to the user defined key words.
     */
    public void updateSQLKeywords() {
        editor.setSQLKeywords(true);
    }
    
    /**
     * Notification of a new keyword added to the list.
     */
    public void keywordsAdded(KeywordEvent e) {
        editor.setSQLKeywords(true);
    }

    /**
     * Notification of a keyword removed from the list.
     */
    public void keywordsRemoved(KeywordEvent e) {
        editor.setSQLKeywords(true);
    }

    /**
     * Sets the activity status bar label text to 
     * that specified.
     *
     * @param the activity label text
     */
    public void setActivityStatusText(String s) {
        statusBar.setExecutionTime(s);
    }

    /**
     * Sets the right status bar label text to 
     * that specified.
     *
     * @param the right label text
     */
    public void setRightStatusText(String s) {
        statusBar.setCommitStatus(s);
    }
    
    /**
     * Sets the text of the left status label.
     *
     * @param the text to be set
     */
    public void setLeftStatusText(String s) {
        statusBar.setStatus(s);
    }
    
    /**
     * Propagates the call to interrupt an executing process.
     */
    public void interrupt() {
        resultsPanel.interrupt();
    }
    
    /**
     * Sets the result set object.
     *
     * @param the executed result set
     * @param whether to return the result set row count
     */
    public int setResultSet(ResultSet rset, boolean showRowNumber)
        throws SQLException {  
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
        throws SQLException {  
        int rowCount = resultsPanel.setResultSet(rset, showRowNumber);
        revalidate();
        return rowCount;
    }
    
    /**
     * Sets the result set object.
     *
     * @param the executed result set
     */
    public void setResultSet(ResultSet rset) throws SQLException {
        resultsPanel.setResultSet(rset, true);
        revalidate();
    }

    /**
     * Sets the result set object.
     *
     * @param the executed result set
     * @param the executed query of the result set
     */
    public void setResultSet(ResultSet rset, String query) throws SQLException {
        resultsPanel.setResultSet(rset, true, query);
        revalidate();
    }

    public void destroyTable() {
        resultsPanel.destroyTable();
    }

    /**
     * Sets to display the result set meta data for the
     * currently selected result set tab.
     */
    public void displayResultSetMetaData() {
        resultsPanel.displayResultSetMetaData();
    }
    
    /**
     * Returns the editor status bar.
     *
     * @return the editor's status bar panel
     */
    public QueryEditorStatusBar getStatusBar() {
        return statusBar;
    }
    
    /**
     * Disables/enables the listener updates as specified.
     */
    public void disableUpdates(boolean disable) {
        editor.disableUpdates(disable);
    }

    /**
     * Returns true that a search can be performed on the editor.
     */
    public boolean canSearch() {
        return true;
    }
    
    /**
     * Disables/enables the caret update as specified.
     */
    public void disableCaretUpdate(boolean disable) {
        editor.disableCaretUpdate(disable);
    }
    
    public ResultSetTableModel getResultSetTableModel() {
        return resultsPanel.getResultSetTableModel();
    }
    
    public void setResultText(int r, int t) {
        resultsPanel.setResultText(r, t);
    }
    
    /**
     * Returns whether a result set panel is selected and that
     * that panel has a result set row count > 0.
     *
     * @return true | false
     */
    public boolean isResultSetSelected() {
        return resultsPanel.isResultSetSelected();
    }

    /**
     * Sets the respective panel background colours within 
     * the editor as specified by the user defined properties.
     */
    public void setPanelBackgrounds() {
        editor.setTextPaneBackground(
                    SystemProperties.getColourProperty("user", "editor.text.background.colour"));
        resultsPanel.setResultBackground(
                        SystemProperties.getColourProperty("user", "editor.results.background.colour"));
    }

    /**
     * Sets the text of the editor pane to the previous
     * query available in the history list. Where no previous
     * query exists, nothing is changed.
     */
    public void selectPreviousQuery() {
        try {
            GUIUtilities.showWaitCursor();
            editor.selectPrevQuery();
        }
        finally {
            GUIUtilities.showNormalCursor();
        }
    }

    /**
     * Sets the text of the editor pane to the next
     * query available in the history list. Where no
     * next query exists, nothing is changed.
     */
    public void selectNextQuery() {
        try {
            GUIUtilities.showWaitCursor();
            editor.selectNextQuery();
        }
        finally {
            GUIUtilities.showNormalCursor();
        }
    }
    
    /**
     * Enables/disables the show meta data button.
     */
    public void setMetaDataButtonEnabled(boolean enable) {
        if (holdMetaData) {
            getTools().setMetaDataButtonEnabled(enable);
        }
        else {
            getTools().setMetaDataButtonEnabled(false);
        }
    }
    
    /**
     * Sets the history next button enabled as specified.
     */
    public void setNextButtonEnabled(boolean enabled) {
        getTools().setNextButtonEnabled(enabled);
    }

    /**
     * Sets the history previous button enabled as specified.
     */
    public void setPreviousButtonEnabled(boolean enabled) {
        getTools().setPreviousButtonEnabled(enabled);
    }

    /**
     * Enables/disables the transaction related buttons.
     */
    public void setCommitsEnabled(boolean enable) {
        getTools().setCommitsEnabled(enable);
    }

    /**
     * Enables/disables the export result set button.
     */
    public void setExportButtonEnabled(boolean enable) {
        getTools().setExportButtonEnabled(enable);
    }
    
    /**
     * Enables/disables the query execution stop button.
     */
    public void setStopButtonEnabled(boolean enable) {        
        getTools().setStopButtonEnabled(enable);
    }

    /**
     * Updates the interface and any system buttons as 
     * required on a focus gain.
     */
    public void focusGained() {
        QueryEditorToolBar tools = getTools();
        tools.setMetaDataButtonEnabled(
                resultsPanel.hasResultSetMetaData() && holdMetaData);
        
        tools.setCommitsEnabled(!editor.getCommitMode());
        tools.setNextButtonEnabled(editor.hasNextHistory());
        tools.setPreviousButtonEnabled(editor.hasPreviousHistory());
        tools.setStopButtonEnabled(editor.isExecuting());
        tools.setExportButtonEnabled(resultsPanel.isResultSetSelected());
        editor.setTextFocus();
    }
    
    /**
     * Does nothing.
     */
    public void focusLost() {}

    private QueryEditorToolBar getTools() {
        return toolBar;
    }
    
    public void destroyConnection() {
        editor.destroyConnection();
    }
    
    public void toggleCommitMode() {
        boolean mode = !editor.getCommitMode();
        editor.setCommitMode(mode);
        getTools().setCommitsEnabled(!mode);
        //setRightStatusText(" Auto-Commit:  " + mode);
    }
    
    
    // --------------------------------------------
    // TabView implementation
    // --------------------------------------------

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        // check if we are saving
        if (SystemUtilities.isPromptingToSave() && contentChanged) {
            if (!GUIUtilities.saveOpenChanges(this)) {
                return false;
            }
        }
        cleanup();
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        focusGained();
        return true;
    }

    /**
     * Indicates the panel is being de-selected in the pane
     */
    public boolean tabViewDeselected() {
        return true;
    }

    // --------------------------------------------

    /**
     * Performs any resource clean up for a pending removal.
     */
    public void cleanup() {
        editor.closingEditor();
        resultsPanel.cleanup();
        statusBar.cleanup();
        
        resultsPanel = null;
        statusBar = null;
        toolBar = null;
        editor = null;

        removeAll();
        EventMediator.deregisterListener(EventMediator.CONNECTION_EVENT, this);
        EventMediator.deregisterListener(EventMediator.KEYWORD_EVENT, this);
        GUIUtilities.registerUndoRedoComponent(null);
    }

    public void closeConnection() {
        //editor.closeConnection();
    }

    public void interruptStatement() {
        editor.interruptStatement();
    }
    
    public String getOpenFilePath() {
        return absolutePath;
    }
    
    public void clearOutputPane() {
        resultsPanel.clearOutputPane();
    }
    
    public void selectAll() {
        editor.selectAll();
    }
    
    public void goToRow(int row) {
        editor.goToRow(row);
    }
    
    public void selectNone() {
        editor.selectNone();
    }
    
    public Vector getHistoryList() {
        return editor.getHistoryList();
    }
    
    /**
     * Executes the currently selected query text.
     */
    public void executeSelection() {
        String query = editor.getSelectedText();
        if (query != null) {
            executeSQLQuery(query);
        }
    }
    
    /**
     * Returns the currently selcted connection properties object.
     *
     * @return the selected connection
     */
    public DatabaseConnection getSelectedConnection() {
        if (connectionsCombo.getSelectedIndex() != -1) {
            return (DatabaseConnection)connectionsCombo.getSelectedItem();
        }
        return null;
    }
    
    /**
     * Executes the specified query.
     *
     * @param the query
     */
    public void executeSQLQuery(String query) {
        if (query == null) {
            query = editor.getQueryAreaText();
        }
        editor.resetExecutingLine();
        resultsPanel.setExecutingSingle(false);
        DatabaseConnection dc = (DatabaseConnection)connectionsCombo.getSelectedItem(); 
        editor.executeSQLQuery(dc, query);
    }

    protected void setExecutingSingle(boolean executingSingle) {
        resultsPanel.setExecutingSingle(executingSingle);
    }
    
    public void executeSQLAtCursor() {
        resultsPanel.setExecutingSingle(true);
        DatabaseConnection dc = (DatabaseConnection)
                        connectionsCombo.getSelectedItem();
        editor.executeSQLAtCursor(dc);
    }

    public JTextComponent getEditorTextComponent() {
        return editor.getQueryArea();
    }
    
    /**
     * Adds a comment tag to the beginning of the current line
     * or selected lines.
     */
    public void addCommentToLines() {
        editor.addCommentToLines();
    }

    /**
     * Removes a comment tag from the current line or selected lines.
     */
    public void removeCommentFromLines() {
        editor.removeCommentFromLines();
    }

    /**
     * Shifts the text on the current line or the currently
     * selected text to the right one TAB.
     */
    public void shiftTextRight() {
        editor.shiftTextRight();
    }

    /**
     * Shifts the text on the current line or the currently
     * selected text to the left one TAB.
     */
    public void shiftTextLeft() {
        editor.shiftTextLeft();
    }

    /**
     * Sets the editor's text content that specified.
     *
     * @param s - the text to be set
     */
    public void setEditorText(String s) {
        editor.setQueryAreaText(s);
    }
    
    /**
     * Moves the caret to the beginning of the specified query.
     *
     * @param query - the query to move the cursor to
     */
    public void caretToQuery(String query) {
        editor.caretToQuery(query);
    }

    /**
     * Loads the specified text into a blank 'offscreen' document
     * before switching to the SQL document.
     */
    public void loadText(String text) {
        editor.loadText(text);
    }
    
    public String getEditorText() {
        return editor.getQueryAreaText();
    }
    
    public void setOutputMessage(int type, String text) {
        resultsPanel.setOutputMessage(type, text);
        revalidate();
    }

    public boolean isOpenFile() {
        return openFile;
    }
    
    /**
     * Sets the state for an open file.
     *
     * @param the absolute file path
     */
    public void setAbsoluteFile(String absolutePath) {
        this.absolutePath = absolutePath;

        openFile = !(MiscUtils.isNull(absolutePath));
        if (openFile) {
            String separator = System.getProperty("file.separator");
            int index = absolutePath.lastIndexOf(separator);
            if (index != -1) {
                fileName = absolutePath.substring(index + 1);
            } else {
                fileName = absolutePath;
            }
        } else {
            fileName = defaultFileName;
        }
    }
    
    /**
     * Returns whether the text component is in a printable state.
     *
     * @return true | false
     */
    public boolean canPrint() {
        return true;
    }
    
    public void setPrintingText(boolean printingText) {
        this.printingText = printingText;
    }
    
    public Printable getPrintable() {
/*
    PrintSelectDialog dialog = new PrintSelectDialog(this);
    int selection = dialog.display();
 
    dialog.dispose();
    dialog = null;
 
      if (selection == PrintSelectDialog.EDITOR_TEXT)
        return new TextPrinter(editor.getQueryAreaText());
 
      else if (selection == PrintSelectDialog.RESULTS_TABLE)
        return new TablePrinter(resultsPanel.getResultsTable(),
                                "Query: " + editor.getQueryAreaText());
 
      else
        return null;
 */
        String text = editor.getQueryAreaText();
        
        if (printingText) {
            return new TextPrinter(text);
        } else {
            /*
            JTable table = resultsPanel.getResultsTable();
            return table.getPrintable(JTable.PrintMode.NORMAL, 
                                      new MessageFormat("Query: " + text), 
                                      new MessageFormat("Page {0}"));
            */
            return new TablePrinter(resultsPanel.getResultsTable(),
                                        "Query: " + text);
             
        }
        
    }
    
    public String getPrintJobName() {
        return "Execute Query - editor";
    }
    
    // ---------------------------------------------
    // TextEditor implementation
    // ---------------------------------------------

    public void paste() {
        editor.paste();
    }
    
    public void copy() {
        editor.copy();
    }
    
    public void cut() {
        editor.cut();
    }
    
    public void deleteLine() {
        editor.deleteLine();
    }
    
    public void deleteWord() {
        editor.deleteWord();
    }
    
    public void deleteSelection() {
        editor.deleteSelection();
    }
    
    public void insertFromFile() {
        editor.insertFromFile();
    }
    
    public void insertLineAfter() {
        editor.insertLineAfter();
    }
    
    public void insertLineBefore() {
        editor.insertLineBefore();
    }
    
    public void changeSelectionCase(boolean upper) {
        editor.changeSelectionCase(upper);
    }
    
    // ---------------------------------------------
    
    // ---------------------------------------------
    // SaveFunction implementation
    // ---------------------------------------------
    
    public String getDisplayName() {
        return toString();
    }
    
    public boolean promptToSave() {
        return contentChanged;
    }
    
    public int save(boolean saveAs) {
        TextFileWriter writer = null;
        String text = editor.getQueryAreaText();
        
        if (openFile && !saveAs) {
            writer = new TextFileWriter(text, absolutePath);
        }
        else {
            writer = new TextFileWriter(text, new File(defaultFileName));
        }
        
        int result = writer.write();
        if (result == SaveFunction.SAVE_COMPLETE) {
            File file = writer.getSavedFile();
            String name = file.getName();
            GUIUtilities.setTabTitleForComponent(this, TITLE + " - " + name);
            absolutePath = file.getAbsolutePath();
            statusBar.setStatus(" File saved to " + name);
            openFile = true;
            contentChanged = false;
        }
        return result;        
    }
    
    // ---------------------------------------------
    
    /**
     * Returns the display name of this panel. This may
     * include the path of any open file.
     *
     * @return the display name
     */
    public String toString() {
        if (openFile) {
            return TITLE + " - " + fileName;
        } else {
            return TITLE + " - " + defaultFileName;
        }
    }

    /**
     * Returns whether the content has changed for a 
     * possible document save.
     *
     * @return true if text content changed, false otherwise
     */
    public boolean isContentChanged() {
        return contentChanged;
    }
    
    /**
     * Sets that the text content of the editor has changed from
     * the original or previously saved state.
     *
     * @param true | false
     */
    public void setContentChanged(boolean contentChanged) {
        this.contentChanged = contentChanged;
    }

    // ---------------------------------------------
    // ConnectionListener implementation
    // ---------------------------------------------
    
    /**
     * Indicates a connection has been established.
     * 
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent) {
        connectionsModel.addElement(connectionEvent.getSource());
        connectionsCombo.setEnabled(true);
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent) {
        connectionsModel.removeElement(connectionEvent.getSource());
        if (connectionsModel.getSize() == 0) {
            connectionsCombo.setEnabled(false);
        }
        // TODO: NEED TO CHECK OPEN CONN
        
    }

    // ---------------------------------------------
    
}



