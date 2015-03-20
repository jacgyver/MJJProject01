/*
 * QueryEditorTextPanel.java
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

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JPopupMenu;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.executequery.SystemUtilities;
import org.executequery.gui.text.TextUtilities;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.gui.SQLExecutor;
import org.underworldlabs.swing.GUIUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>This object is the primary mediator between the parent
 *  <code>QueryEditor</code> object and the actual Query Editor's
 *  text pane - the <code>QueryEditorTextPane</code>. All commands for the
 *  text pane are propagated through here. This includes all requests
 *  to execute queries, maintainance of the executed history list,
 *  the editor's popup menu to the more simple cut/copy/paste commands.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.8 $
 * @date     $Date: 2006/08/24 09:38:11 $
 */
public class QueryEditorTextPanel extends JPanel
                                  implements SQLExecutor,
                                             MouseListener {
    
    /** The SQL text pane */
    private QueryEditorTextPane queryPane;
    
    /** The editor's controller */
    private QueryEditor queryEditor;
    
    /** Analyses and executes queries */
    private QueryAnalyser analyser;
    
    /** The text pane's popup menu */
    private PopMenu popup;
    
    /** The current history index */
    private int historyNum;
    
    /** Constructs a new instance. */
    public QueryEditorTextPanel(QueryEditor queryEditor) {
        super(new BorderLayout());
        
        this.queryEditor = queryEditor;
        
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        historyNum = -1;
        //historyList = new Vector();
    }
    
    /** Initializes the state of this instance. */
    private void jbInit() throws Exception {
        // setup the query text panel and associated scroller
        queryPane = new QueryEditorTextPane(this);
        analyser = new QueryAnalyser(this);
        
        JScrollPane queryScroller = new JScrollPane();
        queryScroller.getViewport().add(queryPane, BorderLayout.CENTER);
        queryScroller.setRowHeaderView(queryPane.getLineBorder());
        queryScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        queryScroller.setBorder(new EditorScrollerBorder());

        //setPreferredSize(QueryEditorConstants.PANEL_SIZE);
        add(queryScroller, BorderLayout.CENTER);

        popup = new PopMenu();
        queryPane.addMouseListener(this);

        boolean commitMode = SystemProperties.getBooleanProperty("user",
                                                    "editor.connection.commit");
        popup.enableCommits(!commitMode);
    }

    public void setActivityStatusText(String s) {
        queryEditor.setActivityStatusText(s);
    }
    
    public void destroyConnection() {
        analyser.destroyConnection();
    }
    
    public void setTextPaneBackground(Color c) {
        queryPane.setBackground(c);
    }
    
    public void setSQLKeywords(boolean reset) {
        queryPane.setSQLKeywords(reset);
    }
    
    public void setExecuting(boolean executing) {
        queryEditor.setExecuting(executing);
    }
    
    public void setOutputMessage(int type, String text) {
        queryEditor.setOutputMessage(type, text);
    }
    
    /*
    public void setExecutingMessage(String s) {
        queryEditor.setExecutingMessage(s);
    }
    */

    /**
     * Indicates that the editor is closing and performs some cleanup.
     */
    protected void closingEditor() {
        closeConnection();

        /* ------------------------------------------------
         * profiling found the popup keeps the 
         * editor from being garbage collected at all!!
         * a call to removeAll() is a work around for now.
         * ------------------------------------------------ 
         */
        popup.removeAll();

        queryEditor = null;        
    }
    
    public void showLineNumbers(boolean show) {
        queryPane.showLineNumbers(show);
    }

    protected void setTextFocus() {
        GUIUtils.requestFocusInWindow(queryPane);
    }

    /**
     * Resets the executing line within the line 
     * number border panel.
     */
    public void resetExecutingLine() {
        queryPane.resetExecutingLine();
    }

    /**
     * Resets the text pane's caret position to zero.
     */
    public void resetCaretPosition() {
        queryPane.setCaretPosition(0);
    }
    
    /**
     * Interrupts any executing statements and propagates the 
     * call to close the connection to the QueryAnalyser object.
     */
    public void closeConnection() {
        interruptStatement();
        analyser.closeConnection();
    }
    
    /**
     * Returns whether a statement execution is in progress.
     *
     * @return true | false
     */
    public boolean isExecuting() {
        return analyser.isExecuting();
    }
    
    /**
     * Sets the editor's auto-commit mode to that specified.
     */
    public void setCommitMode(boolean mode) {
        analyser.setCommitMode(mode);
        popup.enableCommits(!mode);
    }
    
    /**
     * Returns the editor's current auto-commit mode.
     */
    public boolean getCommitMode() {
        return analyser.getCommitMode();
    }

    // -------------------------------------
    // Executed query history methods
    // -------------------------------------
    
    /**
     * Selects the next query from the history list and places the
     * query text into the editor.
     */
    public void selectNextQuery() {
        Vector history = SystemUtilities.getSqlCommandHistory();
        queryPane.setText((String)history.elementAt(decrementHistoryNum()));
    }

    /**
     * Selects the previous query from the history list and places the
     * query text into the editor.
     */
    public void selectPrevQuery() {
        Vector history = SystemUtilities.getSqlCommandHistory();
        queryPane.setText((String)history.elementAt(incrementHistoryNum()));
    }

    /**
     * Increments the history index value.
     */
    private int incrementHistoryNum() {
        //  for previous
        Vector history = SystemUtilities.getSqlCommandHistory();
        if (historyNum < history.size() - 1) {
            historyNum++;
        }

        queryEditor.setNextButtonEnabled(true);
        
        if (historyNum == history.size() - 1) {// || historyNum == 0) {
            queryEditor.setPreviousButtonEnabled(false);
        }

        return historyNum;        
    }

    /**
     * Decrements the history index value.
     */
    private int decrementHistoryNum() {
        if (historyNum != 0) {
            historyNum--;
        }
        
        queryEditor.setPreviousButtonEnabled(true);
        
        if (historyNum == 0) {
            queryEditor.setNextButtonEnabled(false);
        }

        return historyNum;
    }
    
    /** ignored statements for the history list */
    private final String[] HISTORY_IGNORE = {"COMMIT", "ROLLBACK"};
    
    /**
     * Adds the secified query to the executed statement history list.
     */
    public void addToHistory(String query) {
        String _query = query.toUpperCase();
        for (int i = 0; i < HISTORY_IGNORE.length; i++) {
            if (HISTORY_IGNORE[i].compareTo(_query) == 0) {
                return;
            }
        }
        SystemUtilities.addSqlCommand(query);
    }
    
    /**
     * Returns whether a call to previous history would be successful.
     */
    public boolean hasPreviousHistory() {
        Vector history = SystemUtilities.getSqlCommandHistory();
        return historyNum < history.size() - 1;
    }

    /**
     * Returns whether a call to next history would be successful.
     */
    public boolean hasNextHistory() {
        return historyNum > 0;
    }
    
    /**
     * Returns the executed query history list.
     */
    public Vector getHistoryList() {
        return SystemUtilities.getSqlCommandHistory();
    }
    
    /**
     * Enters the specified text at the editor's current 
     * insertion point.
     *
     * @param text - the text to insert
     */
    public void insertTextAtCaret(String text) {
        int caretIndex = queryPane.getCaretPosition();
        queryPane.replaceSelection(text);
        setTextFocus();
    }
    
    public JTextPane getQueryArea() {
        return queryPane;
    }

    public void selectAll() {
        TextUtilities.selectAll(queryPane);
    }
    
    public void selectNone() {
        TextUtilities.selectNone(queryPane);
    }

    public void focusGained() {
        queryEditor.focusGained();
    }

    public void focusLost() {
        if (queryEditor != null) {
            queryEditor.focusLost();
        }
    }
    
    public void setRightStatusText(String s) {
        queryEditor.setRightStatusText(s);
    }

    /**
     * Sets the editor's text content that specified.
     *
     * @param s - the text to be set
     */
    public void setQueryAreaText(String s) {
        try {
            // uninstall listeners on the text pane
            queryPane.uninstallListeners();
            // clear the current held edits
            queryPane.clearEdits();
            // set the text
            queryPane.setText(s);
        }
        finally {
            // reinstall listeners on the text pane
            queryPane.reinstallListeners();
        }
    }
    
    /**
     * Loads the specified text into a blank 'offscreen' document
     * before switching to the SQL document.
     */
    public void loadText(String text) {
        queryPane.loadText(text);
    }

    /**
     * Clears the output pane.
     */
    public void clearOutputPane() {
        if (analyser.isExecuting()) {
            return;
        }
        queryEditor.clearOutputPane();
    }
    
    public QueryEditorStatusBar getStatusBar() {
        return queryEditor.getStatusBar();
    }
    
    public void disableUpdates(boolean disable) {
        queryPane.disableUpdates(disable);
    }
    
    public void disableCaretUpdate(boolean disable) {
        queryPane.disableCaretUpdate(disable);
    }
    
    public void preferencesChanged() {
        queryPane.resetAttributeSets();
        analyser.preferencesChanged(); 
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the connection thats been closed
     */
    public void disconnected(DatabaseConnection dc) {
        analyser.disconnected(dc);
    }
    
    // -----------------------------------
    // regex replacement arrays
    // -----------------------------------

    private static final String[] REGEX_CHARS = {
        "\\*", "\\^", "\\.", "\\[", "\\]", "\\(", "\\)", 
        "\\?", "\\&", "\\{", "\\}", "\\+"};

    private static final String[] REGEX_SUBS = {
        "\\\\*", "\\\\^", "\\\\.", "\\\\[", "\\\\]", "\\\\(", "\\\\)", 
        "\\\\?", "\\\\&", "\\\\{", "\\\\}", "\\\\+"};

    /**
     * Moves the caret to the beginning of the specified query.
     *
     * @param query - the query to move the cursor to
     */
    public void caretToQuery(String query) {
        
        /*
        String num = query.substring(query.indexOf("Z") + 1, query.indexOf("Z") + 3);
        int number = Integer.parseInt(num);
        Log.debug("number: " + number);
        */
        
        // replace any regex control chars
        for (int i = 0; i < REGEX_CHARS.length; i++) {
            //if (i == number) i++;

            query = query.replaceAll(REGEX_CHARS[i], REGEX_SUBS[i]);
            
            //Log.debug("index : " + i + " " + query);
        }

        /*
        Log.debug("---------------------");
        Log.debug(query);
        Log.debug("---------------------");
        */
        
        // replace the block comment marker
        //query = query.replaceAll(
        //                QueryEditorConstants.BLOCK_COMMENT_REGEX,
        //                ".*/\\\\*.*\\\\*/.*");

        Matcher matcher = Pattern.compile(query, Pattern.DOTALL).
                                            matcher(queryPane.getText());

        if (matcher.find()) {
            int index = matcher.start();
            //Log.debug("index: " + index);
            if (index != -1) {
                queryPane.setCaretPosition(index);
            }
        }
        matcher = null;
        GUIUtils.requestFocusInWindow(queryPane);
    }

    /**
     * Returns the currently selected text, or null if not text 
     * is currently selected.
     *
     * @return selected text
     */
    public String getSelectedText() {
        String selection = queryPane.getSelectedText();
        if (selection != null && selection.trim().length() > 0) {
            return selection;
        }
        return null;
    }
    
    public void executeSQLAtCursor(DatabaseConnection dc) {
        if (analyser.isExecuting()) {
            return;
        }
        queryPane.executeSQLAtCursor(dc);
        historyNum = 0;
    }

    /**
     * Executes the specified query with the specified connection properties
     * object.
     * 
     * @param dc - the database connection properties object
     * @param query - the query
     * @param executeAsBlock - whether to execute as a block
     */
    public void executeSQLQuery(DatabaseConnection dc, 
                                String query, boolean executeAsBlock) {
        if (analyser.isExecuting()) {
            return;
        }

        if (query == null) {
            query = getQueryAreaText();
        }
        
        if (query.trim().length() == 0) {
            return;
        }

        //queryPane.resetExecutingLine();
        historyNum = 0;
        queryEditor.setPreviousButtonEnabled(true);
        queryEditor.setNextButtonEnabled(false);
        analyser.executeSQLQuery(dc, query, executeAsBlock);        
    }

    /**
     * Executes the specified query.
     *
     * @param query - the query
     */
    public void executeSQLQuery(String query) {
        executeSQLQuery(queryEditor.getSelectedConnection(), query, false);
    }

    /**
     * Executes the specified query as a 'block' if specified.
     *
     * @param the query
     * @param whether to execute ALL query text as one statement
     */
    public void executeSQLQuery(String query, boolean executeAsBlock) {
        executeSQLQuery(queryEditor.getSelectedConnection(), query, executeAsBlock);
    }

    /**
     * Executes the specified query using the specified connection
     * properties object.
     *
     * @param the the database connection object
     * @param the query
     */
    public void executeSQLQuery(DatabaseConnection dc, String query) {
        executeSQLQuery(dc, query, false);
    }
    
    public void setLeftStatusText(String s) {
        queryEditor.setLeftStatusText(s);
    }
    
    public void goToRow(int row) {
        queryPane.goToRow(row);
    }
    
    public void destroyTable() {
        queryEditor.destroyTable();
    }
    
    /**
     * Propagates the call to interrupt the statement currently being 
     * executed to the QueryAnalyser.
     */
    public void interruptStatement() {
        analyser.interruptStatement();
    }

    /** 
     * Sets the table results to the specified
     * <code>ResultSet</code> object for display.
     *
     *  @param the table results to display
     *  @param whether the row count is displayed
     *  @return the row count of this <code>ResultSet</code> object
     */
    public int setResultSet(ResultSet rset, boolean showRowNumber)
        throws SQLException {
        return setResultSet(rset, showRowNumber, null);
    }

    /** 
     * Sets the table results to the specified
     * <code>ResultSet</code> object for display.
     *
     *  @param the table results to display
     *  @param whether the row count is displayed
     *  @param the executed query of the result set
     *  @return the row count of this <code>ResultSet</code> object
     */
    public int setResultSet(ResultSet rset, boolean showRowNumber, String query)
      throws SQLException {
        return queryEditor.setResultSet(rset, showRowNumber, query);
    }

    public void interrupt() {
        //queryEditor.interrupt();
    }

    /** 
     * Sets the table results to the specified
     * <code>ResultSet</code> object for display.
     *
     * @param the table results to display
     */
    public void setResultSet(ResultSet rset) throws SQLException {
        queryEditor.setResultSet(rset);
    }
    
    /** 
     * Sets the table results to the specified
     * <code>ResultSet</code> object for display.
     *
     * @param the table results to display
     * @param the executed query of the result set
     */
    public void setResultSet(ResultSet rset, String query) throws SQLException {
        queryEditor.setResultSet(rset, query);
    }

    public void setResultText(int r, int t) {
        queryEditor.setResultText(r, t);
    }
    
    public String getQueryAreaText() {
        return queryPane.getText();
    }
    
    public void setStopButtonEnabled(boolean enable) {
        queryEditor.setStopButtonEnabled(enable);
        popup.enableExecutes(enable);
    }

    /**
     * Adds a comment tag to the beginning of the current line
     * or selected lines.
     */
    public void addCommentToLines() {
        queryPane.addUndoEdit();
        if (getSelectedText() == null) {
            int start = queryPane.getCurrentRowStart();
            queryPane.insertTextAtOffset(start, "--");
        }
        else {
            int index = queryPane.getSelectionStart();
            int firstRow = queryPane.getRowAt(index);
            
            index = queryPane.getSelectionEnd();
            int lastRow = queryPane.getRowAt(index);
            
            for (int i = firstRow; i <= lastRow; i++) {
                index = queryPane.getRowStartOffset(i);
                queryPane.insertTextAtOffset(index, "--");
            }
        }
        
        // add this as an edit if its not
        ensureUndo();
    }

    /**
     * Ensures an edit may be undone by forcing an end
     * to the current compound edit within the text pane.
     */
    protected void ensureUndo() {
        if (!queryPane.canUndo()) {
            queryPane.addUndoEdit();
        }
    }
    
    /** pattern matcher to check for comments to be removed */
    private Matcher commentRemovalMatcher;
    
    /**
     * Removes a comment tag from the current line or selected lines.
     */
    public void removeCommentFromLines() {
        if (queryPane.getDocument().getLength() > 0) {
            
            if (commentRemovalMatcher == null) {
                String regex = "^\\s*--";
                commentRemovalMatcher = Pattern.compile(regex).matcher("");
            }

            // add an undo edit
            queryPane.addUndoEdit();
            
            try {
                int start = queryPane.getSelectionStart();
                int end = queryPane.getSelectionEnd();
                
                Element map = queryPane.getElementMap();
                start = queryPane.getRowAt(start);
                end = queryPane.getRowAt(end);
                
                String text = null;
                Document document = queryPane.getDocument();
                for (int i = start; i <= end; i++) {
                    Element line = map.getElement(i);
                    int startOffset = line.getStartOffset();
                    int endOffset = line.getEndOffset();

                    text = queryPane.getText(startOffset, (endOffset - startOffset));
                    commentRemovalMatcher.reset(text);

                    if (commentRemovalMatcher.find()) {
                        // retrieve the exact index of '--' since 
                        // matcher will return first whitespace
                        int index = text.indexOf("--");
                        document.remove(startOffset + index, 2);
                    }

                }

                // add this as an edit if its not
                ensureUndo();

            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }
    }
    
    /**
     * Shifts the text on the current line or the currently
     * selected text to the right one TAB.
     */
    public void shiftTextRight() {
        if (getSelectedText() == null) {
            int start = queryPane.getCurrentRowStart();
            queryPane.shiftTextRight(start);
        }
        else { // simulate a tab key for selected text
            try {
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_TAB);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shifts the text on the current line or the currently
     * selected text to the left one TAB.
     */
    public void shiftTextLeft() {
        if (getSelectedText() == null) {
            int start = queryPane.getCurrentRowStart();
            int end = queryPane.getCurrentRowEnd();
            queryPane.shiftTextLeft(start, end);
        }
        else { // simulate a tab key for selected text
            try {
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_TAB);
                robot.keyRelease(KeyEvent.VK_SHIFT);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }        
    }

    // ---------------------------------------------
    // TextFunction implementation
    // ---------------------------------------------
    
    public void paste() {
        queryPane.paste();
    }
    
    public void copy() {
        queryPane.copy();
    }
    
    public void cut() {
        queryPane.cut();
    }
    
    public void changeSelectionCase(boolean upper) {
        queryPane.addUndoEdit();
        TextUtilities.changeSelectionCase(queryPane, upper);
    }
    
    public void deleteLine() {
        queryPane.addUndoEdit();
        TextUtilities.deleteLine(queryPane);
    }
    
    public void deleteWord() {
        queryPane.addUndoEdit();
        TextUtilities.deleteWord(queryPane);
    }
    
    public void deleteSelection() {
        queryPane.addUndoEdit();
        TextUtilities.deleteSelection(queryPane);
    }
    
    public void insertFromFile() {
        queryPane.addUndoEdit();
        TextUtilities.insertFromFile(queryPane);
    }
    
    public void insertLineAfter() {
        queryPane.addUndoEdit();
        TextUtilities.insertLineAfter(queryPane);
    }
    
    public void insertLineBefore() {
        queryPane.addUndoEdit();
        TextUtilities.insertLineBefore(queryPane);
    }
    
    // ---------------------------------------------
    
    /**
     * Propagates the call to the parent QueryEditor object
     * that the text content has been altered from the original
     * or previously saved state.
     *
     * @param true | false
     */
    public void setContentChanged(boolean contentChanged) {
        queryEditor.setContentChanged(contentChanged);
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    
    private static Insets borderInsets;
    private static Color borderColour;
    
    private class EditorScrollerBorder implements Border {
        
        protected EditorScrollerBorder() {
            if (borderInsets == null) {
                borderInsets = new Insets(0,0,1,0);
            }
            if (borderColour == null) {
                borderColour = GUIUtilities.getDefaultBorderColour();
            }
        }
        
        public Insets getBorderInsets(Component c) {
            return borderInsets;
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(borderColour);
            g.drawLine(x, height-1, width, height-1);
        }
        
        public boolean isBorderOpaque() {
            return false;
        }
        
    }


    /** The Query Editor's popup menu function */
    private class PopMenu extends JPopupMenu implements ActionListener {
        
        private JMenuItem execute;
        private JMenuItem executeBlock;
        private JMenuItem stop;
        private JMenuItem clearOutput;
        private JMenuItem rollback;
        private JMenuItem commit;
        
        public PopMenu() {
            JMenuItem cut = new JMenuItem(ActionBuilder.get("cut-command"));
            cut.setText("Cut");
            cut.setIcon(null);
            
            JMenuItem copy = new JMenuItem(ActionBuilder.get("copy-command"));
            copy.setText("Copy");
            copy.setIcon(null);
            
            JMenuItem paste = new JMenuItem(ActionBuilder.get("paste-command"));
            paste.setText("Paste");
            paste.setIcon(null);

            execute = new JMenuItem("Execute");
            execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
            execute.addActionListener(this);
            
            JMenuItem partialExecute = new JMenuItem(
                    ActionBuilder.get("execute-at-cursor-command"));
            partialExecute.setText("Execute Query at Cursor");
            partialExecute.setIcon(null);

            JMenuItem executeSelection = new JMenuItem(
                    ActionBuilder.get("execute-selection-command"));
            executeSelection.setText("Execute Selected Query Text");
            executeSelection.setIcon(null);

            executeBlock = new JMenuItem("Execute as Single Statement");
            //execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
            executeBlock.addActionListener(this);
            
            stop = new JMenuItem("Cancel Query");
            stop.addActionListener(this);
            
            commit = new JMenuItem("Commit");
            commit.addActionListener(this);
            
            rollback = new JMenuItem("Rollback");
            rollback.addActionListener(this);

            clearOutput = new JMenuItem("Clear Output Log");
            clearOutput.addActionListener(this);
            
            JMenuItem help = new JMenuItem(ActionBuilder.get("help-command"));
            help.setIcon(null);
            help.setActionCommand("qedit");
            help.setText("Help");
            
            add(cut);
            add(copy);
            add(paste);
            addSeparator();
            add(execute);
            add(partialExecute);
            add(executeSelection);
            add(executeBlock);
            add(stop);
            addSeparator();
            add(commit);
            add(rollback);
            addSeparator();
            add(clearOutput);
            addSeparator();
            add(help);
            
            stop.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            
            if (source == stop) {
                interruptStatement();
            }
            else if (source == execute) {
                executeSQLQuery(null);
            }
            else if (source == executeBlock) {
                queryEditor.setExecutingSingle(false);
                executeSQLQuery(null, true);
            }
            else if (source == clearOutput) {
                clearOutputPane();
            }
            else if (source == commit) {
                executeSQLQuery("commit");
            }
            else if (source == rollback) {
                executeSQLQuery("rollback");
            }

        }
        
        public void enableExecutes(boolean enable) {
            stop.setEnabled(enable);
            execute.setEnabled(!enable);
        }
        
        public void enableCommits(boolean enable) {
            commit.setEnabled(enable);
            rollback.setEnabled(enable);
        }
        
    } // class PopMenu

}



