/*
 * SingleColumnTableModel.java
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


package org.underworldlabs.swing.table;

import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Basic updateable table model with a single column.
 * 
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class SingleColumnTableModel extends AbstractTableModel {
    
    /** the column header */
    private String header;
    
    /** the data values */
    private String[] values;
    
    /** Creates a new instance of SingleColumnTableModel */
    public SingleColumnTableModel() {}

    public SingleColumnTableModel(String header) {
        this.header = header;
    }

    public SingleColumnTableModel(String header, String[] values) {
        this.header = header;
        this.values = values;
    }

    public SingleColumnTableModel(String header, Vector<String> values) {
        this.header = header;
        setValues(values);
    }

    public SingleColumnTableModel(String header, List<String> values) {
        this.header = header;
        setValues(values);
    }

    public void setValues(List<String> _values) {
        values = new String[_values.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = _values.get(i);
        }
        fireTableDataChanged();
    }

    public void setValues(Vector<String> _values) {
        values = new String[_values.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = _values.elementAt(i);
        }
        fireTableDataChanged();
    }

    public void setValues(String[] values) {
        this.values = values;
        fireTableDataChanged();
    }
    
    public int getColumnCount() {
        return 1;
    }

    public int getRowCount() {
        if (values == null) {
            return 0;
        }
        return values.length;
    }

    public Object getValueAt(int row, int col) {
        if (values == null) {
            return null;
        }
        return values[row];
    }

    public String getColumnName(int col) {
        return header;
    }

}



