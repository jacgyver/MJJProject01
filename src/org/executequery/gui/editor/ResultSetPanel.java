/*
 * ResultSetPanel.java
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
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.underworldlabs.util.SystemProperties;
import org.underworldlabs.swing.table.RowNumberHeader;
import org.underworldlabs.swing.table.TableSorter;
import org.executequery.print.PrintUtilities;
import org.executequery.print.TablePrinter;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Simple SQL result set display panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/07/15 16:36:32 $
 */
public class ResultSetPanel extends JPanel {
    
    /** the table display */
    private QueryEditorResultsTable table;
    
    /** the table model */
    private ResultSetTableModel model;
    
    /** the table scroll pane */
    private JScrollPane scroller;
    
    /** the table sorter */
    private TableSorter sorter;
    
    /** whether a result set exists in this view */
    private boolean hasResultSet;
    
    /** whether to displaay the row header */
    private boolean showRowHeader;
    
    /** the table display default column width */
    private int columnWidth;
    
    /** the associated meta data panel */
    private ResultSetMetaDataPanel metaDataPanel;
    
    /** table pop-up menu */
    private PopMenu popupMenu;
    
    /** the row number header */
    private RowNumberHeader rowNumberHeader;
    
    /** Creates a new instance of ResultSetPanel */
    public ResultSetPanel() {
        super(new BorderLayout());
        init();
    }
    
