/*
 * BrowserTreeCellRenderer.java
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


package org.executequery.components.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.executequery.Constants;
import org.underworldlabs.swing.tree.AbstractTreeCellRenderer;
import org.executequery.gui.browser.BrowserConstants;
import org.executequery.gui.browser.BrowserTreeNode;
import org.executequery.gui.browser.ConnectionObject;
import org.executequery.gui.browser.DatabaseObject;
import org.underworldlabs.swing.plaf.UIUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Tree cell renderer or the database browser.
 * <P>
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/07/15 16:24:39 $
 */
public class BrowserTreeCellRenderer extends AbstractTreeCellRenderer {
    
    /** String literal '(connected)' */
    private static final String DEFAULT_CONNECTED = " (connected)";

    /** Icon collection for nodes */
    private static Map icons;
    
    /** this label's font */
    private static Font font;
    
    private static Color textBackground;
    private static Color textForeground;
    private static Color selectedBackground;
    private static Color selectedTextForeground;

    /**
     * Constructs a new instance and initialises any variables
     */
    public BrowserTreeCellRenderer(Map icons) {
        this.icons = icons;
        /*
        if (font == null) {
            font = new Font("Dialog", Font.PLAIN, 11);
        }
        */

        textBackground = UIManager.getColor("Tree.textBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        selectedBackground = UIManager.getColor("Tree.selectionBackground");
        selectedTextForeground = UIManager.getColor("Tree.selectionForeground");

        if (UIUtils.isGtkLookAndFeel()) {
            // has default black border on selection - ugly and wrong!
            setBorderSelectionColor(null);
        }

        //setFont(font);
        sb = new StringBuffer();
    }
    
    /**
     * Sets the value of the current tree cell to value. If 
     * selected is true, the cell will be drawn as if selected. 
     * If expanded is true the node is currently expanded and if 
     * leaf is true the node represets a leaf and if hasFocus 
     * is true the node currently has focus. tree is the JTree 
     * the receiver is being configured for. Returns the Component 
     * that the renderer uses to draw the value.
     *
     * @return the Component that the renderer uses to draw the value
     */
    public Component getTreeCellRendererComponent(JTree tree, 
                                                  Object value,
                                                  boolean isSelected, 
                                                  boolean isExpanded,
                                                  boolean isLeaf, 
                                                  int row, 
                                                  boolean hasFocus) {
        
	this.hasFocus = hasFocus;

        BrowserTreeNode bNode = null;
        DefaultMutableTreeNode child = (DefaultMutableTreeNode)value;
        DatabaseObject node = (DatabaseObject)child.getUserObject();
        
        int type = node.getType();
        //Log.debug("node: " +node+ " type: " + type);

        switch (type) {

            case BrowserConstants.ROOT_NODE:
                setIcon((ImageIcon)icons.get(
                        BrowserConstants.CONNECTIONS_IMAGE));
                break;

            case BrowserConstants.HOST_NODE:
                ConnectionObject _node = (ConnectionObject)node;
                if (_node.isConnected()) {
                    setIcon((ImageIcon)icons.get(
                            BrowserConstants.HOST_CONNECTED_IMAGE));
                } else {
                    setIcon((ImageIcon)icons.get(
                            BrowserConstants.HOST_NOT_CONNECTED_IMAGE));
                }
                break;
                
            case BrowserConstants.CATALOG_NODE:
                setIcon((ImageIcon)icons.get(BrowserConstants.CATALOG_IMAGE));
                break;
                
            case BrowserConstants.SCHEMA_NODE:
                setIcon((ImageIcon)icons.get(BrowserConstants.SCHEMA_IMAGE));
                break;
                
            case BrowserConstants.SYSTEM_FUNCTION_NODE:

                if (child.isLeaf())
                    setIcon((ImageIcon)icons.get(BrowserConstants.SYSTEM_FUNCTIONS_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;
                
            case BrowserConstants.FUNCTIONS_NODE:
                
                if (child.isLeaf())
                    setIcon((ImageIcon)icons.get(BrowserConstants.FUNCTIONS_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;
                
            case BrowserConstants.INDEX_NODE:
                
                if (child.isLeaf())
                    setIcon((ImageIcon)icons.get(BrowserConstants.INDEXES_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;
                
            case BrowserConstants.PROCEDURE_NODE:
                
                if (child.isLeaf())
                    setIcon((ImageIcon)icons.get(BrowserConstants.PROCEDURES_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;
                
            case BrowserConstants.SEQUENCE_NODE:
                
                if (child.isLeaf())
                    setIcon((ImageIcon)icons.get(BrowserConstants.SEQUENCES_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;
                
            case BrowserConstants.SYNONYM_NODE:
                
                if (child.isLeaf())
                    setIcon((ImageIcon)icons.get(BrowserConstants.SYNONYMS_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;

            case BrowserConstants.VIEW_NODE:

                if (child.isLeaf())
                    setIcon((ImageIcon)icons.get(BrowserConstants.VIEWS_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;

            case BrowserConstants.SYSTEM_TABLE_NODE:

                bNode = (BrowserTreeNode)child;

                if (child.isLeaf() || !bNode.isTypeParent())
                    setIcon((ImageIcon)icons.get(BrowserConstants.SYSTEM_TABLES_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;
                
            case BrowserConstants.TABLE_NODE:
                
                bNode = (BrowserTreeNode)child;

                if (child.isLeaf() || !bNode.isTypeParent())
                    setIcon((ImageIcon)icons.get(BrowserConstants.TABLES_IMAGE));
                else
                    setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                
                break;

            case BrowserConstants.COLUMN_NODE:
                setIcon((ImageIcon)icons.get(BrowserConstants.COLUMNS_IMAGE));
                break;

            case BrowserConstants.SYSTEM_DATE_TIME_FUNCTIONS_NODE:
            case BrowserConstants.SYSTEM_NUMERIC_FUNCTIONS_NODE:
            case BrowserConstants.SYSTEM_STRING_FUNCTIONS_NODE:
                setIcon((ImageIcon)icons.get(BrowserConstants.SYSTEM_FUNCTIONS_IMAGE));
                break;
                
/*
        case TRIGGER_NODE:
          setIcon((ImageIcon)icons.get(SCHEMA_IMAGE));
          break;
 
        case OTHER_NODE:
          setIcon((ImageIcon)icons.get(SCHEMA_IMAGE));
          break; */
                
            default:
                setIcon((ImageIcon)icons.get(BrowserConstants.DATABASE_OBJECT_IMAGE));
                break;
                
        }
        
        String label = node.getName();
        if ((type == BrowserConstants.SCHEMA_NODE || 
                type == BrowserConstants.CATALOG_NODE) && node.isDefaultCatalog()) {
            label += DEFAULT_CONNECTED;
        }
        setText(label);

        if (node.getType() == BrowserConstants.HOST_NODE) {
            setToolTipText(buildToolTip((ConnectionObject)node));
        }
        else {
            setToolTipText(label);
        }

        selected = isSelected;
        if(!selected) {
            setForeground(textForeground);
        } else {
            setForeground(selectedTextForeground);
        }

        return this;
    }

    /** tool tip string buffer */
    private StringBuffer sb;
    
    /**
     * Builds a HTML tool tip describing this tree node.
     *
     * @param the connection object
     */
    private String buildToolTip(ConnectionObject node) {
        // reset
        sb.setLength(0);
        
        // build the html display
        sb.append("<html>");
        sb.append(Constants.TABLE_TAG_START);
        sb.append("<tr><td><b>");
        sb.append(node.getName());
        sb.append("</b></td></tr>");
        sb.append(Constants.TABLE_TAG_END);
        sb.append("<hr>");
        sb.append(Constants.TABLE_TAG_START);
        sb.append("<tr><td>Host:</td><td width='30'></td><td>");
        sb.append(node.getDatabaseConnection().getHost());
        sb.append("</td></tr><td>Data Source:</td><td></td><td>");
        sb.append(node.getDatabaseConnection().getSourceName());
        sb.append("</td></tr><td>User:</td><td></td><td>");
        sb.append(node.getDatabaseConnection().getUserName());
        sb.append("</td></tr><td>Driver:</td><td></td><td>");
        sb.append(node.getDatabaseConnection().getDriverName());
        sb.append("</td></tr>");
        sb.append(Constants.TABLE_TAG_END);
        sb.append("</html>");
        return sb.toString();
    }

}



