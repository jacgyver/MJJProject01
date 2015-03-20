/*
 * ConnectCommand.java
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
import org.executequery.ConnectionProperties;

import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.BaseCommand;
import org.executequery.actions.OpenFrameCommand;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.gui.browser.ConnectionsTreePanel;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Executes the Database | Connect... | New Connection command.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:55 $
 */
public class ConnectCommand extends OpenFrameCommand
                            implements BaseCommand {
    
    public void execute(ActionEvent e) {
        
        String command = e.getActionCommand();
        GUIUtilities.ensureDockedTabVisible(ConnectionsTreePanel.PROPERTY_KEY);        

        ConnectionsTreePanel panel = (ConnectionsTreePanel)GUIUtilities.
                                getDockedTabComponent(ConnectionsTreePanel.PROPERTY_KEY);
        
        if (MiscUtils.isNull(command) || 
                command.equals("New Connection")) {
            
            panel.newConnection();

            /*
            if (!isFrameOpen(LoginPanel.TITLE)) {
                GUIUtilities.addInternalFrame(LoginPanel.TITLE, 
                                              LoginPanel.FRAME_ICON,
                                              new LoginPanel(true), 
                                              true, false, false, false);
            }
             */

        }
        else {
            DatabaseConnection dc = ConnectionProperties.getDatabaseConnection(command);
            panel.setSelectedConnection(dc);

            /*
            GUIUtilities.addInternalFrame(
                    LoginPanel.TITLE, 
                    LoginPanel.FRAME_ICON,
                    new LoginPanel(ConnectionProperties.getDatabaseConnection(command)),
                    true, false, false, false);
             */
        }

    }
    
}







