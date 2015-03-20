/*
 * ExecuteQueryFrame.java
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


package org.executequery;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.underworldlabs.swing.GlassPanePanel;
import org.underworldlabs.util.MiscUtils;
import org.underworldlabs.util.SystemProperties;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * Main application frame.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class ExecuteQueryFrame extends JFrame 
                               implements ComponentListener {

    private int lastX;
    private int lastY;
    private int lastWidth;
    private int lastHeight;

    public ExecuteQueryFrame() {
        super("Execute Query");
        ImageIcon frameIcon = GUIUtilities.loadIcon("ApplicationIcon32.gif");
        setIconImage(frameIcon.getImage());

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                SystemUtilities.exitProgram(); }
        });

        addComponentListener(this);
        setLayout(new BorderLayout());
        getRootPane().setGlassPane(new GlassPanePanel());
    }

    public void setSizeAndPosition(int x, int y, int width, int height) {
        lastX = x;
        lastY = y;
        lastWidth = width;
        lastHeight = height;
        setSize(width, height);
        setLocation(x, y);
    }

    public void setSizeAndPosition() {
        String value = SystemProperties.getStringProperty("user", "window.position");
        String[] values = MiscUtils.splitSeparatedValues(value, ",");
        
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        
        int dimValue = 0;
        
        for (int i = 0; i < values.length; i++) {
            dimValue = Integer.parseInt(values[i]);
            switch (i) {
                case 0:
                    x = dimValue;
                case 1:
                    y = dimValue;
                case 2:
                    width = dimValue;
                case 3:
                    height = dimValue;
            }
        }
        setSizeAndPosition(x, y, width, height);
    }

    private void savePosition() {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        if (x == lastX && y == lastY && 
                width == lastWidth && height == lastHeight) {
            return;
        }

        lastX = x;
        lastY = y;
        lastWidth = width;
        lastHeight = height;

        StringBuffer sb = new StringBuffer();
        sb.append(x).
           append(',').
           append(y).
           append(',').
           append(width).
           append(',').
           append(height);
        SystemProperties.setStringProperty("user", "window.position", sb.toString());
        GUIUtilities.updatePreferencesToFile();
    }
    
    public void componentMoved(ComponentEvent e) {
        savePosition();
    }

    public void componentResized(ComponentEvent e) {
        savePosition();
    }

    public void componentHidden(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}

}



