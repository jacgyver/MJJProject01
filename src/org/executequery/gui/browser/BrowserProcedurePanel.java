/*
 * BrowserProcedurePanel.java
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.print.Printable;
import java.sql.DatabaseMetaData;

import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import org.executequery.GUIUtilities;

import org.executequery.databasemediators.DatabaseProcedure;
import org.executequery.databasemediators.ProcedureParameter;
import org.executequery.gui.DefaultTable;

import org.executequery.print.TablePrinter;
import org.underworldlabs.swing.DisabledField;
import org.executequery.gui.forms.AbstractFormObjectViewPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.8 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class BrowserProcedurePanel extends AbstractFormObjectViewPanel {
    
    public static final String NAME = "BrowserProcedurePanel";
    
    private DisabledField procNameField;
    private DisabledField schemaNameField;
    
    private JLabel noValuesLabel;
    private JLabel objectNameLabel;
    
    private JTable table;
    private ProcedureTableModel model;
    
    private HashMap cache;
    
    /** the browser's control object */
    private BrowserController controller;

    public BrowserProcedurePanel(BrowserController controller) {
        super();
        this.controller = controller;

        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void init() throws Exception {        
        model = new ProcedureTableModel();
        table = new DefaultTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(20);
        table.setCellSelectionEnabled(true);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        
        JPanel paramPanel = new JPanel(new BorderLayout());
        paramPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
        paramPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.add("Description", paramPanel);
        
        objectNameLabel = new JLabel();
        procNameField = new DisabledField();
        schemaNameField = new DisabledField();
        
        JPanel base = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Insets insets = new Insets(10,10,5,5);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx++;
        gbc.insets = insets;
        gbc.gridy++;
        base.add(objectNameLabel, gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        gbc.insets.right = 5;
        base.add(new JLabel("Schema:"), gbc);
        gbc.insets.right = 10;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        gbc.insets.bottom = 10;
        gbc.fill = GridBagConstraints.BOTH;
        base.add(tabs, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 5;
        gbc.insets.top = 10;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.gridy = 0;
        gbc.gridx = 1;
        base.add(procNameField, gbc);
        ++gbc.gridy;
        gbc.insets.top = 0;
        base.add(schemaNameField, gbc);
        
        setHeaderText("Database Procedure");
        setHeaderIcon(GUIUtilities.loadIcon("Procedure24.gif", true));
        setContentPanel(base);
        cache = new HashMap();
        
    }
    
    public String getLayoutName() {
        return NAME;
    }
    
    public Printable getPrintable() {
        return new TablePrinter(table, procNameField.getText());
    }
    
    public void refresh() {
        cache.clear();
    }
    
    public void cleanup() {}
    
    public JTable getTable() {
        return table;
    }

    public void removeObject(Object object) {
        if (cache.containsKey(object)) {
            cache.remove(object);
        }
    }
    
    public boolean hasObject(Object object) {
        return cache.containsKey(object);
    }

    public void setValues(DatabaseObject metaObject) {
        DatabaseProcedure procedure = (DatabaseProcedure)cache.get(metaObject);
        setValues(metaObject, procedure);
    }
    
    public void setValues(DatabaseObject metaObject, DatabaseProcedure procedure) {
        int type = metaObject.getType();
        switch (type) {
            case BrowserConstants.FUNCTIONS_NODE:
                objectNameLabel.setText("Function Name:");
                setHeaderText("Database Function");
                setHeaderIcon(GUIUtilities.loadIcon("Function24.gif", true));
                break;

            case BrowserConstants.PROCEDURE_NODE:
                objectNameLabel.setText("Procedure Name:");
                setHeaderText("Database Procedure");
                setHeaderIcon(GUIUtilities.loadIcon("Procedure24.gif", true));
                break;

            case BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE:
                objectNameLabel.setText("Function Name:");
                setHeaderText("Database System String Function");
                setHeaderIcon(GUIUtilities.loadIcon("SystemFunction24.gif", true));
                break;

            case BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE:
                objectNameLabel.setText("Function Name:");
                setHeaderText("Database System Numeric Function");
                setHeaderIcon(GUIUtilities.loadIcon("SystemFunction24.gif", true));
                break;

            case BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE:
                objectNameLabel.setText("Function Name:");
                setHeaderText("Database System Date/Time Function");
                setHeaderIcon(GUIUtilities.loadIcon("SystemFunction24.gif", true));
                break;
        }

        if (procedure != null) {
            procNameField.setText(procedure.getName());
            model.setValues(procedure.getParameters());
        } else {
            procNameField.setText(metaObject.getName());
        }

        schemaNameField.setText(metaObject.getSchemaName());
    }
    
    class ProcedureTableModel extends AbstractTableModel {
        
        private String UNKNOWN = "UNKNOWN";
        private String RETURN = "RETURN";
        private String RESULT = "RESULT";
        private String IN = "IN";
        private String INOUT = "INOUT";
        private String OUT = "OUT";
        
        private String[] columns = {"Parameter", "Data Type", "Mode"};
        private ProcedureParameter[] procParams;
        
        public ProcedureTableModel() {}
        
        public ProcedureTableModel(ProcedureParameter[] _procParams) {
            procParams = _procParams;
        }
        
        public int getRowCount() {
            
            if (procParams == null)
                return 0;
            
            return procParams.length;
        }
        
        public int getColumnCount() {
            return columns.length;
        }
        
        public void setValues(ProcedureParameter[] _procParams) {
            
            if (_procParams == procParams)
                return;
            
            procParams = _procParams;
            fireTableDataChanged();
            
        }
        
        public Object getValueAt(int row, int col) {
            ProcedureParameter param = procParams[row];
            
            switch (col) {
                
                case 0:
                    return param.getName();
                    
                case 1:
                    
                    if (param.getSize() > 0)
                        return param.getSqlType() + "(" + param.getSize() + ")";
                    else
                        return param.getSqlType();
                    
                case 2:
                    int mode = param.getType();
                    
                    switch (mode) {
                        
                        case DatabaseMetaData.procedureColumnIn:
                            return IN;
                            
                        case DatabaseMetaData.procedureColumnOut:
                            return OUT;
                            
                        case DatabaseMetaData.procedureColumnInOut:
                            return INOUT;
                            
                        case DatabaseMetaData.procedureColumnUnknown:
                            return UNKNOWN;
                            
                        case DatabaseMetaData.procedureColumnResult:
                            return RESULT;
                            
                        case DatabaseMetaData.procedureColumnReturn:
                            return RETURN;
                            
                        default:
                            return UNKNOWN;
                            
                    }
                    
                default:
                    return UNKNOWN;
                    
            }
            
        }
        
        public void setValueAt(Object value, int row, int col) {
            ProcedureParameter param = procParams[row];
            
            switch (col) {
                
                case 0:
                    param.setName((String)value);
                    break;
                    
                case 1:
                    param.setSqlType((String)value);
                    break;
                    
                case 2:
                    
                    if (value == IN)
                        param.setType(DatabaseMetaData.procedureColumnIn);
                    
                    else if (value == OUT)
                        param.setType(DatabaseMetaData.procedureColumnOut);
                    
                    else if (value == INOUT)
                        param.setType(DatabaseMetaData.procedureColumnInOut);
                    
                    else if (value == UNKNOWN)
                        param.setType(DatabaseMetaData.procedureColumnUnknown);
                    
                    else if (value == RESULT)
                        param.setType(DatabaseMetaData.procedureColumnResult);
                    
                    else if (value == RETURN)
                        param.setType(DatabaseMetaData.procedureColumnReturn);
                    
                    
            }
            
            fireTableCellUpdated(row, col);
            
        }
        
        public String getColumnName(int col) {
            return columns[col];
        }
        
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        
    } // class ParameterTableModel
    
    
}



