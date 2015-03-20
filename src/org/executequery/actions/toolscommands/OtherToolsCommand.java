/*
 * OtherToolsCommand.java
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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.actions.ReflectiveAction;

import org.executequery.gui.SystemLogsViewer;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Misc action implementations.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class OtherToolsCommand extends ReflectiveAction {
    
    public OtherToolsCommand() {}

    public void resetAllLogs(ActionEvent e) {
        GUIUtilities.resetAllLogs();
    }    

    public void resetSystemLog(ActionEvent e) {
        GUIUtilities.resetSystemLog();
    }    

    public void resetImportLog(ActionEvent e) {
        GUIUtilities.resetImportLog();
    }

    public void resetExportLog(ActionEvent e) {
        GUIUtilities.resetExportLog();
    }    

    public void viewSystemLog(ActionEvent e) {
        viewLog(SystemLogsViewer.ACTIVITY);
    }

    public void viewImportLog(ActionEvent e) {
        viewLog(SystemLogsViewer.IMPORT);
    }

    public void viewExportLog(ActionEvent e) {
        viewLog(SystemLogsViewer.EXPORT);
    }

    protected void viewLog(final int type) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // check if we have one already
                JPanel panel = GUIUtilities.getCentralPane(SystemLogsViewer.TITLE);
                if (panel != null) {
                    ((SystemLogsViewer)panel).setSelectedLog(type);
                } 
                // others add to pane
                else {
                    GUIUtilities.addCentralPane(SystemLogsViewer.TITLE,
                                                SystemLogsViewer.FRAME_ICON, 
                                                new SystemLogsViewer(type),
                                                null,
                                                true);
                }
            }
        });
    }

}



