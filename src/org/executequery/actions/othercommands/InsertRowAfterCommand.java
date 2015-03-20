/*
 * InsertRowAfterCommand.java
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
import javax.swing.JPanel;

import org.executequery.GUIUtilities;
import org.executequery.gui.browser.BrowserViewPanel;
import org.executequery.gui.table.TableFunction;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Inserts a table row within relevant table type
 *  functions (ie. browser, create table, etc) after the
 *  currently selected row.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class InsertRowAfterCommand extends BaseOtherActionCommand {

    public void execute(ActionEvent e) {
        JPanel panel = GUIUtilities.getSelectedCentralPane();
        if (panel != null) {
            TableFunction tableFunction = null;

            // check if the current panel is a TableFunction
            if (panel instanceof TableFunction) {
                tableFunction = (TableFunction)panel;
            }

            // otherwise, check if we are on the browser
            // then check if the current browser view is a 
            // TableFunction implementation
            else if (panel instanceof BrowserViewPanel) {
                BrowserViewPanel viewPanel = (BrowserViewPanel)panel;
                if (viewPanel.getCurrentView() instanceof TableFunction) {
                    tableFunction = (TableFunction)viewPanel.getCurrentView();
                }
            }
            // do the action
            if (tableFunction != null) {
                tableFunction.insertAfter();
            }
        }
        
    }
    
    /*
    private TableFunction panel;
    
    public InsertRowAfterCommand(TableFunction panel) {
        super(IconUtilities.loadIcon("RowInsertAfter16.gif"));
        this.panel = panel;
    }

    public void execute(ActionEvent e) {
        
        if (panel != null) {
            panel.insertAfter();
        }
        else {
            TableFunction tableFunction = (TableFunction)
            GUIUtilities.getSelectedCentralPane(BrowserPanel.TITLE);
            
            if (tableFunction != null)
                tableFunction.insertAfter();
            
        } 
        
    }
    */
}






