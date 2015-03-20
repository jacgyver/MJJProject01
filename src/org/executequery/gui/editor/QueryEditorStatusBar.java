/*
 * QueryEditorStatusBar.java
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


package org.executequery.gui.editor;

import javax.swing.JLabel;
import org.underworldlabs.swing.AbstractStatusBarPanel;
import org.underworldlabs.swing.IndeterminateProgressBar;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Query Editor status bar panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class QueryEditorStatusBar extends AbstractStatusBarPanel {
    
    /** The buffer containing constantly changing values */
    private StringBuffer caretBuffer;
    
    /** the progress bar */
    private IndeterminateProgressBar progressBar;
    
    /** the status bar panel fixed height */
    private static final int HEIGHT = 19;
    
    public QueryEditorStatusBar() {
        super(HEIGHT);
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** <p>Initialises the state of this instance. */
    private void jbInit() throws Exception {
        caretBuffer = new StringBuffer();
        // setup the progress bar
        progressBar = new IndeterminateProgressBar(false);

        addLabel(0, 100, true); // activity label
        addComponent(progressBar, 1, 120, false); // progress bar
        addLabel(2, 90, false); // execution time
        addLabel(3, 35, false); // insert mode
        addLabel(4, 60, false); // caret position
        addLabel(5, 40, true); // commit mode

        // set some labels to center alignment
        getLabel(3).setHorizontalAlignment(JLabel.CENTER);
        getLabel(4).setHorizontalAlignment(JLabel.CENTER);
    }
    
    /**
     * Cleanup code to ensure the progress thread is dead.
     */
    public void cleanup() {
        progressBar.cleanup();
        progressBar = null;
    }
    
    /**
     * Starts the progress bar.
     */
    public void startProgressBar() {
        progressBar.start();
    }

    /**
     * Stops the progress bar.
     */
    public void stopProgressBar() {
        progressBar.stop();
    }

    /**
     * Sets the query execution time to that specified.
     */
    public void setExecutionTime(String text) {
        setLabelText(2, text);
    }
    
    /**
     * Sets the editor commit status to the text specified.
     */    
    public void setCommitStatus(String text) {
        setLabelText(5, text);
    }

    /**
     * Sets the editor insert mode to that specified.
     */    
    public void setInsertionMode(String text) {
        setLabelText(3, text);
    }

    /**
     * Sets the editor status to the text specified.
     */
    public void setStatus(String text) {
        setLabelText(0, text);
    }
    
    /**
     * Sets the caret position to be formatted.
     *
     * @param l - the line number
     * @param c - the column number
     */
    public void setCaretPosition(int l, int c) {
        caretBuffer.append(" ").append(l).append(':').append(c);
        setLabelText(4, caretBuffer.toString());
        caretBuffer.setLength(0);
    }
 
}



