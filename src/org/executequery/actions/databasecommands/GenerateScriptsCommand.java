/*
 * GenerateScriptsCommand.java
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
import org.executequery.gui.scriptgenerators.GenerateScriptsWizard;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Execution for CREATE TABLE script generation.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/06/07 14:57:14 $
 */
public class GenerateScriptsCommand extends OpenFrameCommand
                                    implements BaseCommand {
    
    public void execute(ActionEvent e) {

        if (!isConnected()) {
            return;
        }

        if (isActionableDialogOpen()) {
            GUIUtilities.acionableDialogToFront();
            return;
        }
        
        if (!isDialogOpen(GenerateScriptsWizard.TITLE)) {
            try {
                GUIUtilities.showWaitCursor();
                BaseDialog dialog = 
                        createDialog(GenerateScriptsWizard.TITLE, false);
                GenerateScriptsWizard panel = new GenerateScriptsWizard(dialog);
                dialog.addDisplayComponent(panel);
                dialog.display();
            }
            finally {
                GUIUtilities.showNormalCursor();
            }
        }
/*
        if (!isConnected()) {
            return;
        }
        else if (!isFrameOpen(GenerateScriptsPanel.TITLE)) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUIUtilities.addInternalFrame(GenerateScriptsPanel.TITLE,
                                                  GenerateScriptsPanel.FRAME_ICON,
                                                  new GenerateScriptsPanel(),
                                                  true, false, false, false);
                }
            });
        }
*/

    }
    
}








