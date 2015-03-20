/*
 * BrowserController.java
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.DatabaseProcedure;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.databasemediators.QuerySender;
import org.executequery.databasemediators.SqlStatementResult;
import org.underworldlabs.jdbc.DataSourceException;
import org.executequery.gui.forms.FormObjectView;
import org.executequery.util.Log;
import org.underworldlabs.util.MiscUtils;
import org.underworldlabs.swing.util.SwingWorker;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Performs SQL execution tasks from browser components.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.13 $
 * @date     $Date: 2006/09/05 12:01:24 $
 */
public class BrowserController {
    
    public static final int UPDATE_CANCELLED = 99;
    
    /** the meta data retrieval object */
    private MetaDataValues metaData;

    /** query sender object */
    private QuerySender querySender;
    
    /** the connections tree panel */
    private ConnectionsTreePanel treePanel;
    
    /** the databse viewer panel */
    private BrowserViewPanel viewPanel;

    /** the swing worker thread */
    private SwingWorker worker;
    
    /** Creates a new instance of BorwserQueryExecuter */
    public BrowserController(ConnectionsTreePanel treePanel) {
        this.treePanel = treePanel;
    }

    /**
     * Connects/disconnects the specified connection.
     *
     * @param dc - the conn to connect/disconnect
     */
    protected void connect(DatabaseConnection dc, boolean reselectRoot) {
        try {
            if (dc.isConnected()) {
                treePanel.setRootSelectOnDisconnect(reselectRoot);
                SystemUtilities.disconnect(dc);
            } else {
                SystemUtilities.connect(dc);
            }
        } catch (DataSourceException e) {
            StringBuffer sb = new StringBuffer();
            sb.append("The connection to the database could not be established.");
            sb.append("\nPlease ensure all required fields have been entered ");
            sb.append("correctly and try again.\n\nThe system returned:\n");
            sb.append(e.getExtendedMessage());
            GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
        }
        finally {
            // reset the disconnect selection
            treePanel.setRootSelectOnDisconnect(false);
        }
    }

    /**
     * Performs the drop database object action.
     */
    protected void dropSelectedObject() {
        try {
            // make sure we are not on a type parent object
            if (treePanel.isTypeParentSelected()) {
                return;
            }

            DatabaseObject object = treePanel.getSelectedDatabaseObject();
            if (object == null) {
                return;
            }

            // display confirmation dialog
            int yesNo = GUIUtilities.displayConfirmDialog(
                               "Are you sure you want to drop " + object + "?");
            if (yesNo == JOptionPane.NO_OPTION) {
                return;
            }

            int result = dropObject(getDatabaseConnection(), 
                                    object);
            if (result >= 0) {
                treePanel.removeSelectedNode();
            }
        }
        catch (SQLException e) {
            StringBuffer sb = new StringBuffer();
            sb.append("An error occurred removing the selected object.").
               append("\n\nThe system returned:\n").
               append(MiscUtils.formatSQLError(e));
            GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
        }        
    }

    /**
     * Ensures we have a browser panel and that it is visible.
     */
    protected void checkBrowserPanel() {
        // check we have the browser view panel
        if (viewPanel == null) {
            viewPanel = new BrowserViewPanel(this);
        }

        // check the panel is in the pane
        JPanel _viewPanel = GUIUtilities.getCentralPane(BrowserViewPanel.TITLE);
        if (_viewPanel == null) {
            GUIUtilities.addCentralPane(BrowserViewPanel.TITLE,
                                        BrowserViewPanel.FRAME_ICON, 
                                        viewPanel,
                                        BrowserViewPanel.TITLE,
                                        true);
        } 
        else {
            GUIUtilities.setSelectedCentralPane(BrowserViewPanel.TITLE);
        }
    }

    /**
     * Saves the connection data to file.
     */
    public boolean saveConnections() {
        return viewPanel.saveConnections();
    }

    /**
     * Informs the view panel of a pending change.
     */
    protected void selectionChanging() {
        if (viewPanel != null) {
            viewPanel.selectionChanging();
        }
    }

