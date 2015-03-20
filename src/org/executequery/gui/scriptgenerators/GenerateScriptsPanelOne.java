/*
 * GenerateScriptsPanelOne.java
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
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.datasource.ConnectionManager;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Step one panel in the generate scripts wizard.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class GenerateScriptsPanelOne extends JPanel {

    /** create table script radio button */
    private JRadioButton createTableButton;
    
    /** drop table script radio button */
    private JRadioButton dropTableButton;
    
    /** The connection combo selection */
    private JComboBox connectionsCombo; 

    /** the schema combo box model */
    private DynamicComboBoxModel connectionsModel;

    /** the parent controller */
    private GenerateScriptsWizard parent;
    
    /** Creates a new instance of GenerateScriptsPanelOne */
    public GenerateScriptsPanelOne(GenerateScriptsWizard parent) {
        super(new GridBagLayout());
        this.parent = parent;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        createTableButton = new JRadioButton("CREATE TABLE script", true);
        dropTableButton = new JRadioButton("DROP TABLE script");
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(createTableButton);
        bg.add(dropTableButton);
        
        // combo boxes
        Vector connections = ConnectionManager.getActiveConnections();
        connectionsModel = new DynamicComboBoxModel(connections);
        connectionsCombo = new JComboBox(connectionsModel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(7,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Connection:"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.insets.top = 5;
        add(connectionsCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets.top = 10;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Select the type of scipt to be generated:"), gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        gbc.insets.left = 20;
        add(createTableButton, gbc);
        gbc.gridy++;
        gbc.weighty = 1.0;
        add(dropTableButton, gbc);
        
        setPreferredSize(GenerateScriptsWizard.CHILD_DIMENSION);
    }

    /**
     * Returns the selected database connection properties object.
     *
     * @return the connection properties object
     */
    public DatabaseConnection getDatabaseConnection() {
        return (DatabaseConnection)connectionsCombo.getSelectedItem();
    }

    /**
     * Returns the type of script to be generated.
     *
     * @return the script type
     */
    protected int getScriptType() {
        if (createTableButton.isSelected()) {
            return GenerateScriptsWizard.CREATE_TABLES;
        } else {
            return GenerateScriptsWizard.DROP_TABLES;
        }
    }
    
}



