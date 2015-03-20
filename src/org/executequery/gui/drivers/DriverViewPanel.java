/*
 * DriverViewPanel.java
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


package org.executequery.gui.drivers;

import org.executequery.base.TabView;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.gui.browser.HostPanel;
import org.executequery.gui.forms.FormObjectViewContainer;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class DriverViewPanel extends FormObjectViewContainer 
                             implements TabView {
    
    public static final String TITLE = "Drivers";
    public static final String FRAME_ICON = "DatabaseDrivers16.gif";

    /** the parent panel containing the selection tree */
    private DriversTreePanel parent;
    
    /** Creates a new instance of DriverViewPanel */
    public DriverViewPanel(DriversTreePanel parent) {
        super();
        this.parent = parent;
    }

    public void displayRootPanel() {
        DriverListPanel panel = null;
        if (!containsPanel(DriverListPanel.NAME)) {
            panel = new DriverListPanel(this);
            addToLayout(panel);
        } 
        else {
            panel = (DriverListPanel)getFormObjectView(DriverListPanel.NAME);
        }
        setView(panel);
    }
    
    /** 
     * Adds a new driver.
     */
    protected void addNewDriver() {
        parent.newDriver();
    }

    /** 
     * Sets the selected driver tree node to the specified driver.
     *
     * @param driver - the driver to select
     */
    public void setSelectedDriver(DatabaseDriver driver) {
        parent.setSelectedDriver(driver);
    }

    /**
     * Indicates that a node name has changed and fires a call
     * to repaint the tree display.
     */
    protected void nodeNameValueChanged(DatabaseDriver driver) {
        parent.nodeNameValueChanged(driver);
    }

    public void valueChanged(DatabaseDriverNode node) {
        DriversPanel panel = null;
        if (!containsPanel(DriversPanel.TITLE)) {
            panel = new DriversPanel(this);
            addToLayout(panel);
        } 
        else {
            panel = (DriversPanel)getFormObjectView(DriversPanel.TITLE);
        }
        panel.setDriver(node.getDriver());
        setView(panel);
    }
    
    protected boolean saveDrivers() {
        if (containsPanel(DriversPanel.TITLE)) {
            DriversPanel panel = (DriversPanel)
                            getFormObjectView(DriversPanel.TITLE);
            return panel.saveDrivers();
        } 
        return true;
    }
    
    // --------------------------------------------
    // DockedTabView implementation
    // --------------------------------------------

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        if (containsPanel(DriversPanel.TITLE)) {
            DriversPanel panel = (DriversPanel)
                            getFormObjectView(DriversPanel.TITLE);
            return panel.tabViewClosing();
        } 
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewDeselected() {
        if (containsPanel(DriversPanel.TITLE)) {
            DriversPanel panel = (DriversPanel)
                            getFormObjectView(DriversPanel.TITLE);
            return panel.tabViewDeselected();
        } 
        return true;
    }

    // --------------------------------------------

}



