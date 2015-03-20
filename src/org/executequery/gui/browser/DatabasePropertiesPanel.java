/*
 * DatabasePropertiesPanel.java
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
import java.util.Hashtable;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.executequery.gui.DefaultTable;
import org.underworldlabs.swing.table.PropertyWrapperModel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Simple panel displaying database meta data properties.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/07/15 13:14:12 $
 */
public class DatabasePropertiesPanel extends ConnectionPropertiesPanel {
    
    /** table model */
    private PropertyWrapperModel model;
    
    /** the table */
    private JTable table;
    
    /** Creates a new instance of DatabasePropertiesPanel */
    public DatabasePropertiesPanel() {
        super(new GridBagLayout());
        init();
    }
    
    private void init() {
        model = new PropertyWrapperModel(PropertyWrapperModel.SORT_BY_KEY);
        table = new DefaultTable(model);
        setTableProperties(table);
        
        GridBagConstraints gbc = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                        GridBagConstraints.SOUTHEAST, 
                                        GridBagConstraints.BOTH,
                                        new Insets(5, 5, 5, 5), 0, 0);
        add(new JScrollPane(table), gbc); 
    }
    
    public void setDatabaseProperties(Hashtable properties) {
        model.setValues(properties, true);
    }
    
    public JTable getTable() {
        return table;
    }
    
}



