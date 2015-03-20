/*
 * AbstractStatusBarPanel.java
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
import java.awt.Insets;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
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
public abstract class AbstractStatusBarPanel extends JPanel {
    
    /** default border colour */
    private static Color BORDER_COLOUR;
    
    /** default for for non-JLabel components */
    private static Border COMPONENT_BORDER;
    
    /** components within this status bar */
    private Vector components;
    
    protected AbstractStatusBarPanel(int height) {
        super(new StatusBarLayout(height));
    }
    
    protected void addLabel(int index, int width, boolean resizable) {
        if (components == null) {
            components = new Vector();
        }
        StatusBarLabel label = new StatusBarLabel(false, true, false, false);
        add(label, new StatusBarLayoutConstraints(index, width, resizable));
        components.add(index, label);
    }

    protected void addComponent(JComponent c, int index, int width, boolean resizable) {
        if (components == null) {
            components = new Vector();
        }
        
        if (COMPONENT_BORDER == null) {
            COMPONENT_BORDER = new StatusBarComponentBorder();
        }

        c.setBorder(COMPONENT_BORDER);
        add(c, new StatusBarLayoutConstraints(index, width, resizable));
        components.add(index, c);
    }

    protected JLabel getLabel(int index) {
        Object object = components.get(index);
        if (object != null && object instanceof JLabel) {
            return (JLabel)object;
        }
        return null;
    }
    
    protected void setLabelText(int index, final String text) {
        Object object = components.get(index);
        if (object != null && object instanceof JLabel) {
            final JLabel label = (JLabel)object;
            Runnable update = new Runnable() {
                public void run() {
                    label.setText(formatText(text));
                    Dimension dim = label.getSize();
                    label.paintImmediately(0, 0, dim.width, dim.height);
                }
            };
            SwingUtilities.invokeLater(update);
        }
    }
    
    private String formatText(String text) {
        if (text != null && text.length() > 0) {
            char firstChar = text.charAt(0);
            if (!Character.isWhitespace(firstChar)) {
                return " " + text;
            }
        }
        return text;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int height = getHeight();
        int width = getWidth();

        if (BORDER_COLOUR == null) {
            BORDER_COLOUR = GUIUtils.getDefaultBorderColour();
        }
        
        Insets insets = getInsets();

        g.setColor(BORDER_COLOUR);
        g.drawRect(insets.left, 
                   insets.top,
                   width - insets.left - insets.right,
                   height - insets.top - insets.bottom - 1);
        
    }
    
}



