/*
 * TableModifier.java
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


package org.executequery.gui.table;

import org.executequery.databasemediators.MetaDataValues;

// defines those objects with table functions requiring sql output
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
public interface TableModifier extends CreateTableSQLSyntax {
    
    int COLUMN_VALUES = 0;
    int CONSTRAINT_VALUES = 1;
    
    /** <p>Generates and prints the SQL text. */
    public void setSQLText();
    
    /** <p>Generates and prints the SQL text with the
     *  specified values as either column values or
     *  constraints values depending on the type parameter.
     *
     *  @param the values to add to the SQL
     *  @param the type of values - column or constraint
     */
    public void setSQLText(String values, int type);
    
    /** 
     * Retrieves the currently selected/created table name.
     *
     * @return the table name
     */
    public String getTableName();
    
}






