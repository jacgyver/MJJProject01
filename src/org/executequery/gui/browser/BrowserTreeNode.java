/*
 * BrowserTreeNode.java
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

import javax.swing.tree.DefaultMutableTreeNode;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class BrowserTreeNode extends DefaultMutableTreeNode {
    
    private boolean typeParent;
    private boolean expanded;
    private DatabaseObject userObject;

    public BrowserTreeNode(DatabaseObject userObject, 
                           boolean allowsChildren) {
        this(userObject, allowsChildren, true);
    }

    public BrowserTreeNode(DatabaseObject userObject, 
                           boolean allowsChildren,
                           boolean typeParent) {
        super(userObject, allowsChildren);
        this.userObject = userObject;
        this.typeParent = typeParent;
        expanded = false;
    }

    public int getNodeType() {
        return userObject.getType();
    }

    /**
     * Returns whether this is the parent node of this type.
     *
     * @return true | false
     */
    public boolean isTypeParent() {
        return typeParent;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public String toString() {
        return userObject.toString();
    }

    public DatabaseObject getDatabaseUserObject() {
        return userObject;
    }

    public boolean isLeaf() {
        if (userObject.getType() == BrowserConstants.HOST_NODE) {
            return super.isLeaf();
        } else {
            return !allowsChildren;
        }
    }
    
}



