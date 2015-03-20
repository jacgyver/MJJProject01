/*
 * CancelButton.java
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


package org.executequery.components;

import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.Insets;

import org.executequery.actions.othercommands.CancelCommand;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Simple button and action for closing an
 *  internal frame typically implemented as a
 *  'Cancel' button within the frame.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class CancelButton extends JButton {
    
    /** <p>Constructs a new instance. */
    public CancelButton() {
        super("Cancel");
        setPreferredSize(new Dimension(75, 35));
        setAction(new CancelCommand());
        setMargin(new Insets(2, 2, 2, 2));
    }
    
    /** <p>Constructs a new instance with the specified
     *  text to be displayed on the button.
     *
     *  @param the text to be displayed
     */
    public CancelButton(String text) {
        super(text);
        setPreferredSize(new Dimension(75, 35));
        setAction(new CancelCommand());
        setMargin(new Insets(2, 2, 2, 2));
    }
    
    
    
}








