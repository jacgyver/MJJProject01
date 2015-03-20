/*
 * HistoryCommand.java
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


package org.executequery.actions.othercommands;

import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.gui.editor.SQLHistoryDialog;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>The Query Editor's history command execution.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class HistoryCommand extends BaseOtherActionCommand {

    public void execute(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Vector history = SystemUtilities.getSqlCommandHistory();
                if (history == null || history.size() == 0) {
                    GUIUtilities.displayInformationMessage(
                            "No SQL command history available");
                    return;
                } 

                JPanel panel = GUIUtilities.getSelectedCentralPane();
                if (panel != null && panel instanceof QueryEditor) {
                    QueryEditor editor = (QueryEditor)panel;
                    new SQLHistoryDialog(history, editor);
                }
                    
            }
        });
    }
    
}



