/*
 * PropertiesEditorGeneral.java
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


package org.executequery.gui.prefs;

import java.util.ArrayList;

import org.underworldlabs.util.SystemProperties;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Query Editor general preferences panel.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PropertiesEditorGeneral extends PropertiesBase {
    
    private SimplePreferencesPanel preferencesPanel;

    /** <p>Constructs a new instance. */
    public PropertiesEditorGeneral() {
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        
        ArrayList list = new ArrayList();

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "General",
                    null));

        String key = "editor.tabs.tospaces";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Convert tabs to spaces",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.tab.spaces";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    1,
                    key,
                    "Tab size",
                    SystemProperties.getProperty("user", key)));

        key = "editor.undo.count";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    3,
                    key,
                    "Undo count",
                    SystemProperties.getProperty("user", key)));

        key = "editor.history.count";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    3,
                    key,
                    "History count",
                    SystemProperties.getProperty("user", key)));
/*
        key = "editor.open.count";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    1,
                    key,
                    "Open editor count",
                    SystemProperties.getProperty(key)));
*/
        key = "editor.connection.commit";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Default editor auto-commit",
                    new Boolean(SystemProperties.getBooleanProperty("user", key))));

        key = "editor.results.metadata";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Retain result set meta data",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.logging.verbose";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Print all SQL to output panel",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.logging.enabled";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Log output to file",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.logging.backups";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    1,
                    key,
                    "Maximum rolling log backups",
                    SystemProperties.getProperty("user", key)));

        key = "editor.logging.path";
        list.add(new UserPreference(
                    UserPreference.FILE_TYPE,
                    key,
                    "Output log file path",
                    SystemProperties.getProperty("user", key)));

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "Display",
                    null));

        key = "editor.display.statusbar";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Status bar",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.display.linenums";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Line numbers",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.display.results";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Results panel",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.display.linehighlight";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Current line highlight",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.display.margin";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Right margin",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "editor.margin.size";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    3,
                    key,
                    "Right margin column",
                    SystemProperties.getProperty("user", key)));

        key = "editor.margin.colour";
        list.add(new UserPreference(
                    UserPreference.COLOUR_TYPE,
                    key,
                    "Right margin colour",
                    SystemProperties.getColourProperty("user", key)));

        UserPreference[] preferences = 
                (UserPreference[])list.toArray(new UserPreference[list.size()]);
        preferencesPanel = new SimplePreferencesPanel(preferences);
        addContent(preferencesPanel);

    }
        
    public void restoreDefaults() {
        preferencesPanel.restoreDefaults();
    }

    public String getName() {
        return getClass().getName();
    }

    public void save() {
        preferencesPanel.savePreferences();
    }
    
}



