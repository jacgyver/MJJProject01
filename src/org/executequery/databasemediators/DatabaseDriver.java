/*
 * DatabaseDriver.java
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

import org.executequery.DatabaseDefinitionCache;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class DatabaseDriver {
    
    private long id;
    private String name;
    private int type;
    private String path;
    private String className;
    private String url;
    private String description;
    
    public DatabaseDriver() {}

    public DatabaseDriver(long id, String name) {
        this.id = id;
        this.name = name;
        type = DatabaseDefinitionCache.INVALID_DATABASE_ID;
    }

    public DatabaseDriver(String name) {
        this(0, name);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description == null ? "Not Available" : description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getURL() {
        return url;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
    
    public int getType() {
        return type;
    }
    
    public void setDatabaseType(int type) {
        this.type = type;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof DatabaseDriver)) {
            return false;
        }        
        DatabaseDriver dd = (DatabaseDriver)obj;
        return dd.getName().equals(name);        
    }
    
    public String toString() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}






