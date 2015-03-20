/*
 * StringCellEditor.java
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


package org.underworldlabs.swing.table;

import javax.swing.JTextField;
import org.underworldlabs.Constants;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Simple string value table column cell editor.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/07/16 15:45:52 $
 */
public class StringCellEditor extends JTextField
                              implements TableCellEditorValue {
    
    public StringCellEditor() {
        super();
        setBorder(null);
        setHorizontalAlignment(JTextField.LEFT);
    }
    
    /**
     * Returns the current editor value from the component
     * defining this object.
     *
     * @return the editor's value
     */
    public String getEditorValue() {
        return getText();
    }

    /**
     * Resets the editor's value to an empty string.
     */
    public void resetValue() {
        setText(Constants.EMPTY);
    }
    
    /**
     * Returns the current editor value string.
     */
    public String getValue() {
        return getText();
    }
    
    /**
     * Sets the editor's value to that specified.
     *
     * @param value - the value to be set
     */
    public void setValue(String value) {
        setText(value);
    }
    
}