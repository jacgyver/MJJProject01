/*
 * HelpWindow.java
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

import org.executequery.GUIUtilities;

import java.awt.Dimension;
import java.awt.Toolkit;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Enumeration;

import javax.help.BadIDException;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelp;
import javax.help.JHelpNavigator;
import javax.help.JHelpSearchNavigator;
import javax.help.JHelpTOCNavigator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.underworldlabs.swing.util.IconUtilities;
import org.underworldlabs.util.SystemProperties;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The system Help window.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.11 $
 * @date     $Date: 2006/09/15 10:54:44 $
 */
public class HelpWindow {

    /** the target page to display */
    private String target;
    
    /** Indicates whether a help search has been requested */
    private boolean isSearch;

    /** IDs to be expanded */
    private static final String[] EXPAND_IDS = {"release-info",
                                                "database-explorer",
                                                "query-editor",
                                                "erd",
                                                "import-export"};

    /** Opens a new help window */
    public HelpWindow() {
        this(null);
    }

    /**
     * Opens a new help window with the specified target page selected.
     *
     * @param page - the target page ID to display
     */
    public HelpWindow(String page) {
        target = page;
        isSearch = false;
        if (page!= null && page.equals("search_help_on")) {
            isSearch = true;
        }        
        execute();
    }

    /**
     * Creates the help context to be shown and displays the frame.
     */
    private void execute() {
        try {
            ClassLoader classLoader = HelpWindow.class.getClassLoader();
            URL url = HelpSet.findHelpSet(classLoader, "eq.hs");
            HelpSet set = new HelpSet(classLoader, url);
            JHelp help = new JHelp(set);

            // ----------------------
            // TODO: check this - help still showing search after a 
            // search selected then a normal selection !!??
            /*
            // check that the current navigator is the default TOC
            boolean resetNavigator = false;
            JHelpNavigator currentNavigator = help.getCurrentNavigator();
            if (!(currentNavigator instanceof JHelpTOCNavigator)) {
                resetNavigator = true;
            }
            */
            // ----------------------

            JFrame f = new JFrame("Execute Query Help");            
            ImageIcon frameIcon = IconUtilities.loadIcon(
                            "/org/executequery/icons/Help16.gif");
            f.setIconImage(frameIcon.getImage());

            f.setSize(800, 700);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = f.getSize();
            
            if (frameSize.height > screenSize.height) {
                frameSize.height = screenSize.height;
            }
            if (frameSize.width > screenSize.width) {
                frameSize.width = screenSize.width;
            }
            
            f.setLocation((screenSize.width - frameSize.width) - 50,
                            (screenSize.height - frameSize.height) / 2);

            try {
                if (!isSearch && target != null) {
                    help.setCurrentID(target);
                }
            }
            catch (BadIDException badIdExc) {}
            
            for (Enumeration i = help.getHelpNavigators(); i.hasMoreElements();) {
                Object o = i.nextElement();
                
                //System.out.println("class: "+o.getClass().getName());

                if (o instanceof JHelpTOCNavigator) {
                    JHelpTOCNavigator toc = (JHelpTOCNavigator)o;

                    // make sure the toc is the current navigator
                    // if we haven't launched in search mode
                    if (!isSearch) {
                        help.setCurrentNavigator(toc);
                    }

                    for (int j = 0; j < EXPAND_IDS.length; j++) {
                        toc.expandID(EXPAND_IDS[j]);
                    }

                    if (!isSearch) {
                        break;
                    }

                }

                if (isSearch && o instanceof JHelpSearchNavigator) {
                    help.setCurrentNavigator((JHelpSearchNavigator)o);
                }
            }
           
            f.setContentPane(help);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setVisible(true);

            // reset initial values - noticed some strange
            // behaviour when reopening help window.
            // TODO: check this - not making a difference -  why did i do it?
            isSearch = false;
            
        }
        catch (HelpSetException e) {
            GUIUtilities.displayExceptionErrorDialog(
                    "The system could not\nfind the help files specified.\n\n" +
                             "System Error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Allows for displaying the help viewer outside of eq itself.
     */
    public static void main(String[] args) {
        // make sure system properties are loaded
        SystemProperties.loadPropertiesResource(
                "system", "org/executequery/eq.system.properties");
        // new default window
        new HelpWindow();
    }
    
}