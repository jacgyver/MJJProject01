/*
 * SqlKeyword.java
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


package org.executequery.gui.keywords;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines an SQL keyword.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class SqlKeyword {
  
    /** the text */
    private String text;
    
    /** the database name */
    private String databaseProductName;
    
    /** indicates a sql92 keyword */
    private boolean sql92;
    
    /** indicates a database specific keyword */
    private boolean databaseSpecific;
    
    /** indicates a user defined keyword */
    private boolean userDefined;
    
    /** Creates a new instance of SqlKeyword */
    public SqlKeyword(String text, 
                      boolean sql92, 
                      boolean databaseSpecific,
                      boolean userDefined) {
        this(text, null, sql92, databaseSpecific, userDefined);
    }

    /** Creates a new instance of SqlKeyword */
    public SqlKeyword(String text, 
                      String databaseProductName,
                      boolean sql92, 
                      boolean databaseSpecific,
                      boolean userDefined) {
        this.text = text;
        this.databaseProductName = databaseProductName;
        this.sql92 = sql92;
        this.databaseSpecific = databaseSpecific;
        this.userDefined = userDefined;
    }

    public String getText() {
        return text;
    }

    public boolean isSql92() {
        return sql92;
    }

    public boolean isDatabaseSpecific() {
        return databaseSpecific;
    }

    public boolean isUserDefined() {
        return userDefined;
    }
    
    public String toString() {
        return text;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public void setDatabaseProductName(String databaseProductName) {
        this.databaseProductName = databaseProductName;
    }

    public void setSql92(boolean sql92) {
        this.sql92 = sql92;
    }

    public void setDatabaseSpecific(boolean databaseSpecific) {
        this.databaseSpecific = databaseSpecific;
    }

    public void setUserDefined(boolean userDefined) {
        this.userDefined = userDefined;
    }
    
}



