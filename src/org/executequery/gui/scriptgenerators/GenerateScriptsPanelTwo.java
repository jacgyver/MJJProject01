/*
 * GenerateScriptsPanelTwo.java
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


package org.executequery.gui.scriptgenerators;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.ListSelectionPanel;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.MetaDataValues;
import org.underworldlabs.jdbc.DataSourceException;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Step two panel in the generate scripts wizard.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/20 17:26:30 $
 */
public class GenerateScriptsPanelTwo extends JPanel 
                                     implements ActionListener {
    
    /** The selected values */
    private Vector tables;
    
    /** whether the current conneciton uses catalogs */
    private boolean useCatalogs;

    /** The list table/column list selection panel */
    private ListSelectionPanel list;
    
    /** The schema list */
    private JComboBox schemaCombo;

    /** the parent controller */
    private GenerateScriptsWizard parent;

    /** Creates a new instance of GenerateScriptsPanelTwo */
    public GenerateScriptsPanelTwo(GenerateScriptsWizard parent) {
        super(new GridBagLayout());
        this.parent = parent;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        MetaDataValues metaData = parent.getMetaDataUtility();
        Vector schemas = metaData.getHostedSchemasVector();
        if (schemas == null || schemas.size() == 0) {
            useCatalogs = true;
            schemas = metaData.getHostedCatalogsVector();
        }

        schemaCombo = new JComboBox(schemas);
        schemaCombo.addActionListener(this);
        
        list = new ListSelectionPanel("Available Tables:", "Selected Tables:");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(7,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Schema:"), gbc);
        gbc.gridx = 1;
        gbc.insets.top = 5;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(schemaCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.insets.top = 5;
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.insets.bottom = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(list, gbc);

        setPreferredSize(GenerateScriptsWizard.CHILD_DIMENSION);
    }

    /** the last selected connection */
    private DatabaseConnection dc;
    
    /**
     * Sets the list data based on the current schema selection.
     */
    protected void setListData() {
        MetaDataValues metaData = parent.getMetaDataUtility();

        // check we haven't changed connection
        if (dc != parent.getDatabaseConnection()) {
            Vector schemas = null;
            try {
                schemas = metaData.getHostedSchemasVector();
                if (schemas == null || schemas.isEmpty()) {
                    useCatalogs = true;
                    schemas = metaData.getHostedCatalogsVector();
                } else {
                    useCatalogs = false;
                }
            }
            catch (DataSourceException e) {
                GUIUtilities.displayExceptionErrorDialog(
                        "Error retrieving the catalog/schema names for the current " +
                        "connection.\n\nThe system returned:\n" +
                        e.getExtendedMessage(), e);
                schemas = new Vector<String>(0);
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(schemas);
            schemaCombo.setModel(model);
        } 
        else {
            // leave all previous selections as they are
            return;
        }

        repopulate();
        dc = parent.getDatabaseConnection();
    }

    /**
     * Returns whether the selected connection is using catalogs.
     */
    protected boolean usingCatalogs() {
        return useCatalogs;
    }
    
    private void repopulate() {
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
            MetaDataValues metaData = parent.getMetaDataUtility();
            list.createAvailableList(
                    metaData.getTables(catalogName, schemaName, "TABLE"));
        }        
        catch (DataSourceException e) {} // should probably do something here        
    }

    /**
     * Whether the selected list has any values in it.
     */
    protected boolean hasSelections() {
        return list.hasSelections();
    }
    
    /**
     * Returns the selected tables in an array.
     *
     * @return the selected tables
     */
    protected String[] getSelectedTables() {
        Vector v = list.getSelectedValues();
        String[] tables = new String[v.size()];
        for (int i = 0; i < tables.length; i++) {
            tables[i] = (String)v.get(i);
        }
        return tables;
    }

    /**
     * Returns the selected schema.
     */
    protected String getSelectedSchema() {
        Object schema = schemaCombo.getSelectedItem();
        if (schema != null) {
            return schema.toString();
        }
        else {
            return Constants.EMPTY;
        }
    }

    public void actionPerformed(ActionEvent e) {
        repopulate();
    }

}



