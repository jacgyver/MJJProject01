/*
 * ImportExportProcess.java
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

import java.awt.Dimension;

import java.util.Vector;
import javax.swing.JDialog;
import org.executequery.databasemediators.DatabaseConnection;

import org.executequery.databasemediators.MetaDataValues;
import org.executequery.gui.browser.ColumnData;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * Interface defining an import or export
 * process. This interface will be implemented
 * by those classes handling data transfer tasks
 * including both the delimited file import/export
 * and the XML file import/export.
 *
 * <p>Retrieval of common information within each
 * process is defined in addition to some minor
 * view information such as the size of child
 * components within the 'wizard' type functionality
 * that is each process.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/06/14 15:09:00 $
 */
public interface ImportExportProcess {
    
    // ------------------------------------------------
    // ---- Import/Export process types constants -----
    // ------------------------------------------------
    
    public static final int XML = 12;
    public static final int DELIMITED = 13;
    public static final int EXCEL = 14;
    
    /** The import from XML process */
    public static final int IMPORT_XML = 0;
    /** The export to XML process */
    public static final int EXPORT_XML = 1;
    /** The import from delimited file process */
    public static final int IMPORT_DELIMITED = 2;
    /** The export to delimited file process */
    public static final int EXPORT_DELIMITED = 3;
    /** The export process */
    public static final int IMPORT = 4;
    /** The import process */
    public static final int EXPORT = 5;
    /** Denotes a single table export process */
    public static final int SINGLE_TABLE = 6;
    /** Denotes a multiple table export process */
    public static final int MULTIPLE_TABLE = 7;
    /** On error log and continue */
    public static final int LOG_AND_CONTINUE = 8;
    /** On error stop transfer */
    public static final int STOP_TRANSFER = 9;
    /** A single file export - multiple table */
    public static final int SINGLE_FILE = 10;
    /** A multiple file export - multiple table */
    public static final int MULTIPLE_FILE = 11;
    
    /** indicator for commit and end of file */
    public static final int COMMIT_END_OF_FILE = -99;

    /** indicator for commit and end of all files */
    public static final int COMMIT_END_OF_ALL_FILES = -98;

    /** XML format with schema element */
    public static final int SCHEMA_ELEMENT = 0;

    /** XML format with table element only */
    public static final int TABLE_ELEMENT = 1;

    /**
     * Returns the transfer format - XML, CSV etc.
     */
    public int getTransferFormat();
    
    /** 
     * Stops the current process. 
     */
    public void stopTransfer();
    
    /** 
     * Flags the current transfer process as cancelled. 
     */
    public void cancelTransfer();
    
    /** 
     * Retrieves the selected rollback size for the transfer.
     *
     * @return the rollback size
     */
    public int getRollbackSize();
    
    /** 
     * Retrieves the action on an error occuring during the import/export process.
     *
     * @return the action on error -<br>either:
     *          <code>ImportExportProcess.LOG_AND_CONTINUE</code> or
     *          <code>ImportExportProcess.STOP_TRANSFER</code>
     */
    public int getOnError();
    
    /** 
     * Retrieves the date format for date fields contained within 
     * the data file/database table.
     *
     * @return the date format (ie. ddMMyyy)
     */
    public String getDateFormat();
    
    /**
     * Returns whether to parse date values.
     *
     * @return true | false
     */
    public boolean parseDateValues();

    /** 
     * Retrieves the column names for this process.
     *
     * @return the column names
     */
    public Vector<ColumnData> getSelectedColumns();
    
    /** 
     * Retrieves the size of the child panel to be added to the main base panel.
     *
     *  @return the size of the child panel
     */
    public Dimension getChildDimension();
    
    /** 
     * Retrieves the <code>MetaDataValues</code> object defined for this process.
     *
     * @return the <code>MetaDataValues</code> helper class
     */
    public MetaDataValues getMetaDataUtility();
    
    /** 
     * Returns the type of transfer - single or multiple table.
     *
     * @return the type of transfer
     */
    public int getTableTransferType();
    
    /** 
     * Begins an import process. 
     */
    public void doImport();
    
    /** 
     * Begins an export process. 
     */
    public void doExport();
    
    /** 
     * Returns the type of transfer - import or export.
     *
     * @return the transfer type - import/export
     */
    public int getTransferType();
    
    /** 
     * Retrieves the selected tables for this process.
     *
     * @return the selected table names
     */
    public String[] getSelectedTables();
    
    /** 
     * Retrieves the table name for this process in the case 
     * of a single table import/export.
     *
     * @return the table name
     */
    public String getTableName();
    
    /** 
     * Returns the type of multiple table transfer - single or multiple file.
     *
     * @return the type of multiple table transfer
     */
    public int getMutlipleTableTransferType();
    
    /**
     * Returns the schema name where applicable.
     *
     * @return the schema name
     */
    public String getSchemaName();
    
    /**
     * Returns the selected database connection properties object.
     *
     * @return the connection properties object
     */
    public DatabaseConnection getDatabaseConnection();

    /**
     * Returns the XML format style for an XML import/export.
     *
     * @return the XML format
     */
    public int getXMLFormat();

    /** 
     * Indicates whether the process (import only) should 
     * be run as a batch process.
     *
     * @return whether to run as a batch process
     */
    public boolean runAsBatchProcess();

    /** 
     * Returns a <code>Vector</code> of <code>DataTransferObject</code> 
     * objects containing all relevant data for the process.
     *
     * @return a <code>Vector</code> of <code>DataTransferObject</code> objects
     */
    public Vector getDataFileVector();

    /**
     * Returns whether to include column names as the
     * first row of a delimited export process.
     *
     * @return true | false
     */
    public boolean includeColumnNames();

    /** 
     * Retrieves the selected type of delimiter within
     * the file to be used with this process.
     *
     * @return the selected delimiter
     */
    public char getDelimiter();

    /**
     * Indicates the process has completed successfully or
     * otherwise as indicated.
     *
     * @param success - true | false
     */
    public void setProcessComplete(boolean success);

    /**
     * Returns whether to trim whitespace on column data values.
     *
     * @return true | false
     */
    public boolean trimWhitespace();
    
    /**
     * Returns the dialog container for this process.
     *
     * @return the dialog or null if there is no dialog
     */
    public JDialog getDialog();
    
}





