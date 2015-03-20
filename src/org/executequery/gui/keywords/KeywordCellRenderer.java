/*
 * KeywordCellRenderer.java
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


package org.executequery.gui.keywords;

import java.awt.Component;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.executequery.Constants;
import org.executequery.GUIUtilities;

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
public class KeywordCellRenderer extends JLabel 
                                 implements TableCellRenderer {
    
    /** icon for a SQL92 keyword */
    private Icon sql92;

    /** icon for a user defined keyword */
    private Icon userDefined;
    
    /** icon for a database specific keyword */
    private Icon databaseSpecific;
    
    /** Creates a new instance of KeywordCellRenderer */
    public KeywordCellRenderer() {
        sb = new StringBuffer();
        setFont(new Font("Dialog", Font.PLAIN, 11));
        sql92 = GUIUtilities.loadIcon("Sql92.gif", true);
        userDefined = GUIUtilities.loadIcon("User16.gif", true);
        databaseSpecific = GUIUtilities.loadIcon("DatabaseKeyword16.gif", true);
    }
    
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean cellHasFocus,
                                                   int row, 
                                                   int col) {

        SqlKeyword keyword = (SqlKeyword)value;
        if (keyword.isSql92()) {
            setIcon(sql92);
        } else if (keyword.isDatabaseSpecific()) {
            setIcon(databaseSpecific);
        } else if (keyword.isUserDefined()) {
            setIcon(userDefined);
        }
        setToolTipText(buildToolTip(keyword));
        setText(keyword.getText());

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }	else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        return this;
    }
    
    /** tool tip concat buffer */
    private StringBuffer sb;
    
    private String buildToolTip(SqlKeyword keyword) {
        // reset
        sb.setLength(0);
        
        // build the html display
        sb.append("<html>");
        sb.append(Constants.TABLE_TAG_START);
        sb.append("<tr><td><b>");
        sb.append(keyword.getText());
        sb.append("</b></td></tr>");
        sb.append(Constants.TABLE_TAG_END);
        sb.append("<hr>");
        sb.append(Constants.TABLE_TAG_START);
        sb.append("<tr><td>");
        if (keyword.isSql92()) {
            sb.append("SQL92 Standard Keyword");
        } else if (keyword.isDatabaseSpecific()) {
            setIcon(databaseSpecific);
            sb.append("Database Defined Keyword:  ");
            sb.append(keyword.getDatabaseProductName());
        } else if (keyword.isUserDefined()) {
            sb.append("User Defined Keyword");
        }
        sb.append("</td></tr>");
        sb.append(Constants.TABLE_TAG_END);
        sb.append("</html>");
        return sb.toString();
    }
    
    public boolean isOpaque() {
        return true;
    }

}


