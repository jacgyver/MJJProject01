/*
 * ConnectionsTreePanel.java
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.executequery.ConnectionProperties;
import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.underworldlabs.swing.tree.DynamicTree;
import org.underworldlabs.swing.toolbar.PanelToolBar;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.MetaDataValues;
import org.executequery.gui.AbstractDockedTabActionPanel;
import org.executequery.components.table.BrowserTreeCellRenderer;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.importexport.ImportExportDelimitedPanel;
import org.executequery.gui.importexport.ImportExportExcelPanel;
import org.executequery.gui.importexport.ImportExportProcess;
import org.executequery.gui.importexport.ImportExportXMLPanel;
import org.executequery.util.Log;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.underworldlabs.swing.util.IconUtilities;
import org.underworldlabs.util.MiscUtils;
import org.underworldlabs.swing.util.SwingWorker;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.26 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class ConnectionsTreePanel extends AbstractDockedTabActionPanel
                                  implements ConnectionListener,
                                             TreeExpansionListener,
                                             TreeSelectionListener {
    
    /** the title for this panel */
    public static final String TITLE = "Connections";
    
    /** the tree object */
    private DynamicTree tree;
    
    /** saved connections */
    private Vector<DatabaseConnection> connections;
    
    /** Worker for meta data retrieval */
    private SwingWorker worker;
    
    /** the browser's control object */
    private BrowserController controller;
    
    /** the old selected browser node */
    private TreePath oldSelectionPath;
    
    /** the tree popup menu */
    private PopMenu popupMenu;
    
    /** indicates whether to reselect the connection 
     * properties node following a disconnection event. */
    private boolean rootSelectOnDisconnect;
    
    // -------------------------------------
    // tool bar buttons

    /** move connection up button */
    private JButton upButton;
    
    /** move connection down button */
    private JButton downButton;

    /** the reload node button */
    private JButton reloadButton;
    
    /** new connection button */
    private JButton newConnectionButton;

    /** delete connection button */
    private JButton deleteConnectionButton;

    // -------------------------------------
    
    /** Creates a new instance of ConnectionsPanel */
    public ConnectionsTreePanel() {
        super(new BorderLayout());
        init();
    }
    
    private void init() {
        // default to not select the root node after a disconnection
        rootSelectOnDisconnect = false;

        // initialise the controller
        controller = new BrowserController(this);
        
        // setup the root node - simple label only
        DatabaseObject rootObject = 
                new DatabaseObject(BrowserConstants.ROOT_NODE,
                                       "Database Connections");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootObject);

        // loop through available connections and add as leaf objects
        connections = ConnectionProperties.getConnectionsVector();
        for (int i = 0, k = connections.size(); i < k; i++) {
            DatabaseConnection dc = connections.elementAt(i);
            ConnectionObject object = new ConnectionObject(dc);
            BrowserTreeNode node = new BrowserTreeNode(object, true);
            root.add(node);
        }
        
        // load the tree and panel icons
        Map icons = new HashMap();        
        for (int i = 0; i < BrowserConstants.NODE_ICONS.length; i++) {
            icons.put(BrowserConstants.NODE_ICONS[i],
                GUIUtilities.loadIcon(BrowserConstants.NODE_ICONS[i], true));
        }
        icons.put(BrowserConstants.DATABASE_OBJECT_IMAGE,
            GUIUtilities.loadIcon(BrowserConstants.DATABASE_OBJECT_IMAGE, true));

        tree = new DynamicTree(root);
        tree.setCellRenderer(new BrowserTreeCellRenderer(icons));
        tree.addTreeSelectionListener(this);
        tree.addTreeExpansionListener(this);
        tree.addMouseListener(new MouseHandler());

        // ---------------------------------------
        // the tool bar        
        PanelToolBar tools = new PanelToolBar();
        newConnectionButton = tools.addButton(
                this, "newConnection", 
                GUIUtilities.getAbsoluteIconPath("NewConnection16.gif"), 
                "New connection");
        deleteConnectionButton = tools.addButton(
                this, "deleteConnection", 
                GUIUtilities.getAbsoluteIconPath("Delete16.gif"),
                "Remove connection");
        upButton = tools.addButton(
                this, "moveConnectionUp", 
                GUIUtilities.getAbsoluteIconPath("Up16.gif"),
                "Move up");
        downButton = tools.addButton(
                this, "moveConnectionDown", 
                GUIUtilities.getAbsoluteIconPath("Down16.gif"), 
                "Move down");

        reloadButton = tools.addButton(
                this, "reloadSelection", 
                GUIUtilities.getAbsoluteIconPath("Reload16.gif"), 
                "Reload the currently selected node");

        // add the tools and tree to the panel
        add(tools, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);

        // register with the event listener
        EventMediator.registerListener(EventMediator.CONNECTION_EVENT, this);
        
        enableButtons(false);
        tree.setSelectionRow(0);
    }
    
    private void enableButtons(final boolean enable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                upButton.setEnabled(enable);
                downButton.setEnabled(enable);
                reloadButton.setEnabled(!enable);
                deleteConnectionButton.setEnabled(enable);                
            }
        });
    }
    
    /**
     * Moves the selected connection (host node) up in the list.
     */
    public void moveConnectionUp() {
        tree.moveSelectionUp();
        // adjust the position of the connection
        Object object = tree.getLastPathComponent();
        moveNode((BrowserTreeNode)object, DynamicTree.MOVE_UP);
    }

    /** 
     * Sets the selected connection tree node to the 
     * specified database connection.
     *
     * @param dc - the database connection to select
     * @param reload - if true, ensures the view panel is set to 
     *        the connection properties
     */
    public void setSelectedConnection(DatabaseConnection dc, boolean reload) {
        try {
            reloadView = true;
            setSelectedConnection(dc);
        }
        finally {
            reloadView = false;
        }
    }
    
    /** 
     * Sets the selected connection tree node to the 
     * specified database connection.
     *
     * @param dc - the database connection to select
     */
    public void setSelectedConnection(DatabaseConnection dc) {
        DefaultMutableTreeNode node = null;
        
        // retrieve the root node and loop through
        DefaultMutableTreeNode root = tree.getRootNode();
        for (Enumeration i = root.children(); i.hasMoreElements();) {
            DefaultMutableTreeNode _node = (DefaultMutableTreeNode)i.nextElement();
            Object userObject = _node.getUserObject();

            // make sure its a connection object
            if (userObject instanceof ConnectionObject) {
                ConnectionObject object = (ConnectionObject)userObject;
                if (object.getDatabaseConnection() == dc) {
                    node = _node;
                    break;
                }
            }

        }
        
        // select the node path
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            tree.scrollPathToVisible(path);
            tree.setSelectionPath(path);

            if (reloadView) {
                Object object = tree.getLastPathComponent();
                if (object instanceof BrowserTreeNode) {
                    controller.valueChanged(
                            getSelectedMetaObject(), (BrowserTreeNode)object, false);
                }
            }

        }
    }

    /**
     * Deletes the selected connection (host node) from the list.
     */
    public void deleteConnection() {
        deleteConnection(null);
    }

    /**
     * Deletes the specified connection (host node) from the list.
     *
     * @param the node representing the connection to be removed
     */
    public void deleteConnection(BrowserTreeNode node) {
        boolean isSelectedNode = false;
        if (node == null) {
            Object object = tree.getLastPathComponent();
            node = (BrowserTreeNode)object;
            isSelectedNode = true;
        }
        else {
            if (tree.getLastPathComponent() == node) {
                isSelectedNode = true;
            }
        }

        int yesNo = GUIUtilities.displayConfirmCancelDialog(
                        "Are you sure you want to delete the connection " +
                        node + " ?");

        if (yesNo != JOptionPane.YES_OPTION) {
            return;
        }

        ConnectionObject metaObject = (ConnectionObject)node.getDatabaseUserObject();
        DatabaseConnection dc = metaObject.getDatabaseConnection();

        // the next selection index will be the index of 
        // the one being removed - (index - 1)
        int index = connections.indexOf(dc);
        if (index == -1) {
            return;
        }
        
        // remove from the connections
        connections.remove(index);
        
        if (index > connections.size() - 1) {
            index = connections.size() - 1;
        }

        if (isSelectedNode) {
            String prefix = connections.get(index).getName();
            tree.removeNode(node, prefix);
        }
        else {
            tree.removeNode(node);
        }

        // save the connections
        controller.saveConnections();
    }

    /**
     * Returns the database connection at the specified point.
     *
     * @return the connection properties object
     */
    protected DatabaseConnection getConnectionAt(Point point) {
        TreePath path = tree.getPathForLocation(point.x, point.y);
        return getConnectionAt(tree.getPathForLocation(point.x, point.y));
    }

    /**
     * Returns the database connection associated with the specified path.
     *
     * @return the connection properties object
     */
    protected DatabaseConnection getConnectionAt(TreePath path) {
        if (path != null) {
            Object object = path.getLastPathComponent();
            if (object instanceof BrowserTreeNode) {
                return getDatabaseConnection((BrowserTreeNode)object);
            }
        }
        return null;
    }

    /**
     * Removes the tree listener.
     */
    protected void removeTreeListener() {
        tree.removeTreeSelectionListener(this);
    }

    /**
     * Adds the tree listener.
     */
    protected void addTreeListener() {
        tree.addTreeSelectionListener(this);
    }

    /**
     * Selects the specified node.
     */
    protected void setNodeSelected(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        tree.setSelectionPath(path);
    }

    
    /**
     * Removes the selected tree node (database object) from the tree.
     */
    public void removeSelectedNode() {
        // remove the listener
        tree.removeTreeSelectionListener(this);
        
        // store the current row
        int row = tree.getSelectionRows()[0];
        
        // retrieve the current selection node
        BrowserTreeNode node = (BrowserTreeNode)tree.getLastPathComponent();
        tree.removeNode(node);
        
        // add listener and select row above
        tree.addTreeSelectionListener(this);
        row = (row == 0 ? 1 : row - 1);
        tree.setSelectionRow(row);
    }
    
    /**
     * Creates a new connection and adds it to the bottom 
     * of the list.
     */
    public void newConnection() {
        newConnection(null);
    }

    /**
     * Creates a new connection based on the specified connection.
     *
     * @param dc - the connection the new one is to be based on
     */
    public void newConnection(DatabaseConnection dc) {
        if (dc == null) {
            String name = buildConnectionName("New Connection");
            dc = new DatabaseConnection(name);
        }

        ConnectionObject metaObject = new ConnectionObject(dc);
        connections.add(dc);
        BrowserTreeNode node = new BrowserTreeNode(metaObject, true);
        tree.addToRoot(node);
    }
    
    /**
     * Moves the selected connection (host node) down in the list.
     */
    public void moveConnectionDown() {
        tree.moveSelectionDown();
        // adjust the position of the connection
        Object object = tree.getLastPathComponent();
        moveNode((BrowserTreeNode)object, DynamicTree.MOVE_DOWN);
    }

    private void moveNode(BrowserTreeNode node, int direction) {
        ConnectionObject metaObject = (ConnectionObject)node.getDatabaseUserObject();
        DatabaseConnection dc = metaObject.getDatabaseConnection();

        int currentIndex = connections.indexOf(dc);
        if (currentIndex == 0 && direction == DynamicTree.MOVE_UP) {
            return;
        }
        
        int newIndex = -1;
        if (direction == DynamicTree.MOVE_UP) {
            newIndex = currentIndex - 1;
        } else {
            newIndex = currentIndex + 1;
            if (newIndex > (connections.size() - 1)) {
                return;
            }
        }

        connections.remove(currentIndex);
        connections.insertElementAt(dc, newIndex);

        // save the connections
        //viewPanel.saveConnections();
    }
    
    /**
     * Indicates that a node name has changed and fires a call
     * to repaint the tree display.
     */
    protected void nodeNameValueChanged(ConnectionObject metaObject) {
        TreeNode node = tree.getNodeFromRoot(metaObject);
        if (node != null) {
            tree.nodeChanged(node);
        }
    }
    
    /**
     * Ensures we have a browser panel and that it is visible.
     */
    private void checkBrowserPanel() {
        controller.checkBrowserPanel();
    }

    /**
     * Override min size for split pane insertion.
     *
     * @return the enforced minimum size
     */
    /*
    public Dimension getMinimumSize() {
        return new Dimension(250, 80);
    }
    */

    /**
     * Returns the currently selected node's user object where the
     * node is a BrowserTreeNode and the user object is a DatabaseObject.
     * If the above is not met, null is returned.
     *
     * @return the user object of the selected node where the
     *         user object is a DatabaseObject
     */
    protected BrowserTreeNode getSelectedBrowserNode() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return null;
        }

        // make sure we have a BrowserTreeNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return null;
        }

        return (BrowserTreeNode)object;
    }

    /**
     * Returns the whether the currently selected node's user object 
     * is a parent type where the node is a BrowserTreeNode and the 
     * user object is a DatabaseObject. If the above is not met, false is 
     * returned, otherwise the object is evaluated.
     *
     * @return true | false
     */
    protected boolean isTypeParentSelected() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return false;
        }

        // make sure we have a BrowserTreeNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return false;
        }
        
        // return the parent connection meta object
        return ((BrowserTreeNode)object).isTypeParent();
    }

    /**
     * Returns the currently selected node's user object where the
     * node is a BrowserTreeNode and the user object is a DatabaseObject.
     * If the above is not met, null is returned.
     *
     * @return the user object of the selected node where the
     *         user object is a DatabaseObject
     */
    protected DatabaseObject getSelectedDatabaseObject() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return null;
        }

        // make sure we have a BrowserTreeNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return null;
        }

        // return the parent connection meta object
        BrowserTreeNode node = (BrowserTreeNode)object;
        return node.getDatabaseUserObject();
    }
    
    /**
     * Returns the selected meta object host node.
     *
     * @return the selected host node meta object
     */
    protected ConnectionObject getSelectedMetaObject() {
        // if path is null return null
        if (tree.isSelectionEmpty()) {
            return null;
        }

        // make sure we have a BrowserTreeNode
        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return null;
        }

        // return the parent connection meta object
        BrowserTreeNode node = (BrowserTreeNode)object;
        BrowserTreeNode parent = getParentNode(node);
        return ((ConnectionObject)parent.getDatabaseUserObject());
    }
    
    /**
     * Returns the selected database connection.
     *
     * @return the selected connection properties object
     */
    public DatabaseConnection getSelectedDatabaseConnection() {
        ConnectionObject object = getSelectedMetaObject();
        if (object != null) {
            return object.getDatabaseConnection();
        }
        return null;
    }
    
    // ------------------------------------------
    // ConnectionListner implementation
    // ------------------------------------------
    
    /**
     * Indicates a connection has been established.
     * 
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent) {
        DatabaseConnection dc = connectionEvent.getSource();
        BrowserTreeNode node = getParentConnectionNode(dc);

        ConnectionObject metaObject = 
                (ConnectionObject)node.getDatabaseUserObject();
        TreePath path = new TreePath(node.getPath());
        TreeExpansionEvent _e = new TreeExpansionEvent(tree, path);
        treeExpanded(_e);
        tree.nodeStructureChanged(node);
    }

    /**
     * Indicates a connection has been closed.
     * 
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent) {
        DatabaseConnection dc = connectionEvent.getSource();
        BrowserTreeNode node = getParentConnectionNode(dc);

        node.setExpanded(false);
        node.removeAllChildren();
        tree.nodeStructureChanged(node);
        
        // check if we select the root node
        if (rootSelectOnDisconnect) {
            tree.setSelectionRow(0);
        }
        // otherwise select the host node
        else {
            tree.setSelectionPath(new TreePath(node.getPath()));
        }

    }

    // ------------------------------------------

    /**
     * Returns the previosuly selected path before the current
     * selection.
     *
     * @return the previous path
     */
    protected TreePath getOldSelectionPath() {
        return oldSelectionPath;
    }

    /**
     * Returns the previously selected browse node before the 
     * current selection.
     *
     * @return the previous node selection
     */
    protected BrowserTreeNode getOldBrowserNodeSelection() {
        Object object = getOldSelectionPath().getLastPathComponent();
        if (object != null && object instanceof BrowserTreeNode) {
            return (BrowserTreeNode)object;
        }
        return null;
    }

    protected BrowserTreeNode getParentConnectionNode(DatabaseConnection dc) {
        for (Enumeration i = tree.getRootNode().children(); i.hasMoreElements();) {
            Object object = i.nextElement();
            if (object instanceof BrowserTreeNode) {
                BrowserTreeNode node = (BrowserTreeNode)object;
                object = node.getUserObject();
                if (object instanceof ConnectionObject) {
                    ConnectionObject conn = (ConnectionObject)object;
                    if (conn.getDatabaseConnection() == dc) {
                        return node;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Notification that the currently selected node (a host)
     * has had their associated db connection closed.
     */
    protected void selectedNodeDisconnected() {
        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return;
        }

        // remove all choldren from the host node 
        // of the current path
        BrowserTreeNode node = (BrowserTreeNode)object;
        if (!(node.getUserObject() instanceof ConnectionObject)) {
            node = getParentNode(node);
        }

        node.setExpanded(false);
        BrowserTreeNode parent = getParentNode(node);
        parent.removeAllChildren();
        tree.nodeStructureChanged(parent);
    }

    /**
     * Notification that the currently selected node (a host)
     * has had their associated db connection created.
     */
    protected void selectedNodeConnected() {
        if (tree.isSelectionEmpty()) {
            return;
        }

        Object object = tree.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return;
        }

        // ensure node is expandable
        BrowserTreeNode node = (BrowserTreeNode)object;
        if (node.isLeaf() && 
                node.getNodeType() == BrowserConstants.HOST_NODE) {
            ConnectionObject metaObject = 
                    (ConnectionObject)node.getDatabaseUserObject();
            TreeExpansionEvent _e = 
                    new TreeExpansionEvent(tree, tree.getSelectionPath());//selectedPath);
            treeExpanded(_e);
            tree.nodeStructureChanged(node);
        }
    }

    // --------------------------------------------------
    // ------- TreeSelectionListener implementation
    // --------------------------------------------------
    
    /**
     * Called whenever the value of the selection changes.
     * This will store the current path selection.
     *
     * @param the event that characterizes the change
     */
    public void valueChanged(TreeSelectionEvent e) {
        // store the last position
        oldSelectionPath = e.getOldLeadSelectionPath();

        // check that we don't have any alter tables
        if (!reselecting && controller.hasAlterTable()) {
            controller.applyTableChange(true);
            return;
        }

        Object object = e.getPath().getLastPathComponent();
        if (object == null) {
            return;
        }

        controller.selectionChanging();

        if (object == tree.getRootNode()) { // root node
            controller.displayRootPanel();
            enableButtons(false);
            return;
        }

        final BrowserTreeNode node = (BrowserTreeNode)object;
        // check if it is an unexpanded host node
        if (node.getNodeType() == BrowserConstants.HOST_NODE) {
            // enable buttons
            enableButtons(true);

            // check expanded state
            if (node.isLeaf()) {
                ConnectionObject metaObject = 
                        (ConnectionObject)node.getDatabaseUserObject();
                if (metaObject.isConnected()) {
                    GUIUtils.invokeAndWait(new Runnable() {
                        public void run() {
                            doHostExpansion(node);
                        }
                    });
                    //doHostExpansion(node);
                }
            }
        } 
        else {
            enableButtons(false);
        }

        try {
            controller.valueChanged(
                    getSelectedMetaObject(), (BrowserTreeNode)object, reloadView);
        } 
        finally {
            // make sure we reset these flags
            reloadView = false;
            reselecting = false;
        }
    }
    
    private boolean connectionValid(boolean showDialog) {
        boolean isConnected = !(SystemUtilities.isConnected());
        if (!isConnected && showDialog) {
            GUIUtilities.displayWarningMessage("No connection available");
        }
        return isConnected;
    }
    
    /**
     * Reloads the currently selected node.
     */
    public void reloadSelection() {
        reloadPath(tree.getSelectionPath());
    }

    /** whether to reload the panel view */
    private boolean reloadView;
    
    /**
     * Reloads the specified tree path.
     */
    public void reloadPath(TreePath path) {

        if (treeExpanding) {
            return;
        }
        
        Object object = path.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return;
        }
        
        BrowserTreeNode node = (BrowserTreeNode)object;
        doHostExpansion(node, true);
    }

    
    // --------------------------------------------------
    // ------- TreeExpansionListener implementation
    // --------------------------------------------------

    public void treeExpanded(TreeExpansionEvent e) {
        TreePath path = e.getPath();
        Object object = path.getLastPathComponent();
        if (!(object instanceof BrowserTreeNode)) {
            return;
        }
        doHostExpansion((BrowserTreeNode)object);
    }
    
    /** provides an indicator that an expansion is in progress */
    private boolean treeExpanding = false;
    
    /**
     * Performs the expansion on a host node.
     *
     * @param node - the host node to be expanded
     */
    private void doHostExpansion(BrowserTreeNode node) {
        doHostExpansion(node, false);
    }
    
    private synchronized void doHostExpansion(final BrowserTreeNode node, 
                                              final boolean reload) {
        // if its already expanded - return
        if (node.isExpanded() && !reload) {
            return;
        }
        
        worker = new SwingWorker() {
            public Object construct() {
                try {
                    treeExpanding = true;
                    //Log.debug("BEFORE expanding "+treeExpanding);
                    GUIUtilities.showWaitCursor();
                    if (reload) {
                        node.removeAllChildren();
                    }
                    return doTreeExpandedWork(node);
                }
                finally {
                    GUIUtilities.showNormalCursor();
                    treeExpanding = false;
                }
            }
            public void finished() {
                try {
                    GUIUtilities.showWaitCursor();
                    BrowserTreeNode _node = (BrowserTreeNode)get();
                    tree.nodeStructureChanged(_node);
                    _node.setExpanded(true);
                    
                    if (reload) {
                        reloadView = true;
                        TreePath path = tree.getSelectionPath();
                        valueChanged(new TreeSelectionEvent(
                                tree, path, true, oldSelectionPath, path));
                    }
                    
                } finally {
                    GUIUtilities.showNormalCursor();
                    treeExpanding = false;
                    //Log.debug("AFTER expanding "+treeExpanding);
                }
            }
        };
        worker.start();        
    }
    
    /**
     * Performs the tree expansion work for the expanded node.
     *
     * @param the node expanded
     */
    private BrowserTreeNode doTreeExpandedWork(BrowserTreeNode node) {
        DatabaseObject metaObject = node.getDatabaseUserObject();
        int type = metaObject.getType();
        
        switch (type) {
            case BrowserConstants.HOST_NODE:
                populateHostBranches(node);                
                break;
            case BrowserConstants.CATALOG_NODE:
                populateSchemaBranches(node);
                break;
            case BrowserConstants.SCHEMA_NODE:
                populateSchemaObjectBranches(node);
                break;
            case BrowserConstants.FUNCTIONS_NODE:
            case BrowserConstants.INDEX_NODE:
            case BrowserConstants.PROCEDURE_NODE:
            case BrowserConstants.SEQUENCE_NODE:
            case BrowserConstants.SYNONYM_NODE:
            case BrowserConstants.SYSTEM_TABLE_NODE:
            case BrowserConstants.TABLE_NODE:
            case BrowserConstants.TRIGGER_NODE:
            case BrowserConstants.VIEW_NODE:
            case BrowserConstants.SYSTEM_FUNCTION_NODE:
            case BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE:
            case BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE:
            case BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE:
            case BrowserConstants.OTHER_NODE:
                populateObjectBranches(node);
                break;
        }

        return node;
    }
    
    public void treeCollapsed(TreeExpansionEvent e) {} // do nothing
    // --------------------------------------------------
    
    /**
     * Populates the branches of a host (connection) node.
     *
     * @param the host node
     */
    private void populateHostBranches(BrowserTreeNode parent) {
        ConnectionObject object = (ConnectionObject)
                                            parent.getDatabaseUserObject();

        if (!object.isConnected()) {
            return;
        }

        BrowserTreeNode node = null;
        DatabaseConnection dc = object.getDatabaseConnection();
        String defaultCatalog = controller.getCatalogName(dc);

        Vector<String> values = controller.getHostedCatalogs(dc);
        if (values == null || values.isEmpty()) {
            object.setCatalogsInUse(false);
            DatabaseObject catalog = 
                new DatabaseObject(BrowserConstants.CATALOG_NODE, 
                                       controller.getDataSourceName(dc));
            catalog.setUseInQuery(false);
            node = new BrowserTreeNode(catalog, true);
            parent.add(node);
        }
        else {
            object.setCatalogsInUse(true);
            for (int i = 0, k = values.size(); i < k; i++) {
                String value = values.get(i);
                DatabaseObject catalog = 
                    new DatabaseObject(BrowserConstants.CATALOG_NODE, value);
                catalog.setCatalogName(value);
                catalog.setDefaultCatalog(defaultCatalog.equalsIgnoreCase(value));
                node = new BrowserTreeNode(catalog, true);
                parent.add(node);   
            }
            
        }

    }
    
    protected BrowserTreeNode getParentNode(BrowserTreeNode child) {
        if (child.getDatabaseUserObject().getType() == 
                BrowserConstants.HOST_NODE) {
            return child;
        }

        BrowserTreeNode _parent = null;
        TreeNode parent = child.getParent();
        while (parent != null) {

            if (parent instanceof BrowserTreeNode) {
                _parent = (BrowserTreeNode)parent;

                if (_parent.getDatabaseUserObject().getType() == 
                        BrowserConstants.HOST_NODE) {
                    return _parent;
                }
                parent = _parent.getParent();
            }

        }
        return null;
    }

    /**
     * Selects the node that matches the specified prefix forward 
     * from the currently selected node.
     *
     * @param prefix - the prefix of the node to select
     */
    protected void selectBrowserNode(final String prefix) {
        // make sure it has its children
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();

        if (node.getChildCount() == 0) {
            doHostExpansion((BrowserTreeNode)node);
        }

//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
                tree.expandSelectedRow();
                tree.selectNextNode(prefix);
//            }
//        });
    }

    /**
     * Returns the available meta key names associated with the
     * specified child node.
     */
    protected String[] getMetaKeyNames(BrowserTreeNode child) {
        // get the host node for this path
        BrowserTreeNode parent = getParentNode(child);
        // get the meta data object from the host node
        ConnectionObject object = (ConnectionObject)
                                            parent.getDatabaseUserObject();
        return object.getMetaKeyNames();
    }

    /**
     * Returns the connection properties object associated with
     * the specified child node.
     */
    protected DatabaseConnection getDatabaseConnection(BrowserTreeNode child) {
        return getConnectionObject(child).getDatabaseConnection();
    }

    /**
     * Returns the ConnectionObject (host node) associated with
     * the specified child node.
     */
    protected ConnectionObject getConnectionObject(BrowserTreeNode child) {
        // get the host node for this path
        BrowserTreeNode parent = getParentNode(child);
        // get the meta data object from the host node
        return (ConnectionObject)parent.getDatabaseUserObject();
    }
    
    private void populateObjectBranches(BrowserTreeNode parent) {
        BrowserTreeNode node = null;

        DatabaseObject object = parent.getDatabaseUserObject();
        DatabaseConnection dc = getDatabaseConnection(parent);
        String defaultSchema = controller.getCatalogName(dc);

        boolean isDefault = 
                defaultSchema.equalsIgnoreCase(object.getSchemaName()) 
                    ||
                defaultSchema.equalsIgnoreCase(object.getCatalogName());

        int type = object.getType();

        // populate the column nodes for table type nodes
        if ((type == BrowserConstants.TABLE_NODE ||
                type == BrowserConstants.SYSTEM_TABLE_NODE) && 
                !parent.isTypeParent()) {

                String[] columns = controller.getColumnNames(
                                                dc,
                                                object.getName(),
                                                object.getSchemaName());

                for (int i = 0; i < columns.length; i++) {
                    DatabaseObject column = new DatabaseObject(
                            BrowserConstants.COLUMN_NODE, columns[i]);
                    column.setCatalogName(object.getCatalogName());
                    column.setSchemaName(object.getSchemaName());
                    column.setParentName(object.getName());
                    column.setDefaultCatalog(isDefault);
                    node = new BrowserTreeNode(column, false);
                    parent.add(node);
                }
        }

        // populate the system functions types
        else if (type == BrowserConstants.SYSTEM_FUNCTION_NODE ||
                    type == BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE ||
                    type == BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE ||
                    type == BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE) {

            String na = "Not Applicable";
            // if its the parent node add the types
            if (parent.isTypeParent()) {
                String systemFunction = "System Function";
                DatabaseObject table = 
                        new DatabaseObject(
                            BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE, 
                            "String Functions");
                table.setSchemaName(na);
                table.setMetaDataKey(systemFunction);
                table.setSystemObject(true);
                node = new BrowserTreeNode(table, true, false);
                parent.add(node);

                table = 
                        new DatabaseObject(
                            BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE, 
                            "Numeric Functions");
                table.setSchemaName(na);
                table.setMetaDataKey(systemFunction);
                table.setSystemObject(true);
                node = new BrowserTreeNode(table, true, false);
                parent.add(node);

                table = 
                        new DatabaseObject(
                            BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE, 
                            "Date/Time Functions");
                table.setSchemaName(na);
                table.setMetaDataKey(systemFunction);
                table.setSystemObject(true);
                node = new BrowserTreeNode(table, true, false);
                parent.add(node);
            }

            else { // add the individual functions
                String[] functions = null;
                switch (type) {

                    case BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE:
                        functions = controller.getSystemFunctions(dc,
                                                           MetaDataValues.STRING_FUNCTIONS);
                        break;

                    case BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE:
                        functions = controller.getSystemFunctions(dc,
                                                           MetaDataValues.NUMERIC_FUNCTIONS);
                        break;

                    case BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE:
                        functions = controller.getSystemFunctions(dc,
                                                           MetaDataValues.TIME_DATE_FUNCTIONS);
                        break;

                }

                if (functions != null && functions.length > 0) {

                    for (int i = 0; i < functions.length; i++) {
                        DatabaseObject table = new DatabaseObject(type, functions[i]);
                        table.setSchemaName(na);
                        table.setMetaDataKey(object.getMetaDataKey());
                        node = new BrowserTreeNode(table, false);
                        parent.add(node);
                    }

                }

            }

        }

        else {
            String[] tables = controller.getTables(dc,
                                                   object.getCatalogName(),
                                                   object.getSchemaName(),
                                                   object.getName());

            // if we have nothing check if its a proc or function node
            if (tables == null || tables.length == 0) {
                tables = controller.checkProcedureTerm(dc, object);
            }

            for (int i = 0; i < tables.length; i++) {
                DatabaseObject table = new DatabaseObject(type, tables[i]);
                table.setCatalogName(object.getCatalogName());
                table.setSchemaName(object.getSchemaName());
                table.setMetaDataKey(object.getMetaDataKey());
                table.setDefaultCatalog(isDefault);

                if (type == BrowserConstants.TABLE_NODE ||
                        type == BrowserConstants.SYSTEM_TABLE_NODE) {
                    node = new BrowserTreeNode(table, true, false);
                }
                else {
                    node = new BrowserTreeNode(table, false);
                }
                parent.add(node);
            }

        }

    }
    
    private void populateSchemaObjectBranches(BrowserTreeNode parent) {
        ConnectionObject hostObject = getConnectionObject(parent);
        DatabaseConnection dc = hostObject.getDatabaseConnection();

        boolean hasKeys = true;
        ArrayList keyNames = null;
        if (!hostObject.hasMetaKeys()) {
            hasKeys = false;
            keyNames = new ArrayList(BrowserConstants.META_TYPES.length);
        }
        
        BrowserTreeNode node = null;
        DatabaseObject schema = parent.getDatabaseUserObject();
        
        for (int i = 0; i < BrowserConstants.META_TYPES.length; i++) {
            DatabaseObject object = 
                new DatabaseObject(i, BrowserConstants.META_TYPES[i]);
            object.setCatalogName(schema.getCatalogName());
            object.setSchemaName(schema.getName());
            object.setMetaDataKey(BrowserConstants.META_TYPES[i]);
            node = new BrowserTreeNode(object, true);
            parent.add(node);
            
            if (!hasKeys) {
                keyNames.add(BrowserConstants.META_TYPES[i]);
            }
            
        }
        
        // add other non-default objects available
        boolean isDerivative = false;
        DatabaseObject object = null;

        String[] tableTypes = controller.getTableTypes(dc);        
        for (int i = 0; i < tableTypes.length; i++) {
            
            if (!MiscUtils.containsValue(
                    BrowserConstants.META_TYPES, tableTypes[i])) {
                
                if (!hasKeys) {
                    keyNames.add(tableTypes[i]);
                }
                
                // check to see if the type is a derivative of a default
                // ie. SYSTEM INDEX is a derivative of INDEX so we use
                // the same icon and display format
                for (int j = 0; j < BrowserConstants.META_TYPES.length; j++) {

                    if (MiscUtils.containsWholeWord(tableTypes[i], 
                            BrowserConstants.META_TYPES[j])) {
                        isDerivative = true;
                        object = new DatabaseObject(j, tableTypes[i]);
                        break;
                    }
                    
                }
                
                if (!isDerivative) {
                    object = new DatabaseObject(
                                    BrowserConstants.OTHER_NODE, tableTypes[i]);
                }

                isDerivative = false;
                object.setCatalogName(schema.getCatalogName());
                object.setSchemaName(schema.getName());
                object.setMetaDataKey(tableTypes[i]);
                node = new BrowserTreeNode(object, true);
                parent.add(node);
                
            }
            
        }
        
        if (!hasKeys) {
            hostObject.setMetaKeyNames(
                    (String[])keyNames.toArray(new String[keyNames.size()]));
        }
        
    }
    
    private void populateSchemaBranches(BrowserTreeNode parent) {
        DatabaseConnection dc = getDatabaseConnection(parent);
        List schemas = controller.getCatalogSchemas(dc);
        String defaultSchema = controller.getSchemaName(dc);

        // no schemas = try by catalogue
        if (schemas == null || schemas.isEmpty()) {
            populateSchemaObjectBranches(parent);
        }
        
        // no schemas or catalogues available - bail
        if (schemas == null || schemas.isEmpty()) {
            return;
        }
        
        BrowserTreeNode node = null;
        DatabaseObject catalog = parent.getDatabaseUserObject();
        for (int i = 0, k = schemas.size(); i < k; i++) {
            String value = (String)schemas.get(i);
            DatabaseObject schema = 
                    new DatabaseObject(BrowserConstants.SCHEMA_NODE, value);
            schema.setCatalogName(catalog.getName());
            schema.setDefaultCatalog(defaultSchema.equalsIgnoreCase(value));
            node = new BrowserTreeNode(schema, true);
            parent.add(node);
            populateSchemaObjectBranches(node);
            node.setExpanded(true);
        }
        
    }

    /**
     * Removes the selected node.<br>
     *
     * This will attempt to propagate the call to the connected
     * database using a DROP statement.
     */
    public void removeTreeNode() {
        
        tree.removeTreeSelectionListener(this);
        int row = tree.getSelectionRows()[0];

        try {
            TreePath selection = tree.getSelectionPath();
            if (selection != null) {

                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)selection.getLastPathComponent();

                DatabaseObject object = (DatabaseObject)node.getUserObject();
                String name = object.getName();

                int yesNo = GUIUtilities.displayConfirmDialog(
                                   "Are you sure you want to drop " + object.getName() + "?");
                if (yesNo == JOptionPane.NO_OPTION) {
                    return;
                }

                else {

                    try {
                        DefaultMutableTreeNode parent = 
                                (DefaultMutableTreeNode)node.getParent();
                        int result = controller.dropObject(
                                getSelectedMetaObject().getDatabaseConnection(),
                                object);

                        if (result >= 0 && parent != null) {
                            tree.removeNode(node);
                            row = (row == 0 ? 1 : row - 1);
                            return;
                        }

                    }
                    catch (SQLException e) {
                        StringBuffer sb = new StringBuffer();
                        sb.append("An error occurred removing the selected object.").
                           append("\n\nThe system returned:\n").
                           append(MiscUtils.formatSQLError(e));
                        GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
                    }

                }

            }
        }
        finally {
            tree.addTreeSelectionListener(this);
            tree.setSelectionRow(row);
        }
        
    }

    /**
     * Returns the name of a new connection to be added where
     * the name of the connection may already exist.
     *
     * @param name - the name of the connection
     */
    private String buildConnectionName(String name) {
        int count = 0;
        for (int i = 0, n = connections.size(); i < n; i++) {
            DatabaseConnection _dc = connections.get(i);
            if (_dc.getName().startsWith(name)) {
                count++;
            }
        }

        if (count > 0) {
            count++;
            name += " " + count;
        }
        return name;
    }
    
    public boolean isRootSelectOnDisconnect() {
        return rootSelectOnDisconnect;
    }

    public void setRootSelectOnDisconnect(boolean rootSelectOnDisconnect) {
        this.rootSelectOnDisconnect = rootSelectOnDisconnect;
    }

    public String toString() {
        return TITLE;
    }
    
    public static final String MENU_ITEM_KEY = "viewConnections";
    
    public static final String PROPERTY_KEY = "system.display.connections";
    
    // ----------------------------------------
    // DockedTabView Implementation
    // ----------------------------------------
    
    /**
     * Returns the display title for this view.
     *
     * @return the title displayed for this view
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Returns the name defining the property name for this docked tab view.
     *
     * @return the key
     */
    public String getPropertyKey() {
        return PROPERTY_KEY;
    }

    /**
     * Returns the name defining the menu cache property
     * for this docked tab view.
     *
     * @return the preferences key
     */
    public String getMenuItemKey() {
        return MENU_ITEM_KEY;
    }

    /** The tree's popup menu function */
    private class PopMenu extends JPopupMenu {
        
        private JMenuItem connect;
        private JMenuItem disconnect;
        private JMenuItem reload;
        private JMenuItem duplicate;
        private JMenuItem delete;
        private JMenuItem recycleConnection;

        private JMenu exportData;
        private JMenu importData;
        
        protected TreePath popupPath;
        protected DatabaseConnection hover;
        
        public PopMenu() {            
            ActionListener listener = new PopupMenuActionListener();

            reload = createMenuItem("Reload", "reload", listener);
            add(reload);
            recycleConnection = createMenuItem("Recycle this connection", "recycle", listener);
            add(recycleConnection);

            addSeparator();
            
            connect = createMenuItem("Connect", "connect", listener);
            add(connect);
            disconnect = createMenuItem("Disconnect", "disconnect", listener);
            add(disconnect);
            
            addSeparator();
            
            duplicate = createMenuItem("Duplicate", "duplicate", listener);
            add(duplicate);
            delete = createMenuItem("Delete", "delete", listener);
            add(delete);
            
            addSeparator();
            
            exportData = new JMenu("Export Data");
            exportData.add(createMenuItem("Export to XML File", "exportXml", listener));
            exportData.add(createMenuItem("Export to Delimited File", "exportDelimited", listener));
            exportData.add(createMenuItem("Export to Excel Spreadsheet", "exportExcel", listener));
            add(exportData);

            importData = new JMenu("Import Data");
            importData.add(
                    createMenuItem("Import from XML File", "importXml", listener));
            importData.add(
                    createMenuItem("Import from Delimited File", "importDelimited", listener));
            add(importData);
            
            addSeparator();
            add(createMenuItem("Connection Properties", "properties", listener));
        }

        private JMenuItem createMenuItem(String text, 
                                         String actionCommand, 
                                         ActionListener listener) {
            JMenuItem menuItem = new JMenuItem(text);
            menuItem.setActionCommand(actionCommand);
            menuItem.addActionListener(listener);
            return menuItem;
        }
        
        public void setToConnect(boolean canConnect) {
            connect.setEnabled(canConnect);
            disconnect.setEnabled(!canConnect);
            delete.setEnabled(canConnect);

            String label = null;
            // check whether reload is available
            if (popupPath != null) {
                Object object = popupPath.getLastPathComponent();
                if (object instanceof BrowserTreeNode) {
                    BrowserTreeNode node = (BrowserTreeNode)object;
                    if (node.getUserObject() instanceof ConnectionObject) {
                        reload.setEnabled(false);
                        exportData.setEnabled(false);
                        importData.setEnabled(false);
                        recycleConnection.setEnabled(!canConnect);
                    } else {
                        label = node.toString();
                        reload.setEnabled(true);
                        recycleConnection.setEnabled(false);

                        boolean importExport = 
                                (node.getDatabaseUserObject().getType() == 
                                    BrowserConstants.TABLE_NODE && !node.isTypeParent());
                        exportData.setEnabled(importExport);
                        importData.setEnabled(importExport);
                    }
                }
            }

            // re-label the menu items
            if (hover != null) {
                String name = hover.getName();
                connect.setText("Connect Data Source " + name);
                disconnect.setText("Disconnect Data Source " + name);
                delete.setText("Remove Data Source " + name);
                duplicate.setText("Create Duplicate of Data Source " + name);
                
                if (label != null) {
                    reload.setText("Reload " + label);
                } else {
                    reload.setText("Reload");
                }
                
            }
            
        }

    }

    // declared public un-intentionally here for the reflective 
    // action extension and its use to work correctly.
    // may need to revise !!??
    public class PopupMenuActionListener extends ReflectiveAction {

        public PopupMenuActionListener() {}

        public void actionPerformed(ActionEvent e) {
            try {
                super.actionPerformed(e);
            }
            finally {
                reloadView = false;
                popupMenu.hover = null;
                popupMenu.popupPath = null;
            }
        }
        
        public void delete(ActionEvent e) {
            if (popupMenu.popupPath != null) {
                BrowserTreeNode node = 
                        (BrowserTreeNode)popupMenu.popupPath.getLastPathComponent();
                deleteConnection(node);
            }
        }
        
        public void recycle(ActionEvent e) {
            ConnectionObject dbObject = getSelectedMetaObject();
            controller.recycleConnection(dbObject.getDatabaseConnection());
        }
        
        public void reload(ActionEvent e) {
            if (popupMenu.popupPath != null) {
                reloadPath(popupMenu.popupPath);
            }
        }
        
        public void disconnect(ActionEvent e) {
            // check if the selected node belongs to the
            // connection about to closed
            boolean selectHover = (getSelectedDatabaseConnection() == popupMenu.hover);
            controller.connect(popupMenu.hover, false);
            
            //System.err.println("selectHover: " + selectHover);
            
            if (selectHover) {
                try {
                    reloadView = true;
                    setSelectedConnection(popupMenu.hover);
                    // select the properties panel for the
                    // just closed connection
                    /*
                    controller.valueChanged(
                            getSelectedMetaObject(),
                            (BrowserTreeNode)popupPath.getLastPathComponent(),
                            false);
                     */
                }
                finally {
                    reloadView = false;
                }
            }
        }
        
        public void duplicate(ActionEvent e) {
            if (popupMenu.hover != null) {
                String name = buildConnectionName(
                                popupMenu.hover.getName() + " (Copy") + ")";

                DatabaseConnection dc = new DatabaseConnection(name);
                dc.setPasswordStored(popupMenu.hover.isPasswordStored());
                dc.setPasswordEncrypted(popupMenu.hover.isPasswordEncrypted());
                dc.setDriverId(popupMenu.hover.getDriverId());
                dc.setDatabaseType(popupMenu.hover.getDatabaseType());
                dc.setHost(popupMenu.hover.getHost());
                dc.setPort(popupMenu.hover.getPort());
                dc.setSourceName(popupMenu.hover.getSourceName());
                dc.setTransactionIsolation(popupMenu.hover.getTransactionIsolation());
                dc.setURL(popupMenu.hover.getURL());
                dc.setUserName(popupMenu.hover.getUserName());

                if (popupMenu.hover.getJdbcProperties() != null) {
                    dc.setJdbcProperties(
                            (Properties)popupMenu.hover.getJdbcProperties().clone());
                }

                if (dc.isPasswordEncrypted()) {
                    dc.setEncryptedPassword(popupMenu.hover.getPassword());
                } else {
                    dc.setPassword(popupMenu.hover.getPassword());
                }

                newConnection(dc);
            }

        }
        
        public void exportExcel(ActionEvent e) {
            importExportDialog(ImportExportProcess.EXCEL);
        }

        public void importXml(ActionEvent e) {
            importExportDialog(ImportExportProcess.IMPORT_XML);
        }

        public void exportXml(ActionEvent e) {
            importExportDialog(ImportExportProcess.EXPORT_XML);
        }

        public void importDelimited(ActionEvent e) {
            importExportDialog(ImportExportProcess.IMPORT_DELIMITED);
        }
        
        public void exportDelimited(ActionEvent e) {
            importExportDialog(ImportExportProcess.EXPORT_DELIMITED);
        }
        
        public void properties(ActionEvent e) {
            reloadView = true;
            setSelectedConnection(popupMenu.hover);
        }
        
        public void connect(ActionEvent e) {
            controller.connect(popupMenu.hover, false);
        }
        
        private void importExportDialog(int transferType) {
            BaseDialog dialog = null;
            JPanel panel = null;

            DatabaseConnection dc = getSelectedDatabaseConnection();
            String schemaName = getSelectedDatabaseObject().getSchemaName();
            String tableName = getSelectedDatabaseObject().getName();

            try {
                GUIUtilities.showWaitCursor();
                switch (transferType) {
                    case ImportExportProcess.EXPORT_DELIMITED:
                        dialog = new BaseDialog("Export Data", false, false);
                        panel = new ImportExportDelimitedPanel(
                                        dialog, ImportExportProcess.EXPORT, 
                                        dc, schemaName, tableName);
                        break;

                    case ImportExportProcess.IMPORT_DELIMITED:
                        dialog = new BaseDialog("Import Data", false, false);
                        panel = new ImportExportDelimitedPanel(
                                        dialog, ImportExportProcess.IMPORT, 
                                        dc, schemaName, tableName);
                        break;

                    case ImportExportProcess.EXPORT_XML:
                        dialog = new BaseDialog("Export XML", false, false);
                        panel = new ImportExportXMLPanel(
                                        dialog, ImportExportProcess.EXPORT, 
                                        dc, schemaName, tableName);
                        break;

                    case ImportExportProcess.IMPORT_XML:
                        dialog = new BaseDialog("Import XML", false, false);
                        panel = new ImportExportXMLPanel(
                                        dialog, ImportExportProcess.IMPORT, 
                                        dc, schemaName, tableName);
                        break;

                    case ImportExportProcess.EXCEL:
                        dialog = new BaseDialog("Export Excel Spreadsheet", false, false);
                        panel = new ImportExportExcelPanel(
                                        dialog, ImportExportProcess.EXPORT, 
                                        dc, schemaName, tableName);
                        break;

                }

                dialog.addDisplayComponent(panel);
                dialog.display();            
            }
            finally {
                GUIUtilities.showNormalCursor();
            }
        }

    }
    
    private boolean reselecting;
    
    private class MouseHandler extends MouseAdapter {
        
        public MouseHandler() {}
        
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() < 2) {
                reselecting = false;
                return;
            }
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null && path == tree.getSelectionPath()) {
                reselecting = true;
                valueChanged(new TreeSelectionEvent(
                        tree, path, true, oldSelectionPath, path));
            }
        }
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                
                if (popupMenu == null) {
                    popupMenu = new PopMenu();
                }

                Point point = new Point(e.getX(), e.getY());
                popupMenu.popupPath = tree.getPathForLocation(point.x, point.y);
                // get the connection at this point
                popupMenu.hover = getConnectionAt(point);
                if (popupMenu.hover != null) {
                    try {
                        // enable/disable items
                        popupMenu.setToConnect(!popupMenu.hover.isConnected());
                        tree.removeTreeSelectionListener(ConnectionsTreePanel.this);
                        tree.setSelectionPath(popupMenu.popupPath);
                    }
                    finally {
                        tree.addTreeSelectionListener(ConnectionsTreePanel.this);
                    }
                    popupMenu.show(e.getComponent(), point.x, point.y);
                }
            }
        }

    }
    
}
