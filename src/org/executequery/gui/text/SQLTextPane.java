/*
 * SQLTextPane.java
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


package org.executequery.gui.text;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import org.executequery.KeywordProperties;
import org.executequery.gui.editor.QueryEditorSettings;
import org.executequery.gui.text.syntax.SQLSyntaxDocument;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Base SQL text pane object.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class SQLTextPane extends JTextPane
                         implements CaretListener {
    
    /** The SQL syntax document */
    protected SQLSyntaxDocument document;
    
    /** whether a caretUpdate will be called */
    private boolean doCaretUpdate;

    /** The current font width for painting */
    protected int fontWidth;

    /** The current font height for painting */
    protected int fontHeight;

    /** Creates a new instance of SQLTextPane */
    public SQLTextPane() {
        document = new SQLSyntaxDocument(
                            KeywordProperties.getSQLKeywords(), this);
        setDocument(document);
        addCaretListener(this);
        setEditorPreferences();
    }

    /**
     * Returns the SQL syntax document associated with this component.
     *
     * @return the SQL document
     */
    public SQLSyntaxDocument getSQLSyntaxDocument() {
        return document;
    }
    
    public void setSQLKeywords(boolean reset) {
        document.setSQLKeywords(KeywordProperties.getSQLKeywords(), reset);
    }

    /** 
     * Overrides <code>processKeyEvent</code> to check for a caret update. 
     */
    protected void processKeyEvent(KeyEvent e) {
        doCaretUpdate = !(e.getID() == KeyEvent.KEY_TYPED);
        super.processKeyEvent(e);
    }

    /**
     * Clears all the text in the text pane.
     */
    public void deleteAll() {
        try {
            document.replace(0, document.getLength(), "", null);
        } catch (BadLocationException badLoc) {}
    }
    
    /**
     * Sets the user defined preferences on the text pane.
     */
    protected void setEditorPreferences() {
        setSelectionColor(QueryEditorSettings.getSelectionColour());
        setSelectedTextColor(QueryEditorSettings.getSelectedTextColour());

        Font font = QueryEditorSettings.getEditorFont();
        setFont(font);

        FontMetrics fm = getFontMetrics(font);
        fontWidth = fm.charWidth('w');
        fontHeight = fm.getHeight();

        boolean tabsToSpaces = QueryEditorSettings.isTabsToSpaces();
        int tabSize = QueryEditorSettings.getTabSize();
        
        if (!tabsToSpaces) {
            setTabs(tabSize);
        }

        document.setTabsToSpaces(tabsToSpaces);
        setCaretColor(QueryEditorSettings.getCaretColour());
    }

    /**
     * Called when the caret position is updated.
     *
     * @param e the caret event
     */
    public void caretUpdate(CaretEvent e) {
        if (doCaretUpdate) {
            final int dot = e.getDot();
            final int mark = e.getMark();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    document.resetBracePosition();
                    if (dot == mark) {
                        document.updateBraces(getCaretPosition());
                    }
                }
            });
        }
    }
    
    public void disableUpdates(boolean disable) {
        if (disable) {
            String text = getText();
            setDocument(new DefaultStyledDocument());
            setText(text);
        }
        else {
            String text = getText();
            setDocument(document);
            setText(text);
        }
    }

    /**
     * Override to ensure no wrap of text.
     */
    public boolean getScrollableTracksViewportWidth() {
        return getSize().width < getParent().getSize().width;
    }

    /**
     * Override to ensure no wrap of text.
     */
    public void setSize(Dimension d) {
        if (d.width < getParent().getSize().width) {
            d.width = getParent().getSize().width;
        }        
        super.setSize(d);
    }

    /**
     * Sets the size of a TAB as specified.
     */
    private void setTabs(int charactersPerTab) {
       
        int tabWidth = fontWidth * charactersPerTab;
        
        TabStop[] tabs = new TabStop[10];
        
        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }
        
        TabSet tabSet = new TabSet(tabs);
        
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        
        document.setParagraphAttributes(0, document.getLength(), attributes, true);
    }

}



