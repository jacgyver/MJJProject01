/*
 * OpenComponentRegister.java
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


package org.executequery.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.SwingConstants;
import org.executequery.GUIUtilities;
import org.executequery.base.DockedTabEvent;
import org.executequery.base.DockedTabListener;
import org.executequery.base.DockedTabView;
import org.executequery.base.TabComponent;
import org.underworldlabs.swing.GUIUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Maintains a register of open central tab panels 
 * and dialogs for quick determination of what is/isn't open,
 * how many etc...
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/06/04 14:19:04 $
 */
public class OpenComponentRegister implements DockedTabListener {
    
    /** open dialog cache */
    private List<JDialog> openDialogs;
    
    /** open main panel cache */
    private List<PanelCacheObject> openPanels;
    
    /** the currently selected main component */
    private Component selectedComponent;
    
    /** Creates a new instance of OpenComponentRegister */
    public OpenComponentRegister() {}
    
    /**
     * Ensures the dialog cache is created.
     */
    private void initDialogChache() {
        if (openDialogs == null) {
            openDialogs = new ArrayList<JDialog>();
        }
    }

    /**
     * Ensures the panel cache is created.
     */
    private void initPanelChache() {
        if (openPanels == null) {
            openPanels = new ArrayList<PanelCacheObject>();
        }
    }

    /**
     * Adds the specified dialog to the open cache.
     *
     * @param the dialog to be added
     */
    public void addOpenPanel(String name, Component component) {
        initPanelChache();
        openPanels.add(new PanelCacheObject(name, component));
    }

    /**
     * Returns the open panel count as registered with this object .
     */
    public int getOpenPanelCount() {
        return (openPanels == null) ? 0 : openPanels.size();
    }

    /**
     * Returns the open panels registered with this object
     * as a list of <code>PanelCacheObject</code>s.
     * 
     * @return the list of <code>PanelCacheObject</code>s
     */
    public List<PanelCacheObject> getOpenPanels() {
        return openPanels;
    }

    /**
     * Returns the open dialog count as registered with this object .
     */
    public int getOpenDialogCount() {
        return (openDialogs == null) ? 0 : openDialogs.size();
    }

    /**
     * Returns the open dialogs registered with this object
     * as a list of <code>JDialog</code>s.
     * 
     * @return the list of <code>JDialog</code>s
     */
    public List<JDialog> getOpenDialogs() {
        return openDialogs;
    }

