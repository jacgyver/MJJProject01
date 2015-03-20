/*
 * ResultsTableCellRenderer.java
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


package org.executequery.gui.editor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

// much of this from the article Christmas Tree Applications at
// http://java.sun.com/products/jfc/tsc/articles/ChristmasTree
// and is an attempt at a better performing cell renderer for the
// results table.
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
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ResultsTableCellRenderer extends DefaultTableCellRenderer {
    
    private static Color background;
    private static Color foreground;
    
    private static Color selectionForeground;
    private static Color selectionBackground;
    
    private static Color tableForeground;
    private static Color tableBackground;
    
    private static Color editableForeground;
    private static Color editableBackground;
    
    private static Border focusBorder;
    
    public ResultsTableCellRenderer() {
        focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
        editableForeground = UIManager.getColor("Table.focusCellForeground");
        editableBackground = UIManager.getColor("Table.focusCellBackground");
        selectionForeground = UIManager.getColor("Table.selectionForeground");
        selectionBackground = UIManager.getColor("Table.selectionBackground");
        tableForeground = UIManager.getColor("Table.foreground");
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus,
    int row, int column) {
        
        if (isSelected) {
            setForeground(selectionForeground);
            setBackground(selectionBackground);
        }
        else {
            
            if (tableBackground == null)
                tableBackground = table.getBackground();
            
            setForeground(tableForeground);
            setBackground(tableBackground);
        }
        
        if (hasFocus) {
            setBorder(focusBorder);
            
            if (table.isCellEditable(row, column)) {
                setForeground(editableForeground);
                setBackground(editableBackground);
            }
            
        } else {
            setBorder(noFocusBorder);
        }
        
        setValue(value);
        
        return this;
    }
    
    public void setTableBackground(Color c) {
        this.tableBackground = c;
    }
    
    public void setBackground(Color c) {
        this.background = c;
    }
    
    public Color getBackground() {
        return background;
    }
    
    public void setForeground(Color c) {
        this.foreground = c;
    }
    
    public Color getForeground() {
        return foreground;
    }
    
    public boolean isOpaque() {
        return background != null;
    }
    
    public void invalidate() {}
    
    public void repaint() {}
    
    public void firePropertyChange(String propertyName,
                                   boolean oldValue, boolean newValue) {}
    
    protected void firePropertyChange(String propertyName,
                                        Object oldValue, Object newValue) {}
    
}






