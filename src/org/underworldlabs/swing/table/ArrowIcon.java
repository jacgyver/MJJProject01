/*
 * ArrowIcon.java
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


package org.underworldlabs.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.UIManager;
import javax.swing.Icon;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * A simple arrow icon for all directions.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/07/15 15:39:46 $
 */
public class ArrowIcon implements Icon {
    
    // direction constants
    public static final int UP = 0;
    public static final int DOWN  = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 2;
    
    private static final int DEFAULT_SIZE = 10;
    
    private int direction;
    private Color fillColour;
    
    public ArrowIcon(int direction) {
        fillColour = UIManager.getColor("controlShadow");
        this.direction = direction;
    }
    
    public ArrowIcon(Color fillColour, int direction) {
        this.fillColour = fillColour;
        this.direction = direction;
    }
    
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
        
        switch (direction) {
            
            case DOWN:
                drawDownArrow(g, x - 1, y);
                break;
                
            case UP:
                drawUpArrow(g, x - 1, y);
                break;
                
            case RIGHT:
                drawRightArrow(g, x - 1, y);
                break;
                
        }
        
    }
    
    public int getIconWidth() {
        return DEFAULT_SIZE;
    }
    
    public int getIconHeight() {
        return DEFAULT_SIZE;
    }
    
    private void drawRightArrow(Graphics g, int xo, int yo) {
        g.setColor(fillColour);
        
        int x = 0, y = 0;
        
        for (int i = 1; i <= DEFAULT_SIZE; i++) {
            
            y = yo + i + 1;
            
            if (i > DEFAULT_SIZE / 2) {
                
                for (int j = DEFAULT_SIZE - i; j >= 1; j--) {
                    x = xo + j;
                    g.drawLine(x, y, x, y);
                }
                
            }
            
            else {
                
                for (int j = 1; j <= i; j++) {
                    x = xo + j;
                    g.drawLine(x, y, x, y);
                }
                
            }
            
        }
    }
    
    private void drawDownArrow(Graphics g, int xo, int yo) {
        g.setColor(fillColour);
        
        int x = 0, y = 0;
        
        for (int i = 1; i <= DEFAULT_SIZE; i++) {
            
            y = yo + i + 2;
            
            for (int j = i; j <= DEFAULT_SIZE; j++) {
                
                if (j > DEFAULT_SIZE - i) {
                    break;
                }

                x = xo + j;
                g.drawLine(x, y, x, y);
                
            }
            
        }
    }
    
    private void drawUpArrow(Graphics g, int xo, int yo) {
        g.setColor(fillColour);
        
        int yOffset = yo + 2 + (DEFAULT_SIZE / 2);
        int x = 0, y = 0;
        
        for (int i = DEFAULT_SIZE; i >= 1; i--) {
            
            y = yOffset - i;
            
            for (int j = i; j <= DEFAULT_SIZE; j++) {
                
                if (j > DEFAULT_SIZE - i) {
                    break;
                }

                x = xo + j;
                g.drawLine(x, y, x, y);
                
            }
            
        }
        
    }
    
}