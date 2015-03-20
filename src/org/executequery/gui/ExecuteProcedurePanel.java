/*
 * ExecuteProcedurePanel.java
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


package org.executequery.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;

import org.executequery.Constants;
import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.base.DefaultTabViewActionPanel;
import org.underworldlabs.swing.actions.ActionUtilities;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.databasemediators.DatabaseConnection;

import org.executequery.databasemediators.DatabaseProcedure;
import org.executequery.databasemediators.DatabaseResourceHolder;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.databasemediators.ProcedureParameter;
import org.executequery.databasemediators.QuerySender;
import org.executequery.databasemediators.SqlStatementResult;
import org.executequery.datasource.ConnectionManager;

import org.executequery.gui.editor.QueryEditorResultsPanel;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.FlatSplitPane;
import org.underworldlabs.swing.GUIUtils;

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
 * @date     $Date: 2006/07/15 13:14:12 $
 */
public class ExecuteProcedurePanel extends DefaultTabViewActionPanel
                                   implements MultiplePanelInstance,
                                              ItemListener,
                                              ConnectionListener,
                                              DatabaseResourceHolder {
    
    public static final String TITLE = "Execute Stored Objects ";
    public static final String FRAME_ICON = "Procedure16.gif";
    
    /** the active connections combo box model */
    private DynamicComboBoxModel connectionsModel;

    /** the schemas combo box model */
    private DynamicComboBoxModel schemaModel;

    /** the active connections combo */
    private JComboBox connectionsCombo;

    /** lists available schemas */
    private JComboBox schemaCombo;
    
    /** the object type combo */
    private JComboBox objectTypeCombo;
    
    /** lists available procedures */
    private JComboBox procedureCombo;
    
    /** the active connections combo box model */
    private DynamicComboBoxModel proceduresModel;

    /** the parameters table */
    private JTable table;
    
    /** proc parameters table model */
    private ParameterTableModel tableModel;
    
    /** the results panel */
    private QueryEditorResultsPanel resultsPanel;
    
    /** database meta data utility */
    private MetaDataValues metaData;
    
    /** execution utility */
    private QuerySender querySender;
    
    private String[] PROCEDURE;
    private String[] FUNCTION;
    
    private boolean useCatalogs;
    
    /** the instance count */
    private static int count = 1;
    
    public ExecuteProcedurePanel() {
        super(new BorderLayout());
        
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void init() throws Exception {

        tableModel = new ParameterTableModel();
        table = new DefaultTable(tableModel);
        table.setRowHeight(20);
        table.getTableHeader().setReorderingAllowed(false);
        table.setCellSelectionEnabled(true);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        
        metaData = new MetaDataValues(true);
        
        // combo boxes
        Vector connections = ConnectionManager.getActiveConnections();
        connectionsModel = new DynamicComboBoxModel(connections);
        connectionsCombo = new JComboBox(connectionsModel);
        connectionsCombo.addItemListener(this);

        schemaModel = new DynamicComboBoxModel();
        schemaCombo = new JComboBox(schemaModel);
        schemaCombo.addItemListener(this);
        schemaCombo.setToolTipText("Select the schema");
        
        objectTypeCombo = new JComboBox(new String[]{"Functions", "Procedures"});
        objectTypeCombo.setToolTipText("Select the database object type");
        objectTypeCombo.addItemListener(this);
        
        proceduresModel = new DynamicComboBoxModel();
        procedureCombo = new JComboBox(proceduresModel);
        procedureCombo.setActionCommand("procedureSelectionChanged");
        procedureCombo.setToolTipText("Select the database object name");
        procedureCombo.addActionListener(this);
        
        JPanel base = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,7,5,8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        base.add(new JLabel("Connection:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.left = 0;        
        base.add(connectionsCombo, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        gbc.insets.left = 7;
        gbc.insets.top = 0;
        base.add(new JLabel("Schema:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.left = 0;
        base.add(schemaCombo, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        gbc.insets.left = 7;
        gbc.insets.top = 0;
        base.add(new JLabel("Object Type:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.left = 0;
        base.add(objectTypeCombo, gbc);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy++;
        gbc.insets.left = 7;
        base.add(new JLabel("Object Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.left = 0;
        base.add(procedureCombo, gbc);
        
        resultsPanel = new QueryEditorResultsPanel();
        JPanel resultsBase = new JPanel(new BorderLayout());
        resultsBase.add(resultsPanel, BorderLayout.CENTER);
        
        JSplitPane splitPane = null;
        if (GUIUtilities.getLookAndFeel() < Constants.GTK_LAF) {
            splitPane = new FlatSplitPane(JSplitPane.VERTICAL_SPLIT,
                                          new JScrollPane(table),
                                          resultsBase);
        }
        else {
            splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                       new JScrollPane(table),
                                       resultsBase);
        }

        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.75);
        splitPane.setDividerSize(5);
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weighty = 1.0;
        gbc.insets.left = 7;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        base.add(splitPane, gbc);
        
        JButton executeButton = ActionUtilities.createButton(this, "Execute", "execute");
        executeButton.setPreferredSize(Constants.FORM_BUTTON_SIZE);
        executeButton.setMinimumSize(Constants.FORM_BUTTON_SIZE);

        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        base.add(executeButton, gbc);
        
        base.setBorder(BorderFactory.createEtchedBorder());
        setBorder(BorderFactory.createEmptyBorder(5,5,7,5));
        
        add(base, BorderLayout.CENTER);
        
        PROCEDURE = new String[]{"PROCEDURE"};
        FUNCTION = new String[]{"FUNCTION"};
        
        // check initial values for possible value inits
        if (connections == null || connections.isEmpty()) {
            enableCombos(false);
        } else {
            DatabaseConnection connection = 
                    (DatabaseConnection)connections.elementAt(0);
            metaData.setDatabaseConnection(connection);
            Vector schemas = metaData.getHostedSchemasVector();
            if (schemas == null || schemas.isEmpty()) {
                useCatalogs = true;
                schemas = metaData.getHostedCatalogsVector();
            }
            schemaModel.setElements(schemas);
            schemaCombo.setSelectedIndex(0);
        }

        EventMediator.registerListener(EventMediator.CONNECTION_EVENT, this);
    }

    private void enableCombos(boolean enable) {
        schemaCombo.setEnabled(enable);
        connectionsCombo.setEnabled(enable);
        schemaCombo.setEnabled(enable);
        
        if (objectTypeCombo.isEnabled()) {
            objectTypeCombo.setEnabled(enable);
        }

        procedureCombo.setEnabled(enable);
    }
    
    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     */    
    public void itemStateChanged(ItemEvent e) {
        // interested in selections only
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        final Object source = e.getSource();
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {
                    setInProcess(true);
                    reloadProcedureList(source);
                }
                finally {
                    setInProcess(false);
                }
            }
        });
    }

    private void reloadProcedureList(Object source) {
        if (source == connectionsCombo) {
            // retrieve connection selection
            DatabaseConnection connection = 
                    (DatabaseConnection)connectionsCombo.getSelectedItem();
            // reset meta data
            metaData.setDatabaseConnection(connection);

            // reset schema values
            Vector schemas = null;
            try {
                schemas = metaData.getHostedSchemasVector();
                if (schemas == null || schemas.isEmpty()) {
                    useCatalogs = true;
                    schemas = metaData.getHostedCatalogsVector();
                }
            }
            catch (DataSourceException e) {
                GUIUtilities.displayExceptionErrorDialog(
                        "Error retrieving the catalog/schema names for " +
                        "the current connection.\n\nThe system returned:\n" + 
                        e.getExtendedMessage(), e);
                schemas = new Vector<String>(0);
            }
            populateSchemaValues(schemas);
        }
        else if (source == schemaCombo) {
            schemaChanged();
        }
        else if (source == objectTypeCombo) {
            objectTypeChanged();
        }
    }
    
    private void populateSchemaValues(final Vector schemas) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                // enable the object type combo
                objectTypeCombo.setEnabled(true);

                // remall all and disable the procedures combo
                proceduresModel.removeAllElements();
                procedureCombo.setEnabled(false);

                if (schemas != null) {
                    schemaModel.setElements(schemas);
                    schemaCombo.setEnabled(true);

                    if (!schemas.isEmpty()) {
                        schemaCombo.setSelectedIndex(0);
                    }

                } 
                else {
                    schemaModel.removeAllElements();
                    schemaCombo.setEnabled(false);
                }
            }
        });
    }

    private void objectTypeChanged() {
        DatabaseProcedure[] procs = null;
        try {
            String catalogName = null;
            String schemaName = null;
            Object value = schemaCombo.getSelectedItem();

            if (value != null) {
                if (useCatalogs) {
                    catalogName = value.toString();
                }
                else {                    
                    schemaName = value.toString();
                }
            }

            // set the connection on the meta data
            DatabaseConnection dc = (DatabaseConnection)
                            connectionsCombo.getSelectedItem();
            metaData.setDatabaseConnection(dc);

            if (objectTypeCombo.isEnabled()) {
            
                // check for 'normal' object types - non proc term
                String[] type = objectTypeCombo.getSelectedIndex() == 0 ?
                                                        FUNCTION : PROCEDURE;

                String schema = useCatalogs ? catalogName : schemaName;              
                procs = metaData.getStoredObjects(schema, type);

                // check the other object type
                if (procs == null || procs.length == 0) {
                    // swap the type over
                    if (type == FUNCTION) {
                        type = PROCEDURE;
                    } else {
                        type = FUNCTION;
                    }

                    // if still empty continue, otherwise bail and
                    // let the user pick the other type
                    if (metaData.hasStoredObjects(schema, type)) {
                        GUIUtils.invokeAndWait(new Runnable() {
                            public void run() {
                                proceduresModel.removeAllElements();
                                procedureCombo.setEnabled(false);                                
                            }
                        });
                        return;
                    }

                }

            }
            
            boolean usedProcedureTerm = false;
            
            // if we don't have any, try the meta data proc term
            if (procs == null || procs.length == 0) {

                // retrieve the procedure names
                String[] procedures = metaData.getProcedureNames(
                                                    catalogName, schemaName, null);

                // check the proc term if we have nothing
                if (procedures == null || procedures.length == 0) {
                    procedures = checkProcedureTerm(catalogName, schemaName);
                }

                // check if we still have none and get the proc details
                if (procedures != null || procedures.length > 0) {
                    procs = metaData.getProcedures(catalogName, schemaName, procedures);
                }

                /*
                // loop through and get the procs
                procs = new DatabaseProcedure[procedures.length];
                for (int i = 0; i < procedures.length; i++) {
                    procs[i] = metaData.getProcedureColumns(catalogName, schemaName, procedures[i]);
                }
                */

                usedProcedureTerm = (procs != null && procs.length > 0);                
            }

            populateProcedureValues(procs, usedProcedureTerm);
        }
        catch (DataSourceException e) {
            GUIUtilities.displayExceptionErrorDialog(
                    "Error retrieving a list of stored object for the " +
                    "selected connection:.\n\nThe system returned:\n" + 
                    e.getExtendedMessage(), e);
        }

    }

    private void populateProcedureValues(final DatabaseProcedure[] procs, 
                                         final boolean usedProcedureTerm) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                // check that we finally have some
                if (procs != null && procs.length > 0) {
                    // reset the procedures combo
                    proceduresModel.setElements(procs);
                    procedureCombo.setSelectedIndex(0);
                    procedureCombo.setEnabled(true);

                    // disable the object type combo if it wasn't 
                    // used to retrieve the procs list
                    if (usedProcedureTerm) {
                        objectTypeCombo.setEnabled(false);
                    } else {
                        objectTypeCombo.setEnabled(true);
                    }

                }
                else {
                    proceduresModel.removeAllElements();
                    procedureCombo.setEnabled(false);
                }

            }
        });
    }
    
    /**
     * Checks the procedure term against a function or procedure node
     * when the returned results from getTables(...) is null or empty.
     *
     * @param object - the meta object
     */
    private String[] checkProcedureTerm(String catalog, String schema) {
        DatabaseConnection dc = (DatabaseConnection)
                        connectionsCombo.getSelectedItem();
        metaData.setDatabaseConnection(dc);
        
        try {
            String procedureTerm = metaData.getProcedureTerm();            
            if (procedureTerm != null) {
                return metaData.getProcedureNames(catalog, schema, null);
            }
        }
        catch (DataSourceException e) {}
        return new String[0];
    }

    /**
     * Called when a schema selection has changed.
     */
    private void schemaChanged() {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                // reset the procedures combo
                proceduresModel.removeAllElements();
                procedureCombo.setEnabled(false);

                // enable the type combo
                objectTypeCombo.setEnabled(true);

                // run action on object type combo
                if (objectTypeCombo.getSelectedIndex() == 0) {
                    objectTypeChanged();
                } else {
                    objectTypeCombo.setSelectedIndex(0);
                }
            }
        });
    }

    public void cleanup() {
        EventMediator.deregisterListener(EventMediator.CONNECTION_EVENT, this);
        if (metaData != null) {
            closeConnection();
        }
        if (querySender != null) {
            try {
                querySender.destroyConnection();
            } catch (SQLException e) {}
        }
    }
    
    public void closeConnection() {
        metaData.closeConnection();
    }

    /**
     * Invoked on selection of a procedure from the combo.
     */
    public void procedureSelectionChanged() {
        int index = procedureCombo.getSelectedIndex();
        DatabaseProcedure proc = (DatabaseProcedure)proceduresModel.getElementAt(index);

        if (proc != null) {
            tableModel.setValues(proc.getParameters());
        } else {
            tableModel.clear();
        }

        tableModel.fireTableDataChanged();
    }
    
    /**
     * Executes the selected procedure.
     */
    public void execute() {
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();

        if (selectedRow != -1 && selectedColumn != -1) {
            if (table.isEditing()) {
                table.getCellEditor(
                        selectedRow, selectedColumn).stopCellEditing();
            }
        }

        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {
                    setInProcess(true);
                    DatabaseConnection dc = (DatabaseConnection)
                                    connectionsCombo.getSelectedItem();
                    if (dc == null) {
                        GUIUtilities.displayErrorMessage(
                                "No database connection is available.");
                        return;
                    }

                    int index = procedureCombo.getSelectedIndex();
                    DatabaseProcedure proc =
                            (DatabaseProcedure)proceduresModel.getElementAt(index);

                    if (proc == null) {
                        return;
                    }

                    //resultsPanel.clearErrorPanel();
                    int type = objectTypeCombo.getSelectedIndex();
                    String text = type == 0 ? " function " : " procedure ";
                    setActionMessage("Executing" + text + proc.getName() + "...");

                    if (querySender == null) {
                        querySender = new QuerySender(dc);
                    } else {
                        querySender.setDatabaseConnection(dc);
                    }

                    SqlStatementResult result = querySender.executeProcedure(proc);
                    Hashtable results = (Hashtable)result.getOtherResult();

                    if (results == null) {
                        setErrorMessage(result.getErrorMessage());
                    }
                    else {
                        setPlainMessage("Statement executed successfully.");
                        int updateCount = result.getUpdateCount();

                        if (updateCount > 0) {
                            setPlainMessage(updateCount + 
                                    updateCount > 1 ? " rows affected." : " row affected.");
                        }

                        String SPACE = " = ";
                        Enumeration keys = results.keys();

                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            setPlainMessage(key + SPACE + results.get(key));
                        }

                    }

                }
                catch(Exception exc) {
                    exc.printStackTrace();
                }
                finally {
                    setInProcess(false);
                }
            }
        });
    }

    private void setActionMessage(final String message) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                resultsPanel.setActionMessage(message);
            }
        });        
    }
    
    private void setPlainMessage(final String message) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                resultsPanel.setPlainMessage(message);
            }
        });
    }

    private void setErrorMessage(final String message) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                resultsPanel.setErrorMessage(message);
            }
        });
    }
    
    // ---------------------------------------------
    // ConnectionListener implementation
    // ---------------------------------------------
    
    /**
     * Indicates a connection has been established.
     * 
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent) {
        enableCombos(true);
        connectionsModel.addElement(connectionEvent.getSource());
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent) {
        connectionsModel.removeElement(connectionEvent.getSource());
        if (connectionsModel.getSize() == 0) {
            enableCombos(false);
        }
        // TODO: NEED TO CHECK OPEN CONN
        
    }

    /**
     * Returns the display name for this view.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return TITLE + (count++);
    }

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        cleanup();
        return true;
    }
    
    
    class ParameterTableModel extends AbstractTableModel {
        
        private String UNKNOWN = "UNKNOWN";
        private String RETURN = "RETURN";
        private String RESULT = "RESULT";
        private String IN = "IN";
        private String INOUT = "INOUT";
        private String OUT = "OUT";
        
        private String[] columns = {"Parameter", "Data Type", "Mode", "Value"};
        private ProcedureParameter[] values;
        
        public ParameterTableModel() {}
        
        public ParameterTableModel(ProcedureParameter[] _procParams) {
            values = _procParams;
        }
        
        public int getRowCount() {
            if (values == null) {
                return 0;
            }            
            return values.length;
        }
        
        public int getColumnCount() {
            return 4;
        }
        
        public void clear() {
            values = null;
        }
        
        public void setValues(ProcedureParameter[] _procParams) {
            values = _procParams;
        }
        
        public Object getValueAt(int row, int col) {
            if (values == null) {
                return "";
            }

            ProcedureParameter param = values[row];
            
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
                    
                case 3:
                    String value = param.getValue();
                    return value == null ? Constants.EMPTY : value;
                    
                default:
                    return UNKNOWN;
                    
            }
            
        }
        
        public void setValueAt(Object value, int row, int col) {
            ProcedureParameter param = values[row];
            
            switch (col) {
                
                case 0:
                    param.setName((String)value);
                    break;
                    
                case 1:
                    param.setSqlType((String)value);
                    break;
                    
                case 2:
                    if (value == IN) {
                        param.setType(DatabaseMetaData.procedureColumnIn);
                    }
                    else if (value == OUT) {
                        param.setType(DatabaseMetaData.procedureColumnOut);
                    }
                    else if (value == INOUT) {
                        param.setType(DatabaseMetaData.procedureColumnInOut);
                    }
                    else if (value == UNKNOWN) {
                        param.setType(DatabaseMetaData.procedureColumnUnknown);
                    }
                    else if (value == RESULT) {
                        param.setType(DatabaseMetaData.procedureColumnResult);
                    }
                    else if (value == RETURN) {
                        param.setType(DatabaseMetaData.procedureColumnReturn);
                    }
                case 3:
                    param.setValue((String)value);
                    
            }
            
            fireTableCellUpdated(row, col);
            
        }
        
        public String getColumnName(int col) {
            return columns[col];
        }
        
        public boolean isCellEditable(int row, int col) {
            
            if (col != 3) {
                return false;
            }
            
            ProcedureParameter param = values[row];
            int mode = param.getType();
            switch (mode) {
                
                case DatabaseMetaData.procedureColumnIn:
                case DatabaseMetaData.procedureColumnInOut:
                    return true;
                    
                case DatabaseMetaData.procedureColumnOut:
                case DatabaseMetaData.procedureColumnUnknown:
                case DatabaseMetaData.procedureColumnResult:
                case DatabaseMetaData.procedureColumnReturn:
                    return false;
                    
                default:
                    return true;
                    
            }
            
        }
        
    } // class ParameterTableModel
    
}



