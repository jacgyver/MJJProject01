/*
 * GenerateScriptsWizard.java
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

import java.awt.Dimension;
import javax.swing.JPanel;
import org.executequery.ActiveComponent;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.wizard.DefaultWizardProcessModel;
import org.underworldlabs.swing.wizard.WizardProcessPanel;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.gui.ActionContainer;
import org.executequery.gui.GeneratedScriptViewer;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Base panel for the generate scripts process.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/09/13 15:15:09 $
 */
public class GenerateScriptsWizard extends WizardProcessPanel
                                   implements ActiveComponent {
    
    public static final String TITLE = "Generate SQL Scripts";
    public static final String FRAME_ICON = "CreateScripts16.gif";

    /** script type identifier for DROP TABLE */
    public static final int DROP_TABLES = 0;

    /** script type identifier for CREATE TABLE */
    public static final int CREATE_TABLES = 1;

    /** indicator to include constraints as ALTER TABLE statements */
    public static final int ALTER_TABLE_CONSTRAINTS = 0;
    
    /** indicator to include constraints with CREATE TABLE statements */
    public static final int CREATE_TABLE_CONSTRAINTS = 1;
    
    /** meta data object */
    private MetaDataValues metaData;
    
    /** the first panel */
    private GenerateScriptsPanelOne firstPanel;
    
    /** the second panel */
    private GenerateScriptsPanelTwo secondPanel;
    
    /** the third panel */
    private GenerateScriptsPanelThree thirdPanel;
    
    /** the fourth and final panel */
    private GenerateScriptsPanelFour fourthPanel;
    
    /** the wizard model */
    private GenerateScriptsWizardModel model;
    
    /** the parent container */
    private ActionContainer parent;
    
    /** the panel dimension */
    protected static final Dimension CHILD_DIMENSION = new Dimension(525, 320);
    
    /** Creates a new instance of GenerateScriptsWizard */
    public GenerateScriptsWizard(ActionContainer parent) {
        this.parent = parent;
        model = new GenerateScriptsWizardModel();
        setModel(model);

        // setup the help button
        /*
        helpButton.setIcon(null);
        helpButton.setText("Help");
        helpButton.setActionCommand("generate-scripts");
        */

        // set the help action
        setHelpAction(ActionBuilder.get("help-command"), "generate-scripts");

        // initialise the first panel
        firstPanel = new GenerateScriptsPanelOne(this);
        model.addPanel(firstPanel);
        prepare();
    }
    
    /** 
     * Retrieves the <code>MetaDataValues</code> object defined for this process.
     *
     * @return the <code>MetaDataValues</code> helper class
     */
    public MetaDataValues getMetaDataUtility() {
        if (metaData == null) {
            metaData = new MetaDataValues();
        }
        metaData.setDatabaseConnection(getDatabaseConnection());
        return metaData;
    }

    /**
     * Whether to include the CASCADE keyword within DROP statments.
     *
     * @return true | false
     */
    protected boolean cascadeWithDrop() {
        return thirdPanel.cascadeWithDrop();
    }
    
    /**
     * Returns the constraints definition format - 
     * as ALTER TABLE statements or within the CREATE TABLE statements.
     */
    protected int getConstraintsStyle() {
        return thirdPanel.getConstraintsStyle();
    }
    
    /**
     * Returns the database connection for the script.
     *
     * @return the connection properties object
     */
    protected DatabaseConnection getDatabaseConnection() {
        return firstPanel.getDatabaseConnection();
    }
    
    /**
     * Returns the selected tables.
     */
    protected String[] getSelectedTables() {
        return secondPanel.getSelectedTables();
    }
    
    /**
     * Returns the output file path.
     *
     * @return the output file path
     */
    protected String getOutputFilePath() {
        return thirdPanel.getOutputFilePath();
    }
    
    /**
     * Returns the type of script to be generated.
     *
     * @return the script type
     */
    protected int getScriptType() {
        return firstPanel.getScriptType();
    }
    
    /**
     * Returns the selected schema.
     */
    protected String getSelectedSchema() {
        return secondPanel.getSelectedSchema();
    }
    
    /**
     * Returns whether the selected connection is using catalogs.
     */
    protected boolean usingCatalogs() {
        return secondPanel.usingCatalogs();
    }

    /**
     * Starts the script generation process.
     */
    private void start() {
        setNextButtonEnabled(false);
        setBackButtonEnabled(false);
        setCancelButtonEnabled(false);
        fourthPanel.start();
    }
    
    /**
     * Defines the action for the NEXT button.
     */
    private boolean doNext() {
        JPanel nextPanel = null;
        int index = model.getSelectedIndex();
        switch (index) {
            
            case 0:
                if (secondPanel == null) {
                    secondPanel = new GenerateScriptsPanelTwo(this);
                }
                secondPanel.setListData();
                nextPanel = secondPanel;
                break;
                
            case 1:
                
                if (!secondPanel.hasSelections()) {
                    GUIUtilities.displayErrorMessage(
                            "You must select at least one table.");
                    return false;
                }
                
                if (thirdPanel == null) {
                    thirdPanel = new GenerateScriptsPanelThree(this);
                }
                nextPanel = thirdPanel;
                break;
                
            case 2:
                
                if (MiscUtils.isNull(thirdPanel.getOutputFilePath())) {
                    GUIUtilities.displayErrorMessage(
                            "You must select an output file for this script.");
                    return false;
                }

                if (fourthPanel == null) {
                    fourthPanel = new GenerateScriptsPanelFour(this);
                }
                nextPanel = fourthPanel;
                model.addPanel(nextPanel);
                start();
                return true;

        }

        model.addPanel(nextPanel);
        return true;
    }

    /**
     * Defines the action for the BACK button.
     */
    private boolean doPrevious() {
        // make sure the cancel button says cancel
        setCancelButtonText("Cancel");
        return true;
    }
    
    /** whether the process was successful */
    private boolean processSuccessful;
    
    protected void finished(boolean success) {
        setButtonsEnabled(true);
        setNextButtonEnabled(false);
        setBackButtonEnabled(true);
        setCancelButtonEnabled(true);

        processSuccessful = success;
        if (success) {
            setCancelButtonText("Finish");
        }
        
    }

    /** 
     * Defines the action for the CANCEL button.
     */
    public void cancel() {
        parent.finished();
    }

    /**
     * Releases database resources before closing.
     */
    public void cleanup() {
        if (metaData != null) {
            metaData.closeConnection();
        }

        // check if we're viewing the script
        if (processSuccessful) {
            if (fourthPanel.viewScriptOnCompletion()) {
                GUIUtils.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            GUIUtilities.showWaitCursor();
                            GUIUtilities.addCentralPane(
                                    GeneratedScriptViewer.TITLE,
                                    GeneratedScriptViewer.FRAME_ICON, 
                                    new GeneratedScriptViewer(
                                            null, thirdPanel.getOutputFilePath()),
                                    GeneratedScriptViewer.TITLE,
                                    true);
                        }
                        finally {
                            GUIUtilities.showNormalCursor();
                        }
                    }
                });
            }
        }
        
    }

    
    private class GenerateScriptsWizardModel extends DefaultWizardProcessModel {
        
        public GenerateScriptsWizardModel() {
            String[] titles = {"Connection and Script Type",
                               "Schema and Table Selection",
                               "Output File Selection",
                               "Generating..."};
            setTitles(titles);

            String[] steps = {"Select the database connection and script type",
                              "Select the schema and tables",
                              "Select the output SQL file and further options",
                              "Generate the script"};
            setSteps(steps);
        }

        public boolean previous() {
            if (doPrevious()) {
                return super.previous();
            }
            return false;
        }
        
        public boolean next() {
            if (doNext()) {
                return super.next();
            }
            return false;
        }

    }

}



