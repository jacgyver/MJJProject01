/*
 * BrowserNodeBasePanel.java
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.print.Printable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.executequery.gui.DefaultTable;

import org.underworldlabs.swing.DisabledField;
import org.executequery.gui.forms.AbstractFormObjectViewPanel;

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
public abstract class BrowserNodeBasePanel extends AbstractFormObjectViewPanel {
    
    protected DisabledField typeField;
    protected JPanel tablePanel;
    protected JScrollPane scroller;
    protected JTable table;
    
    public BrowserNodeBasePanel(String labelText) {
        super();
        
        try {
            init(labelText);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void init(String labelText) throws Exception {
        
        JPanel base = new JPanel(new GridBagLayout());
        typeField = new DisabledField();
        
        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(10,5,5,5);
        gbc.insets = ins;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        base.add(new JLabel(labelText), gbc);
        gbc.insets.top = 8;
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        base.add(typeField, gbc);
        
        table = new DefaultTable();
        table.setRowHeight(20);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        tablePanel = new JPanel(new GridBagLayout());
        scroller = new JScrollPane(table,
                                   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        tablePanel.add(scroller, getPanelConstraints());
        
        // add to the panel
        gbc.insets.top = 10;
        gbc.weighty = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.insets.left = 5;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        base.add(tablePanel, gbc);

        setContentPanel(base);
        
    }
    
    /** Performs some cleanup and releases resources before being closed. */
    public abstract void cleanup();
    
    /** Refreshes the data and clears the cache */
    public abstract void refresh();
    
    /** Returns the print object - if any */
    public abstract Printable getPrintable();
    
    /** Returns the name of this panel */
    public abstract String getLayoutName();

}


