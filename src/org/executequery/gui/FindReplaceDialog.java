/*
 * FindReplaceDialog.java
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.ActionUtilities;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.executequery.gui.text.TextEditor;
import org.executequery.search.TextAreaSearch;
import org.underworldlabs.swing.GUIUtils;

// find replace dialog for text components
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
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class FindReplaceDialog extends JDialog
                               implements ActionListener {
    
    public static final int FIND = 0;
    public static final int REPLACE = 1;
    
    private JButton findNextButton;
    private JButton closeButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    
    private JCheckBox wholeWordsCheck;
    private JCheckBox regexCheck;
    private JCheckBox matchCaseCheck;
    private JCheckBox replaceCheck;
    private JCheckBox wrapCheck;
    
    private JRadioButton searchUpRadio;
    private JRadioButton searchDownRadio;
    
    private JComboBox findField;
    private JComboBox replaceField;
    
    public FindReplaceDialog(int type) {
        super(GUIUtilities.getParentFrame(), "Find and Replace", false);
        
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        setFindReplace(!(type == FIND));
        
        pack();
        this.setLocation(GUIUtilities.getLocationForDialog(this.getSize()));
        findField.requestFocusInWindow();
        setVisible(true);
    }
    
    private void jbInit() throws Exception {
        JPanel findPanel = new JPanel(new GridBagLayout());
        
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        
        Dimension optionsDim = new Dimension(300, 90);
        optionsPanel.setPreferredSize(optionsDim);
        
        TextEditor textFunction = GUIUtilities.getTextEditorInFocus();
        JTextComponent textComponent = textFunction.getEditorTextComponent();
        String selectedText = textComponent.getSelectedText();
        
        if (selectedText != null && selectedText.length() > 0) {
            addFind(selectedText);
        }
       
        findField = new JComboBox(TextAreaSearch.getPrevFindValues());
        replaceField = new JComboBox(TextAreaSearch.getPrevReplaceValues());
        findField.setEditable(true);
        replaceField.setEditable(true);
        
        Dimension comboDim = findField.getSize();
        comboDim.setSize(comboDim.getWidth(), 23);
        findField.setPreferredSize(comboDim);
        replaceField.setPreferredSize(comboDim);
        
        KeyAdapter keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                fields_keyPressed(e);
            }
        };
        
        JTextField comboField= (JTextField)((ComboBoxEditor)findField.getEditor()).
        getEditorComponent();
        comboField.addKeyListener(keyListener);
        
        comboField= (JTextField)((ComboBoxEditor)replaceField.getEditor()).
        getEditorComponent();
        comboField.addKeyListener(keyListener);
        
        wholeWordsCheck = new JCheckBox("Whole words only");
        matchCaseCheck = new JCheckBox("Match case");
        wrapCheck = new JCheckBox("Wrap Search", true);

        replaceCheck = ActionUtilities.createCheckBox("Replace:", "setToReplace");
        regexCheck = ActionUtilities.createCheckBox("Regular expressions", "setToRegex"); 
        
        searchUpRadio = new JRadioButton("Search Up");
        searchDownRadio = new JRadioButton("Search Down", true);
        
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(searchUpRadio);
        btnGroup.add(searchDownRadio);
        
        findNextButton = new JButton("Find Next");
        replaceButton = new JButton("Replace");
        replaceAllButton = new JButton("Replace All");
        closeButton = ActionUtilities.createButton("Close", "close");

        Dimension btnDim = new Dimension(85, 30);
        findNextButton.setPreferredSize(btnDim);
        closeButton.setPreferredSize(btnDim);
        replaceAllButton.setPreferredSize(btnDim);
        replaceButton.setPreferredSize(btnDim);
        
        Insets btnInsets = new Insets(2,2,2,2);
        findNextButton.setMargin(btnInsets);
        closeButton.setMargin(btnInsets);
        replaceAllButton.setMargin(btnInsets);
        replaceButton.setMargin(btnInsets);
        
        findNextButton.setMnemonic('F');
        replaceButton.setMnemonic('R');
        replaceAllButton.setMnemonic('A');
        
        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(5,10,0,5);
        gbc.insets = ins;
        gbc.anchor = GridBagConstraints.WEST;
        optionsPanel.add(matchCaseCheck, gbc);
        gbc.gridy = 1;
        gbc.insets.top = 0;
        optionsPanel.add(wholeWordsCheck, gbc);
        gbc.gridy = 2;
        gbc.insets.bottom = 10;
        optionsPanel.add(regexCheck, gbc);
        gbc.insets.bottom = 10;
        gbc.insets.left = 20;
        gbc.insets.right = 5;
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        optionsPanel.add(wrapCheck, gbc);
        gbc.insets.bottom = 0;
        gbc.gridy = 1;
        optionsPanel.add(searchDownRadio, gbc);
        gbc.gridy = 0;
        gbc.insets.top = 5;
        optionsPanel.add(searchUpRadio, gbc);
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.insets.left = 10;
        gbc.insets.top = 13;
        gbc.insets.right = 5;
        findPanel.add(new JLabel("Find Text:"), gbc);
        gbc.insets.top = 10;
        gbc.insets.top = 5;
        gbc.insets.left = 5;
        gbc.gridy = 1;
        findPanel.add(replaceCheck, gbc);
        gbc.insets.top = 10;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        findPanel.add(findField, gbc);
        gbc.gridy = 1;
        gbc.insets.top = 5;
        findPanel.add(replaceField, gbc);
        gbc.insets.left = 5;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.insets.bottom = 10;
        findPanel.add(optionsPanel, gbc);
        gbc.gridwidth = 1;
        gbc.insets.top = 0;
        gbc.insets.bottom = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        findPanel.add(findNextButton, gbc);
        gbc.gridx = 1;
        findPanel.add(replaceButton, gbc);
        gbc.gridx = 2;
        findPanel.add(replaceAllButton, gbc);
        gbc.gridx = 3;
        findPanel.add(closeButton, gbc);
        
        ReflectiveAction action = new ReflectiveAction(this);
        replaceCheck.addActionListener(action);
        regexCheck.addActionListener(action);
        closeButton.addActionListener(action);

        findNextButton.addActionListener(this);
        replaceButton.addActionListener(this);
        replaceAllButton.addActionListener(this);
        
        setResizable(false);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(findPanel, BorderLayout.CENTER);
    }
    
    private void fields_keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_ENTER) {
            JTextField comboField= (JTextField)((ComboBoxEditor)findField.getEditor()).
            getEditorComponent();
            
            if (comboField == e.getSource()) {
                startFindReplace(findNextButton);
            }
            else {
                startFindReplace(replaceButton);
            }
            
        }
        
    }
    
    public void dispose() {
        TextAreaSearch.setTextComponent(null);
        super.dispose();
    }
    
    private void startFindReplace(Object button) {
        
        try {
            GUIUtilities.showWaitCursor();
            
            String find = (String)(findField.getEditor().getItem());
            String replacement = (String)(replaceField.getEditor().getItem());
            
            if (replaceCheck.isSelected() && find.compareTo(replacement) == 0) {
                GUIUtilities.displayErrorMessage(
                "The replacement text must be different to the find text.");
                return;
            }
            
            addFind(find);
            
            TextEditor textFunction = GUIUtilities.getTextEditorInFocus();
            
            TextAreaSearch.setTextComponent(textFunction.getEditorTextComponent());
            TextAreaSearch.setFindText(find);
            TextAreaSearch.setSearchDirection(searchUpRadio.isSelected() ?
                                                TextAreaSearch.SEARCH_UP :
                                                TextAreaSearch.SEARCH_DOWN);
            
            boolean useRegex = regexCheck.isSelected();
            TextAreaSearch.setUseRegex(useRegex);
            
            if (useRegex)
                TextAreaSearch.setWholeWords(false);
            else
                TextAreaSearch.setWholeWords(wholeWordsCheck.isSelected());
            
            TextAreaSearch.setMatchCase(matchCaseCheck.isSelected());
            TextAreaSearch.setWrapSearch(wrapCheck.isSelected());
            
            if (button == findNextButton) {
                TextAreaSearch.findNext(false, true);
            }            
            else if (button == replaceButton) {
                
                if (!replaceCheck.isSelected()) {
                    return;
                }
                
                addReplace(replacement);
                TextAreaSearch.setReplacementText(replacement);
                TextAreaSearch.findNext(true, true);
                
            }
            else if (button == replaceAllButton) {
                
                if (!replaceCheck.isSelected()) {
                    return;
                }
                
                addReplace(replacement);
                TextAreaSearch.setReplacementText(replacement);
                TextAreaSearch.replaceAll();
                
            }
            
            findField.requestFocusInWindow();
            GUIUtils.scheduleGC();
            
        }
        finally {
            GUIUtilities.showNormalCursor();
        }
        
    }
    
    public void setToReplace(ActionEvent e) {
        setFindReplace(replaceCheck.isSelected());
    }
    
    public void setToRegex(ActionEvent e) {
        wholeWordsCheck.setEnabled(!regexCheck.isSelected());
    }
    
    public void close(ActionEvent e) {
        dispose();
    }
    
    public void actionPerformed(ActionEvent e) {
        startFindReplace(e.getSource());
    }
    
    private void addFind(String s) {
        TextAreaSearch.addPrevFindValue(s);
    }
    
    private void addReplace(String s) {
        TextAreaSearch.addPrevReplaceValue(s);
    }
    
    private void setFindReplace(boolean replace) {
        replaceCheck.setSelected(replace);
        replaceField.setEditable(replace);
        replaceField.setEnabled(replace);
        replaceField.setOpaque(replace);
    }
    
    
}



