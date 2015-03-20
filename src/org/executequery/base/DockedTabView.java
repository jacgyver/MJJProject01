/*
 * DockedTabView.java
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


package org.executequery.base;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Defines a docked tab view (non-central panel).
 * This provides methods to retrieve menu and preference 
 * key names for saving application state.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:55 $
 */
public interface DockedTabView extends TabView {
    
    /**
     * Returns the name defining the property name for this docked tab view.
     *
     * @return the key
     */
    public String getPropertyKey();

    /**
     * Returns the position of this tab view.
     *
     * @return the tab position (SwingConstants...)
     */
    public int getUserPreferencePosition();

    /**
     * Returns the name defining the menu cache property
     * for this docked tab view.
     *
     * @return the preferences key
     */
    public String getMenuItemKey();

    /**
     * Returns the display title for this view.
     *
     * @return the title displayed for this view
     */
    public String getTitle();
    
}



