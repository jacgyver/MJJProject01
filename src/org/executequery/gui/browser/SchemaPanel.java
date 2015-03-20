/*
 * SchemaPanel.java
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

import java.awt.print.Printable;

import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import org.executequery.GUIUtilities;

import org.executequery.print.TablePrinter;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class SchemaPanel extends BrowserNodeBasePanel {
    
    public static final String NAME = "SchemaPanel";
    
    private JLabel noResultsLabel;
    private SchemaModel model;
    private HashMap cache;
    
    /** the browser's control object */
    private BrowserController controller;

    public SchemaPanel(BrowserController controller) {
        super("Schema Name:");
        this.controller = controller;

        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() throws Exception {        
        noResultsLabel = new JLabel("No information for this object is available.",
                                    JLabel.CENTER);

        model = new SchemaModel();
        table.setModel(model);
        
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        tablePanel.setBorder(BorderFactory.createTitledBorder("Available Objects"));
        setHeaderText("Database Schema");
        setHeaderIcon(GUIUtilities.loadIcon("User24.gif"));
        
        cache = new HashMap();
        
    }
    
    public Printable getPrintable() {
        return new TablePrinter(table, "Database Schema: " + typeField.getText());
    }
    
    public String getLayoutName() {
        return NAME;
    }
    
    public void refresh() {
        cache.clear();
    }
    
    public void cleanup() {}
    
    public JTable getTable() {
        return table;
    }
    
    /**
     * Indicates a selection for this panel to be displayed.
     */
    protected void selected(ConnectionObject parent, DatabaseObject databaseObject) {
        setValues(databaseObject.getName(), 
                  controller.getTables(parent.getDatabaseConnection(),
                                       databaseObject.getCatalogName(),
                                       databaseObject.getName(),
                                       parent.getMetaKeyNames()));
    }

    public void setValues(String schemaName, DatabaseObject[] values) {
        typeField.setText(schemaName);
        boolean hadResults = model.getRowCount() > 0;

        model.setValues(values);

        if (values == null || values.length == 0) {
            tablePanel.remove(scroller);
            tablePanel.add(noResultsLabel, getPanelConstraints());
            tablePanel.validate();
        }
        else {
            if (!hadResults) {
                tablePanel.remove(noResultsLabel);
                tablePanel.add(scroller, getPanelConstraints());
                tablePanel.validate();
            }
        }

    }
    
    private class SchemaModel extends AbstractTableModel {
        
        private DatabaseObject[] values;
        private String[] header = {"Catalog","Schema","Name","Type","Remarks"};
        
        public SchemaModel() {
            values = new DatabaseObject[0];
        }
        
        public void setValues(DatabaseObject[] values) {
            this.values = values;
            fireTableDataChanged();
        }
        
        public int getRowCount() {
            return values.length;
        }
        
        public int getColumnCount() {
            return 5;
        }
        
        public String getColumnName(int col) {
            return header[col];
        }
        
        public Object getValueAt(int row, int col) {
            DatabaseObject object = values[row];
            
            switch (col) {
                
                case 0:
                    return object.getCatalogName();
                    
                case 1:
                    return object.getSchemaName();
                    
                case 2:
                    return object.getName();
                    
                case 3:
                    return object.getMetaDataKey();
                    
                case 4:
                    return object.getRemarks();
                    
                default:
                    return "NULL";
                    
            }
            
        }
        
        public boolean isCellEditable() {
            return false;
        }
        
    }
    
} // class



