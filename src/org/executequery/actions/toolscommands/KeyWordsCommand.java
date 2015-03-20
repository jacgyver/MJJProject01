/*
 * KeyWordsCommand.java
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


package org.executequery.actions.toolscommands;

import java.awt.event.ActionEvent;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.BaseCommand;
import org.executequery.actions.OpenFrameCommand;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.UserDefinedWordsPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Executes the Tools | User Defined Keywords command.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/06/03 01:24:59 $
 */
public class KeyWordsCommand extends OpenFrameCommand
                             implements BaseCommand {
    
    public void execute(ActionEvent e) {
        if (isActionableDialogOpen()) {
            GUIUtilities.acionableDialogToFront();
            return;
        }
        
        if (!isDialogOpen(UserDefinedWordsPanel.TITLE)) {
            try {
                GUIUtilities.showWaitCursor();
                BaseDialog dialog = 
                        createDialog(UserDefinedWordsPanel.TITLE, false, false);
                UserDefinedWordsPanel panel = 
                        new UserDefinedWordsPanel(dialog);
                dialog.addDisplayComponentWithEmptyBorder(panel);
                dialog.display();
                GUIUtilities.showNormalCursor();            
            }
            finally {
                GUIUtilities.showNormalCursor();
            }
        }

        /*
        if (!isFrameOpen(UserDefinedWordsPanel.TITLE)) {        
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    UserDefinedWordsPanel panel = new UserDefinedWordsPanel();
                    GUIUtilities.addInternalFrame(UserDefinedWordsPanel.TITLE,
                                                  "DefaultApplicationIcon16.gif", 
                                                  panel,
                                                  true, false, true, false);
        
                    panel.setFocusComponent();
                }
            });
        }
        */
    }
    
}








