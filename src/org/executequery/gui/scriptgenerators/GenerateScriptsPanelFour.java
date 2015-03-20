/*
 * GenerateScriptsPanelFour.java
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.wizard.InterruptibleWizardProcess;
import org.underworldlabs.swing.wizard.WizardProgressBarPanel;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.gui.browser.ColumnConstraint;
import org.executequery.gui.browser.ColumnData;
import org.executequery.util.Log;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.util.DateUtils;
import org.underworldlabs.swing.util.SwingWorker;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Step three panel in the generate scripts wizard.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class GenerateScriptsPanelFour extends JPanel 
                                      implements InterruptibleWizardProcess {
    
    /** result indicator for success */
    private final String SUCCESS = "success";

    /** result indicator for success */
    private final String FAILED = "failed";

    /** result indicator for success */
    private final String CANCELLED = "cancelled";
    
    /** the view script when done check box */
    private JCheckBox viewScriptCheck;
    
    /** the progress bar panel */
    private WizardProgressBarPanel progressPanel;
    
    /** the parent controller */
    private GenerateScriptsWizard parent;

    /** The worker thread */
    private SwingWorker worker;
    
    /** The file to save to */
    private File file;

    /** Creates a new instance of GenerateScriptsPanelFour */
    public GenerateScriptsPanelFour(GenerateScriptsWizard parent) {
        super(new GridBagLayout());
        this.parent = parent;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        viewScriptCheck = new JCheckBox("View generated script");
        viewScriptCheck.setEnabled(false);
        progressPanel = new WizardProgressBarPanel(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx++;
        gbc.gridy++;
        gbc.insets = new Insets(10,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(progressPanel, gbc);
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets.top = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(viewScriptCheck, gbc);
    }

    /**
     * The status of the view script check box.
     */
    protected boolean viewScriptOnCompletion() {
        return viewScriptCheck.isSelected();
    }
    
    /**
     * Starts the generation process in a worker thread.
     */
    protected void start() {
        worker = new SwingWorker() {
            public Object construct() {
                if (parent.getScriptType() == GenerateScriptsWizard.CREATE_TABLES) {
                    return createTableScript();
                } else {
                    return dropTableScript();
                }
            }
            public void finished() {
                GUIUtilities.scheduleGC();
                boolean success = (get() == SUCCESS);
                viewScriptCheck.setEnabled(success);
                parent.finished(success);
            }
        };
        worker.start();
    }
    
    private Object dropTableScript() {
        
        // make sure the progress panel is clear
        progressPanel.reset();
        viewScriptCheck.setEnabled(false);

        PrintWriter writer = null;
        try {

            String[] tables = parent.getSelectedTables();
            progressPanel.setMinimum(0);
            progressPanel.setMaximum(tables.length + 1);
            
            String catalogName = null;
            String schemaName = null;
            if (parent.usingCatalogs()) {
                catalogName = parent.getSelectedSchema();
            } else {
                schemaName = parent.getSelectedSchema();
            }

            MetaDataValues metaData = parent.getMetaDataUtility();
            
            progressPanel.appendProgressText("Generating script...\n");
            
            // initialise the writer
            file = new File(parent.getOutputFilePath());
            writer = new PrintWriter(new FileWriter(file, false), true);

            String databaseProductName = null;
            try {
                databaseProductName = metaData.getDatabaseProductName();
            }
            catch (DataSourceException e) {
                Log.error("Error retrieving database product name: " + 
                        e.getMessage());
                databaseProductName = "Not Available";
            }

            String header = createHeader(
                                file.getName(),
                                schemaName != null ? schemaName : catalogName,
                                databaseProductName);
            writer.println(header);

            String DROP_TABLE = "DROP TABLE ";
            String STATEMENT_END = ";";

            if (parent.cascadeWithDrop()) {
                STATEMENT_END = " CASCADE;";
            }

            StringBuffer sb = new StringBuffer();

            // start the table loop
            for (int i = 0; i < tables.length; i++) {
                
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
             
                String tableName = tables[i];
                progressPanel.appendProgressText(
                        "Generating DROP TABLE " + tableName);

                sb.append(DROP_TABLE).
                   append(tableName).
                   append(STATEMENT_END);
             
                writer.println(sb.toString());
                sb.setLength(0);
                
                progressPanel.setProgressStatus(i+1);
            }
            
            progressPanel.appendProgressText(
                    "\nScript generated successfully to " + file.getName());
            progressPanel.appendProgressText("Done.");
            return SUCCESS;
        }
        catch (IOException e) {
            progressPanel.appendExceptionError("Error writing to ouput file.", e);
            return FAILED;
        }
        catch (InterruptedException e) {
            progressPanel.appendProgressText(
                    "Script generation cancelled on user request");
            // delete the file if it exists
            if (file != null && file.exists()) {
                file.delete();
            }
            return CANCELLED;
        } 
        finally {
            // make sure the progress bar is full
            progressPanel.finished();
            if (writer != null) {
                writer.close();
                writer = null;
            }
        } 
    }
    
    private Object createTableScript() {
        
        // make sure the progress panel is clear
        progressPanel.reset();
        viewScriptCheck.setEnabled(false);

        PrintWriter writer = null;
        try {

            String[] tables = parent.getSelectedTables();
            progressPanel.setMinimum(0);
            progressPanel.setMaximum(tables.length + 2);

            String catalogName = null;
            String schemaName = null;
            if (parent.usingCatalogs()) {
                catalogName = parent.getSelectedSchema();
            } else {
                schemaName = parent.getSelectedSchema();
            }

            MetaDataValues metaData = parent.getMetaDataUtility();
            
            progressPanel.appendProgressText("Generating script...\n");
            
            // initialise the writer
            file = new File(parent.getOutputFilePath());
            writer = new PrintWriter(new FileWriter(file, false), true);

            String databaseProductName = null;
            try {
                databaseProductName = metaData.getDatabaseProductName();
            }
            catch (DataSourceException e) {
                Log.error("Error retrieving database product name: " + 
                        e.getMessage());
                databaseProductName = "Not Available";
            }

            String header = createHeader(
                                file.getName(),
                                schemaName != null ? schemaName : catalogName,
                                databaseProductName);
            writer.println(header);

            String scriptText = null;
            int constraintStyle = parent.getConstraintsStyle();
            boolean includeConstraints = (constraintStyle != -1);
            boolean constraintsAsAlter = (constraintStyle == 
                        GenerateScriptsWizard.ALTER_TABLE_CONSTRAINTS);
            
            Vector<ColumnConstraint> constraintsVector = null;
            if (constraintsAsAlter) {
                constraintsVector = new Vector<ColumnConstraint>();
            }
            
            // start the table loop
            for (int i = 0; i < tables.length; i++) {
                
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                
                String tableName = tables[i];
                progressPanel.appendProgressText(
                        "Generating CREATE TABLE " + tableName);

                ColumnData[] cda = null;
                try {
                    cda = metaData.getColumnMetaData(
                                            catalogName, schemaName, tableName);
                }
                catch (DataSourceException e) {
                    progressPanel.appendProgressErrorText(
                            "Error retrieving column meta data: " + 
                            e.getMessage());
                    progressPanel.setProgressStatus(i+1);
                    continue;
                }
                
                if (includeConstraints) {
                
                    if (constraintsAsAlter) {
                        scriptText = ScriptGenerationUtils.
                                        createTableScript(tableName, cda, false);

                        // store the constraint values
                        Vector<ColumnConstraint> ccv = null;        
                        for (int j = 0; j < cda.length; j++) {

                            if (cda[j].isKey()) {
                                ccv = cda[j].getColumnConstraintsVector();
                                for (int m = 0, k = ccv.size(); m < k; m++) {
                                    constraintsVector.add(ccv.get(m));
                                }
                            }

                        }

                    }
                    else {
                        scriptText = ScriptGenerationUtils.createTableScript(
                                            tableName, cda, true);
                    }

                } 
                else {
                    scriptText = ScriptGenerationUtils.createTableScript(
                                        tableName, cda);
                }
                
                writer.println(scriptText);
                writer.println();
                
                progressPanel.setProgressStatus(i+1);
            }
            
            if (constraintsAsAlter) {
                progressPanel.appendProgressText("\nGenerating ALTER TABLE statements...");
                scriptText = ScriptGenerationUtils.
                                alterTableConstraintsScript(constraintsVector);
                writer.println();
                writer.println(scriptText);
            }

            progressPanel.appendProgressText(
                    "\nScript generated successfully to " + file.getName());
            progressPanel.appendProgressText("Done.");
            return SUCCESS;
        }
        catch (IOException e) {
            progressPanel.appendExceptionError("Error writing to ouput file.", e);
            return FAILED;
        }
        catch (InterruptedException e) {
            progressPanel.appendProgressText(
                    "Script generation cancelled on user request");
            // delete the file if it exists
            if (file != null && file.exists()) {
                file.delete();
            }
            return CANCELLED;
        } 
        finally {
            // make sure the progress bar is full
            progressPanel.finished();
            if (writer != null) {
                writer.close();
                writer = null;
            }
        } 

    }

    private String createHeader(String fileName, String schema, String database) {
        // set the current date and time stamp
        DateUtils dt = new DateUtils();
        String timeDate = dt.getLongDateTime();

        StringBuffer sb = new StringBuffer(500);
        String line_1 = "-- ---------------------------------------------------\n";
        sb.append(line_1).
           append("--\n-- SQL script ").
           append("generated by Execute Query.\n-- Generated ").
           append(timeDate).
           append("\n--\n").
           append(line_1).
           append("--\n-- Program:      ").
           append(file.getName()).
           append("\n-- Description:  SQL ").
           append(parent.getScriptType() == GenerateScriptsWizard.CREATE_TABLES ?
                      "create " : "drop ").
           append("tables script.\n-- Schema:       ").
           append(schema).
           append("\n-- Database:     ").
           append(database).
           append("\n--\n").append(line_1).
           append("\n");
        return sb.toString();
    }
    
    /**
     * Processes a stop action.
     */
    public void stop() {
        worker.interrupt();
    }
            
    
}



