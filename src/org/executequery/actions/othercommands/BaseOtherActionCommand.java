/*
 * BaseOtherActionCommand.java
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

package org.executequery.actions.othercommands;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.underworldlabs.swing.actions.BaseCommand;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * This is the base class for the action library. All
 * actions will inherit from this class. The CommandAction
 * defines a generic implementation of actionPerformed.
 * Here actionPerformed simply calls the execute method
 * on its command object.<br>
 * A developer can use this type directly by passing in
 * the command, string, and action. However, convenience
 * implementations are available that already provide the
 * string and icon. The developer simply needs to provide
 * the proper command.<br>
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public abstract class BaseOtherActionCommand extends AbstractAction
                                             implements BaseCommand {
    
    public BaseOtherActionCommand() {
        super();
    }
    
    /**
     *  This constructor creates an action without an icon.
     *
     *  @param name the action's name
     */
    public BaseOtherActionCommand(String name) {
        super(name);
    }
    
    /**
     *  This constructor creates an action with an icon but no name.
     *  (for buttons that require only an icon)
     *
     *  @param icon the action's icon
     */
    public BaseOtherActionCommand(Icon icon) {
        super(null, icon);
    }
    
    /**
     *  This constructor creates an action with an icon.
     *
     *  @param name the action's name
     *  @param icon the action's icon
     */
    public BaseOtherActionCommand(String name, Icon icon) {
        super(name, icon);
    }
    
    /**
     *  <p>ActionPerformed is what executed the command.<br>
     *  ActionPerformed is called whenever the action is acted upon.
     *  @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        execute(e);
    }
    
    /** <p>Performs the execution for this action.
     *
     *  @param the action event
     */
    public abstract void execute(ActionEvent e);
    
}






