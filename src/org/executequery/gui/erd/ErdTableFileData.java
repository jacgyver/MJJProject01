/*
 * ErdTableFileData.java
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


package org.executequery.gui.erd;

import java.awt.Rectangle;

import java.io.Serializable;

import java.util.Hashtable;

import org.executequery.gui.browser.ColumnData;

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
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ErdTableFileData implements Serializable {
    
    /** The table name displayed */
    private String tableName;
    /** The table's columns */
    private ColumnData[] columns;
    
    /** The CREATE TABLE script for a new table */
    private String createTableScript;
    /** The ALTER TABLE script for a definition change */
    private String alterTableScript;
    /** The ALTER TABLE script for a constraint change */
    private String addConstraintScript;
    /** The ALTER TABLE script for a constraint drop */
    private String dropConstraintScript;
    /** <code>Hashtable</code> containing table modifications */
    private Hashtable alterTableHash;
    /** The table bounds */
    private Rectangle tableBounds;
    
    public ErdTableFileData() {}
    
    public Rectangle getTableBounds() {
        return tableBounds;
    }
    
    public void setTableBounds(Rectangle tableBounds) {
        this.tableBounds = tableBounds;
    }
    
    public void setAddConstraintScript(String addConstraintScript) {
        this.addConstraintScript = addConstraintScript;
    }
    
    public String getAddConstraintScript() {
        return addConstraintScript;
    }
    
    public void setDropConstraintScript(String dropConstraintScript) {
        this.dropConstraintScript = dropConstraintScript;
    }
    
    public String getDropConstraintScript() {
        return dropConstraintScript;
    }
    
    public Hashtable getAlterTableHash() {
        return alterTableHash;
    }
    
    public void setAlterTableHash(Hashtable alterTableHash) {
        this.alterTableHash = alterTableHash;
    }
    
    public String getAlterTableScript() {
        return alterTableScript;
    }
    
    public void setAlterTableScript(String alterTableScript) {
        this.alterTableScript = alterTableScript;
    }
    
    public String getCreateTableScript() {
        return createTableScript;
    }
    
    public void setCreateTableScript(String createTableScript) {
        this.createTableScript = createTableScript;
    }
    
    public ColumnData[] getColumnData() {
        return columns;
    }
    
    public void setColumnData(ColumnData[] columns) {
        this.columns = columns;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    
}






