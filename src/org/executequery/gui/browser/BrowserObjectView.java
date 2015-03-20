/*
 * BrowserObjectView.java
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

import java.awt.print.Printable;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines those components with a browser view.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/11 02:18:36 $
 */
public interface BrowserObjectView {
    
    /** Performs some cleanup and releases resources before being closed. */
    public void cleanup();
    
    /** Refreshes the data and clears the cache */
    public void refresh();
    
    /** Returns the print object - if any */
    public Printable getPrintable();
    
    /** Returns the name of this panel */
    public String getLayoutName();
    
    /** Propagates the call to the relevant component. */
    public void validate();

    /** Propagates the call to the relevant component. */
    public void repaint();
    
}



