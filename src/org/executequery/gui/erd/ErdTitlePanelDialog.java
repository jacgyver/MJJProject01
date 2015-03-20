/*
 * ErdTitlePanelDialog.java
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.executequery.Constants;

import org.executequery.GUIUtilities;
import org.executequery.components.TextFieldPanel;
import org.underworldlabs.util.DateUtils;

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
public class ErdTitlePanelDialog extends JDialog {
    
    /** The ERD parent panel */
    private ErdViewerPanel parent;
    /** The name text field */
    private JTextField nameTextField;
    /** The date text field */
    private JTextField dateTextField;
    /** The revision text field */
    private JTextField revTextField;
    /** The name text field */
    private JTextField databaseTextField;
    /** The author text field */
    private JTextField authorTextField;
    /** The author text field */
    private JTextField fileTextField;
    /** The description text area */
    private JTextArea descTextArea;
    /** Whether this is a new title panel */
    private boolean isNew;
    
    public ErdTitlePanelDialog(ErdViewerPanel parent) {
        super(GUIUtilities.getParentFrame(), "ERD Title", true);
        this.parent = parent;
        isNew = true;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        display();
    }
    
    public ErdTitlePanelDialog(ErdViewerPanel parent, String title, String date,
                               String description, String database, String author,
                               String revision, String fileName) {
        
        super(GUIUtilities.getParentFrame(), "ERD Title", true);
        this.parent = parent;
        isNew = false;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        nameTextField.setText(title);
        dateTextField.setText(date);
        revTextField.setText(revision);
        databaseTextField.setText(database);
        authorTextField.setText(author);
        fileTextField.setText(fileName);
        descTextArea.setText(description);
        
        display();
    }
    
    private void display() {
        pack();
        Dimension dialogSize = new Dimension(450, 315);
        setSize(dialogSize);
        this.setLocation(GUIUtilities.getLocationForDialog(dialogSize));

        nameTextField.requestFocusInWindow();
        nameTextField.selectAll();

        setVisible(true);

/*
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                nameTextField.requestFocusInWindow();
                nameTextField.selectAll();
            }
        });
 */
    }
    
    private void jbInit() throws Exception {
        JButton createButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE);
        createButton.setPreferredSize(Constants.BUTTON_SIZE);
        
        ActionListener btnListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons_actionPerformed(e); }
        };
        
        cancelButton.addActionListener(btnListener);
        createButton.addActionListener(btnListener);
        
        nameTextField = new JTextField();
        dateTextField = new JTextField();
        revTextField = new JTextField();
        databaseTextField = new JTextField();
        authorTextField = new JTextField();
        fileTextField = new JTextField();
        descTextArea = new JTextArea();
        
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        
        TextFieldPanel panel = new TextFieldPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.left = 0;
        gbc.weightx = 1.0;
        panel.add(nameTextField, gbc);
        gbc.insets.top = 0;
        gbc.gridy = 1;
        panel.add(dateTextField, gbc);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.insets.left = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.left = 0;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(descTextArea), gbc);
        gbc.weighty = 0;
        gbc.gridy = 3;
        panel.add(databaseTextField, gbc);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.insets.left = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Database:"), gbc);
        gbc.gridy = 4;
        panel.add(new JLabel("Revision:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        panel.add(revTextField, gbc);
        gbc.gridy = 5;
        gbc.gridx = 1;
        panel.add(authorTextField, gbc);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.insets.left = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridy = 6;
        panel.add(new JLabel("File Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(fileTextField, gbc);
        gbc.gridy = 7;
        gbc.gridx = 2;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createButton, gbc);
        gbc.gridx = 3;
        gbc.insets.left = 0;
        gbc.weightx = 0;
        panel.add(cancelButton, gbc);
        
        initialiseValues();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        
        c.add(panel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH,
                                new Insets(7, 7, 7, 7), 0, 0));
        
    }
    
    private void initialiseValues() {
        //MetaDataValues meta = new MetaDataValues();
        //databaseTextField.setText(meta.getDatabaseProductName());
        
        DateUtils time = new DateUtils();
        dateTextField.setText(time.getDate());
        
        fileTextField.setText(parent.getErdFileName());
    }
    
    private void create() {
        
        if (isNew) {
            ErdTitlePanel erdTitlePanel = new ErdTitlePanel(parent, nameTextField.getText(),
            dateTextField.getText(),
            descTextArea.getText(),
            databaseTextField.getText(),
            authorTextField.getText(),
            revTextField.getText(),
            fileTextField.getText());
            
            parent.addTitlePanel(erdTitlePanel);
        }
        
        else {
            ErdTitlePanel erdTitlePanel = parent.getTitlePanel();
            erdTitlePanel.resetValues(nameTextField.getText(),
            dateTextField.getText(),
            descTextArea.getText(),
            databaseTextField.getText(),
            authorTextField.getText(),
            revTextField.getText(),
            fileTextField.getText());
            
            parent.repaintLayeredPane();
        }
        
        GUIUtilities.scheduleGC();
        dispose();
    }
    
    private void buttons_actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if (command.equals("Cancel"))
            dispose();
        
        else if (command.equals("Add"))
            create();
        
    }
    
}




