/*
 * FocusComponentPanel.java
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

import java.awt.Component;

/**
 * Defines those component that contain a field or similar 
 * that requires focus after being realised.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.2 $
 * @date     $Date: 2006/06/28 14:12:54 $
 */
public interface FocusComponentPanel {
    
    /**
     * Returns the default focus component of this object.
     */
    public Component getDefaultFocusComponent();
    
}