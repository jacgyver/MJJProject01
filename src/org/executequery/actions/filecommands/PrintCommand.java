/*
 * PrintCommand.java
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
import java.awt.print.Printable;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.BaseCommand;
import org.executequery.gui.editor.PrintSelectDialog;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.print.*;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.util.SwingWorker;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * Implements the 'PRINT' command for those objects where 
 * this function is available.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PrintCommand implements BaseCommand {
    
    /** <p>Executes the print command.
     *
     *  @param the originating event
     */
    public void execute(ActionEvent e) {
        
        // check if the active window is printable
        /*
        if (!GUIUtilities.canPrint())
            return;
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
                new PrintSelectDialog((QueryEditor)printFunction);
                return;
            } 
        
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    return doPrint();
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
    
    /**
     * Performs the actual printing.
     *
     * @return a 'success' or 'failure' result
     */
    private Object doPrint() {
        PrintFunction printFunction = null;        
        try {
            printFunction = GUIUtilities.getPrintableInFocus();
            Printable printable = printFunction.getPrintable();
            // Create a print job
            return PrintUtilities.print(printable, printFunction.getPrintJobName());
        } 
        finally {
            printFunction = null;
        }        
    }
    
}


