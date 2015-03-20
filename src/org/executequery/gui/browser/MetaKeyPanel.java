/*
 * MetaKeyPanel.java
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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import org.executequery.GUIUtilities;

import org.executequery.print.TablePrinter;

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
public class MetaKeyPanel extends BrowserNodeBasePanel {
    
    public static final String NAME = "MetaKeyPanel";
    
    private MetaKeyModel model;
    private JLabel noValuesLabel;
    private ImageIcon[] icons;
    private HashMap cache;
    
    private static String HEADER_PREFIX = "Database Object: ";
    
    /** the browser's control object */
    private BrowserController controller;

    public MetaKeyPanel(BrowserController controller) {
        super("Object Type Name:");
        this.controller = controller;

        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() throws Exception {
        noValuesLabel = new JLabel("No objects of this type are available.",
                                    JLabel.CENTER);
        
        tablePanel.setBorder(BorderFactory.createTitledBorder("Available Objects"));
        
        model = new MetaKeyModel();
        table.setModel(model);
        
        // add the mouse listener
        table.addMouseListener(new MouseHandler());
        
        setHeaderIcon(GUIUtilities.loadIcon("DatabaseObject24.gif"));
        cache = new HashMap();
    }
    
    public String getLayoutName() {
        return NAME;
    }
    
    public void refresh() {
        cache.clear();
    }
    
    public void cleanup() {}
    
    public Printable getPrintable() {
        return new TablePrinter(table, typeField.getText());
    }
    
    public JTable getTable() {
        return table;
    }

    public boolean hasObject(Object object) {
        return cache.containsKey(object);
    }

    public void setValues(String name) {
        setValues(name, (String[])cache.get(name));
    }
    
    public void setValues(String name, String[] values) {
        tablePanel.removeAll();
        typeField.setText(name);
        
        if (values == null || values.length == 0) {
            tablePanel.add(noValuesLabel, getPanelConstraints());
        }
        else {
            model.setValues(values);
            tablePanel.add(scroller, getPanelConstraints());
        }
        
        setHeaderText(HEADER_PREFIX + name);
    }
    
    private class MouseHandler extends MouseAdapter {

        public MouseHandler() {}

        /*
        public void mousePressed(MouseEvent e) {
            mouseClicked(e);
        }
        */

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() < 2) {
                return;
            }

            int mouseX = e.getX();
            int mouseY = e.getY();

            int row = table.rowAtPoint(new Point(mouseX, mouseY));
            Object object = model.getValueAt(row, 0);
            if (object == null) {
                return;
            }

            controller.selectBrowserNode(object.toString());
        }
    }

    private class MetaKeyModel extends AbstractTableModel {
        
        private String[] values;
        private String header = "Object Name";
        
        public MetaKeyModel() {
            values = new String[0];
        }
        
        public void setValues(String[] values) {
            this.values = values;
            fireTableDataChanged();
        }
        
        public int getRowCount() {
            return values.length;
        }
        
        public int getColumnCount() {
            return 1;
        }
        
        public String getColumnName(int col) {
            return header;
        }
        
        public Object getValueAt(int row, int col) {
            return values[row];
        }
        
        public boolean isCellEditable() {
            return false;
        }
        
    }
    
}



