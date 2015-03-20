/*
 * PropsTreeCellRenderer.java
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

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import org.executequery.Constants;
import org.executequery.GUIUtilities;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Properties frame tree renderer.
 * <P>
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class PropsTreeCellRenderer extends JLabel
                                   implements TreeCellRenderer {
    
    private Color textBackground;
    private Color textForeground;
    private Color selectionBackground;
    
    private Font font;
    
    private ImageIcon emptyImage;
    
    public PropsTreeCellRenderer() {
        textBackground = UIManager.getColor("Tree.textBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        
        // smaller font
        //font = new Font("Dialog", Font.PLAIN, 11);
        
        emptyImage = GUIUtilities.loadIcon("BlankTreeNode.gif", true);
        setOpaque(true);
    }
    
    public Component getTreeCellRendererComponent(JTree tree, 
                                                  Object value,
                                                  boolean bSelected, 
                                                  boolean bExpanded,
                                                  boolean bLeaf, 
                                                  int iRow, 
                                                  boolean bHasFocus) {
        
        String labelText = value.toString();
        
        setIcon(emptyImage);
        
        if(!bSelected) {
            setBackground(textBackground);
            setForeground(textForeground);
        }
        else {
            setBackground(selectionBackground);
            
            if (GUIUtilities.getLookAndFeel() == Constants.WIN_LAF) {
                setForeground(Color.WHITE);
            } else {
                setForeground(textForeground);
            }

        }

        // reset the font
        //setFont(font);
        
        // Add the text to the cell
        setText(labelText);
        setIconTextGap(0);
        
        return this;
    }
    
}









