/*
 * JavaSQLTypesPanel.java
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


package org.executequery.gui.browser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.executequery.gui.DefaultTable;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Displays java.sql.Types in a table with full properties
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/07/15 13:14:12 $
 */
public class JavaSQLTypesPanel extends ConnectionPropertiesPanel {
    
    private JTable table;
    
    /** Creates a new instance of JavaSQLTypesPanel */
    public JavaSQLTypesPanel() {
        super(new GridBagLayout());
        init();
    }
    
    private void init() {
        Field[] fields = java.sql.Types.class.getDeclaredFields();
        String[][] values = new String[fields.length][2];
        String[] labels = {"Name", "Value"};
        
        try {
            for (int i = 0; i < fields.length; i++) {
                values[i][0] = fields[i].getName();
                values[i][1] = Integer.toString(fields[i].getInt(fields[i].getName()));
            }
        } 
        catch (IllegalAccessException e) {
            e.printStackTrace();
            add(new JLabel("Not Available"));
            return;
        }

        table = new DefaultTable(values, labels);
        setTableProperties(table);

        GridBagConstraints gbc = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                        GridBagConstraints.SOUTHEAST, 
                                        GridBagConstraints.BOTH,
                                        new Insets(5, 5, 5, 5), 0, 0);
        add(new JScrollPane(table), gbc); 
    }
    
}



