/*
 * SortableHeaderRenderer.java
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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

import javax.swing.table.TableCellRenderer;
import org.underworldlabs.Constants;


// Header renderer for the table sorter model
/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/07/15 16:36:53 $
 */
public class SortableHeaderRenderer extends DefaultTableHeaderRenderer
                                    implements TableCellRenderer {

    /** the up arrow icon */
    private ArrowIcon upIcon;
    
    /** the down arrow icon */
    private ArrowIcon downIcon;
    
    /** the table sorter for this header */
    private TableSorter sorter;
    
    /** the light gradient colour 1 */
    private Color colour1;

    /** the dark gradient colour 2 */
    private Color colour2;

    public SortableHeaderRenderer(TableSorter sorter) {
        super(DEFAULT_HEIGHT);

        this.sorter = sorter;

        // init the icons
        upIcon = new ArrowIcon(ArrowIcon.UP);
        downIcon = new ArrowIcon(ArrowIcon.DOWN);

        // set the sort icon to the right of the text
        setHorizontalTextPosition(JLabel.LEFT);
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        int modelColumn = table.convertColumnIndexToModel(column);
        int iconType = sorter.getHeaderRendererIcon(modelColumn);
        if (iconType == -1) {
            setIcon(null);
        } else {
            setIcon(iconType == ArrowIcon.UP ? upIcon : downIcon);
        }
        
        return super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
    }

}