    /** 
     * Sets the selected connection tree node to the 
     * specified database connection.
     *
     * @param dc - the database connection to select
     */
    protected void setSelectedConnection(DatabaseConnection dc) {
        treePanel.setSelectedConnection(dc, true);
    }

    /**
     * Reloads the database properties meta data table panel.
     */
    protected void updateDatabaseProperties() {
        FormObjectView view = viewPanel.getFormObjectView(HostPanel.NAME);
        if (view != null) {
            HostPanel panel = (HostPanel)view;
            panel.updateDatabaseProperties();
        }
    }

    /** 
     * Adds a new connection.
     */
    protected void addNewConnection() {
        treePanel.newConnection();
    }

    /**
     * Indicates that a node name has changed and fires a call
     * to repaint the tree display.
     */
    protected void nodeNameValueChanged(ConnectionObject metaObject) {
        treePanel.nodeNameValueChanged(metaObject);
    }

    /**
     * Indicates a change in the tree selection value.<br>
     * This will determine and builds the object view panel to be
     * displayed based on the specified host node connection object
     * and the selected node as specified.
     *
     * @param the connection host parent object
     * @param the selected node
     */
    public synchronized void valueChanged(final ConnectionObject parent, 
                                          final BrowserTreeNode node,
                                          final boolean reload) {
        worker = new SwingWorker() {
            public Object construct() {
                try {
                    treePanel.setInProcess(true);
                    return buildPanelView(parent, node, reload);
                }
                finally {
                    treePanel.setInProcess(false);
                }
            }
            public void finished() {
                try {
                    GUIUtilities.showWaitCursor();
                    FormObjectView panel = (FormObjectView)get();
                    if (panel != null) {
                        viewPanel.setView(panel);
                        checkBrowserPanel();
                    }
                } finally {
                    GUIUtilities.showNormalCursor();
                }
            }
        };
        worker.start();
    }

