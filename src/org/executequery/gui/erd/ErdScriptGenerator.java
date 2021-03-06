/*
 * ErdScriptGenerator.java
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.executequery.Constants;

import org.executequery.GUIUtilities;
import org.executequery.gui.browser.ColumnData;
import org.executequery.gui.scriptgenerators.*;
import org.underworldlabs.jdbc.DataSourceException;

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
public class ErdScriptGenerator extends BaseScriptGeneratorPanel
                                implements ScriptGenerator {
    
    /** The parent process */
    private ErdViewerPanel parent;
    
    /** The cancel button */
    private JButton cancelButton;
    
    /** The generate button */
    private JButton generateButton;
    
    /** The tables to generate scripts for as an array */
    private ErdTable[] tables;
    
    /** The dialog container */
    private ErdScriptGeneratorDialog dialog;
    
    public ErdScriptGenerator(Vector _tables, ErdViewerPanel parent) {
        super(_tables);
        
        this.parent = parent;
        
        int v_size = _tables.size();
        tables = parent.getAllComponentsArray();
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void jbInit() throws Exception {
        cancelButton = new JButton("Cancel");
        generateButton = new JButton("Generate");
        
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE);
        generateButton.setPreferredSize(Constants.BUTTON_SIZE);
        
        Insets btnInsets = new Insets(0,0,0,0);
        cancelButton.setMargin(btnInsets);
        generateButton.setMargin(btnInsets);
        
        ActionListener buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons_actionPerformed(e);
            }
        };
        
        cancelButton.addActionListener(buttonListener);
        generateButton.addActionListener(buttonListener);
        
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,7,7);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0;
        buttonPanel.add(generateButton, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets.left = 0;
        buttonPanel.add(cancelButton, gbc);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // assign the connection (may be null)
        metaData.setDatabaseConnection(parent.getDatabaseConnection());
        
        dialog = new ErdScriptGeneratorDialog(this);
        dialog.display();
        
    }
    
    public void dispose() {
        dialog.dispose();
    }
    
    public String getScriptFilePath() {
        return pathField.getText();
    }
    
    public void setResult(int result) {
        
    }
    
    public ColumnData[] getColumnDataArray(String tableName) {
        
        ColumnData[] columnData = null;
        
        for (int i = 0; i < tables.length; i++) {
            
            if (tables[i].getTableName().equals(tableName)) {
                columnData = tables[i].getTableColumns();
                break;
            }
            
        }
        
        return columnData;
        
    }
    
    public Vector getSelectedTables() {
        return listPanel.getSelectedValues();
    }
    
    public boolean hasSelectedTables() {
        return listPanel.hasSelections();
    }
    
    public boolean includeConstraintsInCreate() {
        return consInCreateCheck.isSelected();
    }
    
    public boolean includeConstraints() {
        return constraintsCheck.isSelected();
    }
    
    public boolean includeConstraintsAsAlter() {
        return consAsAlterCheck.isSelected();
    }
    
    public void enableButtons(boolean enable) {
        cancelButton.setEnabled(enable);
        generateButton.setEnabled(enable);
    }
    
    public String getDatabaseProductName() {
        if (parent.getDatabaseConnection() == null) {
            return "Not Available";
        }
        try {
            return metaData.getDatabaseProductName();
        }
        catch (DataSourceException e) {
            return "Not Available";
        }
    }
    
    public String getSchemaName() {
        if (parent.getDatabaseConnection() == null) {
            return "Not Available";
        }
        return metaData.getSchemaName().toUpperCase();
    }
    
    private void buttons_actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        
        if (button == cancelButton) {
            dispose();
        }
        else if (button == generateButton) {
            
            if (hasRequiredFields()) {
                CreateTableScriptsGenerator generator =
                                                new CreateTableScriptsGenerator(this);
                generator.generate();
            }
            
        }
        
    }
    
    
    class ErdScriptGeneratorDialog extends JDialog {
        
        private ErdScriptGenerator _parent;
        
        public ErdScriptGeneratorDialog(ErdScriptGenerator _parent) {
            super(GUIUtilities.getParentFrame(), "Generate Scripts", true);
            
            Container c = this.getContentPane();
            c.setLayout(new BorderLayout());
            c.add(_parent, BorderLayout.CENTER);
            
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
        
        protected void display() {
            pack();
            setLocation(GUIUtilities.getLocationForDialog(getSize()));
            setVisible(true);
        }
        
    } // class ErdScriptGeneratorDialog
    
    
}



