/*
 * ColumnIndexTableModel.java
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

import javax.swing.table.AbstractTableModel;

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
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class ColumnIndexTableModel extends AbstractTableModel {
    
    /** The index data */
    private ColumnIndex[] data;
    
    private static final String[] header = {"Index Name", "Indexed Column", "Non-Unique"};
    
    public ColumnIndexTableModel() {}
    
    public ColumnIndexTableModel(ColumnIndex[] data) {
        this.data = data;
    }
    
    public void setIndexData(ColumnIndex[] data) {
        if (this.data == data) {
            return;
        }
        this.data = data;
        fireTableDataChanged();
    }
    
    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }
    
    public int getColumnCount() {
        return header.length;
    }
    
    public String getColumnName(int col) {
        return header[col];
    }
    
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    public Object getValueAt(int row, int col) {
        ColumnIndex cid = data[row];        
        switch(col) {
            case 0:
                return cid.getIndexName();
            case 1:
                return cid.getIndexedColumn();
            case 2:
                return new Boolean(cid.isNonUnique());
            default:
                return null;
        }
    }
    
    public void setValueAt(Object value, int row, int col) {
        ColumnIndex cid = data[row];        
        switch (col) {
            case 0:
                cid.setIndexName((String)value);
                break;
            case 1:
                cid.setIndexedColumn((String)value);
                break;
            case 2:
                cid.setNonUnique(((Boolean)value).booleanValue());
                break;
        }
        
        fireTableRowsUpdated(row, row);
    }
    
    public Class getColumnClass(int col) {
        if (col == 2)
            return Boolean.class;
        else
            return String.class;
    }
    
    
}






