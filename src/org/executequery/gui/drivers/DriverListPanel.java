/*
 * DriverListPanel.java
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


package org.executequery.gui.drivers;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.Printable;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import org.executequery.DatabaseDefinitionCache;
import org.executequery.GUIUtilities;
import org.executequery.JDBCProperties;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.gui.DefaultTable;
import org.executequery.gui.forms.AbstractFormObjectViewPanel;
import org.executequery.print.TablePrinter;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Driver root node view panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class DriverListPanel extends AbstractFormObjectViewPanel
                                  implements MouseListener,
                                             ActionListener {
    
    public static final String NAME = "DriverListPanel";
    
    /** the table display */
    private JTable table;
    
    /** the table model */
    private DriversTableModel model;
    
    /** the parent panel containing the selection tree */
    private DriverViewPanel parent;
    
    /** Creates a new instance of DriverListPanel */
    public DriverListPanel(DriverViewPanel parent) {
        super();
        this.parent = parent;
        init();
    }
    
    private void init() {
        model = new DriversTableModel(JDBCProperties.getDriversVector());
        table = new DefaultTable(model);
        table.setRowHeight(20);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        // add the mouse listener for selection clicks
        table.addMouseListener(this);
        
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(80);
        tcm.getColumn(1).setPreferredWidth(140);
        tcm.getColumn(2).setPreferredWidth(70);
        
        // new connection button
        JButton button = new JButton("New Driver");
        button.addActionListener(this);
        
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.add(new JScrollPane(table), getPanelConstraints());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Available Drivers"));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10,10,5,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("User defined JDBC drivers."), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets.top = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Select the New Driver button to register a new " +
                             "driver with the system"), gbc);
        gbc.gridx = 1;
        gbc.insets.left = 0;
        gbc.insets.bottom = 0;
        panel.add(button, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets.left = 10;
        gbc.insets.top = 10;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(tablePanel, gbc);
        
        setHeaderText("JDBC Drivers");
        setHeaderIcon(GUIUtilities.loadIcon("DatabaseDriver24.gif"));
        setContentPanel(panel);
    }

    public void actionPerformed(ActionEvent e) {
        GUIUtilities.ensureDockedTabVisible(DriversTreePanel.PROPERTY_KEY);
        parent.addNewDriver();
    }
    
    // ----------------------------------
    // MouseListener implementation
    // ----------------------------------
    
    public void mouseClicked(MouseEvent e) {

        // only interested in double clicks
        if (e.getClickCount() < 2) {
            return;
        } 
        
        // get the row double-clicked
        int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
        if (row == -1) {
            return;
        }

        // select the driver in the tree
        if (row < model.getRowCount()) {
            DatabaseDriver driver = (DatabaseDriver)model.getValueAt(row, 0);
            GUIUtilities.ensureDockedTabVisible(DriversTreePanel.PROPERTY_KEY);
            parent.setSelectedDriver(driver);
        }
        
        
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    
    public String getLayoutName() {
        return NAME;
    }
    
    public void refresh() {}    
    public void cleanup() {}
    
    public Printable getPrintable() {
        return new TablePrinter(table, "JDBC Drivers");
    }

    private class DriversTableModel extends AbstractTableModel {
        
        private Vector<DatabaseDriver> values;
        private String[] header = {"Driver Name", "Description", 
                                   "Database", "Class"};
        
        public DriversTableModel(Vector<DatabaseDriver> values) {
            this.values = values;
        }
        
        public void setValues(Vector<DatabaseDriver> values) {
            this.values = values;
            fireTableDataChanged();
        }
        
        public int getRowCount() {
            return values.size();
        }
        
        public int getColumnCount() {
            return header.length;
        }
        
        public String getColumnName(int col) {
            return header[col];
        }
        
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return values.elementAt(row);
                case 1:
                    return values.elementAt(row).getDescription();
                case 2:
                    return DatabaseDefinitionCache.
                                getDatabaseDefinition(values.elementAt(row).getType()).getName();
                case 3:
                    return values.elementAt(row).getClassName();
            }
            return values.elementAt(row);
        }
        
        public boolean isCellEditable() {
            return false;
        }
        
    }

}



