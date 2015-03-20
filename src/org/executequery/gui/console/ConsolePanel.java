/*
 * ConsolePanel.java
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


package org.executequery.gui.console;

import java.awt.BorderLayout;
import java.awt.Dimension;

import org.executequery.ActiveComponent;
import org.executequery.base.DefaultTabView;
import org.executequery.gui.*;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The system console base panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/07 15:05:30 $
 */
public class ConsolePanel extends DefaultTabView
                          implements ActiveComponent,
                                     MultiplePanelInstance {
    
    public static final String TITLE = "System Console ";
    public static final String FRAME_ICON = "SystemConsole16.gif";
    
    private Console console;
    
    /** Constructs a new instance. */
    public ConsolePanel() {
        super(new BorderLayout());

        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** Initializes the state of this instance. */
    private void jbInit() throws Exception {
        console = new Console(true);
        setPreferredSize(new Dimension(600, 400));        
        add(console, BorderLayout.CENTER);
    }
    
    public void cleanup() {
        console.cleanup();
    }
    
    public String toString() {
        return TITLE;
    }
    
    /** the instance counter */
    private static int count = 1;
    
    /**
     * Returns the display name for this view.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return TITLE + (count++);
    }

    // --------------------------------------------
    // DockedTabView implementation
    // --------------------------------------------

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        cleanup();
        return true;
    }

}



