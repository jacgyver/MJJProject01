/*
 * ErdPrintableDialog.java
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


package org.executequery.gui.erd;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.Printable;

import javax.swing.JDialog;
import javax.swing.text.JTextComponent;
import org.executequery.EventMediator;

import org.executequery.GUIUtilities;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;
import org.executequery.gui.text.SimpleSqlTextPanel;
import org.executequery.gui.text.TextEditor;
import org.executequery.gui.text.TextEditorContainer;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * A non-modal dialog containing an <code>SQLTextPanel</code>
 * object. This base class provides the functionality as
 * indicated within the <code>TextEditor</code> interface
 * including cut/copy/paste, save and print functions.
 * 
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ErdPrintableDialog extends JDialog
                                implements TextEditorContainer,
                                           KeywordListener {
    
    /** The SQL text panel */
    protected SimpleSqlTextPanel sqlText;
    
    /** <p>Constructs a new instance with the specified name.
     *
     *  @param the name of this dialog
     */
    public ErdPrintableDialog(String name) {
        super(GUIUtilities.getParentFrame(), name, false);
        sqlText = new SimpleSqlTextPanel();
        
        GUIUtilities.setFocusedDialog(this);
        
        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                dialogFocusChanged(true);
            }
            public void focusLost(FocusEvent e) {
                dialogFocusChanged(false);
            }
        });
        EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
    }
    
    /** <p>Constructs a new instance with the specified name
     *  and whether the SQL text panel should be created.
     *
     *  @param the name of this dialog
     *  @param whether to create the SQL text panel
     */
    public ErdPrintableDialog(String name, boolean createSQLPanel) {
        super(GUIUtilities.getParentFrame(), name, false);
        
        if (createSQLPanel) {
            sqlText = new SimpleSqlTextPanel();
        }
        
        GUIUtilities.setFocusedDialog(this);
        
        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                dialogFocusChanged(true);
            }
            public void focusLost(FocusEvent e) {
                dialogFocusChanged(false);
            }
        });
        EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
    }
    
    /**
     * Notification of a new keyword added to the list.
     */
    public void keywordsAdded(KeywordEvent e) {
        if (sqlText != null) {
            sqlText.setSQLKeywords(true);
        }
    }

    /**
     * Notification of a keyword removed from the list.
     */
    public void keywordsRemoved(KeywordEvent e) {
        if (sqlText != null) {
            sqlText.setSQLKeywords(true);
        }
    }

    /** <p>Called for a change in focus as specified. This
     *  method will pass this object into <code>GUIUtilities</code>
     *  methods <code>setFocusedDialog(JDialog)</code> and
     *  <code>removeFocusedDialog(JDialog)</code> depending on
     *  the focus parameter specified.
     *
     *  @param whether this dialog has focus
     */
    private void dialogFocusChanged(boolean hasFocus) {
        
        if (hasFocus)
            GUIUtilities.setFocusedDialog(this);
        else
            GUIUtilities.removeFocusedDialog(this);
        
    }
    
    /** <p>Simple call to make this dialog visible. */
    protected void display() {
        pack();
        this.setLocation(GUIUtilities.getLocationForDialog(this.getSize()));
        setVisible(true);
    }
    
    /** <p>Removes this dialog from the application
     *  controller <code>GUIUtilities</code> object before
     *  a call to <code>super.dispose()</code>.
     */
    public void dispose() {
        EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
        GUIUtilities.removeFocusedDialog(this);
        super.dispose();
    }
    
    public String getDisplayName() {
        return "";
    }

    // ------------------------------------------------
    // ----- TextEditorContainer implementations ------
    // ------------------------------------------------
    
    /**
     * Returns the SQL text pane as the TextEditor component 
     * that this container holds.
     */
    public TextEditor getTextEditor() {
        return sqlText;
    }
    
}