    private void init() {
        Color bg = SystemProperties.getColourProperty("user",
                "editor.results.background.colour");
        table = new QueryEditorResultsTable();
        
        // this is set for the bg of any remaining 
        // header region outside the cells themselves
        table.getTableHeader().setBackground(bg);
        
        scroller = new JScrollPane(table,
                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setBackground(bg);
        scroller.setBorder(null);
        scroller.getViewport().setBackground(bg);

        add(scroller, BorderLayout.CENTER);
        setTableProperties();
        
        table.addMouseListener(new MouseHandler());
    }

    /**
     * Sets the results background to that specified.
     *
     * @param the colour to set
     */
    public void setResultBackground(Color c) {
        scroller.setBackground(c);
        scroller.getViewport().setBackground(c);
        if (table != null) {
            table.getTableHeader().setBackground(c);
        }
    }

    public void destroyTable() {
        table = null;
        if (popupMenu != null) {
            popupMenu.removeAll();
        }
        popupMenu = null;
    }

    public void interrupt() {
        if (model != null) {
            model.interrupt();
        }
    }
    
    public int setResultSet(ResultSetTableModel model, boolean showRowNumber)
        throws SQLException {
        this.model = model;

        int rowCount = model.getRowCount();
        if (rowCount > 0) {
            buildTable(rowCount);            
        }

        return rowCount;
    }

    /**
     * Builds the result set table display.
     *
     * @param the row count
     */
    private void buildTable(int rowCount) throws SQLException {
        boolean sorterWasNull = false;
        
        if (sorter == null) {
            sorterWasNull = true;
            sorter = new TableSorter(model);
        }
        else {
            sorter.setTableModel(model);
        }
        
        if (table == null) {
            table = new QueryEditorResultsTable(sorter);
            scroller.getViewport().add(table);
        }
        else {
            table.setModel(sorter);
        }
        
        // reset the table header
        if (sorterWasNull) {
            sorter.setTableHeader(table.getTableHeader());
        }
        
        setTableColumnWidth();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        hasResultSet = true;

        if (showRowHeader) {
            addRowNumberHeader();
        }
    }

    protected void tableDataChanged() {
        if (showRowHeader) {
            addRowNumberHeader();
        }        
    }
    
    private void addRowNumberHeader() {
        boolean needRepaint = false;
        if (rowNumberHeader == null) {
            rowNumberHeader = new RowNumberHeader(table);
            rowNumberHeader.setBackground(SystemProperties.getColourProperty(
                                "user", "editor.results.background.colour"));
        }
        else {
            rowNumberHeader.setTable(table);
        }
        scroller.setRowHeaderView(rowNumberHeader);
    }
    
    /**
     * Sets the user defined (preferences) table properties.
     */
    public void setTableProperties() {
        table.setDragEnabled(true);
        table.setCellSelectionEnabled(true);

        table.setBackground(
            SystemProperties.getColourProperty("user", "results.table.cell.background.colour"));
        
        table.setRowHeight(
            SystemProperties.getIntProperty("user", "results.table.column.height"));
        
        table.setRowSelectionAllowed(
            SystemProperties.getBooleanProperty("user", "results.table.row.select"));
        
        table.getTableHeader().setResizingAllowed(
            SystemProperties.getBooleanProperty("user", "results.table.column.resize"));

        table.getTableHeader().setReorderingAllowed(
            SystemProperties.getBooleanProperty("user", "results.table.column.reorder"));
        
        showRowHeader = SystemProperties.getBooleanProperty("user", "results.table.row.numbers");
        if (showRowHeader) {
            addRowNumberHeader();
        } else {
            if (rowNumberHeader != null) { 
                // remove the row header if its there now
                scroller.setRowHeaderView(null);
            }
            rowNumberHeader = null;
        }
        
        columnWidth = SystemProperties.getIntProperty("user", "results.table.column.width");
        setTableColumnWidth();
        
        if (model != null) {
            model.setHoldMetaData(SystemProperties.getBooleanProperty(
                                            "user", "editor.results.metadata"));
        }
    }
    
    /**
     * Sets the table column width.
     */
    private void setTableColumnWidth() {
        TableColumnModel tcm = table.getColumnModel();        
        if (columnWidth != 75) {
            TableColumn col = null;
            for (Enumeration i = tcm.getColumns(); i.hasMoreElements();) {
                col = (TableColumn)i.nextElement();
                col.setPreferredWidth(columnWidth);
            }
        }
    }

    /**
     * Returns the model row count.
     *
     * @return the row ount displayed
     */
    public int getRowCount() {
        return model.getRowCount();
    }
    
    /**
     * Indicates whether the model has retained the ResultSetMetaData.
     *
     * @return true | false
     */
    public boolean hasResultSetMetaData() {
        if (model == null) {
            return false;        
        } else {
            return model.hasResultSetMetaData();
        }
    }

    /**
     * Returns the table display.
     *
     * @return the table
     */
    public JTable getResultsTable() {
        return table;
    }

    /**
     * Sets to display the result set meta data.
     */
    public ResultSetMetaDataPanel getResultSetMetaDataPanel() {
        if (!model.hasResultSetMetaData()) {
            return null;
        }

        if (metaDataPanel == null) {
            metaDataPanel = new ResultSetMetaDataPanel(model.getResultSetMetaData());
        }
        else {
            metaDataPanel.setMetaData(model.getResultSetMetaData());
        }
        return metaDataPanel;
    }

    /** 
     * Returns the result set table model.
     * 
     * @return the table model
     */
    public ResultSetTableModel getResultSetTableModel() {
        return model;
    }
    
    protected void printResultSet(boolean printSelection) {
        JTable printTable = null;
        if (printSelection) {
            TableModel _model = buildSelectedCellsModel();
            if (_model != null) {
                printTable = new JTable(_model);
            } else {
                return;
            }
        }
        else {
            printTable = table;
        }

        Printable printable = new TablePrinter(printTable, null);
        PrintUtilities.print(printable, "Execute Query - editor");
    }
    
    protected TableModel buildSelectedCellsModel() {
        int cols = table.getSelectedColumnCount();
        int rows = table.getSelectedRowCount();
        
        if (cols == 0 && rows == 0) {
            return null;
        }

        int[] selectedRows = table.getSelectedRows();
        int[] selectedCols = table.getSelectedColumns();

        Vector data = new Vector(rows);
        Vector columns = new Vector(cols);

        for (int i = 0; i < rows; i++) {
            Vector rowVector = new Vector(cols);
            for (int j = 0; j < cols; j++) {
                rowVector.add(table.getValueAt(
                        selectedRows[i], selectedCols[j]));
                if (i == 0) {
                    columns.add(table.getColumnName(selectedCols[j]));
                }
            }
            data.add(rowVector);
         }
        
        return new DefaultTableModel(data, columns);
    }
    
    protected void selectRow(Point point) {
        if (point != null) {
            table.clearSelection();
            int row = table.rowAtPoint(point);
            table.setColumnSelectionAllowed(false);
            table.setRowSelectionAllowed(true);
            table.setRowSelectionInterval(row, row);
        }
    }

    protected void selectColumn(Point point) {
        if (point != null) {
            int column = table.columnAtPoint(point);
            table.setColumnSelectionAllowed(true);
            table.setRowSelectionAllowed(false);
            table.setColumnSelectionInterval(column, column);
        }
    }

    /**
     * Performs the cell data copy for a cell selection.
     */
    protected void copySelectedCells() {
        StringBuffer sb = new StringBuffer();
        int cols = table.getSelectedColumnCount();
        int rows = table.getSelectedRowCount();
        
        if (cols == 0 && rows == 0) {
            return;
        }

        int[] selectedRows = table.getSelectedRows();
        int[] selectedCols = table.getSelectedColumns();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(table.getValueAt(selectedRows[i], selectedCols[j]));
               if (j < cols - 1) {
                   sb.append("\t");
               }
            }
            sb.append("\n");
         }

