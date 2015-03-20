/*
 * CloseTabContentPanel.java
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


package org.underworldlabs.swing.plaf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

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
public class CloseTabContentPanel extends JPanel {
    
    /** the highlight border width */
    private static final int BORDER_WIDTH = 4;
    
    /** The added tab panel border */
    private static Border border;

    /** the active color for the tab border */
    private static Color activeColor;
    
    /** the displayed component */
    private Component component;
    
    /** the associated menu item */
    private TabMenuItem tabMenuItem;
    
    /** Creates a new instance of CloseTabContentPanel */
    public CloseTabContentPanel(int tabPlacement, Component component) {
        super(new BorderLayout());
        
        this.component = component;
        
        if (activeColor == null) {
            activeColor = UIManager.getColor("InternalFrame.activeTitleBackground");
        }
        
        if (border == null) {
            switch (tabPlacement) {
                case JTabbedPane.TOP:
                    border = BorderFactory.createMatteBorder(
                                            BORDER_WIDTH, 0, 0, 0, activeColor);
                    break;
                case JTabbedPane.BOTTOM:
                    border = BorderFactory.createMatteBorder(
                                            0, 0, BORDER_WIDTH, 0, activeColor);
                    break;
            }
        }
        add(component, BorderLayout.CENTER);
        setBorder(border);
    }

    public Component getDisplayComponent() {
        return component;
    }
    
    public TabMenuItem getTabMenuItem() {
        return tabMenuItem;
    }

    public void setTabMenuItem(TabMenuItem tabMenuItem) {
        this.tabMenuItem = tabMenuItem;
    }

}



