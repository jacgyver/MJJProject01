/*
 * BaseRootPane.java
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


package org.executequery.base;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import org.underworldlabs.swing.GlassCapturePanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/06/14 16:11:00 $
 */
public class BaseRootPane extends JRootPane {
    
    /** The glass pane to trap mouse events */
    private Component glassPane;

    /** Creates a new instance of BaseRootPane */
    public BaseRootPane(Component mainComponent) {
        getContentPane().add(mainComponent);        
        // setup the glass pane
        glassPane = new GlassPanePanel((JComponent)mainComponent);
        setGlassPane(glassPane);
        glassPane.setVisible(true);
    }

    /**
     * Switches the visibility of the glass pane.
     *
     * @param visible - visibility switch
     */
    public void setGlassPaneVisible(boolean visible) {
        glassPane.setVisible(visible);
    }
    
    class GlassPanePanel extends GlassCapturePanel {
        
        public GlassPanePanel(JComponent mainComponent) {
            super(mainComponent);
        }
        
        /*
         * Dispatch an event clone, retargeted for the specified target.
         */
        protected void retargetMouseEvent(int id, MouseEvent e, Component target) {
            super.retargetMouseEvent(id, e, target);
            // if its a focus type event, focus the tab panel
            if (id == MouseEvent.MOUSE_CLICKED ||
                    id == MouseEvent.MOUSE_CLICKED ||
                    id == MouseEvent.MOUSE_PRESSED ||
                    id == MouseEvent.MOUSE_RELEASED) {

                final int x = e.getX();
                final int y = e.getY();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        findTabContainerAt(x, y);
                    }
                });

            }
        }
        
        private void findTabContainerAt(int x, int y) {
            // check we don't already have it
            if (mainComponent instanceof TabPane) {
                ((TabPane)mainComponent).focusGained();
                return;
            }

            Point point = null;
            Component component = null;
            Component lastComponent = mainComponent;
            
            int count = 1;
            while ((component = lastComponent.getComponentAt(x, y)) != null) {

                if (component == lastComponent) { // short-circuit
                    return;
                }

                point = SwingUtilities.convertPoint(lastComponent, x, y, component);
                x = point.x;
                y = point.y;

                if (component instanceof TabPane) {
                    ((TabPane)component).focusGained();
                    break;
                }
                lastComponent = component;
            }

        }

    } // GlassCapturePanel

}



