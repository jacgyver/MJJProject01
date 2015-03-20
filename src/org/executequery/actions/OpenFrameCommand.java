/*
 * OpenFrameCommand.java
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


package org.executequery.actions;
import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.executequery.gui.BaseDialog;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Base command for those opening a new frame
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/03 01:24:59 $
 */
public abstract class OpenFrameCommand {
    
    protected boolean isConnected() {
        if (!SystemUtilities.isConnected()) {
            GUIUtilities.displayErrorMessage(
                            "Not Connected.\nPlease connect to continue.");
            return false;
        }
        return true;
    }

    protected boolean isActionableDialogOpen() {
        return GUIUtilities.isActionableDialogOpen();
    }
    
    protected boolean isDialogOpen(String title) {
        if (GUIUtilities.isDialogOpen(title)) {
            GUIUtilities.setSelectedDialog(title);
            return true;
        }
        return false;
    }
    
    /**
     * Creates a dialog component with the specified name
     * and modality.
     *
     * @param the dialog name
     * @param whether the dialog is to be modal
     */
    protected BaseDialog createDialog(String name, boolean modal) {
        return new BaseDialog(name, modal);
    }

    /**
     * Creates a dialog component with the specified name
     * and modality.
     *
     * @param the dialog name
     * @param whether the dialog is to be modal
     * @param wether the dialog is resizeable
     */
    protected BaseDialog createDialog(String name, boolean modal, boolean resizeable) {
        return new BaseDialog(name, modal, resizeable);
    }

}



