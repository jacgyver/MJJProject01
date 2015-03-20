/*
 * SaveOnExitDialog.java
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


package org.executequery.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.executequery.GUIUtilities;
import org.executequery.gui.SaveFunction;

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
public class SaveOnExitDialog extends JDialog
                              implements ActionListener {
    
    public static final int DISCARD_OPTION = 0;
    
    /** The frames list */
    private JList list;

    /** The button choice result */
    private int result;
    
    public SaveOnExitDialog() {
        super(GUIUtilities.getParentFrame(), "Save Changes", true);
        
        result = -1;
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        pack();
        setLocation(GUIUtilities.getLocationForDialog(getSize()));
        setVisible(true);
    }
    
    private void jbInit() throws Exception {
        
        JButton saveButton = new JButton("Save Selected");
        JButton cancelButton = new JButton("Cancel");
        JButton discardButton = new JButton("Discard All");
        
        Insets buttonInsets = new Insets(0,0,0,0);
        saveButton.setMargin(buttonInsets);
        cancelButton.setMargin(buttonInsets);
        discardButton.setMargin(buttonInsets);
        
        Dimension buttonSize = new Dimension(130, 25);
        saveButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        discardButton.setPreferredSize(buttonSize);
        
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        discardButton.addActionListener(this);
        
        List<SaveFunction> panels = GUIUtilities.getOpenSaveFunctionPanels();
        
        list = new JList(panels.toArray());
        list.setSelectionInterval(0, panels.size() - 1);
        
        JPanel base = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = 3;
        base.add(new JLabel("The following open frames have unsaved changes"), gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets.top = 0;
        base.add(new JScrollPane(list), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        base.add(saveButton, gbc);
        gbc.insets.left = 0;
        gbc.gridx = 1;
        base.add(discardButton, gbc);
        gbc.gridx = 2;
        base.add(cancelButton, gbc);
        
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(base, BorderLayout.CENTER);
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                result = SaveFunction.SAVE_CANCELLED;
                dispose();
            }
        });
        
    }
    
    public int saveChanges() {
        int result = -1;
        Object[] selectedFrames = list.getSelectedValues();
        
        for (int i = 0; i < selectedFrames.length; i++) {
            SaveFunction saveFunction = (SaveFunction)selectedFrames[i];
            result = saveFunction.save(false);

            if (result != SaveFunction.SAVE_COMPLETE) {
                break;
            }
            
        }
        return result;
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if (command.equals("Cancel")) {
            result = SaveFunction.SAVE_CANCELLED;
        }
        else if (command.equals("Discard All")) {
            result = DISCARD_OPTION;
        }
        else if (command.equals("Save Selected")) {
            result = saveChanges();
        }
        
        dispose();
        
    }
    
    public int getResult() {
        return result;
    }
    
}




