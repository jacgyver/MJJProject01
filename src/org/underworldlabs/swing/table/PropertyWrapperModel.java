/*
 * PropertyWrapperModel.java
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.swing.table.AbstractTableModel;
import org.underworldlabs.util.KeyValuePair;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** Simple wrapper class for key/value property values
 *  providing table model and sorting by key or value.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class PropertyWrapperModel extends AbstractTableModel {

    public static final int SORT_BY_KEY = 0;
    public static final int SORT_BY_VALUE = 1;

    private static final String[] HEADER = {"Property", "Value"};

    private int sortBy;
    private KeyValuePair[] valuePairs;

    public PropertyWrapperModel() {
        this(SORT_BY_KEY);
    }

    public PropertyWrapperModel(int sortBy) {
        this.sortBy = sortBy;
    }

    public PropertyWrapperModel(Properties values) {
        this(values, SORT_BY_KEY);
    }

    public PropertyWrapperModel(Properties values, int sortBy) {
        this.sortBy = sortBy;
        setValues(values);
    }

    public PropertyWrapperModel(Hashtable values) {
        this(values, SORT_BY_KEY);
    }

    public PropertyWrapperModel(Hashtable values, int sortBy) {
        this.sortBy = sortBy;
        setValues(values, false);
    }

    public void setValues(Properties values) {
        int count = 0;
        String key = null;
        valuePairs = new KeyValuePair[values.size()];

        for (Enumeration i = values.propertyNames(); i.hasMoreElements();) {
            key = (String)i.nextElement();
            valuePairs[count++] = new KeyValuePair(key, values.getProperty(key));
        }
        fireTableDataChanged();
    }

    public void setValues(Hashtable values, boolean sort) {
        int count = 0;
        String key = null;
        valuePairs = new KeyValuePair[values.size()];

        for (Enumeration i = values.keys(); i.hasMoreElements();) {
            key = (String)i.nextElement();
            valuePairs[count++] = new KeyValuePair(key, (String)values.get(key));
        }
        
        if (sort) {
            sort();
        } else {
            fireTableDataChanged();
        }
        
    }
    
    public void sort() {
        if (valuePairs == null || valuePairs.length == 0) {
            return;
        }
        Arrays.sort(valuePairs, new KeyValuePairSorter());
        fireTableDataChanged();
    }

    public void sort(int sortBy) {
        this.sortBy = sortBy;
        sort();
    }
    
    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        if (valuePairs == null) {
            return 0;
        }
        return valuePairs.length;
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }
    
    public Object getValueAt(int row, int col) {        
        KeyValuePair value = valuePairs[row];
        
        if (col == 0) {
            return value.key;
        } else {
            return value.value;
        }
        
    }

    public String getColumnName(int col) {
        return HEADER[col];
    }


    class KeyValuePairSorter implements Comparator {

        public int compare(Object obj1, Object obj2) {
            KeyValuePair pair1 = (KeyValuePair)obj1;
            KeyValuePair pair2 = (KeyValuePair)obj2;

            String value1 = null;
            String value2 = null;

            if (sortBy == SORT_BY_KEY) {
                value1 = pair1.key;
                value2 = pair2.key;
            } else {
                value1 = pair1.value;
                value2 = pair2.value;            
            }

            int result = value1.compareTo(value2);

            if (result < 0) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else {
                return 0;            
            }
        }

    } // class KeyValuePairSorter

    
}



