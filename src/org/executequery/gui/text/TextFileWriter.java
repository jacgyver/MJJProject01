/*
 * TextFileWriter.java
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


package org.executequery.gui.text;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.underworldlabs.swing.FileSelector;
import org.executequery.gui.SaveFunction;
import org.executequery.components.FileChooserDialog;

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
public class TextFileWriter {
    
    /** The text to save */
    private String text;
    
    /** The path to save to */
    private String path;
    
    /** The selected file */
    private File selectedFile;
    
    /** <p>Creates a new object with the specified text to
     *  be saved to the specified path.
     *
     *  @param the text to be saved
     *  @param the path to save to
     */
    public TextFileWriter(String text, File selectedFile) {
        this.text = text;
        this.selectedFile = selectedFile;
    }
    
    /** <p>Creates a new object with the specified text to
     *  be saved to the specified path.
     *
     *  @param the text to be saved
     *  @param the path to save to
     */
    public TextFileWriter(String text, String path) {
        this.text = text;
        this.path = path;
    }
    
    /** <p>Creates a new object with the specified text to save.
     *  A call to <code>write()</code> will open a file selector
     *  dialog to retrieve the path.
     *
     *  @param the text to be saved
     */
    public TextFileWriter(String text) {
        this.text = text;
    }
    
    /**
     * Writes the content to file.
     *
     * @return the result of the process:
     *         SaveFunction.SAVE_INVALID,
     *         SaveFunction.SAVE_FAILED,
     *         SaveFunction.SAVE_CANCELLED,
     *         SaveFunction.SAVE_COMPLETE
     */
    private int writeFile() {
        
        if (path == null || path.length() == 0) {
            return SaveFunction.SAVE_INVALID;
        }
        
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(path, false), true);
            writer.println(text);
            writer.close();
            writer = null;
            SystemUtilities.addRecentOpenFile(path);
            return SaveFunction.SAVE_COMPLETE;
        }
        catch (IOException e) {
            StringBuffer sb = new StringBuffer();
            sb.append("An error occurred saving to file.").
               append("\n\nThe system returned:\n").
               append(e.getMessage());
            GUIUtilities.displayExceptionErrorDialog(sb.toString(), e);
            path = null;
            return SaveFunction.SAVE_FAILED;
        }
        
    }
    
    public int write() {
        if (showSaveDialog()) {
            int result = writeFile();

            String message = null;
            switch (result) {
                case SaveFunction.SAVE_COMPLETE:
                    message = "I/O process complete";
                    break;
                case SaveFunction.SAVE_FAILED:
                    message = "I/O process failed";
                    break;

                case SaveFunction.SAVE_INVALID:
                    message = "I/O process invalid";
                    break;
            }
            if (message != null) {
                setStatusText(message);
            }

            return result;
        } else {
            setStatusText("I/O process cancelled");
            return SaveFunction.SAVE_CANCELLED;
        }
    }
    
    private void setStatusText(String message) {
        GUIUtilities.getStatusBar().setSecondLabelText(message);
    }
    
    public File getSavedFile() {
        return new File(path);
    }
    
    private boolean showSaveDialog() {
        
        if (path != null && path.length() > 0) { // already have path
            return true;
        }
        
        FileSelector textFiles = new FileSelector(new String[] {"txt"}, "Text files");
        FileSelector sqlFiles = new FileSelector(new String[] {"sql"}, "SQL files");
        
        FileChooserDialog fileChooser = new FileChooserDialog();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(textFiles);
        fileChooser.addChoosableFileFilter(sqlFiles);
        
        if (selectedFile != null) {
            fileChooser.setSelectedFile(selectedFile);
        }

        int result = fileChooser.showSaveDialog(GUIUtilities.getParentFrame());
        if (result == JFileChooser.CANCEL_OPTION) {
            return false;
        }
        
        if (fileChooser.getSelectedFile() != null) {
            path = fileChooser.getSelectedFile().getAbsolutePath();
        }
        
        String extension = null;
        FileFilter filter = fileChooser.getFileFilter();
        
        if (filter == textFiles) {
            extension = ".txt";
        }
        else if (filter == sqlFiles) {
            extension = ".sql";
        }

        if (!path.endsWith(extension)) {
            path += extension;
        }
        
        return true;
    }
    
}






