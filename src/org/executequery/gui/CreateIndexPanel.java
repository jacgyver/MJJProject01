/*
 * CreateIndexPanel.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import org.executequery.ActiveComponent;
import org.executequery.EventMediator;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.ActionUtilities;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.databasemediators.QuerySender;
import org.executequery.databasemediators.SqlStatementResult;
import org.executequery.datasource.ConnectionManager;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;
import org.executequery.gui.browser.ColumnData;
import org.executequery.components.BottomButtonPanel;
import org.executequery.gui.text.TextEditor;
import org.executequery.gui.text.SimpleSqlTextPanel;
import org.executequery.gui.text.TextEditorContainer;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The Create Index panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.11 $
 * @date     $Date: 2006/09/13 14:51:12 $
 */
public class CreateIndexPanel extends JPanel
                              implements FocusComponentPanel,
                                         ActiveComponent,
                                         TableModelListener,
                                         KeywordListener,
                                         TextEditorContainer,
                                         ActionListener,
                                         ItemListener {
    
    public static final String TITLE = "Create Index";
    public static final String FRAME_ICON = "NewIndex16.gif";
    
    
    /** The schema combo box */
    protected JComboBox schemaCombo;
    
    /** the schema combo box model */
    protected DynamicComboBoxModel schemaModel;
    
    /** The connection combo selection */
    protected JComboBox connectionsCombo; 

    /** the connections combo box model */
    protected DynamicComboBoxModel connectionsModel;

    /** the table combo box model */
    protected DynamicComboBoxModel tablesModel;

    private JTextField nameField;
    private JComboBox tableCombo;
    private JCheckBox normalCheck;
    private JCheckBox uniqueCheck;
    private JCheckBox bitmapCheck;
    private JCheckBox unsortedCheck;
    
    private boolean useCatalogs;
    private MetaDataValues metaData;
    private Vector tableVector;
    
    private SimpleSqlTextPanel sqlText;
    private JTable selectedTable;
    private CreateIndexModel model;
    
    private JButton moveUpButton;
    private JButton moveDownButton;
    
    private StringBuffer sqlBuffer;
    
    private static String ON = "\n    ON ";
    private static String CREATE_UNIQUE = "CREATE UNIQUE INDEX ";
    private static String CREATE_BITMAP = "CREATE BITMAP INDEX ";
    private static String CREATE_INDEX = "CREATE INDEX ";
    private static String NO_SORT = " NOSORT";
    private static String NEW_LINE = "\n    ";
    private static String SPACE = " ";
    private static String EMPTY = "";
    private static char COMMA = ',';
    private static char DOT = '.';
    private static char B_OPEN = '(';
    private static char B_CLOSE = ')';
    
    /** the parent container */
    private ActionContainer parent;

    
    /** <p>Constructs a new instance. */
    public CreateIndexPanel(ActionContainer parent) {
        super(new BorderLayout());
        this.parent = parent;
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        
        nameField = new JTextField();
        metaData = new MetaDataValues(true);

        // combo boxes
        Vector connections = ConnectionManager.getActiveConnections();
        connectionsModel = new DynamicComboBoxModel(connections);
        connectionsCombo = new JComboBox(connectionsModel);
        connectionsCombo.addItemListener(this);

        schemaModel = new DynamicComboBoxModel();
        schemaCombo = new JComboBox(schemaModel);
        schemaCombo.addItemListener(this);

        tablesModel = new DynamicComboBoxModel();
        tableCombo = new JComboBox(tablesModel);
        tableCombo.addItemListener(this);

        normalCheck = new JCheckBox("Normal", true);
        uniqueCheck = new JCheckBox("Unique");
        bitmapCheck = new JCheckBox("Bitmap");
        unsortedCheck = new JCheckBox("Unsorted");
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(normalCheck);
        bg.add(uniqueCheck);
        bg.add(bitmapCheck);
        bg.add(unsortedCheck);
        
        JPanel checkPanel = new JPanel();
        checkPanel.add(normalCheck);
        checkPanel.add(uniqueCheck);
        checkPanel.add(bitmapCheck);
        checkPanel.add(unsortedCheck);
        
        sqlText = new SimpleSqlTextPanel();
        
        // build the table panel
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setPreferredSize(new Dimension(480, 175));
        
        model = new CreateIndexModel();
        model.addTableModelListener(this);
        tableVector = new Vector();
        
        selectedTable = new DefaultTable(model);
        selectedTable.setRowHeight(20);
        selectedTable.setRowSelectionAllowed(false);
        selectedTable.setColumnSelectionAllowed(false);
        selectedTable.getTableHeader().setReorderingAllowed(false);
        TableColumnModel tcm = selectedTable.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(250);
        tcm.getColumn(1).setPreferredWidth(120);
        tcm.getColumn(2).setMaxWidth(70);
        JScrollPane tableScroller = new JScrollPane(selectedTable);
        
        // build the table's tools panel
        moveUpButton = ActionUtilities.createButton(
                this,
                "Up16.gif",
                "Move the selection up", 
                null);

        moveDownButton = ActionUtilities.createButton(
                this,
                "Down16.gif",
                "Move the selection down", 
                null);

        // add table panel components
        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(0,0,0,5);
        gbc.insets = ins;
        tablePanel.add(moveUpButton, gbc);
        gbc.gridy = 1;
        gbc.insets.top = 5;
        tablePanel.add(moveDownButton, gbc);
        gbc.insets.top = 0;
        gbc.insets.right = 0;
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 3;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        tablePanel.add(tableScroller, gbc);
        
        // add all components
        JPanel mainPanel = new JPanel(new GridBagLayout());
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1;
        gbc.insets.top = 5;
        gbc.insets.bottom = 5;
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        mainPanel.add(new JLabel("Connection:"), gbc);
        gbc.insets.top = 2;
        gbc.insets.bottom = 5;
        gbc.gridy++;
        mainPanel.add(new JLabel(useCatalogs ? "Catalog:" : "Schema:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Table:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Index Name:"), gbc);
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.top = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(connectionsCombo, gbc);
        gbc.insets.top = 0;
        gbc.gridy++;
        mainPanel.add(schemaCombo, gbc);
        gbc.gridy++;
        mainPanel.add(tableCombo, gbc);
        gbc.gridy++;
        mainPanel.add(nameField, gbc);
        gbc.insets.left = 0;
        gbc.insets.right = 0;
        gbc.insets.top = 0;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(checkPanel, gbc);
        gbc.gridy++;
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(tablePanel, gbc);
        gbc.gridy++;
        gbc.weighty = 0.6;
        gbc.insets.bottom = 5;
        mainPanel.add(sqlText, gbc);
        
        mainPanel.setBorder(BorderFactory.createEtchedBorder());
        
        normalCheck.addActionListener(this);
        uniqueCheck.addActionListener(this);
        bitmapCheck.addActionListener(this);
        unsortedCheck.addActionListener(this);
        
        JPanel base = new JPanel(new BorderLayout());
        base.add(mainPanel, BorderLayout.CENTER);
        base.add(new BottomButtonPanel(this, "Create", "create-index", true), 
                 BorderLayout.SOUTH);
        
        // add the base to the panel
        setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        add(base, BorderLayout.CENTER);

        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                tableChanged(null); }
        });
        
        setPreferredSize(new Dimension(520,480));
        
        //schemaCombo.setSelectedIndex(0);
        
        // check initial values for possible value inits
        if (connections == null || connections.isEmpty()) {
            schemaCombo.setEnabled(false);
            connectionsCombo.setEnabled(false);
            tableCombo.setEnabled(false);
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
            //schemaChanged();
        }

        // register as a keyword listener
        EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
    }
    
    /**
     * Releases database resources before closing.
     */
    public void cleanup() {
        EventMediator.deregisterListener(EventMediator.KEYWORD_EVENT, this);
        metaData.closeConnection();
    }

    /**
     * Notification of a new keyword added to the list.
     */
    public void keywordsAdded(KeywordEvent e) {
        sqlText.setSQLKeywords(true);
    }

    /**
     * Notification of a keyword removed from the list.
     */
    public void keywordsRemoved(KeywordEvent e) {
        sqlText.setSQLKeywords(true);
    }

    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     */    
    public void itemStateChanged(ItemEvent event) {
        // interested in selections only
        if (event.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        final Object source = event.getSource();
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {
                    parent.setInProcess(true);
                    if (source == connectionsCombo) {
                        connectionChanged();
                    }
                    else if (source == schemaCombo) {
                        schemaChanged();
                    }
                    else if (source == tableCombo) {
                        tableChanged();            
                    }
                }
                finally {
                    parent.setInProcess(false);
                }
            }
        });
    }

    private void connectionChanged() {
        // retrieve connection selection
        DatabaseConnection connection = 
                (DatabaseConnection)connectionsCombo.getSelectedItem();
        // reset meta data
        metaData.setDatabaseConnection(connection);

        try {
            Vector schemas = metaData.getHostedSchemasVector();
            if (schemas == null || schemas.isEmpty()) {
                useCatalogs = true;
                schemas = metaData.getHostedCatalogsVector();
            } else {
                useCatalogs = false;
            }
            populateSchemaValues(schemas);
        }
        catch (DataSourceException e) {
            GUIUtilities.displayExceptionErrorDialog(
                    "Error retrieving the catalog/schema names for the " +
                    "current connection.\n\nThe system returned:\n" + 
                    e.getExtendedMessage(), e);
            populateSchemaValues(new Vector(0));
        }
    }
    
    private void populateTableValues(final String[] tables) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                tablesModel.removeAllElements();
                
                if (tables == null || tables.length == 0) {
                    tableCombo.setEnabled(false);
                } 
                else {
                    tableCombo.setEnabled(true);
                    tablesModel.setElements(tables);
                }

            }
        });
    }
    
    private void populateSchemaValues(final Vector schemas) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                schemaModel.removeAllElements();
                tablesModel.removeAllElements();

                schemaModel.setElements(schemas);
                schemaCombo.setEnabled(true);

                if (schemaModel.getSize() > 0) {
                    schemaCombo.setSelectedIndex(0);
                }
                else {
                    tablesModel.removeAllElements();
                    tableCombo.setEnabled(false);
                }

            }
        });
    }
        
    private void schemaChanged() {
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
            String[] tables = metaData.getTables(catalogName, schemaName, "TABLE");
            populateTableValues(tables);
        }
        catch (DataSourceException e) {
            GUIUtilities.displayExceptionErrorDialog(
                    "Error retrieving the table list for the selected " +
                    "catalog/schema.\n\nThe system returned:\n" + 
                    e.getExtendedMessage(), e);
            populateTableValues(new String[0]);
        }        
        tableChanged();
    }
    
    /**
     * Returns the index name field.
     */
    public Component getDefaultFocusComponent() {
        return nameField;
    }

    private void tableChanged() {
        tableVector.clear();
        String name = (String)tableCombo.getSelectedItem();
        
        if (name == null || name.length() == 0) {
            GUIUtils.invokeAndWait(new Runnable() {
                public void run() {
                    tableChanged(null);
                    model.fireTableDataChanged();
                }
            });
            return;
        }
        
        ColumnData[] cd = null;        
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

        try {
            cd = metaData.getColumnMetaData(catalogName, schemaName, name);
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer();
            sb.append("An error occurred retrieving table and column data.").
               append("\n\nThe system returned:\n").
               append(e.getMessage());
            GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
        }
        
        if (cd != null) {
            
            for (int i = 0; i < cd.length; i++) {
                TableDefinition td = new TableDefinition();
                td.name = cd[i].getColumnName();
                td.type = cd[i].getColumnType();
                tableVector.add(td);
            }
            
        }

        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                model.fireTableDataChanged();
                tableChanged(null);
            }
        });
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JCheckBox) {
            tableChanged(null);
        }
        else if (source == moveUpButton || source == moveDownButton) {
            move_actionPerformed(source);
        }
        else {
            // check the command for a create
            if ("Create".equals(e.getActionCommand())) {
                GUIUtils.startWorker(new Runnable() {
                    public void run() {
                        try {
                            parent.setInProcess(true);
                            createIndex();
                        }
                        finally {
                            parent.setInProcess(false);
                        }
                    }
                });
            }
        }

    }

    private void createIndex() {
        DatabaseConnection dc = (DatabaseConnection)
                        connectionsCombo.getSelectedItem();
        if (dc == null) {
            GUIUtilities.displayErrorMessage(
                    "No database connection is available.");
            return;
        }

        try {
            QuerySender qs = new QuerySender(dc);
            SqlStatementResult result = qs.updateRecords(sqlText.getSQLText());

            if (result.getUpdateCount() >= 0) {
                GUIUtilities.displayInformationMessage(
                        "Index " + nameField.getText() + " created.");
                parent.finished();
            }
            else {
                SQLException e = result.getSqlException();
                if (e != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("An error occurred applying the specified changes.").
                       append("\n\nThe system returned:\n").
                       append(MiscUtils.formatSQLError(e));
                    GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
                } 
                else {
                    GUIUtilities.displayErrorMessage(result.getErrorMessage());
                }
            }
            
        }        
        catch (Exception e) {
            GUIUtilities.displayExceptionErrorDialog("Error:\n" + e.getMessage(), e);
        }
        
    }
    
    public void tableChanged(TableModelEvent e) {
        String name = (String)tableCombo.getSelectedItem();
        String schema = (String)schemaCombo.getSelectedItem();
        sqlBuffer = new StringBuffer(100);

        if (uniqueCheck.isSelected()) {
            sqlBuffer.append(CREATE_UNIQUE);
        }
        else if (bitmapCheck.isSelected()) {
            sqlBuffer.append(CREATE_BITMAP);
        }
        else {
            sqlBuffer.append(CREATE_INDEX);
        }

        sqlBuffer.append(nameField.getText() == null ? EMPTY : nameField.getText());
        sqlBuffer.append(ON);
        
        if (schema != null && schema.length() > 0) {
            sqlBuffer.append(schema).append(DOT);
        }

        sqlBuffer.append(name == null ? EMPTY : name).append(SPACE);
        sqlBuffer.append(B_OPEN);
        
        for (int i = 0, k = tableVector.size(); i < k; i++) {
            TableDefinition td = (TableDefinition)tableVector.elementAt(i);
            
            if (td.order != null && td.order.booleanValue()) {
                
                sqlBuffer.append(td.name);

                if (i != k - 1) {
                    sqlBuffer.append(COMMA).append(SPACE);
                }

            }
            
        }
        sqlBuffer.append(B_CLOSE);
        
        if (unsortedCheck.isSelected()) {
            sqlBuffer.append(NO_SORT);
        }
        
        String text = sqlBuffer.toString();
        int comma_index = text.lastIndexOf(COMMA);
        
        if (comma_index == text.length() - 3) {
            sqlBuffer.delete(comma_index, comma_index + 2);
        }

        //sqlText.setSQLText(sqlBuffer.toString());
        final String _sqlText = sqlBuffer.toString();
        GUIUtils.invokeLater(new Runnable() {
            public void run() {
                sqlText.setSQLText(_sqlText);
            }
        });
        
    }
    
    private void move_actionPerformed(Object source) {
        
        int selection = selectedTable.getSelectedRow();
        
        if (source == moveUpButton) {
            
            if (selection == -1 || selection == 0) {
                return;
            }
            
            int newPostn = selection - 1;
            TableDefinition move = (TableDefinition)tableVector.elementAt(selection);
            tableVector.removeElementAt(selection);
            tableVector.add(newPostn, move);
            selectedTable.setRowSelectionInterval(newPostn, newPostn);
            model.fireTableRowsUpdated(newPostn, selection);
        }
        
        else if (source == moveDownButton) {
            
            if (selection == -1 || selection == tableVector.size() - 1)
                return;
            
            int newPostn = selection + 1;
            TableDefinition move = (TableDefinition)tableVector.elementAt(selection);
            tableVector.removeElementAt(selection);
            tableVector.add(newPostn, move);
            selectedTable.setRowSelectionInterval(newPostn, newPostn);
            model.fireTableRowsUpdated(selection, newPostn);
        }
        
    }
    
    // ------------------------------------------------
    // ----- TextEditorContainer implementations ------
    // ------------------------------------------------
    
    /**
     * Returns the SQL text pane as the TextEditor component 
     * that this container holds.
     */
    public TextEditor getTextEditor() {
        return sqlText;
    }

    
    
    private class CreateIndexModel extends AbstractTableModel {
        
        protected String[] header = {"Column Name", "Datatype", "Select"};
        
        public CreateIndexModel() {}
        
        public int getColumnCount() {
            return 3;
        }
        
        public int getRowCount() {
            return tableVector.size();
        }
        
        public Object getValueAt(int row, int col) {
            TableDefinition tc = (TableDefinition)tableVector.elementAt(row);
            
            switch(col) {
                case 0:
                    return tc.name;
                case 1:
                    return tc.type;
                case 2:
                    return tc.order;
                default:
                    return null;
            }
        }
        
        public void setValueAt(Object value, int row, int col) {
            TableDefinition tc = (TableDefinition)tableVector.elementAt(row);
            
            switch (col) {
                case 0:
                    tc.name = (String)value;
                    break;
                case 1:
                    tc.type = (String)value;
                    break;
                case 2:
                    tc.order = value != null ? (Boolean)value : null;
                    break;
            }
            
            fireTableRowsUpdated(row, row);
        }
        
        public boolean isCellEditable(int row, int col) {
            if (col < 2) {
                return false;
            } else {
                return true;
            }
        }
        
        public String getColumnName(int col) {
            return header[col];
        }
        
        public Class getColumnClass(int col) {
            if (col == 2)
                return Boolean.class;
            else
                return String.class;
        }
        
    } // CreateIndexModel
    
    
    static class TableDefinition {
        String name;
        String type;
        Boolean order;
        
        TableDefinition() {}
        
        public String toString() {
            return name;
        }
        
    } // TableDefinition
    
    
    public String getDisplayName() {
        return "";
    }
    
    public String toString() {
        return TITLE;
    }
    
}







