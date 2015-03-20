/*
 * TablePKeyModel.java
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
 * @date     $Date: 2006/05/14 06:56:55 $
 */
public class TablePKeyModel extends AbstractTableModel {
    
    private String[] header = {"Name", "Column"};
    
    private String keyName;
    private String column;
    
    public TablePKeyModel(String s1, String s2) {
        keyName = s1;
        column = s2;
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    public int getRowCount() {
        return 1;
    }
    
    public Object getValueAt(int row, int col) {
        
        switch(col) {
            case 0:
                return keyName;
            case 1:
                return column;
            default:
                return null;
        }
    }
    
    public void setValueAt(Object value, int row, int col) {
        
        switch (col) {
            case 0:
                keyName = (String)value;
                break;
            case 1:
                column = (String)value;
                break;
        }
        
        fireTableRowsUpdated(row, row);
    }
    
    public boolean isCellEditable(int row, int col) {
        if (col == 0)
            return true;
        else
            return false;
    }
    
    public String getColumnName(int col) {
        return header[col];
    }
    
}






