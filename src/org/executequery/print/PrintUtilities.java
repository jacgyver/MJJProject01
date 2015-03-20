/*
 * PrintUtilities.java
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


package org.executequery.print;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.JOptionPane;

import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.executequery.util.Log;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Utility class aiding printing.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PrintUtilities {
    
    /** The page setup file */
    private static final String PAGE_SETUP_FILE = SystemUtilities.getUserPropertiesPath() +
    "print.setup";
    /** The print attribute set */
    private static PrintRequestAttributeSet format;
    /** The printer job */
    private static PrinterJob job;
    
    private static void savePrintSetup() {
        File setupFile = new File(PAGE_SETUP_FILE);        
        try {
            FileOutputStream fileOut = new FileOutputStream(setupFile);
            ObjectOutputStream obOut = new ObjectOutputStream(fileOut);
            obOut.writeObject(format);
        }
        catch(Exception e) {
            e.printStackTrace();
        }        
        GUIUtilities.scheduleGC();
    }
    
    public static PageFormat pageSetup() {
        PrinterJob prnJob = getPrintJob("PageSetupOnly");
        
        PageFormat pageFormat = null;
        
        if (prnJob.pageDialog(format) != null) {
            savePrintSetup();
            pageFormat = getPageFormat();
            SystemUtilities.setPageFormat(pageFormat);
        }
        
        GUIUtilities.scheduleGC();
        return pageFormat;
    }
    
    public static String print(Printable printable, String jobName) {
        job = getPrintJob(jobName);
        job.setPrintable(printable);
        return print(job);
    }
    
    public static String print(Book book, String jobName) {
        job = getPrintJob(jobName);
        job.setPageable(book);
        return print(job);
    }
    
    private static String print(PrinterJob _job) {
        
        if(!_job.printDialog(format)) {
            return "cancelled";
        }
        
        try {
            _job.print(format);
            savePrintSetup();
            SystemUtilities.setPageFormat(getPageFormat());
            return "Done";
        }
        catch (PrinterException exception) {
            String message = exception.getMessage();
            System.err.println("Printing error: " + message);
            int option = GUIUtilities.displayConfirmCancelErrorMessage(
                                "Print error:\n" + message);

            if (option == JOptionPane.CANCEL_OPTION) {
                return "Failed";
            } else {
                return print(_job);
            }
            
        }
        
        finally {
            GUIUtilities.scheduleGC();
        }
        
    }
    
    private static PrinterJob getPrintJob(String jobName) {
        
        job = PrinterJob.getPrinterJob();
        
        format = new HashPrintRequestAttributeSet();
        
        File setupFile = new File(PAGE_SETUP_FILE);
        
        if (setupFile.exists()) {
            
            try {
                FileInputStream fileIn = new FileInputStream(setupFile);
                ObjectInputStream obIn = new ObjectInputStream(fileIn);
                format = (HashPrintRequestAttributeSet)obIn.readObject();
            }
            catch(Exception e) {
                Log.error("Error loading saved printer setup: " + e.getMessage());
            }

            format.add(Chromaticity.MONOCHROME);
            format.add(new JobName(jobName, null));
            format.add(new PrinterResolution(600,600,ResolutionSyntax.DPI));

//            PrinterResolution pres = format.get(PrinterResolution.class);

        }
        
        return job;
    }
    
    public static PageFormat getPageFormat() {
        //convert from PrintRequestAttributeSet to the pageFormat
        PrinterJob prnJob = getPrintJob(" ");//PrinterJob.getPrinterJob();
        PageFormat pf = prnJob.defaultPage();
        Paper pap = pf.getPaper();
        
        MediaSizeName media=(MediaSizeName)format.get(Media.class);
        MediaSize ms = MediaSize.getMediaSizeForName(media);
        
        MediaPrintableArea mediaarea=(MediaPrintableArea)format.get(MediaPrintableArea.class);
        
        if(mediaarea != null) {
            pap.setImageableArea(
                    (double)(mediaarea.getX(MediaPrintableArea.INCH)*72),
                    (double)(mediaarea.getY(MediaPrintableArea.INCH)*72),
                    (double)(mediaarea.getWidth(MediaPrintableArea.INCH)*72),
                    (double)(mediaarea.getHeight(MediaPrintableArea.INCH)*72));
        }
        
        if(ms != null) {
            pap.setSize(
                    (double)(ms.getX(MediaSize.INCH)*72),
                    (double)(ms.getY(MediaSize.INCH)*72));
        }
        
        pf.setPaper(pap);
        OrientationRequested orientation = (OrientationRequested)format.get(
                                                    OrientationRequested.class);
        if(orientation != null) {
            
            if(orientation.getValue() == OrientationRequested.LANDSCAPE.getValue()) {
                pf.setOrientation(PageFormat.LANDSCAPE);
            }
            else if(orientation.getValue() == OrientationRequested.REVERSE_LANDSCAPE.getValue()) {
                pf.setOrientation(PageFormat.REVERSE_LANDSCAPE);
            }
            else if(orientation.getValue()==OrientationRequested.PORTRAIT.getValue()) {
                pf.setOrientation(PageFormat.PORTRAIT);
            }
            else if(orientation.getValue()==OrientationRequested.REVERSE_PORTRAIT.getValue()) {
                //doesnt exist??
                //pf.setOrientation(PageFormat.REVERSE_PORTRAIT);
                //then just do the next best thing
                pf.setOrientation(PageFormat.PORTRAIT);
            }
            
        }
        
        return pf;
        
    }
    
}