    /**
     * Determines and builds the object view panel to be
     * displayed based on the specified host node connection object
     * and the selected node as specified.
     *
     * @param the connection host parent object
     * @param the selected node
     */
    private FormObjectView buildPanelView(ConnectionObject parent, 
                                          BrowserTreeNode node, 
                                          boolean reload) {

        // if the parent is null - bail
        if (parent == null) {
            return null;
        }
        
        try {
            DatabaseConnection dc = parent.getDatabaseConnection();
            DatabaseObject databaseObject = node.getDatabaseUserObject();
            
            String catalog = parent.isCatalogsInUse() ? 
                                databaseObject.getCatalogName() : null;
            String schema = databaseObject.getSchemaName();
            String name = databaseObject.getName();

            // determine the type of selection
            int type = databaseObject.getType();
            
            //Log.debug("Node type selected: " + type);
            
            switch (type) {

                case BrowserConstants.HOST_NODE:

                    HostPanel hostPanel = null;
                    if (!viewPanel.containsPanel(HostPanel.NAME)) {
                        hostPanel = new HostPanel(this);
                        viewPanel.addToLayout(hostPanel);
                    } 
                    else {
                        hostPanel = (HostPanel)viewPanel.getFormObjectView(HostPanel.NAME);
                    }

                    hostPanel.setValues(parent);
                    return hostPanel;

                // catalog node:
                // this will display the schema table list
                case BrowserConstants.CATALOG_NODE:

                    CatalogPanel catalogPanel = null;
                    if (!viewPanel.containsPanel(CatalogPanel.NAME)) {
                        catalogPanel = new CatalogPanel(this);
                        viewPanel.addToLayout(catalogPanel);
                    } 
                    else {
                        catalogPanel = (CatalogPanel)viewPanel.getFormObjectView(CatalogPanel.NAME);
                    }

                    catalogPanel.setValues(name, getCatalogSchemas(dc));
                    return catalogPanel;

                case BrowserConstants.SCHEMA_NODE:

                    SchemaPanel schemaPanel = null;
                    if (!viewPanel.containsPanel(SchemaPanel.NAME)) {
                        schemaPanel = new SchemaPanel(this);
                        viewPanel.addToLayout(schemaPanel);
                    } 
                    else {
                        schemaPanel = (SchemaPanel)viewPanel.getFormObjectView(SchemaPanel.NAME);
                    }

                    schemaPanel.selected(parent, databaseObject);
                    return schemaPanel;
                    
            }
            
            // if not a leaf node
            if (!node.isLeaf()) {
            
                // if its a system object - need a meta table display
                if (databaseObject.isSystemObject()) {
                    
                    // use the same meta panel
                    
                    MetaKeyPanel metaKeyPanel = null;
                    if (!viewPanel.containsPanel(MetaKeyPanel.NAME)) {
                        metaKeyPanel = new MetaKeyPanel(this);
                        viewPanel.addToLayout(metaKeyPanel);
                    } 
                    else {
                        metaKeyPanel = (MetaKeyPanel)viewPanel.getFormObjectView(MetaKeyPanel.NAME);
                    }

                    String[] values = null;
                    switch (type) {
                        case BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE:
                            values = getSystemFunctions(dc,MetaDataValues.STRING_FUNCTIONS);
                            break;

                        case BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE:
                            values = getSystemFunctions(dc,MetaDataValues.NUMERIC_FUNCTIONS);
                            break;

                        case BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE:
                            values = getSystemFunctions(dc,MetaDataValues.TIME_DATE_FUNCTIONS);
                            break;
                    }
                    metaKeyPanel.setValues(name, values);
                    return metaKeyPanel;
                }

                // if its a meta parent node
                else if (node.isTypeParent()) {
                    MetaKeyPanel metaKeyPanel = null;
                    if (!viewPanel.containsPanel(MetaKeyPanel.NAME)) {
                        metaKeyPanel = new MetaKeyPanel(this);
                        viewPanel.addToLayout(metaKeyPanel);
                    } 
                    else {
                        metaKeyPanel = (MetaKeyPanel)viewPanel.getFormObjectView(MetaKeyPanel.NAME);
                    }

                    // TODO: don't think this is using a cache!!!

                    // if its the system function node - just add the functions types
                    if (type == BrowserConstants.SYSTEM_FUNCTION_NODE) {
                        String[] values = {"String Functions", 
                                           "Numeric Functions", 
                                           "Date/Time Functions"};
                        metaKeyPanel.setValues(name, values);
                        return metaKeyPanel;
                    }

                    if (!metaKeyPanel.hasObject(databaseObject)) {
                        String[] values = getTables(dc, catalog, schema, name);
                        // if we have no values here - see if proc or function
                        if (values == null || values.length == 0) {
                            values = checkProcedureTerm(dc, databaseObject);
                        }
                        metaKeyPanel.setValues(name, values);
                    }
                    else {                
                        metaKeyPanel.setValues(name);
                    }

                    return metaKeyPanel;

                }
            }

            // if we have nothing here - must be a specific type

            switch (type) {
                case BrowserConstants.FUNCTIONS_NODE:
                case BrowserConstants.PROCEDURE_NODE:
                case BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE:
                case BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE:
                case BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE:

                    BrowserProcedurePanel procsPanel = null;
                    if (!viewPanel.containsPanel(BrowserProcedurePanel.NAME)) {
                        procsPanel = new BrowserProcedurePanel(this);
                        viewPanel.addToLayout(procsPanel);
                    } 
                    else {
                        procsPanel = (BrowserProcedurePanel)
                                        viewPanel.getFormObjectView(BrowserProcedurePanel.NAME);
                    }

                    // set the catalog and schema values to null if 
                    // its a system function type
                    if (type == BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE ||
                            type == BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE ||
                            type == BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE) {
                        catalog = null;
                        schema = null;
                    }

                    // maybe force a reload here
                    if (reload) {
                        procsPanel.removeObject(databaseObject);
                    }
                    
                    if (!procsPanel.hasObject(databaseObject)) {
                        procsPanel.setValues(
                                databaseObject, getProcedureColumns(dc, catalog, schema, name));
                    }
                    else {
                        procsPanel.setValues(databaseObject);
                    }
                    return procsPanel;

                case BrowserConstants.TABLE_NODE:

                    if (databaseObject.isDefaultCatalog()) {
                        BrowserTableEditingPanel editingPanel = viewPanel.getEditingPanel();
                        editingPanel.selectionChanged(databaseObject, reload);
                        return editingPanel;
                    }
                    break;

                case BrowserConstants.COLUMN_NODE:
                    TableColumnPanel columnPanel = null;
                    if (!viewPanel.containsPanel(TableColumnPanel.NAME)) {
                        columnPanel = new TableColumnPanel(this);
                        viewPanel.addToLayout(columnPanel);
                    } 
                    else {
                        columnPanel =
                            (TableColumnPanel)viewPanel.getFormObjectView(TableColumnPanel.NAME);
                    }

                    Map columnMap = null;
                    if (reload || !columnPanel.hasObject(databaseObject)) {
                        String parentName = databaseObject.getParentName();
                        columnMap = getColumnProperties(dc, schema, parentName, name);
                    }
                    columnPanel.setValues(databaseObject, columnMap, reload);
                    return columnPanel;

            }

            ObjectDefinitionPanel objectDefnPanel = null;
            if (reload || !viewPanel.containsPanel(ObjectDefinitionPanel.NAME)) {
                objectDefnPanel = new ObjectDefinitionPanel(this);
                viewPanel.addToLayout(objectDefnPanel);
            } 
            else {
                objectDefnPanel = (ObjectDefinitionPanel)
                                viewPanel.getFormObjectView(ObjectDefinitionPanel.NAME);
            }
            objectDefnPanel.changeTable(databaseObject, reload);
            return objectDefnPanel;
                
        }
        catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * Selects the node that matches the specified prefix forward 
     * from the currently selected node.
     *
     * @param prefix - the prefix of the node to select
     */
    protected void selectBrowserNode(String prefix) {
        treePanel.selectBrowserNode(prefix);
    }
    
    /**
     * Displays the root main view panel.
     */
    protected void displayRootPanel() {
        checkBrowserPanel();
        viewPanel.displayRootPanel();
    }
    
    /**
     * Applies the table alteration changes.
     */
    protected void applyTableChange(boolean valueChange) {
        
        BrowserTableEditingPanel editingPanel = viewPanel.getEditingPanel();
        
        // check we actually have something to apply
        if (!editingPanel.hasSQLText()) {
            return;
        }

        // retrieve the browser node
        BrowserTreeNode node = null;
        if (valueChange) {
            // if we are selecting a new node, get the previous selection
            node = treePanel.getOldBrowserNodeSelection();
        } else {
            // otherwise get the current selection
            node = treePanel.getSelectedBrowserNode();
        }
        
        try {
            treePanel.removeTreeListener();
            
            // if specified, ask the user again
            if (valueChange) {
                int yesNo = GUIUtilities.displayConfirmCancelDialog(
                                            "Do you wish to apply your changes?");
                if (yesNo == JOptionPane.NO_OPTION) {
                    node = treePanel.getSelectedBrowserNode();
                    editingPanel.selectionChanged(node.getDatabaseUserObject(), true);
                    editingPanel.resetSQLText();
                    return;
                }
                else if (yesNo == JOptionPane.CANCEL_OPTION) {
                    treePanel.setNodeSelected(node);
                    return;
                }
            }

            // apply the changes to the database
            if (querySender == null) {
                querySender = new QuerySender();
            }
            querySender.setDatabaseConnection(getDatabaseConnection());

            SqlStatementResult result = null;
            StringTokenizer st = new StringTokenizer(
                                        editingPanel.getSQLText().trim(), ";\n");

            try {
                while (st.hasMoreTokens()) {
                    result = querySender.updateRecords(st.nextToken());
                    if (result.getUpdateCount() < 0) {
                        editingPanel.setSQLText();
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
                        treePanel.setNodeSelected(node);
                        return;
                    }
                }
            } 
            catch (SQLException e) {
                StringBuffer sb = new StringBuffer();
                sb.append("An error occurred applying the specified changes.").
                   append("\n\nThe system returned:\n").
                   append(MiscUtils.formatSQLError(e));
                GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
                treePanel.setNodeSelected(node);
                return;
            }

            // reset the current panel
            editingPanel.selectionChanged(node.getDatabaseUserObject(), true);
            editingPanel.resetSQLText();
            treePanel.setNodeSelected(node);
        }
        finally {
            treePanel.addTreeListener();
        }
    }
    
    /**
     * Returns whether a table alteration has occurred and 
     * is actionable.
     *
     * @return true | false
     */
    protected boolean hasAlterTable() {
        if (viewPanel == null) {
            return false;
        }
        return viewPanel.getEditingPanel().hasSQLText();
    }
    
    /**
     * Checks the procedure term against a function or procedure node
     * when the returned results from getTables(...) is null or empty.
     *
     * @param dc - the database connection objeect
     * @param object - the meta object
     */
    protected String[] checkProcedureTerm(DatabaseConnection dc, 
                                          DatabaseObject object) {
        int type = object.getType();
        if (type == BrowserConstants.FUNCTIONS_NODE || 
                type == BrowserConstants.PROCEDURE_NODE) {
            // check the procedure term
            String procedureTerm = getProcedureTerm(dc);
            if (procedureTerm != null) {
                String catalog = object.getCatalogName();
                String schema = object.getSchemaName();
                String metaKey = object.getMetaDataKey();
                if (procedureTerm.toUpperCase().equals(metaKey)) {
                    return getProcedureNames(dc, catalog, schema, null);
                }
            }
        }
        return new String[0];
    }
    
    
    // --------------------------------------------
    // Meta data propagation methods
    // --------------------------------------------
    
    /**
     * Generic exception handler.
     */
    private void handleException(Throwable e) {        
        if (Log.isDebugEnabled()) {
            Log.debug("Error retrieving data.", e);
        }
        
        boolean isDataSourceException = (e instanceof DataSourceException);
        GUIUtilities.displayExceptionErrorDialog(
                "Error retrieving the selected database " +
                "object.\n\nThe system returned:\n" + 
                (isDataSourceException ? 
                    ((DataSourceException)e).getExtendedMessage() : e.getMessage()), e);
        
        if (isDataSourceException) {
            if (((DataSourceException)e).wasConnectionClosed()) {
                connect(treePanel.getSelectedDatabaseConnection(), false);
            }
        }
        
    }
    
    /** 
     * Propagates the call to the meta data object.
     */    
    protected String[] getSystemFunctions(DatabaseConnection dc, int type) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getSystemFunctions(type);
        } 
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected String[] getProcedureNames(DatabaseConnection dc,
                                         String catalog, 
                                         String schema, 
                                         String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getProcedureNames(catalog, schema, name);
        } 
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected String getProcedureTerm(DatabaseConnection dc) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getProcedureTerm();
        } 
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected String[] getTableTypes(DatabaseConnection dc) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getTableTypes();
        }
        catch (DataSourceException e) {
            handleException(e);
            return new String[0];
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected String[] getTables(DatabaseConnection dc, 
                              String catalog, String schema, String metaName) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getTables(catalog, schema, metaName);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new String[0];
        }
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected DatabaseObject[] getTables(DatabaseConnection dc,
                                         String catalog, 
                                         String schema, 
                                         String[] types) {
        checkMetaDataObject();
        metaData.setDatabaseConnection(dc);
        try {
            return metaData.getTables(catalog, schema, types);
        } 
        catch (DataSourceException e) {
            handleException(e);
            return new DatabaseObject[0];
        }
    }

    protected ResultSet getDataTypesResultSet() {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getDataTypesResultSet();
        }
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }
    }
    
    protected String[] getDatabaseKeywords() {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getDatabaseKeywords();
        }
        catch (DataSourceException e) {
            handleException(e);
            return new String[0];
        }
    }
    
    protected Hashtable getDatabaseProperties() {
        try {
            checkMetaDataObject();
            DatabaseConnection dc = getDatabaseConnection();
            if (dc != null) {
                metaData.setDatabaseConnection(getDatabaseConnection());
                return metaData.getDatabaseProperties();
            }
        }
        catch (DataSourceException e) {
            handleException(e);
        }
        return new Hashtable(0);
    }
            
    /** 
     * Propagates the call to the meta data object.
     */
    protected String[] getColumnNames(DatabaseConnection dc, 
                                   String table, String schema) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getColumnNames(table, schema);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new String[0];
        }
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected String getSchemaName(DatabaseConnection dc) {
        checkMetaDataObject();
        metaData.setDatabaseConnection(dc);
        return metaData.getSchemaName();
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected List getCatalogSchemas(DatabaseConnection dc) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getHostedCatalogSchemas();
        }
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected String getDataSourceName(DatabaseConnection dc) {
        checkMetaDataObject();
        metaData.setDatabaseConnection(dc);
        return metaData.getDataSourceName();
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector<String> getHostedCatalogs(DatabaseConnection dc) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getHostedCatalogsVector();
        }
        catch (DataSourceException e) {
            handleException(e);
            return new Vector(0);
        }
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector<String> getHostedSchemas(DatabaseConnection dc) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getHostedSchemasVector();
        }
        catch (DataSourceException e) {
            handleException(e);
            return new Vector(0);
        }
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector<String> getColumnNamesVector(DatabaseConnection dc, 
                                                  String table, String schema) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getColumnNamesVector(table, schema);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new Vector(0);
        }
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector<String> getColumnNamesVector(String table, String schema) {
        return getColumnNamesVector(getDatabaseConnection(), table, schema);
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector<String> getHostedSchemas() {
        return getHostedSchemas(getDatabaseConnection());
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector<String> getTables(String schema) {
        return getTables(getDatabaseConnection(), schema);
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector<String> getTables(DatabaseConnection dc, String schema) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getSchemaTables(schema);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new Vector(0);
        }
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected String getCatalogName(DatabaseConnection dc) {
        checkMetaDataObject();
        metaData.setDatabaseConnection(dc);
        return metaData.getCatalogName();
    }

    /** 
     * Propagates the call to the meta data object.
     */
    protected String[] getDataTypesArray() {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getDataTypesArray();
        }
        catch (DataSourceException e) {
            handleException(e);
            return new String[0];
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected Map getColumnProperties(DatabaseConnection dc,
                                      String schema, 
                                      String table, 
                                      String column) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getColumnProperties(schema, table, column);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new Hashtable(0);
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected DatabaseProcedure getProcedureColumns(DatabaseConnection dc,
                                                 String catalog, 
                                                 String schema, 
                                                 String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(dc);
            return metaData.getProcedureColumns(catalog, schema, name);
        }
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected String[] getExportedKeyTables(String catalog, String schema, String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getExportedKeyTables(
                    isUsingCatalogs() ? catalog : null, schema, name);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new String[0];
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected String[] getImportedKeyTables(String catalog, String schema, String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getImportedKeyTables(
                    isUsingCatalogs() ? catalog : null, schema, name);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new String[0];
        }
    }
    
    /**
     * Propagates the call to the meta object.
     */
    protected ResultSet getTableMetaData(String catalog, String schema, String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getTableMetaData(
                    isUsingCatalogs() ? catalog : null, schema, name);
        }
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected Vector getTableIndexes(String catalog, String schema, String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getTableIndexes(
                    isUsingCatalogs() ? catalog : null, schema, name);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new Vector(0);
        }
    }
    
    /** 
     * Propagates the call to the meta data object.
     */
    protected TablePrivilege[] getPrivileges(String catalog, String schema, String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getPrivileges(
                    isUsingCatalogs() ? catalog : null, schema, name);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new TablePrivilege[0];
        }
    }
    
    protected ColumnData[] getColumnData(String catalog, String schema, String name) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getColumnMetaData(
                    isUsingCatalogs() ? catalog : null, schema, name);
        }
        catch (DataSourceException e) {
            handleException(e);
            return new ColumnData[0];
        }
    }

    /**
     * Recycles the specified connection object for the browser.
     *
     * @param dc - the connection to be recycled
     */
    protected void recycleConnection(DatabaseConnection dc) {
        try {
            checkMetaDataObject();
            metaData.recycleConnection(dc);
        }
        catch (DataSourceException e) {
            handleException(e);
        }
    }
    
    /**
     * Ensures the meta data object is initialised.
     */
    private void checkMetaDataObject() {
        if (metaData == null) {
            metaData = new MetaDataValues(true);
        }
    }
    
    /**
     * Returns true if the currently selected connection is using
     * catalogs in meta data retrieval.
     *
     * @return true | false
     */
    protected boolean isUsingCatalogs() {
        return treePanel.getSelectedMetaObject().isCatalogsInUse();
    }
    
    /**
     * Retrieves the selected database connection properties object.
     */
    protected DatabaseConnection getDatabaseConnection() {
        ConnectionObject object = treePanel.getSelectedMetaObject();
        if (object != null) {
            return object.getDatabaseConnection();
        }
        return null;
    }

    /**
     * Retrieves the table data row count for the specified table.
     *
     * @param schema - the schema name (may be null)
     * @param table - the table name
     * @return the data row count as a String or a formatted error message
     */
    protected String getTableDataRowCount(String schema, String table) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return String.valueOf(metaData.getTableDataRowCount(schema, table));
        }
        catch (DataSourceException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Retrieves the data in its entirety from the specified table. 
     *
     * @param schema - the schema name (may be null)
     * @param table - the table name
     * @return the table data
     */
    public ResultSet getTableData(String schema, String table) {
        try {
            checkMetaDataObject();
            metaData.setDatabaseConnection(getDatabaseConnection());
            return metaData.getTableData(schema, table);
        }
        catch (DataSourceException e) {
            handleException(e);
            return null;
        }        
    }

    /**
     * Drops the specified database object.
     *
     * @param dc - the database connection
     * @param object - the object to be dropped
     */
    public int dropObject(DatabaseConnection dc, DatabaseObject object) 
        throws SQLException {

        String queryStart = null;
        int type = object.getType();
        switch (type) {

            case BrowserConstants.CATALOG_NODE:
            case BrowserConstants.SCHEMA_NODE:
            case BrowserConstants.OTHER_NODE:
                GUIUtilities.displayErrorMessage(
                    "Dropping objects of this type is not currently supported");
                return UPDATE_CANCELLED;

            case BrowserConstants.FUNCTIONS_NODE:
                queryStart = "DROP FUNCTION ";
                break;

            case BrowserConstants.INDEX_NODE:
                queryStart = "DROP INDEX ";
                break;

            case BrowserConstants.PROCEDURE_NODE:
                queryStart = "DROP PROCEDURE ";
                break;

            case BrowserConstants.SEQUENCE_NODE:
                queryStart = "DROP SEQUENCE ";
                break;

            case BrowserConstants.SYNONYM_NODE:
                queryStart = "DROP SYNONYM ";
                break;

            case BrowserConstants.SYSTEM_TABLE_NODE:
            case BrowserConstants.TABLE_NODE:
                queryStart = "DROP TABLE ";
                break;

            case BrowserConstants.TRIGGER_NODE:
                queryStart = "DROP TRIGGER ";
                break;

            case BrowserConstants.VIEW_NODE:
                queryStart = "DROP VIEW ";
                break;

        }

        if (querySender == null) {
            querySender = new QuerySender(dc);
        } else {
            querySender.setDatabaseConnection(dc);
        }

        String name = object.getName();
        return querySender.updateRecords(queryStart + name).getUpdateCount();
    }

    /**
     * Propagates the call to the meta data object.
     */
    protected void closeConnection() {
        if (metaData != null) {
            metaData.closeConnection();
        }
    }

}