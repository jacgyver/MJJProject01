/*
 * QueryEditorToolBar.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.executequery.Constants;
import org.underworldlabs.swing.actions.ActionBuilder;

import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.swing.toolbar.PanelToolBar;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The Query Editor's tool bar.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/09/13 15:15:09 $
 */
public class QueryEditorToolBar extends PanelToolBar {
    
    public static final String NAME = "Query Editor Tool Bar";
    
    /** button access map */
    private Map<String, RolloverButton> buttons;
    
    /** Constructs a new instance. */
    public QueryEditorToolBar() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /** 
     * Initializes the state of this instance. 
     */
    private void jbInit() throws Exception {
        buttons = new HashMap<String,RolloverButton>();

        addButton(createButton("execute-command", 
                     "Execute the contents of the query editor"));

        addButton(createButton("execute-at-cursor-command", 
                     "Execute query at cursor"));
        
        addButton(createButton("execute-selection-command", 
                     "Execute the current text selection"));
        
        addButton(createButton("editor-stop-command", 
                     "Cancel Current Statement"));
        
        addSeparator();

        addButton(createButton("clear-editor-output-command", 
                     "Clear the editor's output log panel"));

        addButton(createButton("sql-history-command", 
                     "SQL command history"));
        
        addButton(createButton("editor-previous-command", 
                     "Previous Statement"));

        addButton(createButton("editor-next-command", 
                     "Next Statement"));

        addSeparator();
        
        addButton(createButton("commit-command", 
                     "Commit all changes since last commit/rollback"));
        
        addButton(createButton("rollback-command", 
                     "Rollback all changes since last commit/rollback"));
  
        addButton(createButton("toggle-autocommit-command", 
                     "Toggle auto-commit on/off"));

        addButton(createButton("editor-conn-change-command", 
                     "Closes the editor's connection and retrieves another from the pool"));

        addSeparator();

        addButton(createButton("editor-rs-metadata-command", 
                     "Display this result set's meta data"));

        addButton(createButton("editor-export-command", 
                     "Export the selected result set to file"));

        addSeparator();

        addButton(createButton("toggle-editor-output-command", 
                     "Show/hide the output pane"));

        addSeparator();
        
        addButton(createButton("shift-text-left-command", 
                     "Shift line/selection left"));

        addButton(createButton("shift-text-right-command", 
                     "Shift line/selection right"));
        
        addSeparator();
        
        addButton(createButton("comment-lines-command", 
                     "Comment"));

        addButton(createButton("remove-comment-lines-command", 
                     "Uncomment"));

        addSeparator();
        
        addButton(createButton("editor-help-command", 
                     "Query Editor help"));
        
    }
    
    /**
     * Enables/disables all tool bar buttons as specified.
     *
     * @param true | false
     */
    public void enableAllButtons(boolean enable) {
        for (Iterator i = buttons.keySet().iterator(); i.hasNext();) {
            String key = i.next().toString();
            buttons.get(key).setEnabled(enable);
        }
    }

    /**
     * Enables/disables the button with the specified action ID.
     *
     * @param actionId - the action ID string
     * @param enable true | false
     */
    public void setButtonEnabled(String actionId, boolean enable) {
        RolloverButton button = buttons.get(actionId);
        if (button != null) {
            button.setEnabled(enable);
        }
    }
    
    public void setMetaDataButtonEnabled(boolean enable) {
        buttons.get("editor-rs-metadata-command").setEnabled(enable);
    }
    
    public void setPreviousButtonEnabled(boolean enable) {
        buttons.get("editor-previous-command").setEnabled(enable);
    }

    public void setNextButtonEnabled(boolean enable) {
        buttons.get("editor-next-command").setEnabled(enable);
    }

    public void setStopButtonEnabled(boolean enable) {
        buttons.get("editor-stop-command").setEnabled(enable);
        buttons.get("execute-command").setEnabled(!enable);
        buttons.get("execute-at-cursor-command").setEnabled(!enable);
        buttons.get("execute-selection-command").setEnabled(!enable);
    }
    
    public void setCommitsEnabled(boolean enable) {
        buttons.get("commit-command").setEnabled(enable);
        buttons.get("rollback-command").setEnabled(enable);
    }
    
    public void setExportButtonEnabled(boolean enable) {
        buttons.get("editor-export-command").setEnabled(enable);
    }
    
    public String toString() {
        return NAME;
    }
    
    /**
     * Creates a button with the action specified by the action name
     * and with the specified tool tip text.
     */
    private RolloverButton createButton(String actionId, String toolTipText) {
        RolloverButton button = 
                new RolloverButton(ActionBuilder.get(actionId), toolTipText);
        button.setText(Constants.EMPTY);
        buttons.put(actionId, button);
        return button;
    }
    
}




