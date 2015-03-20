/*
 * BrowserNodePanel.java
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


package org.executequery.gui.browser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import javax.swing.border.Border;
import org.underworldlabs.swing.GradientLabel;

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
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class BrowserNodePanel extends JPanel {
    
    protected static Border emptyBorder;
    protected GradientLabel gradientLabel;
    
    public BrowserNodePanel() {
        super(new BorderLayout());
        gradientLabel = new GradientLabel();
        /*
        JPanel gradientPanel = new JPanel(new GridBagLayout());
        gradientPanel.add(gradientLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                                GridBagConstraints.SOUTHEAST, 
                                                GridBagConstraints.BOTH,
                                                new Insets(4, 0, 0, 0), 0, 0));
        */
        //add(gradientPanel, BorderLayout.NORTH);
        add(gradientLabel, BorderLayout.NORTH);
    }
    
    static {
        emptyBorder = BorderFactory.createEmptyBorder(5,5,5,5);
    }
    
    protected void setContentPanel(JComponent panel) {
        add(panel, BorderLayout.CENTER);
    }
    
    public void setHeader(String text, ImageIcon icon) {
        gradientLabel.setText(text);
        gradientLabel.setIcon(icon);
    }
    
    public void setHeaderText(String text) {
        gradientLabel.setText(text);
    }
    
    public void setHeaderIcon(ImageIcon icon) {
        gradientLabel.setIcon(icon);
    }
    
}



