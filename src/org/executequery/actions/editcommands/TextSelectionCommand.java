/*
 * TextSelectionCommand.java
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
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.executequery.gui.text.TextEditor;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class TextSelectionCommand extends ReflectiveAction {
    
    public TextSelectionCommand() {}
    
    public void selectAll(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.selectAll();
        }
        textFunction = null;
    }
    
    public void selectNone(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.selectNone();
        }
        textFunction = null;
    }

    public void insertAfter(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.insertLineAfter();
        }
        textFunction = null;
    }

    public void insertBefore(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.insertLineBefore();
        }
        textFunction = null;
    }

    public void insertFromFile(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.insertFromFile();
        }
        textFunction = null;
    }

    public void deleteWord(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.deleteWord();
        }
        textFunction = null;
    }

    public void deleteSelection(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.deleteSelection();
        }
        textFunction = null;
    }
    
    public void deleteLine(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.deleteLine();
        }
        textFunction = null;
    }

    public void toLowerCase(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.changeSelectionCase(false);
        }
        textFunction = null;
    }

    public void toUpperCase(ActionEvent e) {
        TextEditor textFunction = (TextEditor)GUIUtilities.getTextEditorInFocus();
        if (textFunction != null) {
            textFunction.changeSelectionCase(true);
        }
        textFunction = null;
    }

}



