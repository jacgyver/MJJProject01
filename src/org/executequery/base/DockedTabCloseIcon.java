/*
 * DockedTabCloseIcon.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Simple icon drawing the close button
 * for a closeable tab on the CloseTabbedPane.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:55 $
 */
public class DockedTabCloseIcon implements TabControlIcon {
    
    /** Creates a new instance of TabCloseButtonIcon */
    public DockedTabCloseIcon() {}
    
    /**
     * Returns the icon's height.
     * 
     * @return the height of the icon
     */
    public int getIconHeight() {
        return ICON_HEIGHT;
    }

    /**
     * Returns the icon's width.
     * 
     * @return the width of the icon
     */
    public int getIconWidth() {
        return ICON_WIDTH;
    }

    /**
     * Draw the icon at the specified location.
     *
     * @param the component
     * @param the graphics context
     * @param x coordinate
     * @param y coordinate
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(ICON_COLOR);
        g.drawLine(x, y, x + ICON_WIDTH - 1, y + ICON_HEIGHT - 1);
        g.drawLine(x + ICON_WIDTH - 1, y, x, y + ICON_HEIGHT - 1);
    }
    
}



