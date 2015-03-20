/*
 * ResultSetTableModel.java
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

import java.lang.reflect.Method;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.underworldlabs.util.SystemProperties;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>The query result set model.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ResultSetTableModel extends AbstractTableModel {
    
    /** The column names */
    protected Vector columnHeaders;

    /** The table values */
    protected ArrayList tableData;

    /** The column types */
    protected int[] columnTypes;
    
    /** the error message */
    private String errorMessage;
    
    /** The result set's meta data */
    private Vector[] rsmdResults;
    
    /** Whether the meta data should be generated */
    private boolean holdMetaData;
    
    /** The maximum number of records displayed */
    private int maxRecords;

    /** Indicates that the query executing has been interrupted */
    private boolean interrupted;
    
    public ResultSetTableModel(ResultSet rs) {
        this(rs, -1);
    }

    public ResultSetTableModel() {
        this(null, -1);
    }

    public ResultSetTableModel(int maxRecords) {
        this(null, maxRecords);
    }

    public ResultSetTableModel(ResultSet rs, int maxRecords) {
        this.maxRecords = maxRecords;
        holdMetaData = SystemProperties.getBooleanProperty("user", "editor.results.metadata");

        if (rs != null) {
            try {
                createTable(rs);
            }
            catch (Exception e) {}            
        }
        
    }

    public void createTable(ResultSet rset) {

        try {
            rsmdResults = null;
            ResultSetMetaData rsmd = rset.getMetaData();

            int count = rsmd.getColumnCount();
            
            if (columnHeaders != null) {
                columnHeaders.clear();
                columnHeaders.ensureCapacity(count);
            } else {
                columnHeaders = new Vector(count);
            }
            
            if (tableData != null) {
                tableData.clear();
            } else {
                tableData = new ArrayList();
            }
            
            /*
            columnHeaders = new Vector(count);
            columnTypes = new int[count];
            tableData = new ArrayList();
            */

            columnTypes = new int[count];            
            for (int i = 1; i <= count; i++) {
                columnHeaders.add(rsmd.getColumnName(i));
                columnTypes[i-1] = rsmd.getColumnType(i);
            }

            int recordCount = 0;
            ArrayList rowData = null;
            interrupted = false;

            while (rset.next()) {

                if (interrupted || Thread.currentThread().interrupted()) {
                    throw new InterruptedException();
                }
                
                recordCount++;
                rowData = new ArrayList(count);
                
                for (int i = 1; i <= count; i++) {
                    
                    try {
                        rowData.add(rset.getObject(i));
                    }
                    catch (Exception e) {
                        rowData.add(rset.getString(i));
                    }
                    
                }
                
                tableData.add(rowData);
                if (recordCount == maxRecords) {
                    break;
                }

            }
            
            if (holdMetaData) {
                setMetaDataVectors(rsmd);
            }
            
            fireTableStructureChanged();
        }
        catch (SQLException sqlExc) {
            //sqlExc.printStackTrace();
            System.err.println("SQL error generating table at: " + 
                    sqlExc.getMessage());
        }
        catch (Exception e) {
            //e.printStackTrace();
            String message = e.getMessage();            
            if (MiscUtils.isNull(message)) {
                System.err.println("Exception generating table.");
            }
            else {
                System.err.println("Exception generating table at: " + message);
            }
        }
        finally {
            if (rset != null) {
                try {
                    rset.close();
                }
                catch (SQLException sqlExc) {}
            }
        }
        
    }
    
    public void interrupt() {
        interrupted = true;
    }
    
    public ArrayList getTableData() {
        return tableData;
    }
    
    public void setHoldMetaData(boolean holdMetaData) {
        this.holdMetaData = holdMetaData;
    }
    
    private void setMetaDataVectors(ResultSetMetaData rsmd) {
        
        Class metaClass = rsmd.getClass();
        Method[] metaMethods = metaClass.getMethods();
        
        String STRING = "String";
        String GET = "get";
        String NO_WANT = "getColumnCount";
        String COL_NAME = "ColumnName";
        
        Vector columnHeaders = null;
        Vector rowData = null;
        Vector tableData = null;
        
        try {
            
            int c_size = rsmd.getColumnCount();
            columnHeaders = new Vector(metaMethods.length - 1);
            tableData = new Vector(c_size);
            
            Object[] obj = new Object[1];
            
            for (int j = 1; j <= c_size; j++) {
                
                obj[0] = new Integer(j);
                rowData = new Vector(metaMethods.length - 1);
                
                for (int i = 0; i < metaMethods.length; i++) {
                    
                    if (i == 20) {
                        break;
                    }
                    
                    try {
                        String s = metaMethods[i].getName();
                        
                        if (s.equals(NO_WANT)) {
                            continue;
                        }
                        
                        Class c = metaMethods[i].getReturnType();
                        
                        if (c.isPrimitive() || c.getName().endsWith(STRING)) {
                            
                            if (s.startsWith(GET)) {
                                s = s.substring(3);
                            }
                            
                            try {
                                Object res = metaMethods[i].invoke(rsmd, obj);
                                
                                if (s.equals(COL_NAME)) {
                                    
                                    if (j == 1) {
                                        columnHeaders.insertElementAt(s,0);
                                    }
                                    rowData.insertElementAt(res.toString(),0);
                                    
                                }
                                
                                else {
                                    
                                    if (j == 1) {
                                        columnHeaders.add(s);
                                    }
                                    rowData.add(res.toString());
                                    
                                }
                                
                            }
                            
                            catch (AbstractMethodError abe) {
                                continue;
                            }
                            
                        }
                        
                    }
                    
                    catch (Exception e) {
                        continue;
                    }
                    
                }
                
                tableData.add(rowData);
                
            }
            
        } catch (java.sql.SQLException sqlExc) {}
        
        rsmdResults = new Vector[2];
        rsmdResults[0] = columnHeaders;
        rsmdResults[1] = tableData;
    }
    
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }
    
    public boolean hasResultSetMetaData() {
        return rsmdResults != null && rsmdResults.length > 0;
    }
    
    public void reset() {
        columnHeaders = null;
        tableData = null;
        columnTypes = null;
        rsmdResults = null;
    }
    
    public Vector[] getResultSetMetaData() {
        return rsmdResults;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public int getColumnCount() {
        if (columnHeaders == null) {
            return 0;
        }
        return columnHeaders.size();
    }
    
    public int getRowCount() {
        if (tableData == null) {
            return 0;
        }
        return tableData.size();
    }
    
    public Object getValueAt(int row, int column) {
        if (row < tableData.size()) {
            List rowData = (ArrayList)(tableData.get(row));
            if (column < rowData.size()) {
                return rowData.get(column);
            }
        }
        return null;
    }
    
    public Object getRowValueAt(int row) {
        return tableData.get(row);
    }
    
    public boolean isCellEditable(int row, int column) {
        return true;
    }
    
    public String getColumnName(int column) {
        return (String)(columnHeaders.elementAt(column));
    }
    
    public Class getColumnClass(int col) {
        
        switch (columnTypes[col]) {
            
            case Types.TINYINT:
                return Short.class;
                
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.BOOLEAN: // don't display the checkbox
                return String.class;
                
            case Types.BIGINT:
            case Types.INTEGER:
            case Types.DECIMAL:
            case Types.NUMERIC:
                return Number.class;
                
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
                return java.util.Date.class;
                
            case Types.DOUBLE:
                return Double.class;
                
            default:
                return Object.class;
                
        }
        
    }
    
}







