/*
 * ResultSetMetaDataPanel.java
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


package org.executequery.gui.editor;

import java.awt.BorderLayout;

import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.executequery.gui.DefaultTable;
import org.underworldlabs.util.SystemProperties;

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
 * @date     $Date: 2006/07/15 13:14:12 $
 */
public class ResultSetMetaDataPanel extends JPanel {
    
    public static final String TITLE = "Result Set Meta Data";
    
    private JTable table;
    
    private RSMetaDataModel rsMetaModel;
    
    public ResultSetMetaDataPanel(Vector[] rsmd) {
        super(new BorderLayout());
        
        try {
            jbInit(rsmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit(Vector[] rsmd) {
        table = new DefaultTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        int rowHeight = SystemProperties.getIntProperty("user", "results.table.column.height");
        table.setRowHeight(rowHeight);
        setMetaData(rsmd);
        
        JScrollPane scroller = new JScrollPane(table);
        scroller.setBorder(null);
        add(scroller, BorderLayout.CENTER);
    }
    
    public void setMetaData(Vector[] rsmdResults) {
        rsMetaModel = new RSMetaDataModel(rsmdResults[0], rsmdResults[1]);
        table.setModel(rsMetaModel);
        table.validate();
    }
    
    static class RSMetaDataModel extends AbstractTableModel {
        
        private Vector columnHeaders;
        private Vector tableData;
        
        public RSMetaDataModel(Vector v1, Vector v2) {
            columnHeaders = v1;
            tableData = v2;
        }
        
        public int getColumnCount() {
            return columnHeaders.size();
        }
        
        public int getRowCount() {
            return tableData.size();
        }
        
        public Object getValueAt(int row, int column) {
            Vector rowData = (Vector)(tableData.elementAt(row));
            return rowData.elementAt(column);
        }
        
        public Object getRowValueAt(int row) {
            return tableData.elementAt(row);
        }
        
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        
        public String getColumnName(int column) {
            return (String)(columnHeaders.elementAt(column));
        }
        
        
    }
    
}






