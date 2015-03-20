/*
 * KeyWordsPanel.java
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.executequery.KeywordProperties;
import org.underworldlabs.swing.table.SingleColumnTableModel;

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
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class KeyWordsPanel extends ConnectionPropertiesPanel {
    
    /** table displaying sql92 keywords */
    private JTable savedWordsTable;
    
    /** table displaying database specific keywords */
    private JTable keywordsTable;
    
    /** table model for the databse specific table */
    private SingleColumnTableModel model;
    
    /** Creates a new instance of KeyWordsPanel */
    public KeyWordsPanel() {
        super(new GridBagLayout());
        init();
    }
    
    private void init() {        
        // setup the database specific words table
        model = new SingleColumnTableModel();
        keywordsTable = new JTable(model);
        setTableProperties(keywordsTable);
        
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.setBorder(BorderFactory.createTitledBorder("Database Specific"));
        panel1.add(new JScrollPane(keywordsTable));
        
        List<String> sql92 = KeywordProperties.getSQL92();
        savedWordsTable = new JTable(
                new SingleColumnTableModel(null, sql92));
        setTableProperties(savedWordsTable);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.setBorder(BorderFactory.createTitledBorder("SQL92"));
        panel2.add(new JScrollPane(savedWordsTable));

        JPanel base = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        base.add(panel2, gbc);
        gbc.insets.left = 3;
        gbc.gridx = 1;
        base.add(panel1, gbc);

        // add main panel
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.ipadx = 0;
        gbc.ipady = 0;        
        add(base, gbc);
    }
    
    public void setDatabaseKeywords(String[] words) {
        model.setValues(words);
    }
    
}



