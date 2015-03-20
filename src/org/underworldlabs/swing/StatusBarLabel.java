/*
 * StatusBarLabel.java
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
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JLabel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Status bar panel label.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class StatusBarLabel extends JLabel {

    /** Indicates to paint the left border */
    private boolean paintLeft;
    
    /** Indicates to paint the right border */
    private boolean paintRight;
    
    /** Indicates to paint the top border */
    private boolean paintTop;
    
    /** Indicates to paint the bottom border */
    private boolean paintBottom;

    /** the border colour */
    private Color borderColour;

    /** the label height */
    private int height;
    
    public StatusBarLabel(boolean paintTop, boolean paintLeft, 
                          boolean paintBottom, boolean paintRight) {
        this(paintTop, paintLeft, paintBottom, paintRight, 20);
    }

    public StatusBarLabel(boolean paintTop, boolean paintLeft, 
                          boolean paintBottom, boolean paintRight, int height) {
        this.paintTop = paintTop;
        this.paintLeft = paintLeft;
        this.paintBottom = paintBottom;
        this.paintRight = paintRight;
        this.height = height;
        setVerticalAlignment(JLabel.CENTER);
        borderColour = GUIUtils.getDefaultBorderColour();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(borderColour);

        int width = getWidth();
        int height = getHeight();

        if (paintTop) {
            g.drawLine(0, 0, width, 0);
        }
        if (paintBottom) {
            g.drawLine(0, height - 1, width, height - 1);
        }

        if (paintLeft) {
            g.drawLine(0, 0, 0, height);
        }
        if (paintRight) {
            g.drawLine(width-1, 0, width-1, height);
        }

    }

}



