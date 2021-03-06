/*
 * QueryEditorResultsExporter.java
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


package org.executequery.gui.editor;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.table.TableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.ActionUtilities;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.executequery.gui.importexport.ImportExportProcess;
import org.underworldlabs.swing.CharLimitedTextField;
import org.executequery.components.FileChooserDialog;
import org.underworldlabs.util.FileUtils;
import org.underworldlabs.util.MiscUtils;
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
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class QueryEditorResultsExporter extends JDialog {
    
    // column headers check
    private JCheckBox columnHeadersCheck;

    // the export type combo
    private JComboBox typeCombo;

    // the delimiter combo
    private JComboBox delimCombo;
    
    // custom delimiter field
    private JTextField customDelimField;
    
    // the file text field
    private JTextField fileNameField;
    
    // The table model to be exported
    private TableModel model;
    
    public QueryEditorResultsExporter(TableModel model) {
        super(GUIUtilities.getParentFrame(), "Export Query Results", true);
        
        this.model = model;   
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        pack();
        this.setLocation(GUIUtilities.getLocationForDialog(this.getSize()));
        setVisible(true);  
    }
    
    private void jbInit() throws Exception {
        ReflectiveAction action = new ReflectiveAction(this);       
        
        String[] delims = {"Pipe","Comma","Semi-colon","Hash","Custom"};
        delimCombo = ActionUtilities.createComboBox(action, delims, "delimeterChanged");
        
        String[] types = {"Delimited File", "Excel Spreadsheet"};
        typeCombo = ActionUtilities.createComboBox(action, types, "exportTypeChanged");
        
        customDelimField = new CharLimitedTextField(1);
        fileNameField = new JTextField();
        
        //Dimension fieldDim = new Dimension(100, 25);
        //delimCombo.setPreferredSize(fieldDim);
        
        JButton browseButton = ActionUtilities.createButton(action, "Browse", "browse");
        //Dimension brBtnDim = new Dimension(60, 25);
        //browseButton.setPreferredSize(brBtnDim);
        //Insets btnIns = new Insets(0,0,0,0);
        //browseButton.setMargin(btnIns);

        JButton okButton = ActionUtilities.createButton(action, "OK", "export");
        JButton cancelButton = ActionUtilities.createButton(action, "Cancel", "cancel");
        
        Dimension btnDim = new Dimension(75, 27);
        cancelButton.setPreferredSize(btnDim);
        okButton.setPreferredSize(btnDim);
        
        columnHeadersCheck = new JCheckBox("Include column names as first row");
        
        // the button panel
        JPanel btnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0;
        btnPanel.add(okButton, gbc);
        gbc.weightx = 0;
        gbc.gridx = 1;
        gbc.insets.left = 5;
        btnPanel.add(cancelButton, gbc);

        // the base panel
        JPanel base = new JPanel(new GridBagLayout());
        gbc.insets = new Insets(8,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.gridx = 0;
        base.add(new JLabel("Select the export type, delimiter and file path below."), gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        gbc.insets.bottom = 0;
        base.add(columnHeadersCheck, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets.bottom = 0;
        gbc.insets.top = 6;
        base.add(new JLabel("File Format:"), gbc);
        gbc.gridx = 1;
        gbc.insets.top = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(typeCombo, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.insets.bottom = 0;
        gbc.insets.top = 6;
        base.add(new JLabel("Delimiter:"), gbc);
        gbc.gridx = 1;
        gbc.insets.top = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(delimCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets.top = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        base.add(new JLabel("Custom:"), gbc);
        gbc.gridx = 1;
        gbc.insets.top = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(customDelimField, gbc);        
        gbc.insets.top = 6;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        base.add(new JLabel("File Path:"), gbc);
        gbc.insets.top = 5;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(fileNameField, gbc);
        gbc.weightx = 0;
        gbc.gridx = 2;
        gbc.insets.left = 0;
        gbc.insets.top = 3;
        gbc.fill = GridBagConstraints.NONE;
        base.add(browseButton, gbc);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets.top = 0;
        gbc.insets.bottom = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        base.add(btnPanel, gbc);
        
        Dimension baseDim = new Dimension(480, 220);
        base.setPreferredSize(baseDim);

        base.setBorder(BorderFactory.createEtchedBorder());
        
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(base, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                            GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH,
                            new Insets(5, 5, 5, 5), 0, 0));

        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        customDelimField.setEnabled(false);
    }
    
    public void dispose() {
        model = null;
        super.dispose();
    }
    
    private int getExportFormatType() {
        int index = typeCombo.getSelectedIndex();
        if (index == 0) {
            return ImportExportProcess.DELIMITED;
        }
        else {
            return ImportExportProcess.EXCEL;
        }
    }
    
    public void exportTypeChanged(ActionEvent e) {
        int index = typeCombo.getSelectedIndex();
        delimCombo.setEnabled(index == 0);
    }
    
    public void delimeterChanged(ActionEvent e) {
        int index = delimCombo.getSelectedIndex();
        boolean enableCustom = (index == 4);
        customDelimField.setEnabled(enableCustom);
        if (enableCustom) {
            customDelimField.requestFocus();
        }
    }
    
    public void browse(ActionEvent e) {
        FileChooserDialog fileChooser = new FileChooserDialog();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        fileChooser.setDialogTitle("Select Export File Path");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

        int result = fileChooser.showDialog(GUIUtilities.getParentFrame(), "Select");
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        String path = file.getAbsolutePath();
        if (getExportFormatType() == ImportExportProcess.EXCEL) {
            if (!path.endsWith(".xls")) {
                path += ".xls";
            }
        }
        fileNameField.setText(path);
    }
    
    public void cancel(ActionEvent e) {
        dispose();
    }
    
    public void export(ActionEvent e) {
        String value = fileNameField.getText();
        if (MiscUtils.isNull(value)) {
            GUIUtilities.displayErrorMessage("You must specify a file to export to.");
            return;
        }
        
        // check if it exists
        if (FileUtils.fileExists(value)) {
            int confirm = GUIUtilities.
                    displayConfirmCancelDialog("Overwrite existing file?");
            if (confirm == JOptionPane.CANCEL_OPTION) {
                return;
            } 
            else if (confirm == JOptionPane.NO_OPTION) {
                fileNameField.selectAll();
                fileNameField.requestFocus();
                return;
            }
        }
        
        if (getExportFormatType() == ImportExportProcess.DELIMITED &&
                delimCombo.getSelectedIndex() == 4) {
            value = customDelimField.getText();
            if (MiscUtils.isNull(value)) {
                GUIUtilities.displayErrorMessage(
                        "You must enter a custom delimeter");
                return;
            }
        }

        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return doExport();
            }
            public void finished() {
                GUIUtilities.displayInformationMessage(
                        "Result set export complete.");
                dispose();
            }
        };
        worker.start();   
    }

    private Object doExport() {
        if (getExportFormatType() == ImportExportProcess.DELIMITED) {
            return exportDelimited();
        }
        else {
            return exportExcel();
        }
    }
    
    private Object exportExcel() {
        HSSFWorkbook workbook = null;
        OutputStream fout = null;
        ResultsProgressDialog progressDialog = null;

        try {
            File exportFile = new File(fileNameField.getText());
            fout = new FileOutputStream(exportFile, false);

            workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Result Set Export");
            
            int rowCount = model.getRowCount();
            int columnCount = model.getColumnCount();

            progressDialog = new ResultsProgressDialog(rowCount);
            setVisible(false);
            progressDialog.pack();
            progressDialog.setLocation(GUIUtilities.getLocationForDialog(
                                                        progressDialog.getSize()));
            progressDialog.setVisible(true);

            HSSFRow row = null;
            HSSFCell cell = null;

            String EMPTY = "";
            int currentRow = 0;

            if (columnHeadersCheck.isSelected()) {
                row = sheet.createRow(currentRow);

                HSSFFont font = workbook.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                HSSFCellStyle style = workbook.createCellStyle();
                style.setFont(font);

                for (int i = 0; i < columnCount; i++) {
                    cell = row.createCell((short)i);
                    cell.setCellValue(model.getColumnName(i));
                    cell.setCellStyle(style);
                }

                style = null;
                font = null;
                currentRow++;
            }
            
            for (int i = 0; i < rowCount; i++) {

                row = sheet.createRow(currentRow);
                for (short j = (short)0; j < columnCount; j++) {
                    Object value = model.getValueAt(i, j);
                    cell = row.createCell(j);
                    cell.setCellValue(value != null ? value.toString() : EMPTY);
                }
                currentRow++;
                progressDialog.increment(i+1);

            }
            
            workbook.write(fout);
            return "done";
        }
        catch (Exception e) {
            String message = "Error writing to file:\n\n" + e.getMessage();
            GUIUtilities.displayExceptionErrorDialog(message, e);
            return "failed";
        }
        finally {
            if (progressDialog != null && progressDialog.isVisible()) {
                progressDialog.dispose();
                progressDialog = null;
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {}
            }
            fout = null;
            workbook = null;
        }
    }
    
    private Object exportDelimited() {
        int delimIndex = delimCombo.getSelectedIndex();
        char delim = 0;
        
        switch (delimIndex) {
            case 0:
                delim = '|';
                break;
            case 1:
                delim = ',';
                break;
            case 2:
                delim = ';';
                break;
            case 3:
                delim = '#';
                break;
            case 4:
                delim = customDelimField.getText().charAt(0);
                break;
        }
        
        ResultsProgressDialog progressDialog = null;
        PrintWriter writer = null;
        File exportFile = null;
        
        try {
            exportFile = new File(fileNameField.getText());
            
            String EMPTY = "";
            StringBuffer rowLines = new StringBuffer(5000);
            writer = new PrintWriter(new FileWriter(exportFile, false), true);
            
            int rowCount = model.getRowCount();
            int columnCount = model.getColumnCount();
            
            progressDialog = new ResultsProgressDialog(rowCount);
            setVisible(false);
            progressDialog.pack();
            progressDialog.setLocation(GUIUtilities.getLocationForDialog(
                                                        progressDialog.getSize()));
            progressDialog.setVisible(true);

            if (columnHeadersCheck.isSelected()) {
                for (int i = 0; i < columnCount; i++) {
                    rowLines.append(model.getColumnName(i));
                    if (i != columnCount - 1) {
                        rowLines.append(delim);
                    }
                }
                writer.println(rowLines.toString());
                rowLines.setLength(0);
            }
            
            for (int i = 0; i < rowCount; i++) {

                for (int j = 0; j < columnCount; j++) {
                    
                    Object value = model.getValueAt(i, j);
                    rowLines.append(value != null ? value : EMPTY);

                    if (j != columnCount - 1) {
                        rowLines.append(delim);
                    }
                    
                }
                
                writer.println(rowLines.toString());
                rowLines.setLength(0);
                progressDialog.increment(i+1);

            }
            /*
            for (int i = 0; i < totalRows; i++) {
                
                ArrayList row = (ArrayList)model.getRowValueAt(i);
                
                for (int j = 0; j < columnCount; j++) {
                    Object value = row.get(j);
                    rowLines.append(value != null ? value : EMPTY);
                    
                    if (j != columnCount - 1) {
                        rowLines.append(delim);
                    }
                    
                }
                
                writer.println(rowLines.toString());
                rowLines.setLength(0);
                progressDialog.increment(i+1);
            } */
            
            return "done";
        }
        catch (Exception e) {
            String message = "Error writing to file:\n\n" + e.getMessage();
            GUIUtilities.displayExceptionErrorDialog(message, e);
            return "failed";
        }
        finally {
            if (progressDialog != null && progressDialog.isVisible()) {
                progressDialog.dispose();
                progressDialog = null;
            }
            if (writer != null) {
                writer.close();
            }
        }
        
    }
    
    
    class ResultsProgressDialog extends JDialog {
        // the progess bar
        private JProgressBar progressBar;

        public ResultsProgressDialog(int recordCount) {
            super(GUIUtilities.getParentFrame(), "Exporting Query Results", false);
            progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, recordCount);
            
            JPanel base = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.insets = new Insets(5,5,5,5);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            base.add(new JLabel("Exporting result set..."), gbc);
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets.top = 0;
            gbc.ipadx = 180;
            gbc.insets.bottom = 10;
            gbc.fill = GridBagConstraints.BOTH;
            base.add(progressBar, gbc);

            base.setBorder(BorderFactory.createEtchedBorder());
            Container c = this.getContentPane();
            c.setLayout(new GridBagLayout());
            c.add(base, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                GridBagConstraints.SOUTHEAST, 
                                GridBagConstraints.BOTH,
                                new Insets(5, 5, 5, 5), 0, 0));

            setResizable(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));            
        }

        public void increment(int value) {
            progressBar.setValue(value);
        }

        public void dispose() {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            setVisible(false);
            super.dispose();
        }

    } // class ResultsProgressDialog
    
}




