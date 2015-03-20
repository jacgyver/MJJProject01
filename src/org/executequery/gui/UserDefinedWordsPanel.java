/*
 * UserDefinedWordsPanel.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import org.executequery.*;
import org.executequery.components.BottomButtonPanel;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/09/13 14:51:12 $
 */
public class UserDefinedWordsPanel extends JPanel
                                   implements FocusComponentPanel,
                                              ActionListener {
    
    public static final String TITLE = "User Defined Keywords";
    
    private List<String> definedTypes;
    private List<String> sql92Types;
    
    private JTable userTable;
    
    private JButton addButton;
    private RolloverButton deleteButton;
    
    private JTextField newWordField;
    
    private KeywordModel userModel;
    
    /** the parent container */
    private ActionContainer parent;

    public UserDefinedWordsPanel(ActionContainer parent) {
        super(new GridBagLayout());
        this.parent = parent;

        try {
            // create a separate word list
            List<String> words = KeywordProperties.getUserDefinedSQL();
            definedTypes = new ArrayList<String>(words.size());
            for (int i = 0, n = words.size(); i < n; i++) {
                definedTypes.add(words.get(i));
            }

            sql92Types = KeywordProperties.getSQL92();
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() throws Exception {
        Vector header = new Vector(1);
        header.add("Keyword");
        
        JPanel tablePanel = new JPanel(new GridBagLayout());
        
        Dimension dim = new Dimension(250, 300);
        
        userModel = new KeywordModel(definedTypes, true);
        userTable = new DefaultTable(userModel);
        userTable.getTableHeader().setResizingAllowed(false);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane js1 = new JScrollPane();
        js1.getViewport().add(userTable);
        
        JPanel userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("User Defined"));
        userPanel.setPreferredSize(dim);
        
        String deleteIcon = "/org/executequery/icons/Delete16.gif";
        deleteButton = new RolloverButton(deleteIcon, "Delete selection");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        userPanel.add(js1, gbc);
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        userPanel.add(deleteButton, gbc);
        
        JTable sql92Table = new DefaultTable(new KeywordModel(sql92Types));
        sql92Table.getTableHeader().setResizingAllowed(false);
        sql92Table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane js2 = new JScrollPane();
        js2.getViewport().add(sql92Table);
        
        JPanel sql92Panel = new JPanel(new BorderLayout());
        sql92Panel.setBorder(BorderFactory.createTitledBorder("SQL92"));
        sql92Panel.add(js2, BorderLayout.CENTER);
        sql92Panel.setPreferredSize(dim);
        
        newWordField = new JTextField();
        addButton = new JButton("Add");
        
        JPanel addPanel = new JPanel(new GridBagLayout());
        
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets.top = 5;
        gbc.insets.bottom = 5;
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        tablePanel.add(userPanel, gbc);
        gbc.insets.right = 5;
        gbc.gridx = 2;
        tablePanel.add(sql92Panel, gbc);
        gbc.insets.top = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(new JLabel("Add Keyword:"), gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        addPanel.add(newWordField, gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.insets.right = 5;
        addPanel.add(addButton, gbc);
        gbc.insets.right = 0;
        gbc.gridwidth = 3;
        gbc.gridy = 1;
        gbc.gridx = 0;
        tablePanel.add(addPanel, gbc);
        
        tablePanel.setBorder(BorderFactory.createEtchedBorder());
        
        newWordField.addActionListener(this);
        addButton.addActionListener(this);
        deleteButton.addActionListener(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(new BottomButtonPanel(this, "Save", 
                      "user-defined-keywords", parent.isDialog()),
                      BorderLayout.SOUTH);
        
        add(mainPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                            GridBagConstraints.SOUTHEAST, 
                                            GridBagConstraints.BOTH,
                                            new Insets(7, 7, 7, 7), 0, 0));
        newWordField.requestFocus();
    }
    
    /**
     * Returns the keyword field.
     */
    public Component getDefaultFocusComponent() {
        return newWordField;
    }

    private void changeKeywords(Object source) {
        if (source == addButton || source == newWordField) {
            String newWord = newWordField.getText();
            
            if (MiscUtils.isNull(newWord)) {
                return;
            }
            
            newWord = newWord.trim().toUpperCase();
            if (Collections.binarySearch(sql92Types, newWord) >= 0) {
                GUIUtilities.displayWarningMessage(
                "The word entered is already part of the SQL92 keyword list.\n");
                newWordField.selectAll();
                newWordField.requestFocus();
                return;
            }
            else if (Collections.binarySearch(userModel.getWords(), newWord) >= 0) {
                GUIUtilities.displayWarningMessage(
                "The word entered is already part of the user defined keyword list.\n");
                newWordField.selectAll();
                newWordField.requestFocus();
                return;
            }

            userModel.addNewWord(newWord);
            newWordField.setText("");
            newWordField.requestFocus();
        } 
        else if (source == deleteButton) {
            int selection = userTable.getSelectedRow();
            if (selection == -1) {
                return;
            }
            
            userModel.deleteWord(selection);
        }
        userTable.revalidate();
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Save")) {
            saveKeywords();
        }
        else {
            changeKeywords(e.getSource());
        }        
    }
    
    private void saveKeywords() {
        if (KeywordProperties.setUserDefinedKeywords(userModel.getWords())) {
            //GUIUtilities.updateSQLKeywords();
            parent.finished();
        }
        else {
            GUIUtilities.displayErrorMessage(
                    "An error occured updating your keywords.\nPlease try again.");
        }        
    }
    
    private class KeywordModel extends AbstractTableModel {
        
        private List<String> words;
        private String header = "Keyword";
        private boolean editable = false;
        
        public KeywordModel(List<String> words) {
            this(words, false);
        }
        
        public KeywordModel(List<String> words, boolean isEditable) {
            Collections.sort(words);
            this.words = words;
            editable = isEditable;
        }
        
        public List<String> getWords() {
            return words;
        }
        
        public void deleteWord(int index) {
            words.remove(index);
            fireTableRowsUpdated(index, definedTypes.size() - 1);
        }
        
        public void addNewWord(String word) {
            words.add(word);
            int row = definedTypes.size() - 1;
            fireTableRowsUpdated(row, row);
        }
        
        public int getColumnCount() {
            return 1;
        }
        
        public int getRowCount() {
            return words.size();
        }
        
        public Object getValueAt(int row, int col) {
            return words.get(row);
        }
        
        public void setValueAt(Object value, int row, int col) {
            words.set(row, (String)value);
            fireTableRowsUpdated(row, row);
        }
        
        public boolean isCellEditable(int row, int col) {
            return editable;
        }
        
        public String getColumnName(int col) {
            return header;
        }
        
        public Class getColumnClass(int col) {
            return String.class;
        }

    } // class KeywordModel
    
    
}



