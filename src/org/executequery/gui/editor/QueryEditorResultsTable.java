/*
 * QueryEditorResultsTable.java
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

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.underworldlabs.swing.table.StringCellEditor;

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
public class QueryEditorResultsTable extends JTable {
    
    private ResultsTableColumnModel columnModel;
    
    private ResultsTableCellRenderer cellRenderer;
    private DefaultCellEditor cellEditor;
    
    private TableColumn dummyColumn = new TableColumn();
    
    public QueryEditorResultsTable(TableModel model) {
        super(model);
        setDefaultOptions();
    }
    
    public QueryEditorResultsTable() {
        super();
        setDefaultOptions();

        final StringCellEditor strCellEditor = new StringCellEditor();
        strCellEditor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cellEditor = new DefaultCellEditor(strCellEditor) {
            public Object getCellEditorValue() {
                return strCellEditor.getValue(); }
        };
        
    }
    
    private void setDefaultOptions() {
        setColumnSelectionAllowed(true);
        columnModel = new ResultsTableColumnModel();
        setColumnModel(columnModel);
        
        cellRenderer = new ResultsTableCellRenderer();
        cellRenderer.setFont(getFont());        
    }
    
    public void setBackground(Color background) {
        if (cellRenderer != null) {
            cellRenderer.setTableBackground(background);
        }
        super.setBackground(background);
    }
    
    public void setFont(Font font) {
        super.setFont(font);
        if (cellRenderer != null) {
            cellRenderer.setFont(font);
        }        
    }
    
    public TableCellRenderer getCellRenderer(int row, int column) {
        return cellRenderer;
    }
    
    public TableCellEditor getCellEditor(int row, int column) {
        return cellEditor;
    }
    
    class ResultsTableColumnModel extends DefaultTableColumnModel {
        
        // dumb work-around for update issue noted
        public TableColumn getColumn(int columnIndex) {
            try {
                return super.getColumn(columnIndex);
            } catch (Exception e) {
                return dummyColumn;
            }
        }
        
    } // class ResultsTableColumnModel
    
}






