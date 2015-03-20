/*
 * AbstractFormObjectViewPanel.java
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


package org.executequery.gui.forms;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.print.Printable;

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
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/07/15 13:14:12 $
 */
public abstract class AbstractFormObjectViewPanel extends JPanel
                                                  implements FormObjectView {
    
    protected static Border emptyBorder;
    protected GradientLabel gradientLabel;
    
    private static GridBagConstraints panelConstraints;
    
    public AbstractFormObjectViewPanel() {
        super(new BorderLayout());
        gradientLabel = new GradientLabel();
        add(gradientLabel, BorderLayout.NORTH);
    }
    
    static {
        emptyBorder = BorderFactory.createEmptyBorder(5,5,5,5);
        panelConstraints = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                                  GridBagConstraints.SOUTHEAST,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(5, 5, 5, 5), 0, 0);
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
    
    /** Performs some cleanup and releases resources before being closed. */
    public abstract void cleanup();
    
    /** Refreshes the data and clears the cache */
    public abstract void refresh();
    
    /** Returns the print object - if any */
    public abstract Printable getPrintable();
    
    /** Returns the name of this panel */
    public abstract String getLayoutName();

    public static GridBagConstraints getPanelConstraints() {
        return panelConstraints;
    }
    
}