    /**
     * Returns whether the panel with the specified
     * name is open - within the opne panel cache.
     *
     * @param the name of the panel
     * @return true | false
     */
    public boolean isPanelOpen(String name) {
        if (openPanels == null || openPanels.isEmpty()) {
            return false;
        }
        for (int i = 0, k = openPanels.size(); i < k; i++) {
            PanelCacheObject object = openPanels.get(i);
            if (object.getName().startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the open panel component with the specified name.
     *
     * @param the name to search for
     * @return the component, or null if not found
     */
    public Component getOpenPanel(String name) {
        if (openPanels == null || openPanels.isEmpty()) {
            return null;
        }
        for (int i = 0, k = openPanels.size(); i < k; i++) {
            PanelCacheObject object = openPanels.get(i);
            if (object.getName().startsWith(name)) {
                return object.getComponent();
            }
        }
        return null;
    }
    
    /**
     * Returns whether the dialog with the specified
     * name is open - within the open dialog cache.
     *
     * @param the name of the dialog
     * @return true | false
     */
    public boolean isDialogOpen(String name) {
        if (openDialogs == null || openDialogs.isEmpty()) {
            return false;
        }
        for (int i = 0, k = openDialogs.size(); i < k; i++) {
            JDialog dialog = openDialogs.get(i);
            if (dialog.getName().startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the dialog with the specified
     * name is open - within the open dialog cache.
     *
     * @param the name of the dialog
     * @return true | false
     */
    public JDialog getOpenDialog(String name) {
        if (openDialogs == null || openDialogs.isEmpty()) {
            return null;
        }
        for (int i = 0, k = openDialogs.size(); i < k; i++) {
            JDialog dialog = openDialogs.get(i);
            if (dialog.getName().startsWith(name)) {
                return dialog;
            }
        }
        return null;
    }

    /**
     * Adds the specified dialog to the open cache.
     *
     * @param the dialog to be added
     */
    public void addDialog(JDialog dialog) {
        initDialogChache();
        openDialogs.add(dialog);
    }

    /**
     * Removes the specified dialog from the open cache.
     *
     * @param the dialog to be removed
     */
    public void removeDialog(JDialog dialog) {
        if (openDialogs != null && !openDialogs.isEmpty()) {
            openDialogs.remove(dialog);
        }
    }

    /**
     * Indicates a tab minimised event.
     *
     * @param the event 
     */
    public void tabMinimised(DockedTabEvent e) {}

    /**
     * Indicates a tab restored from minimised event.
     *
     * @param the event 
     */
    public void tabRestored(DockedTabEvent e) {}

    /**
     * Indicates a tab selected event.
     *
     * @param the event 
     */
    public void tabSelected(DockedTabEvent e) {
        TabComponent tabComponent = (TabComponent)e.getSource();
        
        int position = tabComponent.getPosition();
        if (position == SwingConstants.CENTER) {
            selectedComponent = tabComponent.getComponent();
        }
        else {
            Component component = tabComponent.getComponent();
            switch (position) {
                case SwingConstants.NORTH_WEST:
                case SwingConstants.SOUTH_WEST:
                case SwingConstants.SOUTH:
                case SwingConstants.NORTH_EAST:
                case SwingConstants.SOUTH_EAST:
                    if (component instanceof DockedTabView) {
                        GUIUtilities.dockedTabComponentSelected(
                                        ((DockedTabView)component).getPropertyKey());
                    }
                    break;
            }
        }

    }

    /**
     * Indicates a tab deselected event.
     *
     * @param the event 
     */
    public void tabDeselected(DockedTabEvent e) {
        TabComponent tabComponent = (TabComponent)e.getSource();
        
        //Log.debug("tab deselected");

        int position = tabComponent.getPosition();
        if (position == SwingConstants.CENTER) {
            selectedComponent = null;
        }
    }
    
    /** 
     * Returns the selected component as registered within
     * this object cache.
     *
     * @return the selected main component or null if there is
     *         no selected component
     */
    public Component getSelectedComponent() {
        return selectedComponent;
    }
    
    /**
     * Indicates a tab closed event.
     *
     * @param the event 
     */
    public void tabClosed(DockedTabEvent e) {
        TabComponent tabComponent = (TabComponent)e.getSource();

        //Log.debug("tab closing: "+tabComponent.getTitle());
        
        // retrieve the position and component
        int position = tabComponent.getPosition();
        Component component = tabComponent.getComponent();

        if (position == SwingConstants.CENTER) {
            PanelCacheObject object = getPanelCacheObject(component);
            openPanels.remove(object);
            object.clear();
            if (selectedComponent == component) {
                selectedComponent = null;
            }
        }
        // check if its docked view and reset associated items
        else {
            switch (position) {
                case SwingConstants.NORTH_WEST:
                case SwingConstants.SOUTH_WEST:
                case SwingConstants.SOUTH:
                case SwingConstants.NORTH_EAST:
                case SwingConstants.SOUTH_EAST:
                    if (component instanceof DockedTabView) {
                        GUIUtilities.dockedTabComponentClosed(
                                        ((DockedTabView)component).getPropertyKey());
                    }
                    break;
            }

        }
        component = null;
        GUIUtils.scheduleGC();
    }

    /** 
     * Returns the cache object containing the specified
     * component or null if not found.
     *
     * @param the component to search for
     * @return the cache object
     */
    private PanelCacheObject getPanelCacheObject(Component component) {
        if (openPanels == null || openPanels.size() == 0) {
            return null;
        }
        for (int i = 0, k = openPanels.size(); i < k; i++) {
            PanelCacheObject object = openPanels.get(i);
            if (object.getComponent() == component) {
                return object;
            }
        }
        return null;
    }
    
    /** 
     * Returns the cache object containing the specified
     * name or null if not found.
     *
     * @param the name to search for
     * @return the cache object
     */
    private PanelCacheObject getPanelCacheObject(String name) {
        if (openPanels == null || openPanels.size() == 0) {
            return null;
        }
        for (int i = 0, k = openPanels.size(); i < k; i++) {
            PanelCacheObject object = openPanels.get(i);
            if (object.getName().startsWith(name)) {
                return object;
            }
        }
        return null;
    }

    /** 
     * Extracts and returns the component of the specified
     * docked tab event.
     *
     * @param the event
     * @return the encapsulated component
     */
    private Component getComponent(DockedTabEvent e) {
        TabComponent source = (TabComponent)e.getSource();
        return source.getComponent();
    }
    
}



