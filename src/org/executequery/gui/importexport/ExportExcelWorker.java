/*
 * ExportExcelWorker.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.executequery.Constants;
import org.executequery.util.Log;
import org.underworldlabs.jdbc.DataSourceException;
import org.underworldlabs.swing.util.SwingWorker;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/09/05 12:07:26 $
 */
public class ExportExcelWorker extends AbstractImportExportWorker
                               implements Constants {
    
    /** The <code>SwingWorker</code> object for this process */
    private SwingWorker worker;
    
    /** The controlling object for this process */
    private ImportExportExcelPanel _parent;
    
    /**
     * Constructs a new instance with the specified parent
     * object - an instance of <code>ImportExportExcelPanel</code>.
     */
    public ExportExcelWorker(ImportExportExcelPanel parent,
                             ImportExportProgressPanel progressPanel) {
        super(parent, progressPanel);
        this._parent = parent;
        transferData();
    }
    
    /** <p>Begins the transfer process setting up the
     *  <code>SwingWorker</code> and creating the progress
     *  dialog.
     */
    private void transferData() {
        reset();
        
        // create the worker
        worker = new SwingWorker() {
            public Object construct() {
                return doWork();
            }
            public void finished() {
                String result = (String)get();
                setResult(result);

                releaseResources(_parent.getDatabaseConnection());

                printResults();
                setProgressStatus(-1);
                _parent.setProcessComplete(result == SUCCESS);
            }
        };
        worker.start();
    }
    
    /** <p>Performs the actual processing for the worker. */
    private Object doWork() {

        appendProgressText("Beginning export to Excel spreadsheet process...");
        appendProgressText("Using connection: " + 
                getParent().getDatabaseConnection().getName());

        // record the start time
        start();
        
        ResultSet rset = null;
        int errorCount = 0;
        int totalRecordCount = 0;
        
        FileOutputStream fout = null;
        try {

            int fileFormat = _parent.getMutlipleTableTransferType();
            boolean isSingleTable = (_parent.getTableTransferType() == 
                                            ImportExportProcess.SINGLE_TABLE);

            Vector files = _parent.getDataFileVector();
            String[] tablesArray = _parent.getSelectedTables();
            
            // ---------------------------
            // --- initialise counters ---
            // ---------------------------
            int columnCount = -1;
            int recordCount = 0;
            int ssRowCount = 0;
            int totalRecords = 0;
            
            HSSFWorkbook workbook = null;
            HSSFRow row = null;
            HSSFCell cell = null;
            
            String[][] sheetNames = _parent.getSheetNameValues();
            
            // ----------------------------------------
            // --- begin looping through the tables ---
            // ----------------------------------------
            
            for (int i = 0; i < tablesArray.length; i++) {
                
                tableCount++;
                setProgressStatus(0);

                String tableName = tablesArray[i];
                DataTransferObject dto = null;
                if (isSingleTable) {
                    dto = (DataTransferObject)files.elementAt(i);
                }
                else {
                    if (fileFormat == ImportExportProcess.MULTIPLE_FILE) {
                        dto = (DataTransferObject)files.elementAt(i);
                    } else {
                        dto = (DataTransferObject)files.elementAt(0);
                    }
                }
                
                totalRecords = getTableRecordCount(tableName);
                setProgressBarMaximum(totalRecords);

                // initialise the file object
                File exportFile = new File(dto.getFileName());
                
                // append some output
                outputBuffer.append("---------------------------\nTable: ");
                outputBuffer.append(tableName);
                outputBuffer.append("\nRecords found: ");
                outputBuffer.append(totalRecords);
                outputBuffer.append("\nExport file: ");
                outputBuffer.append(exportFile.getName());
                appendProgressText(outputBuffer);

                // retrieve the columns to be exported (or all)
                Vector columns = getColumns(tableName);
                columnCount = columns.size();

                // initialise the output stream
                fout = new FileOutputStream(exportFile, false);

                String sheetName = tablesArray[i];
                if (!isSingleTable) {
                    sheetName = sheetNames[i][1];
                }

                if (workbook == null) {
                    workbook = new HSSFWorkbook();
                }
                HSSFSheet sheet = workbook.createSheet(sheetName);

                appendProgressText("Exporting data...");
                
                // retrieve the result set
                rset = getResultSet(tableName, columns);

                while (rset.next()) {
                    
                    if (Thread.interrupted()) {
                        rset.close();
                        rset = null;
                        fout.close();
                        setProgressStatus(totalRecords);
                        throw new InterruptedException();
                    }
                    
                    if (recordCount == 0 && _parent.includeColumnNames()) {

                        row = sheet.createRow(ssRowCount);
                        
                        HSSFFont font = workbook.createFont();
                        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                        
                        HSSFCellStyle style = workbook.createCellStyle();
                        style.setFont(font);
                        
                        for (int j = 0; j < columnCount; j++) {
                            cell = row.createCell((short)j);
                            cell.setCellValue(columns.elementAt(j).toString());
                            cell.setCellStyle(style);
                        }
                        
                        style = null;
                        font = null;
                        ssRowCount++;
                        
                    }
                    
                    row = sheet.createRow(ssRowCount);
                    setProgressStatus(recordCount);
                    
                    for (short j = (short)0; j < columnCount; j++) {
                        String cellValue = rset.getString(j + 1);
                        cell = row.createCell(j);
                        cell.setCellValue(cellValue != null ? cellValue : EMPTY);                        
                    }
                    
                    ssRowCount++;
                    recordCount++;
                    totalRecordCount++;
                    
                }                
                rset.close();
                
                if (isSingleTable || fileFormat == ImportExportProcess.MULTIPLE_FILE) {
                    workbook.write(fout);
                    fout.close();
                    fout = null;
                    workbook = null;
                }
                
                setProgressStatus(totalRecords);
                
                recordCount = 0;
                ssRowCount = 0;

                outputBuffer.append("Export successful for table: ");
                outputBuffer.append(tableName);
                appendProgressText(outputBuffer);
                
            }
            
            if (!isSingleTable && fileFormat == ImportExportProcess.SINGLE_FILE) {                
                workbook.write(fout);
                if (fout != null) {
                    fout.close();
                }                
                workbook = null;
            }
            
            fout = null;
            return SUCCESS;
        }        
        catch (InterruptedException e) {
            cancelStatement();
            return CANCELLED;
        }
        catch (SQLException e) {
            logException(e);
            outputExceptionError("SQL error exporting table data to file", e);
            return FAILED;
        }
        catch (DataSourceException e) {
            logException(e);
            outputExceptionError("Error exporting table data to file", e);
            return FAILED;
        }
        catch (IOException e) {
            logException(e);
            outputExceptionError("I/O error exporting table data to file", e);
            return FAILED;
        }
        catch (OutOfMemoryError e) {
            outputExceptionError("Error exporting table data to file", e);
            return FAILED;
        }
        finally {
            
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {}
            }
            
            finish();
            setTableCount(tableCount);
            setRecordCount(totalRecordCount + errorCount);
            setErrorCount(errorCount);
            setRecordCountProcessed(totalRecordCount);
        }

    }
    
    private void logException(Throwable e) {
        if (Log.isDebugEnabled()) {
            Log.debug("Error on Excel export.", e);
        }
    }
    
    /** 
     * Cancels an in progress SQL statement. 
     */
    private void cancelStatement() {
        try {
            if (stmnt != null) {
                stmnt.cancel();
                stmnt.close();
            }
        } catch (SQLException e) {}
    }
    
    /** 
     * Cancels the current in-process transfer. 
     */
    public void cancelTransfer() {
        worker.interrupt();
        _parent.cancelTransfer();
    }
    
    /** 
     * Indicates that the process has completed. 
     */
    public void finished() {}
    
    
}



