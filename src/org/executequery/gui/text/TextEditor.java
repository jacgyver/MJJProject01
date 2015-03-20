/*
 * TextEditor.java
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


package org.executequery.gui.text;

import javax.swing.text.JTextComponent;

import org.executequery.print.PrintFunction;
import org.executequery.gui.SaveFunction;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines a panel with a text component that may
 * be manipulated - print, cut, copy, change case etc.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public interface TextEditor extends PrintFunction,
                                    SaveFunction {
    
    /**
     * Returns the text component's text.
     *
     * @return the text component text
     */
    public String getEditorText();

    /**
     * Returns the actual text component.
     *
     * @return the text component
     */
    public JTextComponent getEditorTextComponent();
    
    /**
     * Cuts the selected text from the text component.
     */
    public void cut();

    /**
     * Copies the selected text from the text component.
     */
    public void copy();
    
    /**
     * Pastes text into the text component at the cursor position.
     */
    public void paste();
    
    /**
     * Disables/enables updates on the text component. This is designed
     * to add remove some of the heavier listeners such as document 
     * change and caret listeners.
     *
     * @param disable - true | false
     */
    public void disableUpdates(boolean disable);
    
    /**
     * Return whether the text component defined by this interface
     * may be text searched.
     *
     * @return true | false
     */
    public boolean canSearch();
    
    public void changeSelectionCase(boolean upper);
    
    public void deleteLine();
    
    public void deleteWord();
    
    public void deleteSelection();
    
    public void insertFromFile();
    
    public void insertLineAfter();
    
    public void insertLineBefore();
    
    public void selectAll();
    
    public void selectNone();
    
    
}






