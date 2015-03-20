/*
 * TableCellEditorValue.java
 *
 * Copyright (C) 2006 Takis Diakoumis
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

/**
 * Defines those objects providing generic table cell 
 * editor functionality.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.2 $
 * @date     $Date: 2006/07/16 15:45:34 $
 */
public interface TableCellEditorValue {

    /**
     * Returns the current editor value from the component
     * defining this object.
     *
     * @return the editor's value
     */
    public String getEditorValue();
    
}
