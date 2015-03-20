/*
 * CreateTableCommand.java
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


package org.executequery.actions.databasecommands;

import java.awt.event.ActionEvent;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.BaseCommand;
import org.executequery.actions.OpenFrameCommand;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.CreateTablePanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * Executes the create table action creating an
 * instance of CreateTablePanel and adding this within
 * an internal frame to the desktop.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/03 01:24:59 $
 */
public class CreateTableCommand extends OpenFrameCommand
                                implements BaseCommand {
    
    /** 
     * Performs the execution of this action.
     *
     * @param the originating <code>ActionEvent</code>
     */
    public void execute(ActionEvent e) {

        if (!isConnected()) {
            return;
        }        

        
        if (isActionableDialogOpen()) {
            GUIUtilities.acionableDialogToFront();
            return;
        }
        
        if (!isDialogOpen(CreateTablePanel.TITLE)) {
            try {
                GUIUtilities.showWaitCursor();
                BaseDialog dialog = 
                        createDialog(CreateTablePanel.TITLE, false);
                CreateTablePanel panel = new CreateTablePanel(dialog);
                dialog.addDisplayComponentWithEmptyBorder(panel);
                dialog.display();
            }
            finally {
                GUIUtilities.showNormalCursor();
            }
        }
    }
    
}


