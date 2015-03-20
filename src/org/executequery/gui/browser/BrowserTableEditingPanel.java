/*
 * BrowserTableEditingPanel.java
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.awt.print.Printable;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.executequery.Constants;
import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.databasemediators.DatabaseTable;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;
import org.executequery.gui.DefaultTable;
import org.executequery.gui.table.EditTableConstraintsPanel;
import org.executequery.gui.table.EditTablePanel;
import org.executequery.gui.table.TableModifier;
import org.executequery.gui.text.SimpleSqlTextPanel;

import org.executequery.print.TablePrinter;

import org.underworldlabs.swing.DisabledField;
import org.executequery.gui.forms.AbstractFormObjectViewPanel;
import org.executequery.gui.scriptgenerators.ScriptGenerationUtils;
import org.executequery.gui.table.TableConstraintFunction;
import org.underworldlabs.swing.FlatSplitPane;
import org.executequery.gui.editor.ResultSetTableModel;
import org.executequery.gui.text.TextEditor;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.9 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class BrowserTableEditingPanel extends AbstractFormObjectViewPanel
                                      implements ActionListener,
                                                 KeywordListener,
                                                 FocusListener,
                                                 TableConstraintFunction,
                                                 ChangeListener {
    
    // TEXT FUNCTION CONTAINER
    
    public static final String NAME = "BrowserTableEditingPanel";
    
    /** Contains the table name */
    private DisabledField tableNameField;
    
    /** Contains the schema name */
    private DisabledField schemaNameField;

    /** Contains the data row count */
    private DisabledField rowCountField;

    /** The table view tabbed pane */
    private JTabbedPane tabPane;
    
    /** The SQL text pane for alter text */
    private SimpleSqlTextPanel alterSqlText;
    
    /** The SQL text pane for create table text */
    private SimpleSqlTextPanel createSqlText;

    /** Contains the column descriptions for a selected table */
    private EditTablePanel columnDataTable;
    
    /** The panel displaying the table's constraints */
    private EditTableConstraintsPanel conPanel;
    
    /** Contains the column indexes for a selected table */
    private JTable columnIndexTable;
    
    /** A reference to the currently selected table.
     *  This is not a new <code>JTable</code> instance */
    private JTable focusTable;
    
    /** Whether the SQL pane is currently visible */
    private boolean hasSQL;
    
    /** Contains the constraints for a selected table */
    private Vector<ColumnConstraint> constraintsVector;
    
    /** The table column index table model */
    private ColumnIndexTableModel citm;
    
    /** Holds temporary SQL text during modifications */
    private StringBuffer sbTemp;

    /** table data tab panel */
    private TableDataTab tableDataPanel;
    
    /** current node selection object */
    private DatabaseObject metaObject;
    
    /** the erd panel */
    private ReferencesDiagramPanel referencesPanel;
    
    /** table privileges list */
    private TablePrivilegeTab tablePrivilegePanel;
    
    /** panel cache */
    private HashMap cache;
    
    /** the apply changes button */
    private JButton applyButton;
    
    /** the cancel changes button */
    private JButton cancelButton;
    
    /** the browser's control object */
    private BrowserController controller;

    /** the meta data table */
    private JTable metaDataTable;
    
    /** the meta data model */
    private ResultSetTableModel metaDataModel;
    
    /** the last focused editor */
    private TextEditor lastFocusEditor;
    
    public BrowserTableEditingPanel(BrowserController controller) {
        this.controller = controller;
        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() throws Exception {
        // the column index table
        citm = new ColumnIndexTableModel();
        columnIndexTable = new DefaultTable(citm);
        columnIndexTable.setRowHeight(20);
        columnIndexTable.setColumnSelectionAllowed(false);
        columnIndexTable.getTableHeader().setReorderingAllowed(false);

        // column indexes panel
        JPanel indexesPanel = new JPanel(new BorderLayout());
        indexesPanel.setBorder(BorderFactory.createTitledBorder("Table Indexes"));
        indexesPanel.add(new JScrollPane(columnIndexTable), BorderLayout.CENTER);

        // table meta data table
        metaDataModel = new ResultSetTableModel();
        metaDataTable = new DefaultTable(metaDataModel);
        metaDataTable.setRowHeight(20);
        metaDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // table meta data panel
        JPanel metaDataPanel = new JPanel(new BorderLayout());
        metaDataPanel.setBorder(BorderFactory.createTitledBorder("Table Column Meta Data"));
        metaDataPanel.add(new JScrollPane(metaDataTable), BorderLayout.CENTER);
        
        // table data panel
        tableDataPanel = new TableDataTab();
        
        // table privileges panel
        tablePrivilegePanel = new TablePrivilegeTab();
        
        // table references erd panel
        referencesPanel = new ReferencesDiagramPanel();
        
        // alter sql text panel
        alterSqlText = new SimpleSqlTextPanel();
        alterSqlText.setSQLTextBackground(Color.WHITE);
        alterSqlText.setBorder(BorderFactory.createTitledBorder("Alter Table"));
        alterSqlText.setPreferredSize(new Dimension(100, 100));
        alterSqlText.getEditorTextComponent().addFocusListener(this);

        // create sql text panel
        createSqlText = new SimpleSqlTextPanel();
        createSqlText.setSQLTextBackground(Color.WHITE);
        createSqlText.setBorder(BorderFactory.createTitledBorder("Create Table"));
        createSqlText.getEditorTextComponent().addFocusListener(this);
        
        // sql text split pane
        FlatSplitPane splitPane = new FlatSplitPane(FlatSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(alterSqlText);
        splitPane.setBottomComponent(createSqlText);
        splitPane.setDividerSize(7);
        splitPane.setResizeWeight(0.25);
        
        // the contraints panel
        conPanel = new EditTableConstraintsPanel(this);
        conPanel.setBorder(BorderFactory.createTitledBorder("Table Keys"));

        // the column decription table 
        columnDataTable = new EditTablePanel(this);
        columnDataTable.setBorder(BorderFactory.createTitledBorder("Table Columns"));
        
        tableNameField = new DisabledField();
        schemaNameField = new DisabledField();
        rowCountField = new DisabledField();
        
        // create the tabbed pane
        tabPane = new JTabbedPane();
        tabPane.add("Description", columnDataTable);
        tabPane.add("Constraints", conPanel);
        tabPane.add("Indexes", indexesPanel);
        tabPane.add("Privileges", tablePrivilegePanel);
        tabPane.add("References", referencesPanel);
        tabPane.add("Data", tableDataPanel);
        tabPane.add("SQL", splitPane);
        tabPane.add("Meta Data", metaDataPanel);
        tabPane.addChangeListener(this);

        // apply/cancel buttons
        applyButton = new JButton("Apply");
        cancelButton = new JButton("Cancel");

        applyButton.setPreferredSize(Constants.FORM_BUTTON_SIZE);
        applyButton.setMinimumSize(Constants.FORM_BUTTON_SIZE);
        cancelButton.setPreferredSize(Constants.FORM_BUTTON_SIZE);
        cancelButton.setMinimumSize(Constants.FORM_BUTTON_SIZE);

        applyButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // add to the base panel
        JPanel base = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(10,5,5,5);
        gbc.insets = ins;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        base.add(new JLabel("Table Name:"), gbc);
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        base.add(tableNameField, gbc);
        gbc.insets.top = 0;
        gbc.gridy++;
        base.add(schemaNameField, gbc);
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        base.add(new JLabel("Schema:"), gbc);
        // add the tab pane
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.bottom = 5;
        gbc.insets.top = 0;
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        base.add(tabPane, gbc);
        // add the bottom components
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.insets.top = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        base.add(new JLabel("Data Row Count:"), gbc);
        gbc.gridx = 2;
        gbc.insets.top = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.insets.right = 0;
        base.add(rowCountField, gbc);
        gbc.gridx = 3;
        gbc.weightx = 0;
        gbc.insets.top = 0;
        gbc.insets.right = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        base.add(applyButton, gbc);
        gbc.gridx = 4;
        gbc.weightx = 0;
        gbc.insets.left = 0;
        base.add(cancelButton, gbc);
        
        hasSQL = false;
        
        // set up and add the focus listener for the tables
        FocusListener tableFocusListener = new FocusListener() {
            public void focusGained(FocusEvent e) {
                focusTable = (JTable)e.getSource(); }
            public void focusLost(FocusEvent e) {}
        };
        columnDataTable.addTableFocusListener(tableFocusListener);
        conPanel.addTableFocusListener(tableFocusListener);

        sbTemp = new StringBuffer(100);        
        cache = new HashMap();

        setContentPanel(base);
        setHeaderIcon(GUIUtilities.loadIcon("DatabaseTable24.gif"));
        setHeaderText("Database Table");
        
        // register for keyword changes
        EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
    }
    
    /**
     * Invoked when a SQL text pane gains the keyboard focus.
     */
    public void focusGained(FocusEvent e) {
        Object source = e.getSource();
        // check which editor we are in
        if (createSqlText.getEditorTextComponent() == source) {
            lastFocusEditor = createSqlText;
        }
        else if (alterSqlText.getEditorTextComponent() == source) {
            lastFocusEditor = alterSqlText;
        }
    }

    /**
     * Invoked when a SQL text pane loses the keyboard focus.
     * Does nothing here.
     */
    public void focusLost(FocusEvent e) {}

    /**
     * Notification of a new keyword added to the list.
     */
    public void keywordsAdded(KeywordEvent e) {
        alterSqlText.setSQLKeywords(true);
        createSqlText.setSQLKeywords(true);
    }

    /**
     * Notification of a keyword removed from the list.
     */
    public void keywordsRemoved(KeywordEvent e) {
        alterSqlText.setSQLKeywords(true);
        createSqlText.setSQLKeywords(true);
    }

    /**
     * Performs the apply/cancel changes.
     *
     * @param e - the event
     */
    public void actionPerformed(ActionEvent e) {
        // if there is nothing to apply or cancel - bail
        if (!hasSQLText()) {
            return;
        }
        
        Object source = e.getSource();
        if (source == applyButton) {
            controller.applyTableChange(false);
        }
        else if (source == cancelButton) {
            selectionChanged(metaObject, true);
            resetSQLText();
        }
    }
    
    public String getLayoutName() {
        return NAME;
    }
    
    public void refresh() {
        cache.clear();
    }
    
    public Printable getPrintable() {
        
        int tabIndex = tabPane.getSelectedIndex();
        
        switch (tabIndex) {
            
            case 0:
                return new TablePrinter(columnDataTable.getTable(),
                                        "Table: " + metaObject.getName());
                
            case 1:
                return new TablePrinter(conPanel.getTable(),
                                        "Constraints for table: " + metaObject.getName(), false);
                
            case 2:
                return new TablePrinter(columnIndexTable,
                                        "Indexes for table: " + metaObject.getName());
                
            case 3:
                return new TablePrinter(tablePrivilegePanel.getTable(),
                                        "Access rights for table: " + metaObject.getName());
                
            case 4:
                return referencesPanel.getPrintable();
                
            case 5:
                return new TablePrinter(tableDataPanel.getTable(),
                                        "Table Data: " + metaObject.getName());

            case 7:
                return new TablePrinter(metaDataTable,
                                        "Table Meta Data: " + metaObject.getName());

            default:
                return null;
                
        }
        
    }
    
    public void cleanup() {
        referencesPanel.cleanup();
        EventMediator.deregisterListener(EventMediator.KEYWORD_EVENT, this);
    }
    
    /**
     * Handles a change tab selection.
     */
    public void stateChanged(ChangeEvent e) {
        int index = tabPane.getSelectedIndex();
        if (index != 5 && tableDataPanel.isExecuting()) {
            tableDataPanel.cancelStatement();
            return;
        }
        
        switch (index) {
            case 2:
                loadIndexes();
                break;
            case 3:
                loadPrivileges();
                break;
            case 4:
                loadReferences();
                break;
            case 5:
                tableDataPanel.getTableData(
                        controller.getDatabaseConnection(), metaObject);
                break;
            case 7:
                loadTableMetaData();
                break;
        }
        
    }
    
    /*
    private boolean executing;
    
    private void loadTableData() {
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    executing = true;
                    showWaitCursor();
                    return setTableResultsPanel(dc, metaObject);
                }
                catch (Exception e) {
                    GUIUtilities.displayExceptionErrorDialog(
                                        "An error occured retrieving the table data.\n" + 
                                        e.getMessage(), e);
                    return "done";
                }
            }
            public void finished() {
                executing = false;
                querySender.releaseResources();
                showNormalCursor();
            }
        };        
        worker.start();
    }
    */

    /**
     * Returns whether the specified object exists in this
     * object cache.
     *
     * @return true | false
     */
    public boolean containsObject(DatabaseObject metaObject) {
        return cache.containsKey(metaObject);
    }
    
    /** 
     * Indicates that the references tab has been previously displayed
     * for the current selection. Aims to keep any changes made to the 
     * references ERD (moving tables around) stays as was previosuly set.
     */
    private boolean referencesLoaded;
    
    /**
     * Loads database table references.
     */
    private void loadReferences() {
        
        if (referencesLoaded) {
            return;
        }

        Vector tableNames = null;
        Vector tableMeta = null;
        CacheObject cacheObject = (CacheObject)cache.get(metaObject);
        
        if (!cacheObject.isReferenceDataLoaded()) {
            
            String catalogName = metaObject.getCatalogName();
            String schemaName = metaObject.getSchemaName();
            String tableName = metaObject.getName();
            
            String[] importedTables = controller.getImportedKeyTables(catalogName,
                                                                      schemaName,
                                                                      tableName);
            
            String[] exportedTables = controller.getExportedKeyTables(catalogName,
                                                                      schemaName,
                                                                      tableName);
            
            int size = importedTables.length + exportedTables.length + 1;
            
            tableNames = new Vector(size);
            tableNames.add(tableName);
            
            DatabaseTable dt = cacheObject.getDatabaseTable();
            ColumnData[] columnData = dt.getColumns();
            tableMeta = new Vector(size);
            tableMeta.add(columnData);
            
            for (int i = 0; i < importedTables.length; i++) {
                tableNames.add(importedTables[i]);
                tableMeta.add(controller.getColumnData(catalogName,
                                                       schemaName,
                                                       importedTables[i]));
            }
            
            for (int i = 0; i < exportedTables.length; i++) {
                tableNames.add(exportedTables[i]);
                tableMeta.add(controller.getColumnData(catalogName,
                                                       schemaName,
                                                       exportedTables[i]));
            }
            
            ReferencesCacheObject reference = new ReferencesCacheObject(tableNames, 
                                                                        tableMeta);
            cacheObject.setReferencesObject(reference);
            
        }        
        else {
            ReferencesCacheObject reference = cacheObject.getReferencesObject();
            tableNames = reference.getTableNames();
            tableMeta = reference.getColumnData();
        }

        referencesPanel.setTables(tableNames, tableMeta);
        referencesLoaded = true;
    }
    
    /**
     * Loads database table indexes.
     */
    private void loadTableMetaData() {
        CacheObject cacheObject = (CacheObject)cache.get(metaObject);
        if (cacheObject == null) {
            return;
        }

        try {
            metaDataModel.createTable(
                    controller.getTableMetaData(metaObject.getCatalogName(),
                                                metaObject.getSchemaName(),
                                                metaObject.getName()));
        }
        finally {
            controller.closeConnection();
        }
    }

    
    /**
     * Loads database table indexes.
     */
    private void loadIndexes() {
        CacheObject cacheObject = (CacheObject)cache.get(metaObject);
        if (cacheObject == null) {
            return;
        }
        
        ColumnIndex[] indexes = null;
        DatabaseTable dt = cacheObject.getDatabaseTable();
        if (!dt.hasIndexes()) {
            Vector _indexes = controller.getTableIndexes(metaObject.getCatalogName(),
                                                         metaObject.getSchemaName(),
                                                         metaObject.getName());
            
            indexes = (ColumnIndex[])_indexes.toArray(new ColumnIndex[_indexes.size()]);
            dt.setIndexes(indexes);
        }
        else {
            indexes = dt.getIndexes();
        }
        
        citm.setIndexData(indexes);
        setIndexTableColumnProperties(columnIndexTable);
    }
    
    /**
     * Loads database table privileges.
     */
    private void loadPrivileges() {
        CacheObject cacheObject = (CacheObject)cache.get(metaObject);
        if (cacheObject == null) {
            return;
        }

        TablePrivilege[] privileges = null;
        DatabaseTable dt = cacheObject.getDatabaseTable();
        if (!dt.hasPrivileges()) {
            privileges = controller.getPrivileges(
                                            metaObject.getCatalogName(),
                                            metaObject.getSchemaName(),
                                            metaObject.getName());
            dt.setPrivileges(privileges);
        }
        else {
            privileges = dt.getPrivileges();
        }
        tablePrivilegePanel.setValues(privileges);
    }
    
    /**
     * Fires a change in the selected table node and reloads
     * the current table display view.
     *
     * @param metaObject - the selected meta object
     * @param reset - whether to reset the view, regardless of the current 
     *                view which may be the same metaObject
     */
    public void selectionChanged(DatabaseObject metaObject, boolean reset) {
        if (this.metaObject == metaObject && !reset) {
            return;
        }
        this.metaObject = metaObject;
        
        if (columnDataTable.isEditing()) {
            columnDataTable.removeEditor();
        }

        columnDataTable.reset();
        ColumnData[] columnData = null;
        tabPane.setSelectedIndex(0);
        
        DatabaseTable dt = null;
        if (reset || !cache.containsKey(metaObject)) {
            columnData = controller.getColumnData(
                                            metaObject.getCatalogName(),
                                            metaObject.getSchemaName(),
                                            metaObject.getName());

            // populate the table object
            dt = new DatabaseTable();
            dt.setValues(metaObject);
            dt.setColumns(columnData);
            
            CacheObject cacheObject = new CacheObject();
            cacheObject.setDatabaseTable(dt);
            cache.put(metaObject, cacheObject);
        }        
        else {
            CacheObject cacheObject = (CacheObject)cache.get(metaObject);
            dt = cacheObject.getDatabaseTable();
            columnData = dt.getColumns();
        }

        tableNameField.setText(dt.getName());
        schemaNameField.setText(dt.getSchema());
        
        String rowCount = controller.getTableDataRowCount(
                                        metaObject.getSchemaName(),
                                        metaObject.getName());
        rowCountField.setText(rowCount);

        loadColumnData(columnData);
        referencesLoaded = false;
    }
    
    /**
     * Fires a change in the selected table node and reloads
     * the current table display view.
     *
     * @param metaObject - the selected meta object
     */
    public void selectionChanged(DatabaseObject metaObject) {
        selectionChanged(metaObject, false);        
    }
    
    /**
     * Loads the table column data.
     */
    private void loadColumnData(ColumnData[] cda) {

        // let the table handle this first to clear if empty
        columnDataTable.setColumnDataArray(cda, controller.getDataTypesArray());

        // ... then check here
        if (cda == null) {
            return;
        }
        
        // configure the constraints panel
        if (constraintsVector == null) {
            constraintsVector = new Vector<ColumnConstraint>();
        } else {
            constraintsVector.clear();
        }
        
        Vector<ColumnConstraint> ccv = null;        
        for (int i = 0; i < cda.length; i++) {
            
            if (cda[i].isKey()) {
                ccv = cda[i].getColumnConstraintsVector();
                for (int j = 0, k = ccv.size(); j < k; j++) {
                    constraintsVector.add(ccv.get(j));
                }
            }

        }
        
        conPanel.reset();
        conPanel.setData(constraintsVector, false);

        try {
            String createTable = ScriptGenerationUtils.
                                      createTableScript(metaObject.getName(), cda);
            String alterTable = ScriptGenerationUtils.
                                      alterTableConstraintsScript(constraintsVector);
            createSqlText.setSQLText(createTable + alterTable);
        } catch (InterruptedException e) {}
        
        columnDataTable.setOriginalData();
        conPanel.setOriginalData();
        
        //databaseTable.setConstraints(ccv);
    }
    
    public void setBrowserPreferences() {
        tableDataPanel.setTableProperties();
    }
    
    public JTable getTableInFocus() {
        return focusTable;
    }
    
    /** <p>Sets table properties for the index table.<br>
     *  This will set cell selection policies and column
     *  widths for each column in the table.
     *
     *  @param the index table to be configured.
     */
    private void setIndexTableColumnProperties(JTable table) {
        table.setCellSelectionEnabled(true);
        
        TableColumnModel tcm = table.getColumnModel();
        TableColumn col = tcm.getColumn(0);
        col.setPreferredWidth(210);
        
        col = tcm.getColumn(1);
        col.setPreferredWidth(150);
        
        col = tcm.getColumn(2);
        col.setPreferredWidth(90);
    }
    
    /**
     * Returns whether the SQL panel has any text in it.
     */
    public boolean hasSQLText() {
        return !(alterSqlText.isEmpty());
    }

    /**
     * Resets the contents of the SQL panel to nothing.
     */
    public void resetSQLText() {
        alterSqlText.setSQLText(EMPTY);
    }

    /**
     * Returns the contents of the SQL text pane.
     */
    public String getSQLText() {
        return alterSqlText.getSQLText();
    }

    // -----------------------------------------------
    // --- TableConstraintFunction implementations ---
    // -----------------------------------------------

    /** 
     * Deletes the selected row on the currently selected table. 
     */
    public void deleteRow() {
        if (focusTable == columnDataTable.getTable()) {
            columnDataTable.markDeleteRow();
        }        
        else if (focusTable == conPanel.getTable()) {
            conPanel.markDeleteRow();
        }
        setSQLText();
    }
    
    /** 
     * Inserts a row after the selected row on the currently selected table. 
     */
    public void insertAfter() {
        
        if (focusTable == columnDataTable.getTable()) {
            columnDataTable.insertAfter();
            columnDataTable.setOriginalData();
        }
        else if (focusTable == conPanel.getTable()) {
            conPanel.insertRowAfter();
//            conPanel.setCellEditor(2, new ComboBoxCellEditor(
//            columnDataTable.getTableColumnData()));
        }
        
    }
    
    public void setSQLText() {
        sbTemp.setLength(0);
        sbTemp.append(columnDataTable.getSQLText()).
               append(conPanel.getSQLText());        
        alterSqlText.setSQLText(sbTemp.toString());
    }
    
    public void setSQLText(String values, int type) {
        sbTemp.setLength(0);
        
        if (type == TableModifier.COLUMN_VALUES) {
            sbTemp.append(values).
                   append(conPanel.getSQLText());
        }        
        else if (type == TableModifier.CONSTRAINT_VALUES) {
            sbTemp.append(columnDataTable.getSQLText()).
                   append(values);
        }
        
        alterSqlText.setSQLText(sbTemp.toString());
    }
    
    public String getTableName() {
        return tableNameField.getText();
    }
    
    public Vector getHostedSchemasVector() {
        return controller.getHostedSchemas();
    }
    
    public Vector getSchemaTables(String schemaName) {
        return controller.getTables(schemaName);
    }
    
    public Vector getColumnNamesVector(String tableName, String schemaName) {
        return controller.getColumnNamesVector(tableName, schemaName);
    }
    
    public void insertBefore() {}
    public void moveColumnUp() {}
    public void moveColumnDown() {}

    public ColumnData[] getTableColumnData() {
        return columnDataTable.getTableColumnData();
    }
    
    public Vector getTableColumnDataVector() {
        return columnDataTable.getTableColumnDataVector();
    }
    
    /** Tab index of the SQL text panes */
    private static final int SQL_PANE_INDEX = 6;
    
    /**
     * Returns the focused TextEditor panel where the selected
     * tab is the SQL text pane.
     */
    protected TextEditor getFocusedTextEditor() {
        if (tabPane.getSelectedIndex() == SQL_PANE_INDEX) {
            if (lastFocusEditor != null) {
                return lastFocusEditor;
            }
        }
        return null;
    }
    

    /** Stored cache object */
    private class CacheObject {
        
        private DatabaseTable databaseTable;
        private DatabaseObject metaObject;
        private ReferencesCacheObject cacheObject;
        
        private boolean referenceDataLoaded;
        
        public CacheObject() {}
        
        public boolean isReferenceDataLoaded() {
            return referenceDataLoaded;
        }
        
        public void setReferencesObject(ReferencesCacheObject cacheObject) {
            if (cacheObject != null) {
                referenceDataLoaded = true;
            }
            this.cacheObject = cacheObject;
        }

        public ReferencesCacheObject getReferencesObject() {
            return cacheObject;
        }

        public DatabaseTable getDatabaseTable() {
            return databaseTable;
        }

        public void setDatabaseTable(DatabaseTable databaseTable) {
            this.databaseTable = databaseTable;
        }
        
    }
    
}



