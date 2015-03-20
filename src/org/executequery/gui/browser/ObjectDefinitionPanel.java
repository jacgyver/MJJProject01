/*
 * ObjectDefinitionPanel.java
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.print.Printable;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;

import javax.swing.event.ChangeListener;
import org.executequery.GUIUtilities;
import org.executequery.gui.table.SimpleTableDescriptionPanel;
import org.executequery.print.TablePrinter;
import org.underworldlabs.swing.DisabledField;
import org.underworldlabs.util.MiscUtils;
import org.executequery.gui.forms.AbstractFormObjectViewPanel;
import org.underworldlabs.swing.GUIUtils;
//import org.executequery.print.*;

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
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class ObjectDefinitionPanel extends AbstractFormObjectViewPanel
                                   implements ChangeListener {
    
    public static final String NAME = "ObjectDefinitionPanel";
    
    /** The table data display */
    private TableDataTab tableDataPanel;
    
    /** The tabbed description pane */
    private JTabbedPane tabPane;
    
    /** The currently selected object */
    private DatabaseObject metaObject;
    
    /** Contains the view name */
    private DisabledField tableNameField;
    
    /** Contains the schema name */
    private DisabledField schemaNameField;
    
    /** table description */
    private SimpleTableDescriptionPanel tableDescPanel;
    
    //private MetaDataValues metaData;
    
    private TablePrivilegeTab tablePrivilegePanel;
    
    private JPanel descBottomPanel;
    private JLabel noResultsLabel;
    
    private boolean hasResults;
    
    private ImageIcon[] icons;

    /** loaded meta data object cache */
    private HashMap cache;
    
    /** the browser's control object */
    private BrowserController controller;

    public ObjectDefinitionPanel(BrowserController controller) {
        super();
        this.controller = controller;
        
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    private void jbInit() throws Exception {
        
        noResultsLabel = new JLabel("No information for this object is available.",
                                    JLabel.CENTER);
        
        JPanel descPanel = new JPanel(new GridBagLayout());
        
        tableNameField = new DisabledField();
        schemaNameField = new DisabledField();
        
        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(10,5,5,5);
        gbc.insets = ins;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        descPanel.add(new JLabel("Name:"), gbc);
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descPanel.add(tableNameField, gbc);
        gbc.insets.top = 0;
        gbc.gridy++;
        descPanel.add(schemaNameField, gbc);
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        gbc.gridx = 0;
        gbc.weightx = 0;
        descPanel.add(new JLabel("Schema:"), gbc);
        
        // configure the table column descriptions panel
        descBottomPanel = new JPanel(new BorderLayout());
        descBottomPanel.setBorder(BorderFactory.createTitledBorder("Columns"));
        
        tableDataPanel = new TableDataTab();
        tablePrivilegePanel = new TablePrivilegeTab();
        
        tabPane = new JTabbedPane();
        tabPane.add("Description", descBottomPanel);
        tabPane.add("Privileges", tablePrivilegePanel);
        tabPane.add("Data", tableDataPanel);
        
        // add the tab pane
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.bottom = 5;
        gbc.insets.top = 5;
        gbc.insets.right = 5;
        gbc.insets.left = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        descPanel.add(tabPane, gbc);
        
        tabPane.addChangeListener(this);
        tableDescPanel = new SimpleTableDescriptionPanel();
        
        icons = new ImageIcon[BrowserConstants.META_TYPE_ICONS.length];
        
        for (int i = 0; i < BrowserConstants.META_TYPE_ICONS.length; i++) {
            icons[i] = GUIUtilities.loadIcon(BrowserConstants.META_TYPE_ICONS[i]);
        }
        
        setHeader("Database Object", icons[0]);
        setContentPanel(descPanel);
        cache = new HashMap();
    }
    
    public String getLayoutName() {
        return NAME;
    }
    
    public Printable getPrintable() {
        
        int tabIndex = tabPane.getSelectedIndex();
        
        switch (tabIndex) {
            
            case 0:
                return new TablePrinter(tableDescPanel.getTable(),
                                        "Table: " + metaObject.getName());

            case 1:
                return new TablePrinter(tablePrivilegePanel.getTable(),
                                        "Access rights for table: " + 
                                                    metaObject.getName());
                
            case 2:
                return new TablePrinter(tableDataPanel.getTable(),
                                        "Table Data: " + metaObject.getName());
                
            default:
                return null;
                
        }
        
    }
    
    public void stateChanged(ChangeEvent e) {
        if (tabPane.getSelectedIndex() == 2) {
            tableDataPanel.getTableData(controller.getDatabaseConnection(),
                                        metaObject);
        }
        else if (tabPane.getSelectedIndex() == 1) {
            resetPrivilegePanel();
        }
        else if (tableDataPanel.isExecuting()) {
            tableDataPanel.cancelStatement();
        }
    }

    private void resetPrivilegePanel() {
        TablePrivilege[] privileges = null;
        CacheObject cacheObject = (CacheObject)cache.get(metaObject);
        
        if (!cacheObject.isPrivilegesLoaded()) {
            privileges = controller.getPrivileges(metaObject.getCatalogName(),
                                                  metaObject.getSchemaName(),
                                                  metaObject.getName());
            cacheObject.setTablePrivilege(privileges);
        }
        else {
            privileges = cacheObject.getTablePrivileges();
        }

        tablePrivilegePanel.setValues(privileges);
    }

    /*
    private void resetPrivilegePanel() {
        GUIUtils.startWorker(new Runnable() {
            public void run() {
        
                try {
                    GUIUtilities.showWaitCursor();
                
                
        TablePrivilege[] privileges = null;
        CacheObject cacheObject = (CacheObject)cache.get(metaObject);
        
        if (!cacheObject.isPrivilegesLoaded()) {
            privileges = controller.getPrivileges(metaObject.getCatalogName(),
                                                  metaObject.getSchemaName(),
                                                  metaObject.getName());
            cacheObject.setTablePrivilege(privileges);
        }
        else {
            privileges = cacheObject.getTablePrivileges();
        }

        populatePrivilegeValues(privileges);
        
                }
                finally {
                    GUIUtilities.showNormalCursor();
                }
        
            }
        });
        
        //tablePrivilegePanel.setValues(privileges);
    }

    */
    
    private void populatePrivilegeValues(final TablePrivilege[] privileges) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                tablePrivilegePanel.setValues(privileges);
            }
        });
    }
    
    public void changeTable(DatabaseObject _metaObject) {
        changeTable(_metaObject, false);
    }
    
    public void changeTable(DatabaseObject _metaObject, boolean reload) {
        
        if (metaObject == _metaObject) {
            return;
        }
        
        //Log.debug("EDT: "+SwingUtilities.isEventDispatchThread());
        
        tabPane.removeChangeListener(this);
        tabPane.setSelectedIndex(0);
        
        if (!reload && cache.containsKey(_metaObject)) {
            CacheObject cacheObject = (CacheObject)cache.get(_metaObject);
            changeTable(_metaObject, cacheObject, false);
        }
        else {
            changeTable(_metaObject, new CacheObject(), true);
        }
        
        tabPane.addChangeListener(this);
        
    }
    
    private void changeTable(DatabaseObject _metaObject,
                             CacheObject cacheObject, boolean store) {
        
        if (metaObject == _metaObject)
            return;
        
        hasResults = false;
        metaObject = _metaObject;
        setHeaderText("Database " + MiscUtils.firstLetterToUpper(metaObject.getMetaDataKey()));
        tableNameField.setText(metaObject.getName());
        schemaNameField.setText(metaObject.getSchemaName());
        
        int type = metaObject.getType();
        
        if (type < icons.length) {
            setHeaderIcon(icons[type]);
        }
        else {
            setHeaderIcon(icons[BrowserConstants.TABLE_NODE]);
        }
        
        int tabIndex = tabPane.getSelectedIndex();
        descBottomPanel.removeAll();
        
        ColumnData[] columnData = cacheObject.getColumnData();
        TablePrivilege[] privileges = cacheObject.getTablePrivileges();
        
        if (!cacheObject.isColumnDataLoaded()) {
            columnData = controller.getColumnData(metaObject.getCatalogName(),
                                                  metaObject.getSchemaName(),
                                                  metaObject.getName());
        }
        
        if (columnData == null) {
            descBottomPanel.add(noResultsLabel, BorderLayout.CENTER);
        }
        else {
            hasResults = true;
            tableDescPanel.setTableColumnData(columnData);
            //        tablePrivilegePanel.setValues(privileges);
            descBottomPanel.add(tableDescPanel, BorderLayout.CENTER);
        }
        
        if (store) {
            cache.put(metaObject, cacheObject);
        }
        repaint();
    }
    
    public void refresh() {
        cache.clear();
    }
    
    public void cleanup() {}
    
    public JTable getTable() {
        int tabIndex = tabPane.getSelectedIndex();
        
        if (!hasResults)
            return null;
        
        switch (tabIndex) {
            
            case 0:
                return tableDescPanel.getTable();
                
            case 1:
                return tablePrivilegePanel.getTable();
                
            case 2:
                return null;
                
            default:
                return null;
                
        }
        
    }
    
    private class CacheObject {
        
        private boolean privilegesLoaded;
        private boolean columnDataLoaded;
        private ColumnData[] columnData;
        private TablePrivilege[] privileges;
        
        public CacheObject() {}
        
        public boolean isPrivilegesLoaded() {
            return privilegesLoaded;
        }
        
        public boolean isColumnDataLoaded() {
            return columnDataLoaded;
        }
        
        public void setTablePrivilege(TablePrivilege[] privileges) {
            privilegesLoaded = true;
            this.privileges = privileges;
        }
        
        public TablePrivilege[] getTablePrivileges() {
            return privileges;
        }
        
        public void setColumnData(ColumnData[] columnData) {
            columnDataLoaded = true;
            this.columnData = columnData;
        }
        
        public ColumnData[] getColumnData() {
            return columnData;
        }
        
    } // class CacheObject
    
}



