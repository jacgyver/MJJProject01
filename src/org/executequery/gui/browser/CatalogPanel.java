/*
 * CatalogPanel.java
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class CatalogPanel extends BrowserNodeBasePanel {
    
    public static final String NAME = "CatalogPanel";
    
    private CatalogModel model;
    
    /** the browser's control object */
    private BrowserController controller;

    public CatalogPanel(BrowserController controller) {
        super("Catalog Name:");
        this.controller = controller;
        
        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() throws Exception {
        
        model = new CatalogModel();
        table.setModel(model);
        
        tablePanel.setBorder(BorderFactory.createTitledBorder("Available Schemas"));
        
        setHeaderText("Database Catalog");
        setHeaderIcon(GUIUtilities.loadIcon("DBImage24.gif"));
        
    }
    
    public String getLayoutName() {
        return NAME;
    }
    
    public void refresh() {}
    
    public void cleanup() {}
    
    public Printable getPrintable() {
        return new TablePrinter(table, "Database Catalog: " + typeField.getText());
    }
    
    public JTable getTable() {
        return table;
    }
    
    /**
     * Indicates a selection for this panel to be displayed.
     */
    protected void selected(ConnectionObject parent, DatabaseObject databaseObject) {
        setValues(databaseObject.getName(), 
                  controller.getCatalogSchemas(parent.getDatabaseConnection()));
    }
    
    public void setValues(String catalogName, List values) {
        typeField.setText(catalogName);
        model.setValues(values);
    }
    
    private class CatalogModel extends AbstractTableModel {
        
        private List values;
        private String header = "Schema Name";
        
        public CatalogModel() {
            values = new ArrayList(0);
        }
        
        public void setValues(List values) {
            this.values = values;
            fireTableDataChanged();
        }
        
        public int getRowCount() {
            if (values == null) {
                return 0;
            }
            return values.size();
        }
        
        public int getColumnCount() {
            return 1;
        }
        
        public String getColumnName(int col) {
            return header;
        }
        
        public Object getValueAt(int row, int col) {
            return values.get(row);
        }
        
        public boolean isCellEditable() {
            return false;
        }
        
    }
    
}



