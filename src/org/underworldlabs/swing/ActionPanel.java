/*
 * ActionPanel.java
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


package org.underworldlabs.swing;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import javax.swing.JPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Base panel with default action listener implementation using reflection.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class ActionPanel extends JPanel
                         implements ActionListener {
    
    private static Object[] args;
    private static Class[] argTypes;
    
    public ActionPanel() {
        super();
    }
    
    public ActionPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }
    
    public ActionPanel(LayoutManager layout) {
        super(layout);
    }
    
    public ActionPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {

            if (argTypes == null) {
                argTypes = new Class[0];
            }

            Method method = getClass().getMethod(command, argTypes);
            
            if (args == null) {
                args = new Object[0];
            }

            method.invoke(this, args);
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}



