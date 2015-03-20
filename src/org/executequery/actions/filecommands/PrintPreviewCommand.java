/*
 * PrintPreviewCommand.java
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


package org.executequery.actions.filecommands;

import java.awt.event.ActionEvent;

import org.executequery.GUIUtilities;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.print.*;
import org.executequery.gui.editor.PrintSelectDialog;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.actions.BaseCommand;
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
public class PrintPreviewCommand implements BaseCommand {
    
    public void execute(ActionEvent e) {
        
        /*
        // check if the active window is printable
        if (!GUIUtilities.canPrint())
            return;
        
        // check if the active window is an editor
        if (GUIUtilities.getSelectedCentralPane() instanceof QueryEditor) {
            new PrintSelectDialog((QueryEditor)GUIUtilities.getSelectedCentralPane(),
            PrintSelectDialog.PRINT_PREVIEW);
            return;
        } 
        
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return showPreview();
            }
        };
        worker.start();
        */
        PrintFunction printFunction = null;
        try {
            printFunction = GUIUtilities.getPrintableInFocus();
            if (printFunction == null) {
                return;
            }
        
             // if the frame in focus is a Query Editor
            // display the print selection dialog (text or table)
            if (printFunction instanceof QueryEditor) {
                new PrintSelectDialog((QueryEditor)printFunction, 
                                      PrintSelectDialog.PRINT_PREVIEW);
                return;
            } 
        
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    return showPreview();
                }
                public void finished() {
                    GUIUtils.scheduleGC();
                }
            };
            worker.start();
        }
        finally {
            printFunction = null;
        }

    }
    
    private String showPreview() {
        
        PrintFunction printFunction = null;
        
        try {
            printFunction = GUIUtilities.getPrintableInFocus();
            
            if (printFunction.getPrintable() != null) {
                new PrintPreviewer(printFunction);
                /*
                GUIUtilities.addInternalFrame(PrintPreviewer.TITLE,
                PrintPreviewer.FRAME_ICON,
                new PrintPreviewer(printFunction),
                true, true, false, true); */
            } 
            
            return "Done";
        } 
        
        finally {
            printFunction = null;
        } 
    }
    
}







