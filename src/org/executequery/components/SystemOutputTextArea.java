/*
 * SystemOutputTextArea.java
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


package org.executequery.components;

import java.awt.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.underworldlabs.util.SystemProperties;

import org.underworldlabs.util.DateUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/04 07:16:47 $
 */
public class SystemOutputTextArea extends JTextArea {
    
    /** The system's output stream */
    private OutputStream logger;
    /** The stream to print the output to file */
    private PrintStream toFile;
    /** The log file to print to */
    private File logFile;
    
    /** reusable characters */
    private static final char NEW_LINE_CHAR = '\n';
    private static final String SPACE = " ";
    private static final String NEW_LINE = "\n";
    private static final char RETURN = System.getProperty("line.separator").charAt(0);
    /** --------------------- */
    
    /** default font for the output area */
    private static final Font defaultFont = new Font("dialog", 0, 11);
    
    /** The default path to the log file */
    private static final String DEFAULT_LOG_PATH = "/logs/system.log";
    
    /** the output file path */
    private String outputFilePath;
    /** Utility to retrieve the time */
    private DateUtils dt;
    /** The string to print */
    private String printString;
    
    public SystemOutputTextArea() {
        this(DEFAULT_LOG_PATH, false);
    }
    
    public SystemOutputTextArea(boolean editable) {
        this(DEFAULT_LOG_PATH, editable);
    }
    
    public SystemOutputTextArea(String _outputFilePath, boolean editable) {
        this(new File(_outputFilePath), editable);
    }
    
    public SystemOutputTextArea(File outputFile, boolean editable) {
        
        logger = new SystemLogger();
        dt = new DateUtils("[HH:mm:ss] ");
        
        try {
            
            if (outputFile == null) {
                outputFilePath = System.getProperty("executequery.user.home.dir") +
                DEFAULT_LOG_PATH;
                logFile = new File(outputFilePath);
            }
            
            else {
                
                if (!outputFile.exists())
                    outputFile.createNewFile();
                
                logFile = outputFile;
                outputFilePath = outputFile.getAbsolutePath();
            }
            
            jbInit();
            setEditable(editable);
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void jbInit() throws Exception {
        
        try {

            if (logFile.exists() && logFile.length() > 1024000) {
                resetSystemLog();
            }
            
            toFile = new PrintStream(new FileOutputStream(logFile, true));
            setFont(defaultFont);
            
        }
        catch (FileNotFoundException fnExc) {
            setNewOutputFile();
        }
        
    }
    
    public void localeChanged() {
        String timezone = SystemProperties.getStringProperty("user", "locale.timezone");
        String language = SystemProperties.getStringProperty("user", "locale.language");
        String country = SystemProperties.getStringProperty("user", "locale.country");
        dt.resetTimeZone(timezone, language, country);
    }
    
    public void resetSystemLog() {
        try {
            
            if (toFile != null) {
                toFile.close();
            }
           
            toFile = new PrintStream(new FileOutputStream(outputFilePath, false));
            toFile.print("");
            toFile.close();

            toFile = new PrintStream(new FileOutputStream(outputFilePath, true));
            
        }
        catch (FileNotFoundException fnExc) {
            setNewOutputFile();
        }
        
    }
    
    public void shuttingDown() {
        if (toFile != null) {
            toFile.print(dt.getFormattedDate() + "System exiting..." + RETURN);
            toFile.close();
        }
    }
    
    private void setNewOutputFile() {
        
        try {
            
            if (toFile != null) {
                toFile.close();
            }
            toFile = new PrintStream(new FileOutputStream(logFile, true));
            
        } catch (FileNotFoundException fnExc) {}
        
    }
    
    private void addOutput(final String s) {
        Runnable update = new Runnable() {
            public void run() {
                printString = (s.replaceAll(NEW_LINE, SPACE)).trim();
                append(printString + NEW_LINE);
                setCaretPosition(getText().length());
                toFile.print(printString + RETURN);
            }
        };        
        SwingUtilities.invokeLater(update);
    }
    
    public OutputStream getOutputStream() {
        return logger;
    }
    
    
    class SystemLogger extends OutputStream {
        
        private StringBuffer buf;
        
        public SystemLogger() {
            buf = new StringBuffer();
        }
        
        public synchronized void write(int b) {
            b &= 0x000000FF;
            char c = (char) b;
            buf.append(String.valueOf(c));
        }
        
        public synchronized void write(byte[] b, int offset, int length) {
            buf.append(new String(b, offset, length));
        }
        
        public synchronized void write(byte[] b) {
            buf.append(new String(b));
        }
        
        public synchronized void flush() {
            synchronized (buf) {
                
                if (buf.length() > 0) {
                    char last = buf.charAt(buf.length() - 1);
                    
                    if (last == NEW_LINE_CHAR || last == RETURN) {
                        dt.reset();
                        addOutput(dt.getFormattedDate() + buf.toString());
                        buf.setLength(0);
                    }
                    
                }
                
            }
            
        } 
        
    } // SystemLogger

}



