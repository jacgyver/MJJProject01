/*
 * ConnectionPanel.java
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import org.executequery.ConnectionProperties;
import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.JDBCProperties;
import org.executequery.SystemUtilities;
import org.executequery.ValidationException;
import org.underworldlabs.swing.actions.ActionUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.datasource.ConnectionManager;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.ActionPanel;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.underworldlabs.swing.NumberTextField;
import org.executequery.components.TextFieldPanel;
import org.executequery.gui.DefaultTable;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.util.MiscUtils;

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
 * @date     $Date: 2006/08/11 12:34:50 $
 */
public class ConnectionPanel extends ActionPanel {
                             //implements FocusListener {
                             //implements ConnectionFunction {
    
    // -------------------------------
    // text fields and combos
    
    private JComboBox driverCombo;
    private JCheckBox encryptPwdCheck;
    private JCheckBox savePwdCheck;
    private JTextField nameField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JTextField hostField;
    private NumberTextField portField;
    private JTextField sourceField;
    private JTextField urlField;

    private JLabel statusLabel;
    
    private JComboBox txCombo;
    private JButton txApplyButton;
    
    // -------------------------------

    /** table model for jdbc properties key/values */
    private JdbcPropertiesTableModel model;
    
    /** connect button */
    private JButton connectButton;

    /** disconnect button */
    private JButton disconnectButton;

    /** the saved jdbc drivers */
    private Vector jdbcDrivers;

    /** any advanced property keys/values */
    private String[][] advancedProperties;
    
    /** the tab basic/advanced tab pane */
    private JTabbedPane tabPane;
    
    /** the connection properties displayed */
    private DatabaseConnection databaseConnection;

    /** the selected meta object representing this connection */
    private ConnectionObject metaObject;
    
    /** the browser's control object */
    private BrowserController controller;

    /** Creates a new instance of ConnectionPanel */
    public ConnectionPanel(BrowserController controller) {
        super(new BorderLayout());
        this.controller = controller;
        init();
    }
    
    private void init() {

        // ---------------------------------
        // create the basic props panel
        
        // initialise the fields
        nameField = new JTextField();
        passwordField = new JPasswordField();
        hostField = new JTextField();
        portField = new NumberTextField();
        sourceField = new JTextField();
        userField = new JTextField();
        urlField = new JTextField();
        
        savePwdCheck = ActionUtilities.createCheckBox("Store Password", "setStorePassword");
        encryptPwdCheck = ActionUtilities.createCheckBox("Encrypt Password", "setEncryptPassword");
        
        savePwdCheck.addActionListener(this);
        encryptPwdCheck.addActionListener(this);
        
        // retrieve the drivers
        buildDriversList();
        
        // ---------------------------------
        // add the basic connection fields
        
        TextFieldPanel mainPanel = new TextFieldPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10,10,5,10);
        gbc.gridy = 0;
        gbc.gridx = 0;
        
        statusLabel = new JLabel();
        addLabelFieldPair(mainPanel, "Status:", 
                statusLabel, "Current connection status", gbc);
        
        addLabelFieldPair(mainPanel, "Connection Name:", 
                nameField, "A friendly name for this connection", gbc);

        addLabelFieldPair(mainPanel, "User Name:", 
                userField, "Login user name", gbc);

        addLabelFieldPair(mainPanel, "Password:", 
                passwordField, "Login password", gbc);

        addComponents(mainPanel, 
                      savePwdCheck,
                      "Store the password with the connection information",
                      encryptPwdCheck, 
                      "Encrypt the password when saving", 
                      gbc);
        
        addLabelFieldPair(mainPanel, "Host Name:", 
                hostField, "Server host name or IP address", gbc);

        addLabelFieldPair(mainPanel, "Port:", 
                portField, "Database port number", gbc);

        addLabelFieldPair(mainPanel, "Data Source:", 
                sourceField, "Data source name", gbc);

        addLabelFieldPair(mainPanel, "JDBC URL:", 
                urlField, "The full JDBC URL for this connection (optional)", gbc);

