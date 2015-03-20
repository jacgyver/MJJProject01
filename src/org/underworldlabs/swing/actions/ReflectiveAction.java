/*
 * ReflectiveAction.java
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


package org.underworldlabs.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

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
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class ReflectiveAction implements ActionListener {
    
    private Object target;

    public ReflectiveAction() {}

    public ReflectiveAction(Object target) {
        this.target = target;
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {

            Class[] argTypes = {e.getClass()};

            Method method = null;

            if (target == null) {
                target = this;
                method = getClass().getMethod(command, argTypes);
            } else {
                method = target.getClass().getMethod(command, argTypes);
            }

            if (method == null) {
                return;
            }

            Object[] args = {e};
            method.invoke(target, args);

        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}