         StringSelection stsel  = new StringSelection(sb.toString());
         Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipBoard.setContents(stsel,stsel);
    }
    
    private class MouseHandler extends MouseAdapter {
        
        public MouseHandler() {}
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                
                if (popupMenu == null) {
                    popupMenu = new PopMenu();
                }

                popupMenu.lastPoint = new Point(e.getX(), e.getY());
                popupMenu.show(e.getComponent(), 
                               popupMenu.lastPoint.x, 
                               popupMenu.lastPoint.y);

            } 
            else {
                // re-enable cell selection
                table.setColumnSelectionAllowed(true);
                table.setRowSelectionAllowed(true);
            }
        }

    } // class MouseHandler
    
    
    /** The table's popup menu function */
    private class PopMenu extends JPopupMenu implements ActionListener {
        
        private JMenuItem copy;
        private JMenuItem selectRow;
        private JMenuItem selectColumn;
        private JMenuItem exportSelection;
        private JMenuItem exportTable;
        private JMenuItem printSelection;
        private JMenuItem printTable;
        
        /** the last point of the popup */
        protected Point lastPoint;

        public PopMenu() {
            copy = new JMenuItem("Copy Selected Cells");
            copy.addActionListener(this);

            selectRow = new JMenuItem("Select Row");
            selectRow.addActionListener(this);

            selectColumn = new JMenuItem("Select Column");
            selectColumn.addActionListener(this);

            exportSelection = new JMenuItem("Export Selection");
            exportSelection.addActionListener(this);

            exportTable = new JMenuItem("Export Table");
            exportTable.addActionListener(this);

            // the print sub-menu
            JMenu printMenu = new JMenu("Print");
            
            printSelection = new JMenuItem("Selection");
            printSelection.addActionListener(this);

            printTable = new JMenuItem("Table");
            printTable.addActionListener(this);

            printMenu.add(printSelection);
            printMenu.add(printTable);
            
            add(copy);
            addSeparator();
            add(selectRow);
            add(selectColumn);
            addSeparator();
            add(exportSelection);
            add(exportTable);
            addSeparator();
            add(printMenu);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Object source = e.getSource();
                if (source == copy) {
                    copySelectedCells();
                }
                else if (source == selectColumn) {
                    selectColumn(lastPoint);
                }
                else if (source == selectRow) {
                    selectRow(lastPoint);
                }
                else if (source == exportSelection) {
                    TableModel selected = buildSelectedCellsModel();
                    if (selected != null) {
                        new QueryEditorResultsExporter(selected);
                    }
                }
                else if (source == exportTable) {
                    new QueryEditorResultsExporter(model);
                }
                else if (source == printSelection) {
                    printResultSet(true);
                }
                else if (source == printTable) {
                    printResultSet(false);
                }
            } finally {
                lastPoint = null;
                
            }

        }
    } // class PopMenu


}



