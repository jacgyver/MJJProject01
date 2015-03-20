/*
 * StatusBarComponentBorder.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Simple border for status bar panels.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class StatusBarComponentBorder implements Border {
    
    /** the border colour */
    private Color borderColour;
    
    /** the border insets */
    private static final Insets insets = new Insets(1,1,1,0);
    
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (borderColour == null) {
            borderColour = GUIUtils.getDefaultBorderColour();
        }
        g.setColor(borderColour);
        // top edge
        g.drawLine(x, y, width, y);
        // bottom edge
        g.drawLine(x, height-1, width, height-1);
        // left edge
        g.drawLine(x, 0, x, height-1);
    }

    public boolean isBorderOpaque() {
        return false;
    }
    
}



