/*
 * CheckVersionCommand.java
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


package org.executequery.actions.helpcommands;

import java.awt.event.ActionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.underworldlabs.swing.actions.BaseCommand;
import org.executequery.gui.InformationDialog;
import org.executequery.util.Log;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.InterruptibleProgressDialog;
import org.underworldlabs.swing.util.InterruptibleProcess;
import org.underworldlabs.swing.util.SwingWorker;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Checks to see if a newer version of Execute Query
 * is available. Some of the code here was taken from
 * a similar function in JEdit by Slava Pestov
 * from http://jedit.org. Thanks.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/09/15 09:37:23 $
 */
public class CheckVersionCommand implements BaseCommand,
                                            InterruptibleProcess {
    
    /** Thread worker object */
    private SwingWorker worker;
    
    /** The progress dialog */
    private InterruptibleProgressDialog progressDialog;
    
    public void execute(ActionEvent e) {
        worker = new SwingWorker() {
            public Object construct() {
                return doWork();
            }
            public void finished() {
                closeProgressDialog();
                GUIUtilities.showNormalCursor();
            }
        };
        
        progressDialog = new InterruptibleProgressDialog(
                            GUIUtilities.getParentFrame(),
                            "Check for update", 
                            "Checking for updated version from http://executequery.org",
                            this);
        
        worker.start();
        progressDialog.run();
    }
    
    private void closeProgressDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (progressDialog != null && progressDialog.isVisible()) {
                    progressDialog.dispose();
                }
                progressDialog = null;                
            }
        });
    }
    
    private Object doWork() {
        try {
            Log.info("Checking for new version update from http://executequery.org");
            
            URL url = new URL(SystemProperties.getProperty("system", "check.version.url"));
            InputStream input = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            String line;
            String version = null;
            String build = null;

            while((line = reader.readLine()) != null) {
                
                if(line.startsWith("version")) {
                    version = line.substring(8).trim();
                }
                else if(line.startsWith("build")) {
                    build = line.substring(6).trim();
                }
                
            } 
            
            reader.close();
            
            if (version != null && build != null) {
                String c_build = System.getProperty("executequery.build");
                
                if (c_build.compareTo(build) < 0) {
                    closeProgressDialog();

                    Log.info("New version " + version + " available.");
                    
                    int yesNo = GUIUtilities.displayYesNoDialog(
                        "New version " +
                        version + " (Build " + build + ") is available for download at. " +
                        "http://executequery.org.\nDo you wish to view the " +
                        "version notes for this release?", "Execute Query Update");
                    
                    if (yesNo == JOptionPane.YES_OPTION) {
                        displayNewVersionInfo();
                    }
                    
                } 
                else {
                    closeProgressDialog();
                    Log.info("No version update available.");
                    GUIUtilities.displayInformationMessage(
                        "No update available.\n" +
                        "This version of Execute Query is up to date.\n" +
                        "Please check back here periodically to ensure you have " +
                        "the latest version.");
                }
                
            } 
            else {
                closeProgressDialog();
                GUIUtilities.displayErrorMessage(
                        "An error occured trying to communicate " +
                        " with the server at http://executequery.org." +
                        "\nPlease try again later.");
            } 

        } 
        catch (MalformedURLException urlExc) {
            showError();
        } 
        catch (IOException ioExc) {
            closeProgressDialog();
            GUIUtilities.displayErrorMessage(
                    "The version file at http://executequery.org " +
                    "could not be opened.\nThis feature requires an " +
                    "active internet connection.");
        } 

        return null;
    }
    
    private void displayNewVersionInfo() {
        try {

            GUIUtilities.showWaitCursor();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressDialog = new InterruptibleProgressDialog(
                        GUIUtilities.getParentFrame(),
                        "Check for update", 
                        "Retrieving new version release notes from http://executequery.org",
                        CheckVersionCommand.this);
                    progressDialog.run();
                }
            });            

            URL url = new URL(SystemProperties.getProperty("system", 
                                        "check.version.notes.url"));

            InputStream input = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            
            String line;
            StringBuffer sb = new StringBuffer(1000);
            char NEW_LINE = '\n';
            
            while((line = reader.readLine()) != null) {
                sb.append(line).append(NEW_LINE);
            }
            
            reader.close();

            final String notes = sb.toString();
            closeProgressDialog();

            GUIUtils.invokeAndWait(new Runnable() {
                public void run() {
                    new InformationDialog("Latest Version Info", 
                        notes, InformationDialog.TEXT_CONTENT_VALUE);
                }
            });

        } 
        catch (MalformedURLException urlExc) {
            showError();
        } 
        catch (IOException ioExc) {
            showError();
        }
        finally {
            GUIUtilities.showNormalCursor();
        }   

    }
    
    private void showError() {
        GUIUtilities.showNormalCursor();
        GUIUtilities.displayErrorMessage(
                "An error occured opening the version info file\n" +
                "at http://executequery.org. Please try again later.");
    }
    
    /**
     * Sets the process cancel flag as specified.
     */
    public void setCancelled(boolean cancelled) {
        interrupt();
    }
    
    /**
     * Indicates thatthis process should be interrupted.
     */
    public void interrupt() {
        worker.interrupt();
    }

}



