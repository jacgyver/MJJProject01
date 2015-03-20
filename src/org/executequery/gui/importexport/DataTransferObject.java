/*
 * DataTransferObject.java
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


package org.executequery.gui.importexport;

import java.io.File;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines a single table row with all relevant data 
 * for the transfer - table name and path to data file.
 */
/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class DataTransferObject {
    
    /** The table name */
    private String tableName;
    
    /** The path to the data file */
    private String fileName;
    
    /** 
     * Constructs a new instance with the specified table name.
     * 
     * @param the table name 
     */
    public DataTransferObject(String tableName) {
        this.tableName = tableName;
    }
    
    /** 
     * Getter method for the table name.
     * 
     * @return the table name 
     */
    public String getTableName() {
        return tableName;
    }
    
    /** 
     * Getter method for the table name.
     * 
     * @return the table name 
     */
    public String getFileName() {
        return fileName;
    }
    
    /** <p>Setter method for the file name.
     *  @param the file name */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public boolean hasDataFile(int type) {
        
        if (type == ImportExportProcess.IMPORT && fileName != null) {
            File file = new File(fileName);
            return file.isFile() && file.exists();
        } else {
            return fileName != null && fileName.length() > 0;
        }
    }
    
    /** <p>Setter method for the table name.
     *  @param the table name */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String toString() {
        return tableName;
    }
    
}






