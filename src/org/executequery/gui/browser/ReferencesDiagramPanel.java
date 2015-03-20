/*
 * ReferencesDiagramPanel.java
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

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.print.Printable;
import java.util.Vector;
import javax.swing.BorderFactory;

import javax.swing.JPanel;

import org.executequery.gui.erd.ErdViewerPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/14 06:56:55 $
 */
public class ReferencesDiagramPanel extends JPanel {
    
    private ErdViewerPanel viewerPanel;
    
    public ReferencesDiagramPanel() {
        super(new BorderLayout());
        viewerPanel = new ErdViewerPanel(false, false);
        viewerPanel.setCanvasBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
        add(viewerPanel, BorderLayout.CENTER);
    }
    
    public void cleanup() {
        viewerPanel.cleanup();
        //viewerPanel = null;
    }
    
    public Printable getPrintable() {
        return viewerPanel.getPrintable();
    }
    
    public void setTables(Vector tableNames, Vector columnData) {
        viewerPanel.resetTableValues(tableNames, columnData);
    }
   
}



