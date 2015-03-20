/*
 * SimpleCloseTabbedPane.java
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


package org.underworldlabs.swing;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;

import org.underworldlabs.swing.plaf.CloseTabbedPaneUI;
import org.underworldlabs.swing.plaf.TabRollOverListener;
import org.underworldlabs.swing.plaf.TabRolloverEvent;

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
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class SimpleCloseTabbedPane extends JTabbedPane {
    
    private List<TabRollOverListener> rollListeners;
    
    public SimpleCloseTabbedPane() {
        this(TOP, SCROLL_TAB_LAYOUT);
    }

    public SimpleCloseTabbedPane(int tabPlacement) {
        this(tabPlacement, SCROLL_TAB_LAYOUT);
    }

    public SimpleCloseTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
    }

    public void fireTabRollOver(TabRolloverEvent e) {
        if (rollListeners == null || rollListeners.isEmpty()) {
            return;
        }

        for (int i = 0, k = rollListeners.size(); i < k; i++) {
            rollListeners.get(i).tabRollOver(e);
        }
    }

    public void fireTabRollOverFinished(TabRolloverEvent e) {
        if (rollListeners == null || rollListeners.isEmpty()) {
            return;
        }

        for (int i = 0, k = rollListeners.size(); i < k; i++) {
            rollListeners.get(i).tabRollOverFinished(e);
        }        
    }

    public void addTabRollOverListener(TabRollOverListener listener) {
        if (rollListeners == null) {
            rollListeners = new ArrayList<TabRollOverListener>();
        }
        rollListeners.add(listener);
        
    }

    public void removeTabRollOverListener(TabRollOverListener listener) {
        if (rollListeners == null) {
            return;
        }
        rollListeners.remove(listener);
    }

    public void addTab(String title, Component component) {
        addTab(title, null, component, null);
    }
    
    public void addTab(String title, Icon icon, Component component) {
        addTab(title, icon, component, null);
    }

    public void addTab(String title, Icon icon, Component component, String tip) {        
        // make sure the pane is visible - may have been empty
        if (!isVisible()) {
            setVisible(true);
        }
        super.addTab(title, icon, component, tip);
    }

    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        // make sure the pane is visible - may have been empty
        if (!isVisible()) {
            setVisible(true);
        }
        super.insertTab(title, icon, component, tip, index);
    }

    public void removeAll() {
        super.removeAll();
        setVisible(false);
    }

    public void remove(int index) {
        super.remove(index);
        if (getTabCount() == 0) {
            setVisible(false);
        }
    }

    public void remove(Component c) {
        super.remove(c);
        if (getTabCount() == 0) {
            setVisible(false);
        }
    }

    protected CloseTabbedPaneUI tabUI;
    
    public TabbedPaneUI getUI() {
        return tabUI;
    }
    
    public void updateUI() {
        tabUI = new CloseTabbedPaneUI();
        setUI(tabUI);
    }
/*
    public void updateUI() {
        setUI(new CloseTabbedPaneUI());
    }
*/
}



