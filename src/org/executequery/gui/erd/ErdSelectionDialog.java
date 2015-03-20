/*
 * ErdSelectionDialog.java
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


package org.executequery.gui.erd;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import org.executequery.Constants;

import org.executequery.GUIUtilities;

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
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ErdSelectionDialog extends JDialog {
    
    /** The ERD parent panel */
    private ErdViewerPanel parent;
    /** The table selection panel */
    private ErdSelectionPanel selectionPanel;
    
    public ErdSelectionDialog(ErdViewerPanel parent) {
        super(GUIUtilities.getParentFrame(), "Add Table", true);
        this.parent = parent;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        pack();
        setLocation(GUIUtilities.getLocationForDialog(getSize()));        
        setVisible(true);
    }
    
    private void jbInit() throws Exception {
        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());
        
        selectionPanel = new ErdSelectionPanel();
        
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE);
        addButton.setPreferredSize(Constants.BUTTON_SIZE);
        
        ActionListener btnListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons_actionPerformed(e); }
        };
        
        addButton.addActionListener(btnListener);
        cancelButton.addActionListener(btnListener);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        c.add(selectionPanel, gbc);
        gbc.insets.top = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        c.add(addButton, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets.left = 0;
        c.add(cancelButton, gbc);
        
        setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
    }
    
    /** <p>Performs the respective action upon selection
     *  of a button within this dialog.
     *
     *  @param the <code>ActionEvent</code>
     */
    private void buttons_actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if (command.equals("Cancel")) {
            dispose();
        }
        else if (command.equals("Add")) {
            
            if (!selectionPanel.hasSelections()) {
                GUIUtilities.displayErrorMessage("You must select at least one table.");
                return;
            }
            
            setVisible(false);
            
            parent.setDatabaseConnection(selectionPanel.getDatabaseConnection());
            ErdGenerateProgressDialog progressDialog =
                        new ErdGenerateProgressDialog(selectionPanel.getSelectedValues(),
                                                      parent,
                                                      selectionPanel.getSchema());
            dispose();
   
        }
        
    }
    
}






