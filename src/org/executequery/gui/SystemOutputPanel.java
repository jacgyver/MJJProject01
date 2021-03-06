/*
 * SystemOutputPanel.java
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

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.PatternLayout;
import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.executequery.components.TextAreaLogAppender;
import org.executequery.util.Log;
import org.underworldlabs.util.FileUtils;
import org.underworldlabs.util.SystemProperties;

// TODO: NEW STUFF

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class SystemOutputPanel extends AbstractDockedTabPanel {
    
    /** This panel's title */
    public static final String TITLE = "Output Console";

    /** the output text area */
    private JTextArea textArea;

    public SystemOutputPanel() {
        super(new BorderLayout());        
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() throws Exception {
        if (!Log.isLogEnabled()) {
            Log.init(SystemProperties.getProperty("user", "system.log.level"));
        }

        textArea = new JTextArea();
        textArea.setFont(new Font("dialog", 0, 11));
        textArea.setEditable(false);
        
        TextAreaLogAppender appender = new TextAreaLogAppender(textArea);
        appender.setLayout(new PatternLayout(Log.PATTERN));
        Log.addAppender(appender);

        JScrollPane scroller = new JScrollPane(textArea);
        //scroller.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scroller.setBorder(BorderFactory.
                    createMatteBorder(1, 0, 0, 0, GUIUtilities.getDefaultBorderColour()));
        add(scroller, BorderLayout.CENTER);
    }

    /**
     * Resets the contents of the system log.
     */
    public void resetSystemLog() {
        try {
            FileUtils.writeFile(SystemUtilities.getSystemLogPath(), Constants.EMPTY);
        } catch (IOException e) {}
    }
    
    public Icon getIcon() {
        return GUIUtilities.loadIcon("SystemOutput.gif");
    }
    
    public String toString() {
        return "Output Console";
    }
    
    // ----------------------------------------
    // DockedTabView Implementation
    // ----------------------------------------

    public static final String MENU_ITEM_KEY = "viewConsole";
    
    public static final String PROPERTY_KEY = "system.display.console";

    
    /**
     * Returns the display title for this view.
     *
     * @return the title displayed for this view
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * Returns the name defining the property name for this docked tab view.
     *
     * @return the key
     */
    public String getPropertyKey() {
        return PROPERTY_KEY;
    }

    /**
     * Returns the name defining the menu cache property
     * for this docked tab view.
     *
     * @return the preferences key
     */
    public String getMenuItemKey() {
        return MENU_ITEM_KEY;
    }

}



