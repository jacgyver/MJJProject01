/*
 * ConstraintCellRenderer.java
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


package org.executequery.gui.table;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.executequery.GUIUtilities;
import org.executequery.gui.browser.ColumnConstraint;

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
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class ConstraintCellRenderer extends JLabel
                                    implements TableCellRenderer {
    
    private static ImageIcon deleteImage;
    private static ImageIcon newImage;

    static {
        deleteImage = GUIUtilities.loadIcon("MarkDeleted16.gif", true);
        newImage = GUIUtilities.loadIcon("MarkNew16.gif", true);
    }

    /** Creates a new instance of ConstraintCellRenderer */
    public ConstraintCellRenderer() {}

    public Component getTableCellRendererComponent(JTable table,
                                Object value, boolean isSelected, boolean hasFocus,
                                int row, int column) {
        

        ColumnConstraint cc = (ColumnConstraint)value;
        if (cc.isMarkedDeleted()) {
            setIcon(deleteImage);
            setToolTipText("This value marked to be dropped");
        }
        else if (cc.isNewConstraint()) {
            setIcon(newImage);
            setToolTipText("This value marked new");            
        }
        else {
            setIcon(null);
        }

        setBackground(Color.WHITE);
        setHorizontalAlignment(JLabel.CENTER);
        return this;
    }

}



