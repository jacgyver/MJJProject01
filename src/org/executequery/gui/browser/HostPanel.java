/*
 * HostPanel.java
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

import java.awt.print.Printable;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.databasemediators.DatabaseConnection;

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
 * Database connection host panel.<br>
 * Displays connection/hsot info and database properties 
 * once connected.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class HostPanel extends AbstractFormObjectViewPanel
                       implements ConnectionListener {
    
    public static final String NAME = "HostPanel";
    
    private DisabledField databaseProductField;
    private DisabledField hostField;
    private DisabledField sourceField;
    private DisabledField schemaField;
    private DisabledField urlField;
    
    private JTable schemaTable;
    private HostModel model;
    
    private boolean initialised;
    
    /** the meta object containing data for the current selection */
    private ConnectionObject metaObject;
    
    
    /** the tab pane display */
    private JTabbedPane tabPane;
    
    /** parent controlling panel */
    //private BrowserViewPanel parent;
    
    /** the connection info pane */
    private ConnectionPanel connectionPanel;
    
    /** the key words panel */
    private KeyWordsPanel keyWordsPanel;
    
    /** the java sql types panel */
    private JavaSQLTypesPanel javaSqlTypesPanel;
    
    /** the database properties pane */
    private DatabasePropertiesPanel databasePropertiesPanel;
    
    /** the data types panel */
    private DataTypesPanel dataTypesPanel;
    
    /** the browser's control object */
    private BrowserController controller;

    public HostPanel(BrowserController controller) {
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
        connectionPanel = new ConnectionPanel(controller);
        databasePropertiesPanel = new DatabasePropertiesPanel();
        keyWordsPanel = new KeyWordsPanel();
        dataTypesPanel = new DataTypesPanel();
        javaSqlTypesPanel = new JavaSQLTypesPanel();

        tabPane = new JTabbedPane(JTabbedPane.TOP);
        tabPane.addTab("Connection", connectionPanel);
        tabPane.addTab("Database Properties", databasePropertiesPanel);
        tabPane.addTab("SQL Keywords", keyWordsPanel);
        tabPane.addTab("Data Types", dataTypesPanel);
        tabPane.addTab("java.sql.Types", javaSqlTypesPanel);
        enableConnectionTabs(false);

        setHeaderText("Database Connection");
        setHeaderIcon(GUIUtilities.loadIcon("Database24.gif"));
        setContentPanel(tabPane);

        // register with the event listener
        EventMediator.registerListener(EventMediator.CONNECTION_EVENT, this);
        
    }
    
    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        connectionPanel.buildDriversList();
        return connectionPanel.tabViewSelected();
    }

    private void enableConnectionTabs(boolean enabled) {
        int[] tabs = new int[]{1, 2, 3};
        for (int i = 0; i < tabs.length; i++) {
            tabPane.setEnabledAt(tabs[i], enabled);
        }
        if (!enabled) {
            tabPane.setSelectedIndex(0);
        }
    }
    
    /**
     * Informs any panels of a new selection being made.
     */
    protected void selectionChanging() {
        connectionPanel.selectionChanging();
    }

    /**
     * Indicates the panel is being de-selected in the pane
     */
    public boolean tabViewDeselected() {
        return connectionPanel.tabViewDeselected();
    }
    
    /**
     * Propagates the call to save connections to the
     * connections panel.
     */
    protected boolean saveConnections() {
        return connectionPanel.saveConnections();
    }
    
    public void setValues(ConnectionObject metaObject) {
        this.metaObject = metaObject;
        connectionPanel.setConnectionValue(metaObject);

        DatabaseConnection databaseConnection = 
                                metaObject.getDatabaseConnection();
        if (databaseConnection.isConnected()) {
            changePanelData();
        } 
        else {
            enableConnectionTabs(false);
        }
    }
    
    /**
     * Reloads the database properties meta data table panel.
     */
    protected void updateDatabaseProperties() {
        Hashtable properties = controller.getDatabaseProperties();
        databasePropertiesPanel.setDatabaseProperties(properties);        
    }
    
    private void changePanelData() {
        // notify the database properties
        Hashtable properties = controller.getDatabaseProperties();
        databasePropertiesPanel.setDatabaseProperties(properties);
        
        // notify the keywords panel
        String[] keywords = controller.getDatabaseKeywords();
        keyWordsPanel.setDatabaseKeywords(keywords);

        // notify the data types panel
        dataTypesPanel.setDataTypes(controller.getDataTypesResultSet());
        
        // enable the tabs
        enableConnectionTabs(true);
    }
    
    /**
     * Indicates a connection has been established.
     * 
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent) {
        // notify connection panel
        connectionPanel.connected(connectionEvent.getSource());
        // notify other panels
        changePanelData();
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent) {
        connectionPanel.disconnected(connectionEvent.getSource());
        enableConnectionTabs(false);
    }

    
    public String getLayoutName() {
        return NAME;
    }
    
    public void refresh() {}    
    public void cleanup() {}
    
    public Printable getPrintable() {
        return new TablePrinter(schemaTable, "Database Server: " + hostField.getText());
    }
    
    public JTable getTable() {
        return schemaTable;
    }
    
    public boolean isInitialised() {
        return initialised;
    }
    
    public void setValues(String sourceName, String schemaName, Vector values) {
        sourceField.setText(sourceName);
        schemaField.setText(schemaName);
        model.setValues(values);
        initialised = true;
    }
    
    public void setValues(String databaseName, String hostName, String sourceName,
                            String schemaName, String urlName, Vector values) {
        databaseProductField.setText(databaseName);
        hostField.setText(hostName);
        sourceField.setText(sourceName);
        schemaField.setText(schemaName);
        urlField.setText(urlName);
        model.setValues(values);
    }
    
    private class HostModel extends AbstractTableModel {
        
        private Vector values;
        private String header = "Catalog Name";
        
        public HostModel() {
            values = new Vector(0);
        }
        
        public void setValues(Vector values) {
            this.values = values;
            fireTableDataChanged();
        }
        
        public int getRowCount() {
            return values.size();
        }
        
        public int getColumnCount() {
            return 1;
        }
        
        public String getColumnName(int col) {
            return header;
        }
        
        public Object getValueAt(int row, int col) {
            return values.elementAt(row);
        }
        
        public boolean isCellEditable() {
            return false;
        }
        
    } // class HostModel
    
} 



