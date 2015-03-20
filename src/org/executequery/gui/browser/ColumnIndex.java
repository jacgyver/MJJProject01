/*
 * ColumnIndex.java
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

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * This object maintains table index data
 * as retrieved from the <code>DatabaseMetaData</code>
 * method <code>getIndexInfo(...)</code> for a particular table 
 * as selected within the Database Browser.<br>
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class ColumnIndex  {
    
    /** Whether the index is non-unique */
    private boolean non_unique;
    
    /** The index name */
    private String name;
    
    /** The indexed column */
    private String column;
    
    /** The index type */
    private int type;
    
    public ColumnIndex() {}
    
    public void setIndexType(int type) {
        this.type = type;
    }
    
    public void setIndexedColumn(String column) {
        this.column = column;
    }
    
    public String getIndexedColumn() {
        return column;
    }
    
    public String getIndexName() {
        return name;
    }
    
    public void setIndexName(String name) {
        this.name = name;
    }
    
    public void setNonUnique(boolean non_unique) {
        this.non_unique = non_unique;
    }
    
    public boolean isNonUnique() {
        return non_unique;
    }
    
    public String toString() {
        return name;
    }
    
}