        addLabelFieldPair(mainPanel, "JDBC Driver:", 
                driverCombo, "The JDBC driver for this database", gbc);

        connectButton = ActionUtilities.createButton("Connect", "connect");
        disconnectButton = ActionUtilities.createButton("Disconnect", "disconnect");

        connectButton.setPreferredSize(Constants.FORM_BUTTON_SIZE);
        disconnectButton.setPreferredSize(Constants.FORM_BUTTON_SIZE);

        Insets buttonInsets = new Insets(1,4,1,4);
        connectButton.setMargin(buttonInsets);
        disconnectButton.setMargin(buttonInsets);
        
        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
        
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.insets.top = 0;
        gbc.insets.left = 0;
        gbc.insets.right = 10;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(connectButton, gbc);
        gbc.gridx++;
        gbc.weightx = 0;
        mainPanel.add(disconnectButton, gbc);
        
        // ---------------------------------
        // create the advanced panel
        
        model = new JdbcPropertiesTableModel();
        JTable table = new DefaultTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scroller = new JScrollPane(table);
        //scroller.setPreferredSize(new Dimension(200, 300));
        
        // advanced jdbc properties
        JPanel advPropsPanel = new JPanel(new GridBagLayout());
        advPropsPanel.setBorder(BorderFactory.createTitledBorder("JDBC Properties"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets.top = 0;
        gbc.insets.left = 10;
        gbc.insets.right = 10;        
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        advPropsPanel.add(
                new JLabel("Enter any key/value pair properties for this connection"), gbc);
        gbc.gridy++;
        advPropsPanel.add(
                new JLabel("Refer to the relevant JDBC driver documentation for possible entries"), gbc);
        gbc.gridy++;
        gbc.insets.bottom = 10;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        advPropsPanel.add(scroller, gbc);        
        
        // transaction isolation
        txApplyButton = ActionUtilities.createButton("Apply", "transactionLevelChanged");
        txApplyButton.setToolTipText("Apply this level to all open connections of this type");
        txApplyButton.setEnabled(false);
        txApplyButton.addActionListener(this);

        // add a dummy select value to the tx levels
        String[] txLevels = new String[Constants.TRANSACTION_LEVELS.length + 1];
        txLevels[0] = "Database Default";
        for (int i = 1; i < txLevels.length; i++) {
            txLevels[i] = Constants.TRANSACTION_LEVELS[i - 1];
        }
        txCombo = new JComboBox(txLevels);

        JPanel advTxPanel = new JPanel(new GridBagLayout());
        advTxPanel.setBorder(BorderFactory.createTitledBorder("Transaction Isolation"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets.top = 0;
        gbc.insets.left = 10;
        gbc.insets.right = 10;
        gbc.insets.bottom = 5;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        advTxPanel.add(
                new JLabel("Default transaction isolation level for this connection"), gbc);
        gbc.gridy++;
        gbc.insets.bottom = 10;
        advTxPanel.add(
                new JLabel("Note: the selected isolation level " +
                           "will apply to ALL open connections of this type."), gbc);        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 0;
        gbc.insets.left = 10;
        gbc.weightx = 0;
        advTxPanel.add(new JLabel("Isolation Level:"), gbc);
        gbc.gridx = 1;
        gbc.insets.left = 5;
        gbc.weightx = 1.0;
        gbc.insets.right = 5;
        gbc.fill = GridBagConstraints.BOTH;
        advTxPanel.add(txCombo, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets.left = 0;
        gbc.insets.right = 10;
        advTxPanel.add(txApplyButton, gbc);
        
        JPanel advancedPanel = new JPanel(new BorderLayout());
        advancedPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        advancedPanel.add(advPropsPanel, BorderLayout.CENTER);
        advancedPanel.add(advTxPanel, BorderLayout.SOUTH);
        
        tabPane = new JTabbedPane(JTabbedPane.BOTTOM);
        tabPane.addTab("Basic", mainPanel);
        tabPane.addTab("Advanced", advancedPanel);

        add(tabPane, BorderLayout.CENTER);
        
    }
    
    /**
     * Retrieves and populates the drivers list.
     */
    protected void buildDriversList() {
        jdbcDrivers = JDBCProperties.getDriversVector();
        int size = jdbcDrivers.size();
        String[] driverNames = new String[size + 1];
        driverNames[0] = "Select...";
        for (int i = 0; i < size; i++) {
            driverNames[i+1] = jdbcDrivers.elementAt(i).toString();
        }
        
        if (driverCombo == null) {
            DynamicComboBoxModel model = new DynamicComboBoxModel();
            model.setElements(driverNames);
            driverCombo = new JComboBox(model);
        } else {
            DynamicComboBoxModel model = (DynamicComboBoxModel)driverCombo.getModel();
            model.setElements(driverNames);
            driverCombo.setModel(model);
            selectDriver();
        }
        
    }

    /**
     * Action performed upon selection of the Apply button
     * when selecting a tx isolation level.
     */
    public void transactionLevelChanged() {
        try {
            applyTransactionLevel(true);
            if (databaseConnection.getTransactionIsolation() == -1) {
                return;
            }
            
            String txLevel = txCombo.getSelectedItem().toString();
            GUIUtilities.displayInformationMessage(
                    "The transaction isolation level " + txLevel + 
                    " was applied successfully.");
        }
        catch (DataSourceException e) {
            GUIUtilities.displayWarningMessage(
                    "The selected isolation level could not be applied.\n" +
                    "The JDBC driver returned:\n\n" +
                    e.getMessage() + "\n\n");
        }
        catch (Exception e) {}
        
    }
    
    /**
     * Applies the tx level on open connections of the type selected.
     */
    private void applyTransactionLevel(boolean reloadProperties) throws DataSourceException {
        // set the tx level from the combo selection
        getTransactionIsolationLevel();        
        int isolationLevel = databaseConnection.getTransactionIsolation();
        // apply to open connections
        ConnectionManager.
                setTransactionIsolationLevel(databaseConnection, isolationLevel);
        
        if (reloadProperties) {
            controller.updateDatabaseProperties();
        }
        
    }
    
    /**
     * Acion implementation on selection of the Connect button.
     */
    public void connect() {
        // ----------------------------
        // some validation
        
        // make sure a name has been entered
        if (nameField.getText().trim().length() == 0) {
            GUIUtilities.displayErrorMessage("You must enter a name for this connection");
            return;
        }

        if (ConnectionProperties.nameExists(databaseConnection, nameField.getText())) {
            GUIUtilities.displayErrorMessage("The name entered for this connection already exists");
            return;
        }
        
        // check a driver is selected
        if (driverCombo.getSelectedIndex() == 0) {
            GUIUtilities.displayErrorMessage("You must select a driver");
            return;
        }

        // check if we have a url - if not check the port is valid
        if (urlField.getText().trim().length() == 0) {
            if (portField.getText().trim().length() > 0) {
                char[] portChars = portField.getText().toCharArray();
                for (int i = 0; i < portChars.length; i++) {                    
                    if (!Character.isDigit(portChars[i])) {
                        GUIUtilities.displayErrorMessage("Invalid port number");
                        return;
                    }
                }
            }
        }

        // otherwise - good to proceed
        
        // populate the object with field values
        populateConnectionObject();
        
        try {
            // connect
            GUIUtilities.showWaitCursor();
            boolean connected = SystemUtilities.connect(databaseConnection);
            if (connected) {
                // apply the tx level if supplied
                try {
                    applyTransactionLevel(false);
                } catch (DataSourceException e) {
                    GUIUtilities.displayWarningMessage(
                            "The selected isolation level could not be applied.\n" +
                            "The JDBC driver returned:\n\n" +
                            e.getMessage() + "\n\n");
                }
            }
        }
        catch (DataSourceException e) {
            StringBuffer sb = new StringBuffer();
            sb.append("The connection to the database could not be established.");
            sb.append("\nPlease ensure all required fields have been entered ");
            sb.append("correctly and try again.\n\nThe system returned:\n");
            sb.append(e.getExtendedMessage());
            GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
        }
        finally {
            GUIUtilities.showNormalCursor();
        }
    }

    /**
     * Informed by a tree selection, this readies the form for
     * a new connection object and value change.
     */
    protected void selectionChanging() {}

    /**
     * Indicates the panel is being de-selected in the pane
     */
    public boolean tabViewDeselected() {
        populateConnectionObject();
        save();
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        enableFields(databaseConnection.isConnected());
        return true;
    }

    /**
     * Checks the current selection for a name change
     * to be propagated back to the tree view.
     */
    private void checkNameUpdate() {
        String oldName = databaseConnection.getName();
        String newName = nameField.getText().trim();
        if (!oldName.equals(newName)) {
            databaseConnection.setName(newName);
            controller.nodeNameValueChanged(metaObject);
        }        
    }
    

    /**
     * Acion implementation on selection of the Disconnect button.
     */
    public void disconnect() {
        try {
            SystemUtilities.disconnect(databaseConnection);
        }
        catch (DataSourceException e) {
            GUIUtilities.displayErrorMessage(
                    "Error disconnecting from data source:\n" + e.getMessage());
        }
    }
    
    /**
     * Retrieves the values from the jdbc properties table
     * and stores them within the current database connection.
     */
    private void storeJdbcProperties() {
        Properties properties = databaseConnection.getJdbcProperties();
        if (properties == null) {
            properties = new Properties();
        } else {
            properties.clear();
        }

        for (int i = 0; i < advancedProperties.length; i++) {
            String key = advancedProperties[i][0];
            String value = advancedProperties[i][1];
            if (!MiscUtils.isNull(key) && !MiscUtils.isNull(value)) {
                properties.setProperty(key, value);
            }
        }
        databaseConnection.setJdbcProperties(properties);
    }

    /**
     * Sets the values of the current database connection
     * within the jdbc properties table.
     */
    private void setJdbcProperties() {
        advancedProperties = new String[20][2];
        Properties properties = databaseConnection.getJdbcProperties();
        if (properties == null || properties.size() == 0) {
            model.fireTableDataChanged();
            return;
        }

        int count = 0;
        for (Enumeration i = properties.propertyNames(); i.hasMoreElements();) {
            String name = (String)i.nextElement();
            if (!name.equalsIgnoreCase("password")) {
                advancedProperties[count][0] = name;
                advancedProperties[count][1] = properties.getProperty(name);
                count++;
            }
        }
        model.fireTableDataChanged();
    }
    
    /**
     * Indicates a connection has been established.
     * 
     * @param the connection properties object
     */
    public void connected(DatabaseConnection databaseConnection) {
        populateConnectionFields(databaseConnection);
        save();
    }

    /**
     * Saves the connection info to file.
     */
    protected boolean saveConnections() {
        try {
            int saved = ConnectionProperties.saveConnections();
        }
        catch (ValidationException e) {
            GUIUtilities.displayErrorMessage(e.getMessage());
            return false;
        }
        return true;
        /*
        if (saved == 0) {
            GUIUtilities.displayErrorMessage("You must enter a name for this connection");
        }
         */        
    }
    
    /**
     * Indicates a connection has been closed.
     * 
     * @param the connection properties object
     */
    public void disconnected(DatabaseConnection databaseConnection) {
        enableFields(false);
    }

    /** 
     * Enables/disables fields as specified.
     */
    private void enableFields(boolean enable) {
        txApplyButton.setEnabled(enable);
        connectButton.setEnabled(!enable);
        disconnectButton.setEnabled(enable);

        if (enable) {
            int count = SystemUtilities.getOpenConnectionCount(databaseConnection);
            statusLabel.setText("Connected [ " + count +
                    (count > 1 ? " connections open ]" : " connection open ]") );
        } else {
            statusLabel.setText("Not Connected");
        }
        paintStatusLabel();
        setEncryptPassword();
    }
    
    /**
     * Changes the state of the save and encrypt password
     * check boxes depending on the whether the encrypt
     * check box is selected.
     */
    public void setEncryptPassword() {
        boolean encrypt = encryptPwdCheck.isSelected();
        if (encrypt && !savePwdCheck.isSelected()) {
            savePwdCheck.setSelected(encrypt);
        }
    }

    /**
     * Changes the state of the encrypt password check
     * box depending on the whether the save password
     * check box is selected.
     */
    public void setStorePassword() {
        boolean store = savePwdCheck.isSelected();
        encryptPwdCheck.setEnabled(store);
    }
    
    /**
     * Sets the values for the tx level on the connection object
     * based on the tx level in the tx combo.
     */
    private void getTransactionIsolationLevel() {
        int index = txCombo.getSelectedIndex();
        if (index == 0) {
            databaseConnection.setTransactionIsolation(-1);
            return;
        }

        int isolationLevel = -1;
        switch (index) {
            case 1:
                isolationLevel = Connection.TRANSACTION_NONE;
                break;
            case 2:
                isolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED;
                break;
            case 3:
                isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
                break;
            case 4:
                isolationLevel = Connection.TRANSACTION_REPEATABLE_READ;
                break;
            case 5:
                isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
                break;
        }        
        databaseConnection.setTransactionIsolation(isolationLevel);
    }

    /**
     * Sets the values for the tx level on the tx combo
     * based on the tx level in the connection object.
     */
    private void setTransactionIsolationLevel() {
        int index = 0;
        int isolationLevel = databaseConnection.getTransactionIsolation();
        switch (isolationLevel) {
            case Connection.TRANSACTION_NONE:
                index = 1;
                break;
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                index = 2;
                break;
            case Connection.TRANSACTION_READ_COMMITTED:
                index = 3;
                break;
            case Connection.TRANSACTION_REPEATABLE_READ:
                index = 4;
                break;
            case Connection.TRANSACTION_SERIALIZABLE:
                index = 5;
                break;
        }
        txCombo.setSelectedIndex(index);
    }
    
    /**
     * Selects the driver for the current connection.
     */
    private void selectDriver() {
        if (databaseConnection == null) {
            return;
        }

        if (databaseConnection.getDriverId() == 0) {
            driverCombo.setSelectedIndex(0);
        } else {
            long driverId = databaseConnection.getDriverId();
            if (driverId != 0) {
                DatabaseDriver driver = JDBCProperties.getDatabaseDriver(driverId);
                if (driver != null) {
                    driverCombo.setSelectedItem(driver.getName());
                }
            }
        }
    }
    
    /**
     * Populates the values of the fields with the values of
     * the specified connection.
     */
    private void populateConnectionFields(DatabaseConnection databaseConnection) {
        // assign as the current connection
        this.databaseConnection = databaseConnection;

        // rebuild the driver list
        buildDriversList();
        
        // populate the field values/selections
        savePwdCheck.setSelected(databaseConnection.isPasswordStored());
        encryptPwdCheck.setSelected(databaseConnection.isPasswordEncrypted());
        nameField.setText(databaseConnection.getName());
        userField.setText(databaseConnection.getUserName());
        passwordField.setText(databaseConnection.getUnencryptedPassword());
        hostField.setText(databaseConnection.getHost());
        portField.setText(databaseConnection.getPort());
        sourceField.setText(databaseConnection.getSourceName());
        urlField.setText(databaseConnection.getURL());

        // set the correct driver selected
        selectDriver();

        // set the jdbc properties
        setJdbcProperties();
        
        // the tx level
        setTransactionIsolationLevel();
        
        // enable/disable fields
        enableFields(databaseConnection.isConnected());
    }
    
    /** Indicates whether a new connection is about to be set */
    private boolean changePending;
    
    /**
     * Populates the values of the selected connection
     * properties bject with the field values.
     */
    private void populateConnectionObject() {
        databaseConnection.setPasswordStored(savePwdCheck.isSelected());
        databaseConnection.setPasswordEncrypted(encryptPwdCheck.isSelected());
        databaseConnection.setUserName(userField.getText());
        databaseConnection.setPassword(
                MiscUtils.charsToString(passwordField.getPassword()));
        databaseConnection.setHost(hostField.getText());
        databaseConnection.setPort(portField.getText());
        databaseConnection.setSourceName(sourceField.getText());
        databaseConnection.setURL(urlField.getText());

        // jdbc driver selection
        int driverIndex = driverCombo.getSelectedIndex();
        if (driverIndex > 0) {
            DatabaseDriver driver = (DatabaseDriver)
                                jdbcDrivers.elementAt(driverIndex - 1);
            databaseConnection.setJDBCDriver(driver);
            databaseConnection.setDriverName(driver.getName());
            databaseConnection.setDriverId(driver.getId());
            databaseConnection.setDatabaseType(Integer.toString(driver.getType()));
        }
        else {
            databaseConnection.setDriverId(0);
            databaseConnection.setJDBCDriver(null);
            databaseConnection.setDriverName(null);
            databaseConnection.setDatabaseType(null);
        }
        
        // retrieve the jdbc properties
        storeJdbcProperties();

        // set the tx level on the connection props object
        getTransactionIsolationLevel();
        
        // check if the name has to update the tree display
        checkNameUpdate();
    }
    
    /**
     * Sets the connection fields on this panel to the
     * values as held within the specified connection
     * properties object.
     *
     * @param the connection to set the fields to
     */
    public void setConnectionValue(ConnectionObject metaObject) {
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(false);

        changePending = true;

        if (databaseConnection != null) {
            populateConnectionObject();
        }

        this.metaObject = metaObject;
        populateConnectionFields(metaObject.getDatabaseConnection());

        // set the focus field
        nameField.requestFocusInWindow();
        nameField.selectAll();

        // queue a save
        save();

        changePending = false;
    }
    
    private void save() {
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                saveConnections();
            }
        });
    }
    
    /**
     * Forces a repaint using paintImmediately(...) on the
     * connection status label.
     */
    private void paintStatusLabel() {
        Runnable update = new Runnable() {
            public void run() {
                Dimension dim = statusLabel.getSize();
                statusLabel.paintImmediately(0, 0, dim.width, dim.height);
            }
        };
        SwingUtilities.invokeLater(update);
    }
    
    private class JdbcPropertiesTableModel extends AbstractTableModel {
        
        protected String[] header = {"Name", "Value"};
        
        public JdbcPropertiesTableModel() {
            advancedProperties = new String[20][2];
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public int getRowCount() {
            return advancedProperties.length;
        }
        
        public Object getValueAt(int row, int col) {
            return advancedProperties[row][col];
        }
        
        public void setValueAt(Object value, int row, int col) {
            advancedProperties[row][col] = (String)value;
            fireTableRowsUpdated(row, row);
        }
        
        public boolean isCellEditable(int row, int col) {
            return true;
        }
        
        public String getColumnName(int col) {
            return header[col];
        }
        
        public Class getColumnClass(int col) {
            return String.class;
        }
        
    } // AdvConnTableModel

    private void addComponents(JPanel panel, 
                               JComponent field1, String toolTip1,
                               JComponent field2, String toolTip2,
                               GridBagConstraints gbc) {
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.insets.top = 0;
        gbc.insets.left = 10;
        gbc.weightx = 0;
        panel.add(field1, gbc);
        field1.setToolTipText(toolTip1);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 1;
        gbc.insets.left = 10;
        gbc.weightx = 1.0;
        panel.add(field2, gbc);
        field2.setToolTipText(toolTip2);
    }

    private void addComponent(JPanel panel, JComponent field, String toolTip,
                              GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets.top = 0;
        gbc.insets.left = 10;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        panel.add(field, gbc);

        if (toolTip != null) {
            field.setToolTipText(toolTip);
        }
    }

    private void addLabelFieldPair(JPanel panel, String label, 
                                        JComponent field, String toolTip,
                                        GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;

        if (panel.getComponentCount() > 0) {
            gbc.insets.top = 0;
        } else {
            gbc.insets.top = 10;
        }

        gbc.insets.left = 10;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 1;
        gbc.insets.left = 5;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        
        if (toolTip != null) {
            field.setToolTipText(toolTip);
        }
    }

}



