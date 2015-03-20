/*
 * ActionContainer.java
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
 * Defines a container (dialog, internal frame etc.) where
 * a component panel completing a process may indicate for this
 * container to close.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/07 15:05:09 $
 */
public interface ActionContainer {

    /**
     * Indicates whether this is a dialog.
     *
     * @return true | false
     */
    public boolean isDialog();
    
    /**
     * Indicates that a [long-running] process has begun or ended
     * as specified. This may trigger the glass pane on or off 
     * or set the cursor appropriately.
     *
     * @param inProcess - true | false
     */
    public void setInProcess(boolean inProcess);

    /**
     * Indicates the process has completed.
     */
    public void finished();

}



