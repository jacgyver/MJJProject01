/*
 * StatusBarPanel.java
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


import javax.swing.BorderFactory;
import javax.swing.JLabel;
import org.executequery.Constants;
import org.underworldlabs.swing.AbstractStatusBarPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:53 $
 */
public class StatusBarPanel extends AbstractStatusBarPanel {
    
    /** the status bar panel fixed height */
    private static final int HEIGHT = 20;    
    
    /** <p>Creates a new instance with the specified values
     *  within respective values.
     *
     *  @param the value displayed in the left-most label
     *  @param the value displayed in the second label from the left
     *  @param the value displayed in the right-most label
     */
    public StatusBarPanel(String text1, String text2) {
        super(HEIGHT);
    
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
        setFirstLabelText(text1);
        setThirdLabelText(text2);
    }
    
    /** <p>Initialises the state of this instance. */
    private void jbInit() throws Exception {
        setBorder(BorderFactory.createEmptyBorder(2,3,3,4));
        addLabel(0, 70, true);
        addLabel(1, 150, true);
        addLabel(2, 50, true);
        addLabel(3, 75, false);
        addComponent(new HeapMemoryStatusSnippet(), 4, 150, false);
    }

    public void setThirdLabelText(String text) {
        setLabelText(2, text);
    }
    
    public void setSecondLabelText(String text) {
        setLabelText(1, text);
    }
    
    public void setFirstLabelText(final String text) {
        setLabelText(0, text);
    }
    
    public void resetStatusBar() {
        setLabelText(0, Constants.EMPTY);
        setLabelText(1, Constants.EMPTY);
        setLabelText(2, Constants.EMPTY);
    }
    
    public void setFourthLabelText(String text, int alignment) {
        JLabel label = getLabel(3);
        if (label != null) {
            label.setHorizontalAlignment(alignment);
            setLabelText(3, text);
        }
    }
    
    public void setFourthLabelText(String text) {
        setLabelText(3, text);
    }

}



