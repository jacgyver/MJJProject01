/*
 * SmoothGradientTreeUI.java
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


package org.underworldlabs.swing.plaf.smoothgradient;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;

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
public final class SmoothGradientTreeUI extends BasicTreeUI {

    private boolean linesEnabled = true;
    private PropertyChangeListener lineStyleHandler;


    public static ComponentUI createUI(JComponent b) {
        return new SmoothGradientTreeUI();
    }

    // Installation ***********************************************************

    public void installUI(JComponent c) {
        super.installUI(c);
        updateLineStyle(c.getClientProperty("Angled"));
        lineStyleHandler = new LineStyleHandler();
        c.addPropertyChangeListener(lineStyleHandler);
    }

    public void uninstallUI(JComponent c) {
        c.removePropertyChangeListener(lineStyleHandler);
        super.uninstallUI(c);
    }
    
    
    // Painting ***************************************************************

    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
        if (linesEnabled) {
            drawDashedVerticalLine(g, x, top, bottom);
        }
    }

    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
        if (linesEnabled) {
            drawDashedHorizontalLine(g, y, left, right);
        }
    }

    // Draws the icon centered at (x,y)
    protected void drawCentered(Component c, Graphics graphics, Icon icon, int x, int y) {
        icon.paintIcon(
            c,
            graphics,
            x - icon.getIconWidth()  / 2 - 1,
            y - icon.getIconHeight() / 2);
    }

    // Helper Code ************************************************************

    private void updateLineStyle(Object lineStyle) {
        linesEnabled = !"None".equals(lineStyle);
    }
    
    // Listens for changes of the line style property 
    private class LineStyleHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name  = e.getPropertyName();
            Object value = e.getNewValue();
            if (name.equals("Angled")) {
                updateLineStyle(value);
            }
        }
    }
    
}



