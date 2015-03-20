/*
 * QueryEditorTextPane.java
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

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.KeywordProperties;
import org.underworldlabs.util.SystemProperties;
import org.executequery.gui.UndoableComponent;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.components.LineNumber;
import org.executequery.gui.text.SQLTextPane;
import org.executequery.gui.text.TextUndoManager;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The SQL text area for the Query Editor.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class QueryEditorTextPane extends SQLTextPane
                                 implements UndoableComponent,
                                            CaretListener,
                                            FocusListener,
                                            DocumentListener {
    
    /** The editor panel containing this text component */
    private QueryEditorTextPanel editorPanel;
    
    /** To display line numbers */
    private LineNumber lineBorder;

    /** The text pane's undo manager */
    protected TextUndoManager undoManager;
    
    /** 
     * Constructs a new text pane with the specified
     * <code>QueryEditorTextPanel</code> as its parent container.
     *
     * @param the parent container
     */
    public QueryEditorTextPane(QueryEditorTextPanel editorPanel) {
        this.editorPanel = editorPanel;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() throws Exception {
        setMargin(new Insets(2, 2, 2, 2));

        if (editorPanel == null) {
            setEditorPreferences();
        }

        // add the line number border and caret listener
        lineBorder = new LineNumber(this);
        addCaretListener(this);

        // undo functionality
        undoManager = new TextUndoManager(this);
        undoManager.setLimit(
                SystemProperties.getIntProperty("user", "editor.undo.count"));

        document.addDocumentListener(this);
        addFocusListener(this);

        setDragEnabled(true);
        setRequestFocusEnabled(true);
        setFocusable(true);        

        // set the caret
        EditorCaret caret = new EditorCaret();
        int blinkRate = UIManager.getInt("TextPane.caretBlinkRate");
        if (blinkRate > 0) {
            caret.setBlinkRate(blinkRate);
        } else {
            caret.setBlinkRate(500);
        }
        setCaret(caret);

        // set to insert mode
        document.setInsertMode(QueryEditorConstants.INSERT_MODE);
    }

    public void showLineNumbers(boolean show) {
        lineBorder.getParent().setVisible(show);
    }
    
    public void disableUpdates(boolean disable) {
        if (disable) {
            addUndoEdit();
            String text = getText();
            setDocument(new DefaultStyledDocument());
            setText(text);
            disableCaretUpdate(true);
        }
        else {
            String text = getText();
            setDocument(document);
            setText(text);
            disableCaretUpdate(false);
        }
    }
    
    public void disableCaretUpdate(boolean disable) {
        
        if (disable) {
            removeCaretListener(this);
        }
        else {
            boolean hasListener = false;
            CaretListener[] caretListners = getCaretListeners();
            
            for (int i = 0; i < caretListners.length; i++) {
                
                if (caretListners[i] == this) {
                    hasListener = true;
                    break;
                }
                
            }
            
            if (!hasListener) {
                addCaretListener(this);
                caretUpdate(null);
            }
            
        }
        
    }
    
    /**
     * Clears the undo managers stored edits
     */
    protected void clearEdits() {
        undoManager.discardAllEdits();
    }

    /**
     * Removes (designed to be temporary) listeners attached
     * to this text pane including the caret updates and 
     * undo/redo listener objects.
     */
    protected void uninstallListeners() {
        removeCaretListener(this);
        document.removeDocumentListener(this);
        undoManager.suspend();
    }

    /**
     * Reinstates listeners attached to this text pane 
     * including the caret updates and undo/redo listener objects.
     */
    protected void reinstallListeners() {
        addCaretListener(this);
        document.addDocumentListener(this);
        undoManager.reinstate();
    }

    /**
     * Loads the specified text into a blank 'offscreen' document
     * before switching to the SQL document. This is most effective
     * for very large chunks of text loaded from file or similar.
     */
    public void loadText(String text) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // uninstall listeners on the text pane
            uninstallListeners();
            // clear the current held edits
            clearEdits();
            
            // create a dummy document to load the text into
            Document _document = new DefaultStyledDocument();
            setDocument(_document);
            
            try {
                // clear the contents of we have any
                int length = document.getLength();
                if (length > 0) {
                    // replace the existing text
                    document.replace(0, length, text, null);
                }
                else {
                    // set the new text
                    document.insertString(0, text, null);
                }
                
            }
            catch (BadLocationException e) {}

            // reset the SQL document
            setDocument(document);            
        }
        finally {
            // update the line border state
            updateLineBorder();
            // reinstall listeners on the text pane
            reinstallListeners();
            // reset the caret
            setCaretPosition(0);
            // reset the cursor
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }        
    }
    
    /**
     * Override to update the line border.
     */
    public void setText(String text) {
        super.setText(text);
        updateLineBorder();
    }

    /**
     * Override to return false.
     */
    public boolean isOpaque() {
        return false;
    }

    /**
     * Paints the current line highlight and right-hand margin
     * before a call to the super class.
     *
     * @param g the <code>Graphics</code> object to protect
     */
    public void paintComponent(Graphics g) {
        int height = getHeight();
        int width = getWidth();
        
        g.setColor(getBackground());
        g.fillRect(0, 0, width, height);
       
        // paint the current line highlight
        if (QueryEditorSettings.isDisplayLineHighlight()) {
            int currentRow = getCurrentCursorRow();
            g.setColor(QueryEditorSettings.getLineHighlightColour());
            g.fillRect(1, (currentRow * fontHeight) + 2, width - 1, fontHeight);
        }
        
        // paint the right-hand margin
        if (QueryEditorSettings.isDisplayRightMargin()) {
            int xPosn = fontWidth * QueryEditorSettings.getRightMarginSize();
            g.setColor(QueryEditorSettings.getRightMarginColour());
            g.drawLine(xPosn, 0, xPosn, height);
        }
        
        super.paintComponent(g);
    }

    public void setQueryAreaText(String s) {
        setText(s);
    }

    /**
     * Shifts the text at position start to position end left.
     *
     * @param start - the start offset
     * @param end - the end offset
     */
    public void shiftTextLeft(int start, int end) {
        addUndoEdit();
        getSQLSyntaxDocument().shiftTabEvent(start, end, false);
    }

    /**
     * Shifts the text at position start to the right.
     * This will usually be the start position of any 
     * particular row.
     *
     * @param start - the start offset
     */
    public void shiftTextRight(int offset) {
        addUndoEdit();
        insertTextAtOffset(offset, "\t");
    }

    /**
     * Inserts the specified text at the offset.
     *
     * @param offset - the insertion point
     * @param text - the text
     */
    public void insertTextAtOffset(int offset, String text) {
        try {
            getDocument().insertString(offset, text, null);
        } catch (BadLocationException e) {}
    }
    
    public JTextPane getQueryArea() {
        return this;
    }
    
    public JComponent getLineBorder() {
        return lineBorder;
    }
    
    public void goToRow(int row) {
        int goToRow = getRowPosition(row-1);
        if (goToRow < 0) {
            GUIUtilities.displayErrorMessage("The line number entered is invalid.");
            return;
        }
        setCaretPosition(goToRow);
    }
    
    protected void setEditorPreferences() {
        // call to super class
        super.setEditorPreferences();
        // set the pane background
        setBackground(QueryEditorSettings.getEditorBackground());
    }
    
    public void setSQLKeywords(boolean reset) {
        document.setSQLKeywords(KeywordProperties.getSQLKeywords(), reset);
    }
    
    public void resetAttributeSets() {
        String text = getText();
        setEditorPreferences();
        document.resetAttributeSets();
        lineBorder.updatePreferences(QueryEditorSettings.getEditorFont());
        lineBorder.repaint();
        setText(text);
    }

    /** 
     * Returns the query around the specified (cursor) position.
     *
     * @param the position
     * @return the query around the specified position
     */
    private String getQueryAt(int position) {
        String text = getText();
        //Log.debug("position: " + position);
        if (MiscUtils.isNull(text)) {
            return Constants.EMPTY;
        }
        
        char[] chars = text.toCharArray();

        if (position == chars.length) {
            position--;
        }
        
        int start = -1;
        int end = -1;
        boolean wasSpaceChar = false;

        // determine the start point
        for (int i = position; i >= 0; i--) {
            if (chars[i] == Constants.NEW_LINE_CHAR) {

                if (i == 0 || wasSpaceChar) {
                    break;
                }
                else if (start != -1) {
                    if(chars[i - 1] == Constants.NEW_LINE_CHAR) {
                        break;
                    }
                    else if (Character.isSpaceChar(chars[i - 1])) {
                        wasSpaceChar = true;
                        i--;
                    }
                }

            }
            else if (!Character.isSpaceChar(chars[i])) {
                wasSpaceChar = false;
                start = i;
            }

        }

        if (start < 0) { // text not found
            for (int j = 0; j < chars.length; j++) {
                if (!Character.isWhitespace(chars[j])) {
                    start = j;
                    break;
                }
            }
        }

        // determine the end point 
        for (int i = start; i < chars.length; i++) {

            if (chars[i] == Constants.NEW_LINE_CHAR) {

                if (i == chars.length - 1 || wasSpaceChar) {
                    if (end == -1) {
                        end = i;
                    }
                    break;
                }                
                else if (end != -1) {
                    if(chars[i + 1] == Constants.NEW_LINE_CHAR) {
                        break;
                    }
                    else if (Character.isSpaceChar(chars[i + 1])) {
                        wasSpaceChar = true;
                        i++;
                    }
                }

            }
            else if (!Character.isSpaceChar(chars[i])) {
                end = i;
                wasSpaceChar = false;
            }
        }

        //Log.debug("start: " + start + " end: " + end);
        
        String query = text.substring(start, end + 1);
        //Log.debug(query);

        if ((MiscUtils.isNull(query) && start != 0) ||
                start == end) {
            return getQueryAt(start);
        }
        
        return query;
    }

    // ----------------------------------------
    // DocumentListener implementation
    // ----------------------------------------
    
    /**
     * Does nothing.
     */
    public void changedUpdate(DocumentEvent e) {}

    /**
     * Notifies the parent QueryPanel that the text content
     * has changed and resets the line number border panel.
     *
     * @param the event object
     */
    public void insertUpdate(DocumentEvent e) {
        editorPanel.setContentChanged(true);
        lineBorder.resetExecutingLine();
    }

    /**
     * Notifies the parent QueryPanel that the text content
     * has changed and resets the line number border panel.
     *
     * @param the event object
     */
    public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

    // ----------------------------------------
    
    /**
     * Resets the executing line within the line 
     * number border panel.
     */
    public void resetExecutingLine() {
        lineBorder.resetExecutingLine();
    }
    
    /**
     * Executes the query determined to be around the current 
     * cursor position using the database connection object 
     * specified.
     *
     * @param the database connection object to execute the query
     */
    public void executeSQLAtCursor(DatabaseConnection dc) {
        String query = getQueryAt(getCaretPosition());
        if (MiscUtils.isNull(query)) {
            return;
        }

        int index = getText().indexOf(query);
        if (query.charAt(0) == Constants.NEW_LINE_CHAR) {
            index++;
        }

        lineBorder.setExecutingLine(getRowAt(index));
        lineBorder.repaint();
        editorPanel.executeSQLQuery(dc, query);
    }

    /**
     * Returns the text as contained within the editor's
     * text panel.
     *
     * @return the editor's text
     */
    public String getQueryAreaText() {
        return getText();
    }

    /** 
     * Overrides <code>processKeyEvent</code> to additional process events. 
     */
    protected void processKeyEvent(KeyEvent e) {
        
        if (e.getID() == KeyEvent.KEY_PRESSED) {

            int keyCode = e.getKeyCode();

            // add the processing for SHIFT-TAB
            if (e.isShiftDown() && keyCode == KeyEvent.VK_TAB) {
                addUndoEdit();
                int currentPosition = getCurrentPosition();
                int selectionStart = getSelectionStart();
                int selectionEnd = getSelectionEnd();
                
                if (selectionStart == selectionEnd) {
                    
                    int newPosition = currentPosition - QueryEditorSettings.getTabSize();
                    int currentRowPosition = getCurrentRowStart();

                    if (!isAtStartOfRow()) {
                        
                        if (newPosition < 0) {
                            setCaretPosition(0);
                        }                        
                        else if (newPosition < currentRowPosition) {
                            setCaretPosition(currentRowPosition);
                        }                        
                        else {
                            setCaretPosition(newPosition);
                        }

                    }

                }
                else {
                    document.shiftTabEvent(selectionStart, selectionEnd);
                }
                
            }

            // toggle insert mode on the document
            else if (keyCode == KeyEvent.VK_INSERT) {
                int insertMode = document.getInsertMode();
                if (insertMode == QueryEditorConstants.INSERT_MODE) {
                    document.setInsertMode(QueryEditorConstants.OVERWRITE_MODE);
                    editorPanel.getStatusBar().setInsertionMode("OVR");
                } 
                else {
                    document.setInsertMode(QueryEditorConstants.INSERT_MODE);
                    editorPanel.getStatusBar().setInsertionMode("INS");                    
                }
                ((EditorCaret)getCaret()).modeChanged();
            }

        }

        super.processKeyEvent(e);        
        updateLineBorder();
    }

    /** the last element count for line border updates */
    private int lastElementCount;

    /**
     * Updates the line border values.
     */
    private void updateLineBorder() {
        int elementCount = document.getDefaultRootElement().getElementCount();
        if (elementCount != lastElementCount) {
            lineBorder.setRowCount(elementCount);
            lastElementCount = elementCount;
        }        
    }
    
    /**
     * Cuts the editor's selected text.
     */
    public void cut() {
        addUndoEdit();
        super.cut();
    }
    
    /**
     * Pastes any previously copied/cut text into the 
     * editor at the cursor or mouse pointer position.
     */
    public void paste() {
        addUndoEdit();
        super.paste();
    }
    
    
    /**
     * Returns true if an undo operation would be 
     * successful now, false otherwise.
     */
    protected boolean canUndo() {
        return undoManager.canUndo();
    }

    /**
     * Completes a compound edit and adds an 
     * undoable edit to the undo manager.
     */
    protected void addUndoEdit() {
        undoManager.addUndoEdit();
    }
    
    /**
     * Executes the undo action.
     */
    public void undo() {
        undoManager.undo();
        updateLineBorder();
    }
    
    /**
     * Executes the redo action.
     */
    public void redo() {
        undoManager.redo();
        updateLineBorder();
    }

    // ----------------------------------------
    // FocusListener implementation
    // ----------------------------------------
    
    /**
     * Updates the state of undo/redo ona focus gain.
     */
    public void focusGained(FocusEvent e) {
        if (e.getSource() != this && editorPanel != null) {
            editorPanel.focusGained();
        }
    }

    /**
     * Updates the state of undo/redo on a focus lost.
     */
    public void focusLost(FocusEvent e) {
        if (editorPanel != null) {
            editorPanel.focusLost();
        }
    }

    // ----------------------------------------
    
    private int currentRow = 0;
    private int currentPosition = 0;

    /**
     * Returns the row number at the specified position.
     *
     * @param position - the position
     */
    protected int getRowAt(int position) {
        Element map = getElementMap();
        return map.getElementIndex(position);        
    }

    /**
     * Called when the caret position is updated.
     *
     * @param e the caret event
     */
    public void caretUpdate(CaretEvent ce) {
        super.caretUpdate(ce);
        currentPosition = getCaretPosition();

        Element map = getElementMap();
        int row = map.getElementIndex(currentPosition);

        if (currentRow != row) {
            currentRow = row;
            //lineBorder.setRowCount(map.getElementCount());
        }

        Element lineElem = map.getElement(row);
        int col = currentPosition - lineElem.getStartOffset();
        editorPanel.getStatusBar().setCaretPosition(row + 1, col + 1);

        repaint();
    }

    protected boolean isAtStartOfRow() {
        return currentPosition == getRowPosition(currentRow);
    }

    /**
     * Returns the start offset of the current row.
     *
     * @return the current row start offset
     */
    protected int getCurrentRowStart() {
        return getElementMap().getElement(currentRow).getStartOffset();
    }

    /**
     * Returns the end offset of the current row.
     *
     * @return the current row end offset
     */
    protected int getCurrentRowEnd() {
        return getElementMap().getElement(currentRow).getEndOffset();
    }

    /**
     * Returns the start offset of the specified row.
     *
     * @param row - the row
     * @return the start offset of row
     */
    protected int getRowStartOffset(int row) {
        try {
            return getElementMap().getElement(row).getStartOffset();
        } catch (Exception e) { // where row passed is dumb value
            return -1;
        }
    }

    /**
     * Returns the end offset of the specified row.
     *
     * @param row - the row
     * @return the end offset of row
     */
    protected int getRowEndOffset(int row) {
        try {
            return getElementMap().getElement(row).getEndOffset();
        } catch (Exception e) { // where row passed is dumb value
            return -1;
        }
    }
    
    /**
     * Returns the start offset of the specified row.
     *
     * @param row - a row in the editor
     * @return the start offset of row
     */
    protected int getRowPosition(int row) {
        try {
            return getElementMap().getElement(row).getStartOffset();
        }
        catch (NullPointerException nullExc) {
            return -1;
        }
    }

    /**
     * Returns the document's root element
     */
    protected Element getElementMap() {
        return getDocument().getDefaultRootElement();
    }

    /**
     * Returns the current caret position.
     */
    protected int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Returns the row number of the current cursor position.
     *
     * @return the current caret row number
     */
    protected int getCurrentCursorRow() {
        return currentRow;
    }

    
    class EditorCaret extends DefaultCaret {

        void modeChanged() {
            repaint();
        }
        
        public void paint(Graphics g) {
            if(document.getInsertMode() == QueryEditorConstants.INSERT_MODE) {
                super.paint(g);
                return;
            }
            JTextComponent comp = getComponent();

            char c;
            int dot = getDot();
            Rectangle r = null;
            try {
                r = comp.modelToView(dot);
                if(r == null) {
                   return;
                }
                c = comp.getText(dot, 1).charAt(0);
            } 
            catch(BadLocationException e) {
                return;
            }

            // erase provious caret
            if ((x != r.x) || (y != r.y)) {
                repaint();
                x = r.x;
                y = r.y;
                height = r.height;
            }

            g.setColor(comp.getCaretColor());
            g.setXORMode(comp.getBackground());

            width = g.getFontMetrics().charWidth(c);
            if (c == '\t' || c == '\n') {
                width = g.getFontMetrics().charWidth('W');
            }

            if (isVisible()) {
                g.fillRect(r.x, r.y, width, r.height);
            }

        }

    }
    
}



