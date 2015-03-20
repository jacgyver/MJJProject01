/*
 * PropertiesDefaults.java
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


package org.executequery.gui.prefs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.executequery.gui.prefs.PropertiesPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * resets defaults on propertie frame
 * <P>
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PropertiesDefaults extends JPanel
                                implements ActionListener {
    
    private PropertiesPanel frame;
    
    /**
     * Constructs a new instance.
     */
    public PropertiesDefaults(PropertiesPanel f) {
        super(new GridBagLayout());
        frame = f;
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the state of this instance.
     */
    private void jbInit() throws Exception {
        
        JButton restoreButton = new JButton("Restore Defaults");
        restoreButton.setPreferredSize(new Dimension(135,30));
        restoreButton.addActionListener(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.top = 20;
        this.add(new JLabel("Restores all preferences to system default values."), gbc);
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        this.add(restoreButton, gbc);
    }

    public void actionPerformed(ActionEvent e) {
        //frame.restoreAllDefaults(); 
    }

}



