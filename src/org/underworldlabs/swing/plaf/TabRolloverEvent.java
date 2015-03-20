/*
 * TabRolloverEvent.java
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


package org.underworldlabs.swing.plaf;

import java.util.EventObject;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines a tab rectangle rollover event.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class TabRolloverEvent extends EventObject {
    
    /** the tab index of the rollover */
    private int index;
    
    /** the x-coord */
    private int x;
    
    /** the y-coord */
    private int y;    

    /** 
     * Creates a new instance of TabRolloverEvent with the
     * specified object as the source of this event.
     *
     * @param the source object
     */
    public TabRolloverEvent(Object source, int index) {
        this(source, index, -1, -1);
    }

    /** 
     * Creates a new instance of TabRolloverEvent with the
     * specified object as the source of this event.
     *
     * @param the source object
     */
    public TabRolloverEvent(Object source, int index, int x, int y) {
        super(source);
        this.index = index;
        this.x = x;
        this.y = y;
    }

    /** 
     * Returns the tab index where this event originated.
     *
     * @return the tab index
     */
    public int getIndex() {
        return index;
    }

    /**
     * The x-coord of the underlying mouse event.
     *
     * @return the x-coord
     */
    public int getX() {
        return x;
    }

    /**
     * The y-coord of the underlying mouse event.
     *
     * @return the y-coord
     */
    public int getY() {
        return y;
    }
    
}



