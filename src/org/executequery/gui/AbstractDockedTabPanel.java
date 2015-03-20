/*
 * AbstractDockedTabPanel.java
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

import java.awt.LayoutManager;
import javax.swing.JPanel;
import org.executequery.GUIUtilities;
import org.executequery.base.DockedTabView;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Abstract tab panel view object.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public abstract class AbstractDockedTabPanel extends JPanel
                                             implements DockedTabView {
    
    /** Creates a new instance of AbstractDockedTabPanel */
    public AbstractDockedTabPanel() {
        super();
    }

    public AbstractDockedTabPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }
    
    public AbstractDockedTabPanel(LayoutManager layout) {
        super(layout);
    }
    
    public AbstractDockedTabPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    // ----------------------------------------
    // DockedTabView Implementation
    // ----------------------------------------

    
    public int getUserPreferencePosition() {
        return GUIUtilities.getDockedComponentPosition(getPropertyKey());
    }

    public abstract String getPropertyKey();
    
    public abstract String getMenuItemKey();

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewDeselected() {
        return true;
    }

}



