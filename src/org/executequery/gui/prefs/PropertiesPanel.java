/*
 * PropertiesPanel.java
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


package org.executequery.gui.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.ActiveComponent;
import org.executequery.gui.*;
import org.executequery.components.table.PropsTreeCellRenderer;
import org.executequery.components.BottomButtonPanel;
import org.underworldlabs.swing.FlatSplitPane;
import org.underworldlabs.swing.tree.DynamicTree;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Main system preferences panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/07/15 13:37:32 $
 */
public class PropertiesPanel extends JPanel
                             implements ActiveComponent,
                                        ActionListener,
                                        TreeSelectionListener {
    
    public static final String TITLE = "Preferences";
    public static final String FRAME_ICON = "Preferences16.gif";

    /** the property selection tree */
    private JTree tree;
    
    /** the right-hand property display panel */
    private JPanel rightPanel;

    /** the base panel layout */
    private CardLayout cardLayout;

    /** map of panels within the layout */
    private HashMap panelMap;
    
    /** the parent container */
    private ActionContainer parent;

    /** Constructs a new instance. */
    public PropertiesPanel(ActionContainer parent) {
        this(parent, -1);
    }

    /** 
     * Constructs a new instance seleting the specified node.
     *
     * @param the node to select
     */
    public PropertiesPanel(ActionContainer parent, int openRow) {
        super(new BorderLayout());
        this.parent = parent;
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (openRow != -1) {
            tree.setSelectionRow(6);
        }
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        JSplitPane splitPane = null;
        if (GUIUtilities.getLookAndFeel() < Constants.GTK_LAF) {
            splitPane = new FlatSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        }
        else {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        }

        splitPane.setDividerSize(6);
        setPreferredSize(new Dimension(625, 450));
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(625, 400));
        
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        splitPane.setRightComponent(rightPanel);
        
        // ----------------------------------
        // initialise branches
        
        ArrayList branches = new ArrayList();
        PropertyNode node = new PropertyNode(PropertyTypes.GENERAL, "General");
        branches.add(node);
        node = new PropertyNode(PropertyTypes.LOCALE, "Locale");
        branches.add(node);
//        node = new PropertyNode(PropertyTypes.VIEW, "View");
//        branches.add(node);
        node = new PropertyNode(PropertyTypes.APPEARANCE, "Display");
        branches.add(node);
        node = new PropertyNode(PropertyTypes.SHORTCUTS, "Shortcuts");
        branches.add(node);
        node = new PropertyNode(PropertyTypes.LOOK_PLUGIN, "Look & Feel Plugins");
        branches.add(node);
        
        node = new PropertyNode(PropertyTypes.TOOLBAR_GENERAL, "Tool Bar");
        node.addChild(new PropertyNode(PropertyTypes.TOOLBAR_FILE, "File Tools"));
        node.addChild(new PropertyNode(PropertyTypes.TOOLBAR_EDIT, "Edit Tools"));
        node.addChild(new PropertyNode(PropertyTypes.TOOLBAR_DATABASE, "Database Tools"));
        node.addChild(new PropertyNode(PropertyTypes.TOOLBAR_BROWSER, "Browser Tools"));
        node.addChild(new PropertyNode(PropertyTypes.TOOLBAR_IMPORT_EXPORT, "Import/Export Tools"));
        node.addChild(new PropertyNode(PropertyTypes.TOOLBAR_SEARCH, "Search Tools"));
        node.addChild(new PropertyNode(PropertyTypes.TOOLBAR_SYSTEM, "System Tools"));
        branches.add(node);

        node = new PropertyNode(PropertyTypes.EDITOR_GENERAL, "Editor");
        node.addChild(new PropertyNode(PropertyTypes.EDITOR_BACKGROUND, "Colours"));
        node.addChild(new PropertyNode(PropertyTypes.EDITOR_FONTS, "Fonts"));
        node.addChild(new PropertyNode(PropertyTypes.EDITOR_SYNTAX, "Syntax Colours"));
        branches.add(node);

        node = new PropertyNode(PropertyTypes.RESULTS, "Results Table");
        branches.add(node);
        node = new PropertyNode(PropertyTypes.CONNECTIONS, "Connection");
        branches.add(node);
        node = new PropertyNode(PropertyTypes.BROWSER_GENERAL, "Database Browser");
        branches.add(node);
        
        DefaultMutableTreeNode root = 
                new DefaultMutableTreeNode(new PropertyNode(PropertyTypes.SYSTEM, "Preferences"));

        List children = null;
        DefaultMutableTreeNode treeNode = null;
        
        for (int i = 0, k = branches.size(); i < k; i++) {
            node = (PropertyNode)branches.get(i);
            treeNode = new DefaultMutableTreeNode(node);
            root.add(treeNode);
            
            if (node.hasChildren()) {
                children = node.getChildren();
                int count = children.size();

                for (int j = 0; j < count; j++) {
                    treeNode.add(new DefaultMutableTreeNode(children.get(j)));
                }

            }
            
        }

        tree = new DynamicTree(root);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setCellRenderer(new PropsTreeCellRenderer());
        
        // expand all rows
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        
        Dimension leftPanelDim = new Dimension(150, 350);
        JScrollPane js = new JScrollPane(tree);
        js.setPreferredSize(leftPanelDim);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.white);
        leftPanel.setMinimumSize(leftPanelDim);
        leftPanel.setMaximumSize(leftPanelDim);
        leftPanel.add(js, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel); 

        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(new BottomButtonPanel(
                            this, null, "prefs", parent.isDialog()), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        
        /*
        add(mainPanel, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                        GridBagConstraints.SOUTHEAST, 
                                        GridBagConstraints.BOTH,
                                        new Insets(5, 5, 5, 5), 0, 0));
         */
        panelMap = new HashMap();
        tree.addTreeSelectionListener(this);
        
        // setup the first panel
        JPanel panel = new PropertiesRootPanel();
        PropertyNode rootObject = (PropertyNode)root.getUserObject();
        String label = rootObject.getLabel();
        panelMap.put(label, panel);
        rightPanel.add(panel, label);
        cardLayout.show(rightPanel, label);

        tree.setSelectionRow(0);
    }

    public void valueChanged(TreeSelectionEvent e) {
        final TreePath path = e.getPath();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getProperties(path.getPath());
            }
        });
    }

    private void getProperties(Object[] selection) {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode)selection[selection.length-1];
        PropertyNode node = (PropertyNode)n.getUserObject();
        
        JPanel panel = null;
        int id = node.getNodeId();
        String label = node.getLabel();

        if (panelMap.containsKey(label)) {
            cardLayout.show(rightPanel, label);
            return;
        }
        
        switch (id) {
            case PropertyTypes.SYSTEM:
                panel = new PropertiesRootPanel();
                break;
            case PropertyTypes.GENERAL:
                panel = new PropertiesGeneral();
                break;
            case PropertyTypes.LOCALE:
                panel = new PropertiesLocales();
                break;
/*
            case PropertyTypes.VIEW:
                panel = new PropertiesView();
                break;
*/
            case PropertyTypes.SHORTCUTS:
                panel = new PropertiesKeyShortcuts();
                break;
            case PropertyTypes.APPEARANCE:
                panel = new PropertiesAppearance();
                break;
            case PropertyTypes.TOOLBAR_GENERAL:
                panel = new PropertiesToolBarGeneral();
                break;
            case PropertyTypes.TOOLBAR_FILE:
                panel = new PropertiesToolBar("File Tools");
                break;
            case PropertyTypes.TOOLBAR_EDIT:
                panel = new PropertiesToolBar("Edit Tools");
                break;
            case PropertyTypes.TOOLBAR_SEARCH:
                panel = new PropertiesToolBar("Search Tools");
                break;
            case PropertyTypes.TOOLBAR_DATABASE:
                panel = new PropertiesToolBar("Database Tools");
                break;
            case PropertyTypes.TOOLBAR_BROWSER:
                panel = new PropertiesToolBar("Browser Tools");
                break;
            case PropertyTypes.TOOLBAR_IMPORT_EXPORT:
                panel = new PropertiesToolBar("Import/Export Tools");
                break;
            case PropertyTypes.TOOLBAR_SYSTEM:
                panel = new PropertiesToolBar("System Tools");
                break;
            case PropertyTypes.LOOK_PLUGIN:
                panel = new PropertiesLookPlugins();
                break;
            case PropertyTypes.EDITOR_GENERAL:
                panel = new PropertiesEditorGeneral();
                break;
            case PropertyTypes.EDITOR_BACKGROUND:
                panel = new PropertiesEditorBackground();
                break;
/*
            case PropertyTypes.EDITOR_DISPLAY:
                panel = new PropertiesEditorDisplay();
                break;
*/
            case PropertyTypes.EDITOR_FONTS:
                panel = new PropertiesEditorFonts();
                break;
            case PropertyTypes.EDITOR_SYNTAX:
                panel = new PropertiesEditorSyntax();
                break;
            case PropertyTypes.RESULTS:
                panel = new PropertiesResults();
                break;
            case PropertyTypes.CONNECTIONS:
                panel = new PropertiesConns();
                break;
            case PropertyTypes.BROWSER_GENERAL:
                panel = new PropertiesBrowserGeneral();
                break;
        }
        
        panelMap.put(label, panel);
        rightPanel.add(panel, label);
        cardLayout.show(rightPanel, label);

    }

    public void actionPerformed(ActionEvent e) {
        GUIUtilities.showWaitCursor();  // ???? 
        Set keys = panelMap.keySet();

        for (Iterator i = keys.iterator(); i.hasNext();) {
            UserPreferenceFunction panel = 
                    (UserPreferenceFunction)panelMap.get(i.next().toString());
            panel.save();
        }

        GUIUtilities.resetToolBar(true);
        // TODO: CHECK THIS
        GUIUtilities.preferencesChanged();

        parent.finished();
    }
    
    public void cleanup() {
        if (panelMap.containsKey("Colours")) {
            PropertiesEditorBackground panel = 
                    (PropertiesEditorBackground)panelMap.get("Colours");
            panel.stopCaretDisplayTimer();
        }
    }
    
}



