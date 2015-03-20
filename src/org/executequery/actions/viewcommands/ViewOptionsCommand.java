/*
 * ViewOptionsCommand.java
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


package org.executequery.actions.viewcommands;

import java.awt.event.ActionEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;

import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.prefs.PropertiesPanel;
import org.executequery.gui.SystemOutputPanel;
import org.executequery.gui.SystemPropertiesDockedTab;
import org.executequery.gui.browser.ConnectionsTreePanel;
import org.executequery.gui.drivers.DriversTreePanel;
import org.executequery.gui.keywords.KeywordsDockedPanel;
import org.executequery.gui.prefs.PropertyTypes;
import org.executequery.gui.sqlstates.SQLStateCodesDockedPanel;
import org.executequery.toolbars.ToolBarManager;

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
 * @date     $Date: 2006/06/14 15:04:55 $
 */
public class ViewOptionsCommand extends ReflectiveAction {
    
    public ViewOptionsCommand() {}
    
    public void viewStatusBar(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayStatusBar(item.isSelected());
    }
    
    public void viewConsole(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        boolean selected = item.isSelected();
        GUIUtilities.displayDockedComponent(SystemOutputPanel.PROPERTY_KEY, selected);
    }

    public void viewConnections(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        boolean selected = item.isSelected();
        GUIUtilities.displayDockedComponent(ConnectionsTreePanel.PROPERTY_KEY, selected);
    }

    public void viewKeywords(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        boolean selected = item.isSelected();
        GUIUtilities.displayDockedComponent(KeywordsDockedPanel.PROPERTY_KEY, selected);
    }

    public void viewSqlStateCodes(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        boolean selected = item.isSelected();
        GUIUtilities.displayDockedComponent(SQLStateCodesDockedPanel.PROPERTY_KEY, selected);
    }

    public void viewDrivers(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        boolean selected = item.isSelected();
        GUIUtilities.displayDockedComponent(DriversTreePanel.PROPERTY_KEY, selected);
    }

    public void viewSystemProperties(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        boolean selected = item.isSelected();
        GUIUtilities.displayDockedComponent(SystemPropertiesDockedTab.PROPERTY_KEY, selected);
        /*
        GUIUtilities.displayPalette(GUIUtilities.SYSTEM_PROPERTIES_PALETTE,
                                    item.isSelected());
         */
    }

    public void viewBrowserTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.BROWSER_TOOLS, item.isSelected());
    }
    
    public void viewFileTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.FILE_TOOLS, item.isSelected());
    }

    public void viewEditTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.EDIT_TOOLS, item.isSelected());
    }

    public void viewSearchTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.SEARCH_TOOLS, item.isSelected());
    }

    public void viewDatabaseTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.DATABASE_TOOLS, item.isSelected());
    }

    public void viewImportExportTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.IMPORT_EXPORT_TOOLS, item.isSelected());
    }

    /*
    public void viewComponentTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.COMPONENT_TOOLS, item.isSelected());
    }

    public void viewWindowTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.WINDOW_TOOLS, item.isSelected());
    }
    */

    public void viewSystemTools(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        GUIUtilities.displayToolBar(ToolBarManager.SYSTEM_TOOLS, item.isSelected());
    }

    public void customizeTools(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUIUtilities.showWaitCursor();
                BaseDialog dialog = new BaseDialog(PropertiesPanel.TITLE, true);
                PropertiesPanel panel = new PropertiesPanel(dialog, PropertyTypes.TOOLBAR_GENERAL);
                dialog.addDisplayComponentWithEmptyBorder(panel);
                dialog.display();
                GUIUtilities.showNormalCursor();
            }
        });
    }
/*
    public void showFrameIcon(ActionEvent e) {
        SystemProperties.setProperty("desktop.frame.icon", "1");
        GUIUtilities.setViewColourProperties(true);
    }

    public void hideFrameIcon(ActionEvent e) {
        SystemProperties.setProperty("desktop.frame.icon", "0");
        GUIUtilities.setViewColourProperties(true);
    }
*/
    public void viewEditorStatusBar(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        SystemProperties.setBooleanProperty("user",
                                "editor.display.statusbar",
                                item.isSelected());
        GUIUtilities.updateOpenEditors(true);

    }

    public void viewEditorLineNumbers(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
        SystemProperties.setBooleanProperty("user",
                                "editor.display.linenums",
                                item.isSelected());
        GUIUtilities.updateOpenEditors(true);
    }

}



