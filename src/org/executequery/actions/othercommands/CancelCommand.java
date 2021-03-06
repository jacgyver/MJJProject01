/*
 * CancelCommand.java
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

import org.executequery.GUIUtilities;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>This represents a generic close/cancel command
 *  performed from any open <code>JInternalFrame</code>.
 *  The effect is to close that frame and remove it from
 *  the desktop.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class CancelCommand extends BaseOtherActionCommand {
    
    private boolean isDialog;
    
    public CancelCommand() {
        this(false);
    }

    /**
     * Creates a new CancelCommand which may be a dialog
     * cancel command or otherwise.
     */
    public CancelCommand(boolean isDialog) {
        super("Cancel");
        this.isDialog = isDialog;
    }

    /** 
     * Performs the close operation.
     *
     *  @param the <code>ActionEvent</code> initiating
     *         this command
     */
    public void execute(ActionEvent e) {
        if (isDialog) {
            GUIUtilities.closeSelectedDialog();
        } else {
            //GUIUtilities.closeSelectedInternalFrame();
        }
    }
    
}







