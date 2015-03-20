/*
 * BrowserViewPanel.java
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
import java.awt.print.Printable;
import org.executequery.base.TabView;
import org.executequery.print.PrintFunction;
import org.executequery.gui.forms.FormObjectViewContainer;
import org.executequery.gui.text.TextEditor;
import org.executequery.gui.text.TextEditorContainer;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Base panel bor browser tree selection views.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class BrowserViewPanel extends FormObjectViewContainer 
                              implements TabView,
                                         PrintFunction,
                                         TextEditorContainer {
    
    /** The title to be applied to the <code>JInternalFrame</code> */
    public static final String TITLE = "Database Browser";

    /** The icon to be applied to the <code>JInternalFrame</code> */
    public static final String FRAME_ICON = "DBmag16.gif";
    
    /** the browser's control object */
    private BrowserController controller;

    /** Creates a new instance of DatabaseViewPanel */
    public BrowserViewPanel(BrowserController controller) {
        this.controller = controller;
    }

    /**
     * Saves the connection data to file.
     */
    public boolean saveConnections() {
        if (isEmpty()) {
            return true;
        }
        if (containsPanel(HostPanel.NAME)) {
            HostPanel hostPanel = (HostPanel)getFormObjectView(HostPanel.NAME);
            return hostPanel.saveConnections();
        }
        return true;
    }

    /**
     * Informs any panels of a new selection being made.
     */
    protected void selectionChanging() {
        if (isEmpty()) {
            return;
        }
        if (containsPanel(HostPanel.NAME)) {
            HostPanel hostPanel = (HostPanel)getFormObjectView(HostPanel.NAME);
            hostPanel.selectionChanging();
        }        
    }
    
    /**
     * Performs the drop database object action.
     */
    public void dropSelectedObject() {
        controller.dropSelectedObject();
    }
    
    // --------------------------------------------
    // DockedTabView implementation
    // --------------------------------------------

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        return saveConnections();
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        // update the driver list on the host panel
        if (containsPanel(HostPanel.NAME)) {
            HostPanel hostPanel = (HostPanel)getFormObjectView(HostPanel.NAME);
            hostPanel.tabViewSelected();
        }
        return true;
    }

    /**
     * Indicates the panel is being de-selected in the pane
     */
    public boolean tabViewDeselected() {
        if (isEmpty()) {
            return true;
        }

        if (currentView instanceof HostPanel) {
            HostPanel hostPanel = (HostPanel)getFormObjectView(HostPanel.NAME);
            return hostPanel.tabViewDeselected();
        }
        return true;
    }

    // --------------------------------------------
    
    protected BrowserTableEditingPanel getEditingPanel() {
        BrowserTableEditingPanel panel = null;
        if (!containsPanel(BrowserTableEditingPanel.NAME)) {
            panel = new BrowserTableEditingPanel(controller);
            addToLayout(panel);
        } 
        else {
            panel = (BrowserTableEditingPanel)
                getFormObjectView(BrowserTableEditingPanel.NAME);
        }
        return panel;
    }

    public void displayRootPanel() {
        ConnectionsListPanel panel = null;
        if (!containsPanel(ConnectionsListPanel.NAME)) {
            panel = new ConnectionsListPanel(controller);
            addToLayout(panel);
        } 
        else {
            panel = (ConnectionsListPanel)getFormObjectView(ConnectionsListPanel.NAME);
        }
        setView(panel);
    }

    // ------------------------------------------------
    // ----- TextEditorContainer implementations ------
    // ------------------------------------------------
    
    /**
     * Returns the SQL text pane as the TextEditor component 
     * that this container holds.
     */
    public TextEditor getTextEditor() {
        if (currentView instanceof BrowserTableEditingPanel) {
            return ((BrowserTableEditingPanel)currentView).getFocusedTextEditor();
        }
        return null;
    }

    // --------------------------------------------------
    // PrintFunction implementation
    // --------------------------------------------------

    /** 
     * The name for this print job.
     *
     * @return the print job's name
     */
    public String getPrintJobName() {
        return "Execute Query - Database Browser";
    }
    
    /** 
     * Returns whether the current browser panel has a printable.
     *
     *  @return true | false
     */
    public boolean canPrint() {
        return getPrintable() != null;
    }
    
    /** 
     * Returns the <code>Printable</code> object. 
     */
    public Printable getPrintable() {
        if (currentView != null) {
            return currentView.getPrintable();
        } else {
            return null;
        }        
    }

    // --------------------------------------------------
    
}



