/*
 * DatabaseProcedure.java
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

import java.util.Vector;

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
public class DatabaseProcedure {
    
    private String name;
    private Vector parameters;
    private String schema;
    
    public DatabaseProcedure() {
        parameters = new Vector();
    }
    
    public DatabaseProcedure(String schema, String name) {
        this();
        this.name = name;
        this.schema = schema;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean hasParameters() {
        return parameters.size() > 0;
    }
    
    public String getName() {
        return name;
    }
    
    public void addParameter(String name, int type, int dataType,
                             String sqlType, int size) {
        parameters.add(new ProcedureParameter(name, type, dataType, sqlType, size));
    }
    
    public ProcedureParameter[] getParameters() {
        return (ProcedureParameter[])parameters.toArray(new
                                       ProcedureParameter[parameters.size()]);
    }
    
    public String toString() {
        return name;
    }
    
    public String getSchema() {
        return schema;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
}



