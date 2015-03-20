/*
 * SaveFunction.java
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


package org.executequery.gui;



/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines those panel views where a save to file is
 * available.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public interface SaveFunction extends MultiplePanelInstance {
    
    /** Indicates a save has been successful. */
    public static final int SAVE_COMPLETE = 0;
    
    /** Indicates a save has failed. */
    public static final int SAVE_FAILED = 1;
    
    /** Indicates a save has been cancelled. */
    public static final int SAVE_CANCELLED = 2;
    
    /** Indicates a save has been invalid. */
    public static final int SAVE_INVALID = 3;
    
    /**
     * Performs a save on this panel view.
     *
     * @param whether to invoke a save as
     */
    public int save(boolean saveAs);

    /**
     * Returns whether to display a save prompt as this 
     * view is being closed.
     *
     * @return true | false
     */
    public boolean promptToSave();
    
}



