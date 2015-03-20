/*
 * IndeterminateProgressBar.java
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
import java.awt.EventQueue;
import java.awt.Graphics;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.UIManager;

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
public class IndeterminateProgressBar extends JComponent
                                      implements Runnable {
    
    private Color scrollbarColour;
    
    private int scrollerWidth;
    private int animationOffset;
    
    private Timer timer;
    private TimerTask paintProgress;
    
    private boolean inProgress;
    private boolean paintBorder;
    
    public IndeterminateProgressBar() {
        this(true);
    }
    
    public IndeterminateProgressBar(boolean paintBorder) {
        inProgress = false;
        scrollerWidth = 20;
        this.paintBorder = paintBorder;

        animationOffset = scrollerWidth * -1;
        scrollbarColour = UIManager.getColor("ProgressBar.foreground");
    }
    
    public void run() {
        animationOffset++;
        repaint();
        if (animationOffset >= 0) {
            animationOffset = scrollerWidth * -1;
        }
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
        inProgress = false;
        repaint();
    }
    
    public void start() {
        
        paintProgress = new TimerTask() {
            public void run() {
                EventQueue.invokeLater(IndeterminateProgressBar.this);
            }
        };
        
        timer = new Timer();
        timer.schedule(paintProgress, 0, 25);
        inProgress = true;
        repaint();
    }
    
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }
    
    public void paintComponent(Graphics g) {
        
        int width = getWidth();
        int height = getHeight();
        
        int y1 = height - 2;
        int y4 = height - 3;
        
        if (paintBorder) {
            // draw the line border
            g.setColor(scrollbarColour);
            g.drawRect(0, 0, width - 2, height - 2);
            width--;
        }
        
        else {
            // draw the default border
            paintBorder(g);
            y1 = height;
            y4 = height - 1;
        }
        
        if (!inProgress) {
            return;
        }
        
        // set the polygon points
        int[] xPosns = {0, 0, 0, 0};
        int[] yPosns = {y1, 1, 1, y1};
        
        // constrain the clip
        //g.setClip(1, 1, width - 3, y4);
        g.setClip(0, 1, width, y4);
        
        g.setColor(scrollbarColour);
        
        for (int i = 0, k = width + scrollerWidth; i < k; i += scrollerWidth) {
            
            xPosns[0] = i + animationOffset;
            xPosns[1] = xPosns[0] + (scrollerWidth / 2);
            xPosns[2] = xPosns[0] + scrollerWidth;
            xPosns[3] = xPosns[1];

            g.fillPolygon(xPosns, yPosns, 4);
            
        }
        
    }
   
}



