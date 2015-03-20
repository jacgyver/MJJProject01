/*
 * CategoryHeaderCellRenderer.java
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


package org.executequery.components.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import org.executequery.Constants;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class CategoryHeaderCellRenderer extends JLabel
                                        implements TableCellRenderer {
    
    private static Font font;
    private static Color background;
    private static Color foreground;
    
    /** Creates a new instance of CategoryHeaderCellRenderer */
    public CategoryHeaderCellRenderer() {}
    
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean cellHasFocus,
                                                   int row, 
                                                   int col) {

        if (background == null) {
            background = table.getGridColor();
            foreground = background.darker().darker();
        }
        
        setBackground(background);

        if (col == 0 || col == 2) {
            setText(Constants.EMPTY);
            return this;
        }

        if (font == null) {
            Font _font = table.getFont();
            font = new Font(_font.getName(), Font.BOLD, _font.getSize());
        }

        setFont(font);
        setForeground(foreground);
        setText(value.toString());        
        return this;
    }

    public void paintComponent(Graphics g) {
        int height = getHeight();
        int width = getWidth();
        g.setColor(background);
        g.fillRect(0, 0, width, height);
        super.paintComponent(g);
    }
    
}



