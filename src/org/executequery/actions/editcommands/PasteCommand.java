/*
 * PasteCommand.java
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


package org.executequery.actions.editcommands;

import java.awt.event.ActionEvent;

import org.executequery.GUIUtilities;
import org.executequery.gui.text.TextEditor;
import org.underworldlabs.swing.actions.BaseCommand;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * <p>Performs the 'PASTE' command and those objects
 *  implementing <code>TextEditor</code>.
 * 
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class PasteCommand implements BaseCommand {
    
    /**
     * <p>Executes the paste command on the <code>TextEditor</code>.
     * 
     * @param the originating event
     */
    public void execute(ActionEvent e) {
        TextEditor textFunction = GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            try {
            textFunction.paste();
            } catch (Exception ex) {ex.printStackTrace();}
        }
        textFunction = null;
    }
    
}








