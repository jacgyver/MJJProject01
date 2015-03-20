/*
 * PropertiesAppearance.java
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
import org.executequery.Constants;
import org.underworldlabs.util.SystemProperties;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * System preferences appearance panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/14 15:06:31 $
 */
public class PropertiesAppearance extends PropertiesBase {
    
    private SimplePreferencesPanel preferencesPanel;
    
    /** <p>Constructs a new instance. */
    public PropertiesAppearance() {
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Initializes the state of this instance. */
    private void jbInit() throws Exception {
        
        ArrayList list = new ArrayList();
        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "General",
                    null));
        
        String key = "system.display.statusbar";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Status bar",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "system.display.console";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "System console",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "system.display.connections";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Connections",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "system.display.drivers";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Drivers",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "system.display.keywords";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "SQL Keywords",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "system.display.state-codes";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "SQL State Codes",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "system.display.systemprops";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "System properties palette",
                    new Boolean(SystemProperties.getProperty("user", key))));

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "Appearance",
                    null));

        key = "startup.display.lookandfeel";
        list.add(new UserPreference(
                    UserPreference.STRING_TYPE,
                    key,
                    "Look and feel (requires restart)",
                    SystemProperties.getProperty("user", key),
                    Constants.LOOK_AND_FEELS));

        key = "desktop.background.custom.colour";
        list.add(new UserPreference(
                    UserPreference.COLOUR_TYPE,
                    key,
                    "Desktop background",
                    SystemProperties.getColourProperty("user", key)));

        key = "decorate.dialog.look";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Decorate dialogs",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "decorate.frame.look";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Decorate frame",
                    new Boolean(SystemProperties.getProperty("user", key))));

        UserPreference[] preferences = 
                (UserPreference[])list.toArray(new UserPreference[list.size()]);
        preferencesPanel = new SimplePreferencesPanel(preferences);
        addContent(preferencesPanel);

    }
    
    public void restoreDefaults() {
        preferencesPanel.savePreferences();
    }
    
    public void save() {
        preferencesPanel.savePreferences();
    }
    
}



