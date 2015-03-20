/*
 * ErdExecuteSQL.java
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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.FlatSplitPane;

import org.executequery.gui.editor.QueryAnalyser;
import org.executequery.gui.SQLExecutor;

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
public class ErdExecuteSQL extends ErdPrintableDialog
                           implements SQLExecutor {
    
    /** The controller for the ERD viewer */
    private ErdViewerPanel parent;
    /** The results text area */
    private JTextArea resultsArea;
    /** Utility to perform the execution */
    private QueryAnalyser queryAnalyser;
    /** The 'Cancel' button */
    private JButton cancelButton;
    /** The 'Close' button */
    private JButton closeButton;
    
    public ErdExecuteSQL(ErdViewerPanel parent) {
        super("Perform Schema Changes");
        
        this.parent = parent;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sqlText.setSQLText(parent.getAllSQLText());
        
        display();
        
    }
    
    public ErdExecuteSQL(ErdViewerPanel parent, String sql) {
        super("Perform Schema Changes");
        
        this.parent = parent;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sqlText.setSQLText(sql);
        
        display();
        
    }
    
    private void jbInit() throws Exception {
        sqlText.setAppending(true);
        sqlText.setSQLTextBackground(Color.WHITE);
        //sqlText.setBorder(null);
        
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setBackground(null);
        resultsArea.setMargin(new Insets(2,2,2,2));
        JScrollPane resultsScroller = new JScrollPane(resultsArea);
        
        Border panelBorder = BorderFactory.createMatteBorder(
                    1, 1, 1, 1, GUIUtilities.getDefaultBorderColour());
        
        resultsScroller.setBorder(panelBorder);
        sqlText.setBorder(panelBorder);
        
        Dimension textDim = new Dimension(530,150);
        sqlText.setPreferredSize(textDim);
        resultsArea.setPreferredSize(textDim);
        
        JSplitPane splitPane = new FlatSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(sqlText);
        splitPane.setBottomComponent(resultsScroller);
        splitPane.setDividerLocation(0.5);
        splitPane.setDividerSize(5);
        
        closeButton = new JButton("Close");
        cancelButton = new JButton("Execute");
        
        Dimension btnDim = new Dimension(80, 30);
        cancelButton.setPreferredSize(btnDim);
        closeButton.setPreferredSize(btnDim);
        
        ActionListener btnListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons_actionPerformed(e); }
        };
        
        cancelButton.addActionListener(btnListener);
        closeButton.addActionListener(btnListener);
        
        queryAnalyser = new QueryAnalyser(this);
        
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        c.add(splitPane, gbc);
        gbc.insets.top = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridy = 1;
        c.add(cancelButton, gbc);
        gbc.insets.left = 0;
        gbc.gridx = 1;
        gbc.weightx = 0;
        c.add(closeButton, gbc);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);        
    }
    
    private void execute() {
        resultsArea.append("Executing...");
        queryAnalyser.executeSQLQuery(sqlText.getSQLText(), false);
    }
    
    public void setStopButtonEnabled(boolean enable) {
        if (enable) {
            cancelButton.setText("Stop");
        }
        cancelButton.setEnabled(enable);
    }
    
    public void setOutputMessage(int type, String text) {
        resultsArea.append("\n\n" + text);
    }
    
    public void setResultText(int result, int type) {
        String text = null;
        
        switch (type) {
            case 20:
                text = "\n\nTable dropped.";
                break;
            case 21:
                text = "\n\nTable created.";
                break;
            case 22:
                text = "\n\nTable altered.";
                break;
            case 24:
                text = "\n\nStatement executed successfully with result code 1.";
                break;
        }
        
        resultsArea.append(text);
    }
    
    private void buttons_actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        
        if (button == closeButton) {
            queryAnalyser.closeConnection();
            queryAnalyser = null;
            dispose();
        }
        
        else if (button == cancelButton) {
            
            String btnText = cancelButton.getText();
            
            if (btnText.equals("Execute"))
                execute();
            
            else if (btnText.equals("Stop")) {
                queryAnalyser.interruptStatement();
                resultsArea.append("\nProcess cancelled");
            }
            
        }
        
    }
    
    
    // -------------------------------------------
    // ---- Unimplemented SQLExecutor methods ----
    // -------------------------------------------
    
    public void setExecuting(boolean executing) {}
    public void setLeftStatusText(String text) {}
    public void addToHistory(String text) {}
    public void setRightStatusText(String text) {}
    public void setActivityStatusText(String text) {}
    public void interrupt() {}
    public void setResultSet(ResultSet rs) throws SQLException {}
    public void setResultSet(ResultSet rs, String query) throws SQLException {}
    public int setResultSet(ResultSet rs, boolean showRowNumber, String query) throws SQLException {
        return 0; }
    public int setResultSet(ResultSet rs, boolean showRowNumber) throws SQLException {
        return 0; }
    // -------------------------------------------
    
}






