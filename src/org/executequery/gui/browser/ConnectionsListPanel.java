/*
 * ConnectionsListPanel.java
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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.Printable;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.executequery.ConnectionProperties;
import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.gui.DefaultTable;
import org.executequery.print.TablePrinter;
import org.executequery.gui.forms.AbstractFormObjectViewPanel;

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
public class ConnectionsListPanel extends AbstractFormObjectViewPanel
                                  implements MouseListener,
                                             ActionListener,
                                             ConnectionListener {
    
    public static final String NAME = "ConnectionsListPanel";
    
    /** the table display */
    private JTable table;
    
    /** the table model */
    private ConnectionsTableModel model;
    
    /** the browser's control object */
    private BrowserController controller;

    /** the pop-up menu */
    private PopMenu popupMenu;
    
    /** Creates a new instance of ConnectionsListPanel */
    public ConnectionsListPanel(BrowserController controller) {
        super();
        this.controller = controller;
        init();
    }
    
    private void init() {
        
        model = new ConnectionsTableModel(
                ConnectionProperties.getConnectionsVector());
        table = new DefaultTable(model);
        table.setRowHeight(20);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        
        // add the mouse listener for selection clicks
        table.addMouseListener(this);
        
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(30);
        tcm.getColumn(0).setMaxWidth(30);
        tcm.getColumn(1).setPreferredWidth(135);
        tcm.getColumn(2).setPreferredWidth(60);
        tcm.getColumn(3).setPreferredWidth(60);
        tcm.getColumn(4).setPreferredWidth(60);
        //tcm.getColumn(5).setPreferredWidth(60);
        //tcm.getColumn(5).setMaxWidth(60);
        
        tcm.getColumn(0).setCellRenderer(new ConnectCellRenderer());
        
        // new connection button
        JButton button = new JButton("New Connection");
        button.addActionListener(this);
        
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.add(new JScrollPane(table), getPanelConstraints());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Available Connections"));
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10,10,5,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("User defined database connection parameters."), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets.top = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Select the New Connection button to add a new " +
                             "connection to the system"), gbc);
        gbc.gridx = 1;
        gbc.insets.left = 0;
        gbc.insets.bottom = 0;
        panel.add(button, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets.left = 10;
        gbc.insets.top = 10;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(tablePanel, gbc);
        
        setHeaderText("Database Connections");
        setHeaderIcon(GUIUtilities.loadIcon("DatabaseConnect24.gif"));
        setContentPanel(panel);

        // register with the event listener
        EventMediator.registerListener(EventMediator.CONNECTION_EVENT, this);
    }
    
    public void actionPerformed(ActionEvent e) {
        GUIUtilities.ensureDockedTabVisible(ConnectionsTreePanel.PROPERTY_KEY);
        controller.addNewConnection();
    }
    
    /*
    private void showConnection(DatabaseConnection dc) {
        GUIUtilities.ensureDockedTabVisible(ConnectionsTreePanel.PROPERTY_KEY);
        controller.setSelectedConnection(dc);
    }
    */
    
    /** the selected/hovered connection */
    private DatabaseConnection databaseConnection;
    
    private DatabaseConnection getConnectionAt(Point point) {
        int row = table.rowAtPoint(point);
        if (row == -1) {
            return null;
        }
        return (DatabaseConnection)model.getValueAt(row, 0);
    }
    
    // ----------------------------------
    // MouseListener implementation
    // ----------------------------------
    
    public void mouseClicked(MouseEvent e) {

        // only interested in double clicks
        if (e.getClickCount() < 2) {
            return;
        } 
        
        Point point = new Point(e.getX(), e.getY());
        DatabaseConnection dc = getConnectionAt(point);
        if (dc == null) {
            return;
        }

        int col = table.columnAtPoint(point);
        if (col == 0) {
            controller.connect(dc, true);
            return;
        }
        
        // select the connection in the tree
        if (model.indexOf(dc) < model.getRowCount()) {
            controller.setSelectedConnection(dc);
        }

        databaseConnection = null;        
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            Point point = new Point(e.getX(), e.getY());            
            // get the connection at this point
            databaseConnection = getConnectionAt(point);
            if (databaseConnection != null) {
                if (popupMenu == null) {
                    popupMenu = new PopMenu();
                }
                // remove the current row selection
                table.clearSelection();
                
                // set the popup row as selected
                int row = model.indexOf(databaseConnection);
                table.addRowSelectionInterval(row, row);
                
                // enable/disable items
                popupMenu.setToConnect(!databaseConnection.isConnected());
                popupMenu.show(e.getComponent(), point.x, point.y);
            }
        }

    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    
    /**
     * Indicates a connection has been established.
     * 
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent) {
        if (isVisible()) {
            DatabaseConnection dc = connectionEvent.getSource();
            int index = model.indexOf(dc);
            model.fireTableCellUpdated(index, 0);
        }
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent) {
        if (isVisible()) {
            DatabaseConnection dc = connectionEvent.getSource();
            int index = model.indexOf(dc);
            model.fireTableCellUpdated(index, 0);
        }
    }

    
    public String getLayoutName() {
        return NAME;
    }
    
    public void refresh() {}    
    public void cleanup() {}
    
    public Printable getPrintable() {
        return new TablePrinter(table, "Database Connections", false);
    }

    /** The table's popup menu function */
    private class PopMenu extends JPopupMenu implements ActionListener {
        
        private JMenuItem connect;
        private JMenuItem disconnect;
        private JMenuItem properties;
        
        public PopMenu() {
            connect = new JMenuItem("Connect");
            connect.addActionListener(this);

            disconnect = new JMenuItem("Disconnect");
            disconnect.addActionListener(this);

            properties = new JMenuItem("Properties");
            properties.addActionListener(this);

            add(connect);
            add(disconnect);
            addSeparator();
            add(properties);
        }

        public void setToConnect(boolean canConnect) {
            connect.setEnabled(canConnect);
            disconnect.setEnabled(!canConnect);
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                Object source = e.getSource();
                if (source == connect) {
                    controller.connect(databaseConnection, true);
                }
                else if (source == disconnect) {
                    controller.connect(databaseConnection, true);
                }
                else if (source == properties) {
                    controller.setSelectedConnection(databaseConnection);
                    //showConnection(databaseConnection);
                }
            } finally {
                databaseConnection = null;
            }

        }
    }

    private class ConnectionsTableModel extends AbstractTableModel {
        
        private Vector<DatabaseConnection> values;
        private String[] header = {"", "Connection Name", "Host", 
                                   "Data Source", "User", "Driver"};
        
        public ConnectionsTableModel(Vector<DatabaseConnection> values) {
            this.values = values;
        }
        
        public int indexOf(DatabaseConnection dc) {
            return values.indexOf(dc);
        }
        
        public void setValues(Vector<DatabaseConnection> values) {
            this.values = values;
            fireTableDataChanged();
        }
        
        public int getRowCount() {
            return values.size();
        }
        
        public int getColumnCount() {
            return header.length;
        }
        
        public String getColumnName(int col) {
            return header[col];
        }
        
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return values.elementAt(row);
                case 1:
                    return values.elementAt(row).getName();
                case 2:
                    return values.elementAt(row).getHost();
                case 3:
                    return values.elementAt(row).getSourceName();
                case 4:
                    return values.elementAt(row).getUserName();
                case 5:
                    return values.elementAt(row).getDriverName();
            }
            return values.elementAt(row);
        }
        
        public boolean isCellEditable() {
            return false;
        }
        
    }

    
    private class ConnectCellRenderer extends JLabel 
                                      implements TableCellRenderer {

        // connection icons
        private ImageIcon connectedImage;
        private ImageIcon notConnectedImage;

        public ConnectCellRenderer() {
            connectedImage = GUIUtilities.loadIcon("Connected.gif", true);
            notConnectedImage = GUIUtilities.loadIcon("Disconnected.gif", true);
        }

        public Component getTableCellRendererComponent(JTable table,
                                    Object value, boolean isSelected, boolean hasFocus,
                                    int row, int column) {

            DatabaseConnection dc = (DatabaseConnection)value;

            setHorizontalAlignment(JLabel.CENTER);

            if (dc.isConnected()) {
                setIcon(connectedImage);
                setToolTipText("Connected (double-click icon to disconnect)");
            } else {
                setIcon(notConnectedImage);
                setToolTipText("Disconnected (double-click icon to connect)");
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }	else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }

        public boolean isOpaque() {
            return true;
        }


    }

}



