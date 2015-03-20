/*
 * DriversPanel.java
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


package org.executequery.gui.drivers;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.executequery.DatabaseDefinitionCache;
import org.executequery.GUIUtilities;
import org.executequery.JDBCProperties;
import org.executequery.ValidationException;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.underworldlabs.swing.actions.ActionUtilities;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.executequery.base.TabView;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.gui.forms.AbstractFormObjectViewPanel;
import org.executequery.components.FileChooserDialog;
import org.executequery.components.TextFieldPanel;
import org.executequery.datasource.DatabaseDefinition;
import org.underworldlabs.swing.FileSelector;
import org.underworldlabs.util.MiscUtils;
import org.executequery.gui.*;

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
public class DriversPanel extends AbstractFormObjectViewPanel
                          implements TabView,
                                     ItemListener {
    
    public static final String TITLE = "Drivers";
    public static final String FRAME_ICON = "DatabaseDrivers16.gif";

    private JTextField nameField;
    private JTextField descField;
    private JTextField pathField;
    private JTextField classField;

    private JComboBox driverUrlCombo;
    private JComboBox databaseNameCombo;
    
    private DynamicComboBoxModel urlComboModel;
    
    /** the currently selected driver */
    private DatabaseDriver databaseDriver;
    
    /** the parent panel containing the selection tree */
    private DriverViewPanel parent;
    
    /** Creates a new instance of DriversPanel */
    public DriversPanel(DriverViewPanel parent) {
        super();
        this.parent = parent;
        init();
    }
    
    private void init() {
        
        ReflectiveAction action = new ReflectiveAction(this);

        JButton browseButton = ActionUtilities.createButton(
                                        action, "Browse", "browseDrivers");
        JButton findButton = ActionUtilities.createButton(
                                        action, "Find", "findDriverClass");

        Dimension btnDim = new Dimension(70,23);
        browseButton.setPreferredSize(btnDim);
        findButton.setPreferredSize(btnDim);
        browseButton.setMinimumSize(btnDim);
        findButton.setMinimumSize(btnDim);

        Insets btnMargin = new Insets(0,0,0,0);
        browseButton.setMargin(btnMargin);
        findButton.setMargin(btnMargin);

        nameField = new JTextField();
        descField = new JTextField();
        pathField = new JTextField();
        classField = new JTextField();

        // retrieve the db name list
        List<DatabaseDefinition> databases = DatabaseDefinitionCache.getDatabaseDefinitions();
        int count = databases.size() + 1;
        Vector<DatabaseDefinition> _databases = new Vector(count);
        
        // create a new list with a dummy value
        for (int i = 1; i < count; i++) {
            _databases.add(databases.get(i - 1));
        }
        // add the dummy
        _databases.insertElementAt(new DatabaseDefinition(
                        DatabaseDefinitionCache.INVALID_DATABASE_ID, "Select..."), 0);
        databaseNameCombo = new JComboBox(_databases);
        databaseNameCombo.addItemListener(this);

        urlComboModel = new DynamicComboBoxModel();
        driverUrlCombo = new JComboBox(urlComboModel);
        driverUrlCombo.setEditable(true);

        JPanel base = new TextFieldPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy++;
        gbc.insets = new Insets(10,10,5,0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        base.add(new JLabel("Driver Name:"), gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        base.add(new JLabel("Description:"), gbc);
        gbc.gridy++;
        base.add(new JLabel("Database:"), gbc);
        gbc.gridy++;
        base.add(new JLabel("JDBC URL:"), gbc);
        gbc.gridy++;
        base.add(new JLabel("Path:"), gbc);
        gbc.gridy++;
        base.add(new JLabel("Class Name:"), gbc);
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.insets.right = 10;
        gbc.insets.top = 10;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;        
        base.add(nameField, gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        base.add(descField, gbc);
        gbc.gridy++;
        base.add(databaseNameCombo, gbc);
        gbc.gridy++;
        base.add(driverUrlCombo, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets.right = 0;
        base.add(pathField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets.right = 10;
        gbc.fill = GridBagConstraints.NONE;
        base.add(browseButton, gbc);
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.insets.right = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(classField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        gbc.insets.right = 10;
        gbc.fill = GridBagConstraints.NONE;
        base.add(findButton, gbc);

        setHeaderText("Database Driver");
        setHeaderIcon(GUIUtilities.loadIcon("DatabaseDriver24.gif"));
        setContentPanel(base);
    }

    public void itemStateChanged(ItemEvent e) {
        // interested in selections only
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        DatabaseDefinition database = getSelectedDatabase();
        int id = database.getId();
        if (id > 0) {
            // reload the urls for the combo selection
            resetUrlCombo(database);
        } else {
            // otherwise clear all
            urlComboModel.removeAllElements();
        }
    }
    
    public DatabaseDefinition getSelectedDatabase() {
        return (DatabaseDefinition)databaseNameCombo.getSelectedItem();
    }
    
    /**
     * Saves the driver data to file.
     */
    protected boolean saveDrivers() {
        //Log.debug("saving drivers");
        try {
            int saved = JDBCProperties.saveDrivers();
        }
        catch (ValidationException e) {
            GUIUtilities.displayErrorMessage(e.getMessage());
            return false;
        }
        return true;
    }
    
    protected void enableFields(boolean enable) {
        nameField.setEnabled(enable);
        descField.setEnabled(enable);
        pathField.setEnabled(enable);
        classField.setEnabled(enable);
        driverUrlCombo.setEnabled(enable);
        databaseNameCombo.setEnabled(enable);
    }
    
    // --------------------------------------------
    // DockedTabView implementation
    // --------------------------------------------

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        populateDriverObject();
        return saveDrivers();
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewDeselected() {
        populateDriverObject();
        return saveDrivers();
    }

    // --------------------------------------------
    
    public void findDriverClass(ActionEvent e) {
        if (databaseDriver.getId() == JDBCProperties.DEFAULT_ODBC_ID) {
            return;
        }

        String paths = pathField.getText();

        if (MiscUtils.isNull(paths)) {
            GUIUtilities.displayErrorMessage(
                    "A valid path to the JDBC library is required");
            return;
        }

        String[] drivers = null;
        try {
            GUIUtilities.showWaitCursor();
            drivers = MiscUtils.findImplementingClasses(
                                            "java.sql.Driver", paths);
        } catch (MalformedURLException urlExc) {
            GUIUtilities.displayErrorMessage(
                    "A valid path to the JDBC library is required");
        } catch (IOException ioExc) {
            GUIUtilities.displayErrorMessage(
                    "An error occured accessing the specified file:\n" +
                    ioExc.getMessage());
        } finally {
            GUIUtilities.showNormalCursor();
        }

        if (drivers == null || drivers.length == 0) {
            GUIUtilities.displayWarningMessage(
                    "No valid classes implementing java.sql.Driver\n"+
                    "were found in the specified resource");
            return;
        }

        int result = -1;
        String value = null;
        while (true) {
            SimpleValueSelectionDialog dialog = 
                    new SimpleValueSelectionDialog("Select JDBC Driver", drivers);
            result = dialog.showDialog();

            if (result == JOptionPane.OK_OPTION) {
                value = dialog.getValue();

                if (value == null) {
                    GUIUtilities.displayErrorMessage(
                            "You must select a driver from the list");
                } else {
                    classField.setText(value);
                    databaseDriver.setClassName(value);
                    break;
                }

            } else {
                break;
            }

        }
    }

    public void browseDrivers(ActionEvent e) {
        if (databaseDriver.getId() == JDBCProperties.DEFAULT_ODBC_ID) {
            return;
        }
        
        FileSelector jarFiles = new FileSelector(new String[] {"jar"}, 
                                                 "Java Archive files");
        FileSelector zipFiles = new FileSelector(new String[] {"zip"}, 
                                                 "ZIP Archive files");
        
        FileChooserDialog fileChooser = new FileChooserDialog();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(zipFiles);
        fileChooser.addChoosableFileFilter(jarFiles);
        
        fileChooser.setDialogTitle("Select JDBC Drivers...");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        
        int result = fileChooser.showDialog(GUIUtilities.getParentFrame(), "Select");
        
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        File[] files = fileChooser.getSelectedFiles();
        
        char COLON = ';';
        StringBuffer sb = new StringBuffer(100);
        
        for (int i = 0; i < files.length; i++) {
            sb.append(files[i].getAbsolutePath());
            if (i != files.length - 1) {
                sb.append(COLON);
            }
        }
        
        pathField.setText(sb.toString());
        databaseDriver.setPath(pathField.getText());
    }

    /**
     * Checks the current selection for a name change
     * to be propagated back to the tree view.
     */
    private void checkNameUpdate() {
        String oldName = databaseDriver.getName();
        String newName = nameField.getText().trim();
        if (!oldName.equals(newName)) {
            databaseDriver.setName(newName);
            parent.nodeNameValueChanged(databaseDriver);
        }        
    }

    /**
     * Populates the driver object from the field values.
     */
    private void populateDriverObject() {
        // ODBC driver can not be changed
        if (databaseDriver.getId() == JDBCProperties.DEFAULT_ODBC_ID) {
            return;
        }

        databaseDriver.setDescription(descField.getText());
        databaseDriver.setClassName(classField.getText());
        databaseDriver.setURL(driverUrlCombo.getEditor().getItem().toString());
        databaseDriver.setPath(pathField.getText());
        
        DatabaseDefinition database = getSelectedDatabase();
        if (database.getId() > 0) {
            databaseDriver.setDatabaseType(database.getId());
        }
        
        checkNameUpdate();
        
        if (databaseDriver.getId() == 0) {
            databaseDriver.setId(System.currentTimeMillis());
            // need to check exisitng conns with this driver's name
        }

    }
    
    public void setDriver(DatabaseDriver databaseDriver) {
        // store the field values of the current selection
        if (this.databaseDriver != null) {
            populateDriverObject();
        }
        
        try {
            databaseNameCombo.removeItemListener(this);
            this.databaseDriver = databaseDriver;
            nameField.setText(databaseDriver.getName());
            descField.setText(
                    databaseDriver.getDescription().equals("Not Available") ?
                                            "" : databaseDriver.getDescription());
            classField.setText(databaseDriver.getClassName());
            pathField.setText(databaseDriver.getPath());

            int databaseId = databaseDriver.getType();
            DatabaseDefinition database = DatabaseDefinitionCache.
                                                getDatabaseDefinition(databaseId);
            
            if (database != null) {
                resetUrlCombo(database);
                databaseNameCombo.setSelectedItem(database);
            } else {
                urlComboModel.removeAllElements();
                databaseNameCombo.setSelectedIndex(0);
            }

            String url = databaseDriver.getURL();
            if (!MiscUtils.isNull(url)) {
                urlComboModel.insertElementAt(url, 0);
            }
            
            if (urlComboModel.getSize() > 0) {
                driverUrlCombo.setSelectedIndex(0);                
            }

            nameField.requestFocusInWindow();
            nameField.selectAll();
        }
        finally {
            databaseNameCombo.addItemListener(this);
        }
    }

    private void resetUrlCombo(DatabaseDefinition database) {
        urlComboModel.removeAllElements();
        for (int i = 0; i < database.getUrlCount(); i++) {
            urlComboModel.addElement(database.getUrl(i));
        }
    }
    
    public DatabaseDriver getDriver() {
        return databaseDriver;
    }
 
    /** Performs some cleanup and releases resources before being closed. */
    public void cleanup() {
        
    }
    
    /** Refreshes the data and clears the cache */
    public void refresh() {}
    
    /** Returns the print object - if any */
    public Printable getPrintable() {
        return null;
    }
    
    /** Returns the name of this panel */
    public String getLayoutName() {
        return TITLE;
    }

    
}



