/*
 * DatabaseObject.java
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

import org.executequery.databasemediators.DatabaseConnection;

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
public class DatabaseObject {
    
    /** the node type */
    private int type;

    /** whether this a system type node */
    private boolean systemObject;
    
    /** the name of the associated schema */
    private String schemaName;
    
    // a 'parent' entity name - eg table name for a column
    private String parentName;

    /** the name of the associated catalog */
    private String catalogName;
    
    /** the meta data key identifier (@see BrowserConstants)*/
    private String metaDataKey;
    
    /** the name of this node */
    private String name;

    /** any remarks associated with this object */
    private String remarks;
    
    /** whether this is the default connected catalog */
    private boolean defaultCatalog;
    
    private boolean useInQuery;
    
    public DatabaseObject() {
        useInQuery = true;
    }
    
    public DatabaseObject(int type, String name) {
        useInQuery = true;
        this.name = name;
        this.type = type;
    }
        
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public String getSchemaName() {
        return schemaName;
    }
    
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
    
    public String getCatalogName() {
        return catalogName;
    }
    
    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }
    
    public String getMetaDataKey() {
        return metaDataKey;
    }
    
    public void setMetaDataKey(String metaDataKey) {
        this.metaDataKey = metaDataKey;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public boolean isDefaultCatalog() {
        return defaultCatalog;
    }
    
    public void setDefaultCatalog(boolean defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public boolean isUseInQuery() {
        return useInQuery;
    }

    public void setUseInQuery(boolean useInQuery) {
        this.useInQuery = useInQuery;
    }

    public boolean isSystemObject() {
        return systemObject;
    }

    public void setSystemObject(boolean systemObject) {
        this.systemObject = systemObject;
    }
    
}



