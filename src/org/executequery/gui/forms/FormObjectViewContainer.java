/*
 * FormObjectViewContainer.java
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


package org.executequery.gui.forms;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

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
 * @date     $Date: 2006/05/14 06:56:57 $
 */
public class FormObjectViewContainer extends JPanel {
    
    
    /** panel map */
    private Map<String,FormObjectView> panels;

    /** The view panel's layout */
    private CardLayout cardLayout;

    /** The current panel view */
    protected FormObjectView currentView;
    
    /**
     * Creates a new instance of FormObjectViewContainer 
     */
    public FormObjectViewContainer() {
        super();        
        // setup the panel and layout manager and apply
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        panels = new HashMap<String,FormObjectView>();
    }
    
    /**
     * Returns the current panel view object.
     *
     * @return the current view panel
     */
    public FormObjectView getCurrentView() {
        return currentView;
    }
    
    /**
     * Sets the current view to the specified panel.
     *
     * @param panel - the panel to be selected
     */
    public void setView(FormObjectView panel) {
        if (currentView != panel) {
            currentView = panel;
            cardLayout.show(this, panel.getLayoutName());
        }
        panel.validate();
        panel.repaint();
    }
    
    /**
     * Adds the specified component to the layout.
     *
     * @param panel - the component to add
     * @throws <code>IllegalArgumentException</code> if the
     *         panel specified is not an instance of <code>JPanel</code>
     */
    public void addToLayout(FormObjectView panel) {
        if (panel instanceof JPanel) {
            String title = panel.getLayoutName();
            JPanel _panel = (JPanel)panel;
            add(_panel, title);
            cardLayout.addLayoutComponent(_panel, title);
            
            // add to the cache if its not there
            if (!panels.containsKey(title)) {
                panels.put(title, panel);
            }
        }
        else {
            throw new IllegalArgumentException(
                    "Panel added to the layout must be an instance of JPanel");
        }
    }

    /**
     * Returns whether the layout cache is empty.
     *
     * @return true | false
     */
    public boolean isEmpty() {
        return panels == null || panels.isEmpty();
    }
    
    /**
     * Returns the panel with the specified name from the layout.
     *
     * @param name - the layout name of the panel
     * @return the panel view object
     */
    public FormObjectView getFormObjectView(String name) {
        return panels.get(name);
    }
    
    /**
     * Returns with the panel cache contains the panel
     * with the specified name.
     *
     * @param name - the layout name of the panel
     * @return true | false
     */
    public boolean containsPanel(String name) {
        if (panels == null) {
            return false;
        }
        return panels.containsKey(name);
    }
    
}



