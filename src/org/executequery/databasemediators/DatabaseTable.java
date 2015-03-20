/*
 * DatabaseTable.java
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


package org.executequery.databasemediators;
import org.executequery.gui.browser.ColumnConstraint;
import org.executequery.gui.browser.ColumnData;
import org.executequery.gui.browser.ColumnIndex;
import org.executequery.gui.browser.DatabaseObject;
import org.executequery.gui.browser.TablePrivilege;

// ----------------------------------
// this is a new consolidating object to be used
// throughout in place of ColumnData and similar 
// combinations.
//-----------------------------------

// TODO: apply this EVERYWHERE


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines a database table.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class DatabaseTable {
    
    /** the name of the associated schema */
    private String schema;

    /** the name of this node */
    private String name;

    /** the name of the associated catalog */
    private String catalog;

    /** the table's columns */
    private ColumnData[] columns;
    
    /** the table's constraints */
    private ColumnConstraint[] constraints;

    /** the table's privileges */
    private TablePrivilege[] privileges;
    
    /** the table's indexes */
    private ColumnIndex[] indexes;

    /** Creates a new instance of DatabaseTable */
    public DatabaseTable() {}

    /** Creates a new instance of DatabaseTable */
    public DatabaseTable(String name) {
        this(name, null);
    }

    /** Creates a new instance of DatabaseTable */
    public DatabaseTable(String name, ColumnData[] columnData) {
        this.name = name;
        this.columns = columnData;
    }

    public void setValues(DatabaseObject object) {
        catalog = object.getCatalogName();
        schema = object.getSchemaName();
        name = object.getName();
    }
    
    public ColumnData[] getColumns() {
        return columns;
    }

    public void setColumns(ColumnData[] columns) {
        this.columns = columns;
    }

    public boolean hasColumns() {
        return columns != null && columns.length > 0;
    }
    
    public ColumnConstraint[] getConstraints() {
        return constraints;
    }

    public void setConstraints(ColumnConstraint[] constraints) {
        this.constraints = constraints;
    }

    public boolean hasConstraints() {
        return constraints != null && constraints.length > 0;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public TablePrivilege[] getPrivileges() {
        return privileges;
    }

    public void setPrivileges(TablePrivilege[] privileges) {
        this.privileges = privileges;
    }

    public boolean hasPrivileges() {
        return privileges != null && privileges.length > 0;
    }

    public ColumnIndex[] getIndexes() {
        return indexes;
    }

    public void setIndexes(ColumnIndex[] indexes) {
        this.indexes = indexes;
    }

    public boolean hasIndexes() {
        return indexes != null && indexes.length > 0;
    }

    public String toString() {
        return name;
    }

}



