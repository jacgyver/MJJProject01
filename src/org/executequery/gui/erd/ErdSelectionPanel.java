/*
 * ErdSelectionPanel.java
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


package org.executequery.gui.erd;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.Vector;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.executequery.databasemediators.DatabaseConnection;

import org.executequery.databasemediators.MetaDataValues;
import org.executequery.datasource.ConnectionManager;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.ListSelectionPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/06/09 01:44:37 $
 */
public class ErdSelectionPanel extends JPanel
                               implements ItemListener {
                                          //ActionListener {
    
    /** The schema combo box */
    protected JComboBox schemaCombo;
    
    /** the schema combo box model */
    protected DynamicComboBoxModel schemaModel;
    
    /** The connection combo selection */
    protected JComboBox connectionsCombo; 

    /** the schema combo box model */
    protected DynamicComboBoxModel connectionsModel;

    /** The add/remove table selections panel */
    private ListSelectionPanel listPanel;
    
    /** the database connection props object */
    private DatabaseConnection databaseConnection;
    
    private boolean useCatalogs;
    
    private MetaDataValues metaData;
    
    public ErdSelectionPanel() {
        this(null);
    }

    public ErdSelectionPanel(DatabaseConnection databaseConnection) {
        super(new GridBagLayout());
        this.databaseConnection = databaseConnection;
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void jbInit() throws Exception {
        
        listPanel = new ListSelectionPanel("Available Tables:", "Selected Tables:");
        metaData = new MetaDataValues(true);

        // combo boxes
        Vector connections = ConnectionManager.getActiveConnections();
        connectionsModel = new DynamicComboBoxModel(connections);
        connectionsCombo = new JComboBox(connectionsModel);
        connectionsCombo.addItemListener(this);

        schemaModel = new DynamicComboBoxModel();
        schemaCombo = new JComboBox(schemaModel);
        schemaCombo.addItemListener(this);

        /*
        Vector schemas = metaData.getHostedSchemasVector();
        
        if (schemas == null || schemas.size() == 0) {
            useCatalogs = true;
            schemas = metaData.getHostedCatalogsVector();
        }
        */

        setBorder(BorderFactory.createEtchedBorder());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(13,10,0,10);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Connection:"), gbc);
        gbc.insets.top = 10;
        gbc.insets.left = 0;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(connectionsCombo, gbc);
        gbc.insets.left = 10;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;        
        add(new JLabel(useCatalogs ? "Catalog:" : "Schema:"), gbc);
        gbc.insets.left = 0;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(schemaCombo, gbc);
        gbc.gridx = 0;       
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets.top = 10;
        gbc.insets.left = 10;
        gbc.insets.bottom = 10;
        gbc.insets.right = 10;
        gbc.fill = GridBagConstraints.BOTH;
        add(listPanel, gbc);
        
        // check initial values for possible value inits
        if (connections == null || connections.isEmpty()) {
            schemaCombo.setEnabled(false);
            connectionsCombo.setEnabled(false);
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
            schemaChanged();
        }
        
    }
    
    public void setInProcess(boolean inProcess) {}
    
    /**
     * Invoked when an item has been selected or deselected by the user.
     */    
    public void itemStateChanged(ItemEvent event) {
        // interested in selections only
        if (event.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }
        listPanel.clear();

        final Object source = event.getSource();
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {
                    setInProcess(true);
                    if (source == connectionsCombo) {
                        connectionChanged();
                    }
                    else if (source == schemaCombo) {
                        schemaChanged();
                    }
                }
                finally {
                    setInProcess(false);
                }
            }
        });
        
        
        /*
        if (source == connectionsCombo) {
            try {
                // retrieve connection selection
                DatabaseConnection connection = 
                        (DatabaseConnection)connectionsCombo.getSelectedItem();
                // reset meta data
                metaData.setDatabaseConnection(connection);
                // reset schema values
                Vector schemas = metaData.getHostedSchemasVector();
                if (schemas == null || schemas.isEmpty()) {
                    useCatalogs = true;
                    schemas = metaData.getHostedCatalogsVector();
                } else {
                    useCatalogs = false;
                }
                schemaCombo.removeItemListener(this);
                schemaModel.setElements(schemas);
                schemaCombo.setEnabled(true);
                //schemaChanged();
            }
            catch (DataSourceException e) {
                GUIUtilities.displayExceptionErrorDialog(
                        "Error retrieving the catalog/schema names for the " +
                        "current connection.\n\nThe system returned:\n" + 
                        e.getExtendedMessage(), e);
            }
            finally {
                schemaCombo.addItemListener(this);
            }
        }
        else if (source == schemaCombo) {
            schemaChanged();
        }
         */
    }

    private void connectionChanged() {
        try {
            // retrieve connection selection
            DatabaseConnection connection = 
                    (DatabaseConnection)connectionsCombo.getSelectedItem();
            // reset meta data
            metaData.setDatabaseConnection(connection);
            // reset schema values
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
        }
    }
    
    private void populateSchemaValues(final Vector schemas) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                schemaModel.setElements(schemas);
                schemaCombo.setEnabled(true);
                if (schemas != null && schemas.size()> 0) {
                    schemaCombo.setSelectedIndex(0);
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
                    "Error retrieving the table names for the selected " +
                    "catalog/schema.\n\nThe system returned:\n" + 
                    e.getExtendedMessage(), e);
            populateTableValues(new String[0]);
        }
    }

    private void populateTableValues(final String[] tables) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                listPanel.createAvailableList(tables);
            }
        });
    }

    public String getSchema() {
        return schemaCombo.getSelectedItem().toString();
    }
    
    /**
     * Releases database resources before closing.
     */
    public void cleanup() {
        metaData.closeConnection();
    }

    public Vector getSelectedValues() {
        return listPanel.getSelectedValues();
    }
    
    public boolean hasSelections() {
        return listPanel.hasSelections();
    }
 
    public DatabaseConnection getDatabaseConnection() {
        if (databaseConnection == null) {
            return (DatabaseConnection)connectionsCombo.getSelectedItem();
        }
        return databaseConnection;
    }
    
}



