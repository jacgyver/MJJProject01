/*
 * ProcedureParameter.java
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

import java.sql.DatabaseMetaData;

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
public class ProcedureParameter {
    
    private String name;
    private int type;
    private int dataType;
    private String sqlType;
    private int size;
    private String value;
    
    private static final String RESULT_STORE = "< Result Store >";
    private static final String RETURN_VALUE = "< Return Value >";
    private static final String UNKNOWN = "< Unknown >";
    
    public ProcedureParameter(String name, int type, int dataType,
                              String sqlType, int size) {
        this.name = name;
        this.type = type;
        this.dataType = dataType;
        this.sqlType = sqlType;
        this.size = size;
    }
    
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
    
    public int getDataType() {
        return dataType;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }
    
    public String getSqlType() {
        return sqlType;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        
        if (name == null) {
            
            if (type == DatabaseMetaData.procedureColumnResult)
                return RESULT_STORE;
            
            else if (type == DatabaseMetaData.procedureColumnReturn)
                return RETURN_VALUE;
            
            else
                return UNKNOWN;
            
        } 
        
        return name;
    }
    
    public String toString() {
        return getName();
    }
    
}



