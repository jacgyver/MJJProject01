/*
 * ErdDeleteRelationshipDialog.java
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import org.executequery.Constants;

import org.executequery.GUIUtilities;
import org.executequery.gui.browser.ColumnConstraint;
import org.executequery.gui.browser.ColumnData;

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
 * @date     $Date: 2006/09/13 15:53:06 $
 */
public class ErdDeleteRelationshipDialog extends ErdPrintableDialog
                                         implements ActionListener {
    
    /** The controller for the ERD viewer */
    private ErdViewerPanel parent;
    /** The table listing constraints */
    private JTable table;
    /** The two related tables */
    private ErdTable[] erdTables;
    /** The SQL text string buffer */
    private StringBuffer sqlBuffer;
    /** The constraints */
    private Vector constraints;
    
    /** The literal 'ALTER TABLE ' */
    private static final String ALTER_TABLE = "ALTER TABLE ";
    /** The literal ' ADD CONSTRAINT ' */
    private static final String DROP_CONSTRAINT = " DROP CONSTRAINT ";
    /** The literal ';' */
    private static final String CLOSE_END = ";\n";
    
    public ErdDeleteRelationshipDialog(ErdViewerPanel parent, ErdTable[] erdTables) {
        super("Delete Table Relationship");
        
        this.parent = parent;
        this.erdTables = erdTables;
        
        ColumnData[] cd1 = erdTables[0].getTableColumns();
        ColumnData[] cd2 = erdTables[1].getTableColumns();
        
        String tableName1 = erdTables[0].getTableName();
        String tableName2 = erdTables[1].getTableName();
        ColumnConstraint[] tableConstraints = null;
        
        constraints = new Vector();
        
        for (int i = 0; i < cd1.length; i++) {
            
            if (!cd1[i].isForeignKey()) {
                continue;
            }

            tableConstraints = cd1[i].getColumnConstraintsArray();            
            if (tableConstraints == null || tableConstraints.length == 0) {
                break;
            }
            
            for (int j = 0; j < tableConstraints.length; j++) {
                if (tableConstraints[j].isPrimaryKey()) {
                    continue;
                }
                
                if (tableConstraints[j].getRefTable().equalsIgnoreCase(tableName2)) {
                    constraints.add(new ColumnConstraintDrop(cd1[i], erdTables[0], j));
                }
            }
            
        }
        
        for (int i = 0; i < cd2.length; i++) {
            
            if (!cd2[i].isForeignKey()) {
                continue;
            }
            
            tableConstraints = cd2[i].getColumnConstraintsArray();
            if (tableConstraints == null || tableConstraints.length == 0) {
                continue;
            }
            
            for (int j = 0; j < tableConstraints.length; j++) {
                if (tableConstraints[j].isPrimaryKey()) {
                    continue;
                }
                
                if (tableConstraints[j].getRefTable().equalsIgnoreCase(tableName1)) {
                    constraints.add(new ColumnConstraintDrop(cd2[i], erdTables[1], j));
                }
            }
            
        }
        
        if (constraints.size() == 0) {
            GUIUtilities.displayErrorMessage(
            "No relation exists between the selected tables");
            super.dispose();
            return;
        }
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        display();
        
    }
    
    private void jbInit() throws Exception {
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");
        
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE);
        deleteButton.setPreferredSize(Constants.BUTTON_SIZE);
        
        cancelButton.addActionListener(this);
        deleteButton.addActionListener(this);
        
        sqlText.setPreferredSize(new Dimension(480, 90));
        
        table = new JTable(new ConstraintTableModel());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(20);
        table.setCellSelectionEnabled(true);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(25);
        tcm.getColumn(1).setPreferredWidth(100);
        tcm.getColumn(2).setPreferredWidth(125);
        tcm.getColumn(3).setPreferredWidth(125);
        tcm.getColumn(4).setPreferredWidth(125);
        tcm.getColumn(5).setPreferredWidth(125);
        
        JScrollPane tableScroller = new JScrollPane(table);
        tableScroller.setPreferredSize(new Dimension(640,130));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Select the constraints to be dropped:"), gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        gbc.insets.top = 0;
        panel.add(tableScroller, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.3;
        panel.add(sqlText, gbc);
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(deleteButton, gbc);
        gbc.gridx = 2;
        gbc.insets.left = 0;
        gbc.weightx = 0;
        panel.add(cancelButton, gbc);
        
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        
        c.add(panel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                            GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH,
                            new Insets(7, 7, 7, 7), 0, 0));
        
        sqlBuffer = new StringBuffer();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);        
    }
    
    private void setSQLText() {
        sqlBuffer.setLength(0);
        ColumnConstraintDrop ccd = null;
        for (int i = 0, n = constraints.size(); i < n; i++) {
            ccd = (ColumnConstraintDrop)constraints.elementAt(i);
            if (ccd.isDropped()) {
                sqlBuffer.append(ccd.getSql());
            }
        }
        sqlText.setSQLText(sqlBuffer.toString());
    }
    
    private void delete() {
        ColumnConstraint cc = null;
        ColumnConstraintDrop ccd = null;        
        for (int i = 0, n = constraints.size(); i < n; i++) {
            ccd = (ColumnConstraintDrop)constraints.elementAt(i);
            ccd.dropConstraint();
        }
        
        constraints = null;
        erdTables = null;
        table = null;
        parent.updateTableRelationships();
        dispose();
    }
    
    private ErdTable getTable(String name) {
        if (erdTables[0].getTableName().equals(name)) {
            return erdTables[0];
        } else {
            return erdTables[1];
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        Object button = e.getSource();
        
        if (button instanceof JButton) {
            String command = e.getActionCommand();
            
            if (command.equals("Cancel"))
                dispose();
            
            else if (command.equals("Delete"))
                delete();
            
        }
        
    }
    
    private class ConstraintTableModel extends AbstractTableModel {
        
        protected String[] header = {" ", "Name", "Referencing Table",
        "Referencing Column", "Referenced Table",
        "Referenced Column"};
        
        public int getColumnCount() {
            return 6;
        }
        
        public int getRowCount() {
            return constraints.size();
        }
        
        public Object getValueAt(int row, int col) {
            ColumnConstraintDrop ccd = (ColumnConstraintDrop)constraints.elementAt(row);
            ColumnConstraint cc = ccd.getColumnConstraint();
            
            switch(col) {
                
                case 0:
                    return new Boolean(ccd.isDropped());
                    
                case 1:
                    return cc.getName();
                    
                case 2:
                    return cc.getTable();
                    
                case 3:
                    return cc.getColumn();
                    
                case 4:
                    return cc.getRefTable();
                    
                case 5:
                    return cc.getRefColumn();
                    
                default:
                    return null;
                    
            }
        }
        
        public void setValueAt(Object value, int row, int col) {
            ColumnConstraintDrop ccd = (ColumnConstraintDrop)constraints.elementAt(row);
            ColumnConstraint cc = ccd.getColumnConstraint();
            
            switch (col) {
                case 0:
                    ccd.setDropped(((Boolean)value).booleanValue());
                    setSQLText();
                    break;
                case 1:
                    cc.setName((String)value);
                    break;
                case 2:
                    cc.setTable((String)value);
                    break;
                case 3:
                    cc.setColumn((String)value);
                    break;
                case 4:
                    cc.setRefTable((String)value);
                    break;
                case 5:
                    cc.setRefColumn((String)value);
                    break;
            }
            
            fireTableRowsUpdated(row, row);
        }
        
        public boolean isCellEditable(int row, int col) {
            return (col == 0);
        }
        
        public String getColumnName(int col) {
            return header[col];
        }
        
        public Class getColumnClass(int col) {
            if (col == 0) {
                return Boolean.class;
            }
            return String.class;
        }
        
    } // ConstraintTableModel
    
    
    class ColumnConstraintDrop {
        
        private boolean dropped;
        private ErdTable erdTable;
        private ColumnConstraint columnConstraint;
        private ColumnData columnData;
        
        public ColumnConstraintDrop(ColumnData columnData,
        ErdTable erdTable, int constraintIndex) {
            columnConstraint = columnData.getColumnConstraintsArray()[constraintIndex];
            this.erdTable = erdTable;
            this.columnData = columnData;
            dropped = false;
        }
        
        public void dropConstraint() {
            
            if (dropped) {
                columnData.removeConstraint(columnConstraint);
                columnData.setForeignKey(false);
                erdTable.setDropConstraintsScript(getSql());
            }
            
        }
        
        public void setColumnConstraint(ColumnConstraint columnConstraint) {
            this.columnConstraint = columnConstraint;
        }
        
        public ColumnConstraint getColumnConstraint() {
            return columnConstraint;
        }
        
        public String getSql() {
            
            if (dropped)
                return ALTER_TABLE + erdTable.getTableName() +
                DROP_CONSTRAINT + columnConstraint.getName() + CLOSE_END;
            else
                return "";
            
        }
        
        public boolean isDropped() {
            return dropped;
        }
        
        public void setDropped(boolean dropped) {
            this.dropped = dropped;
        }
        
    } // ColumnConstraintDrop
   
}