/*
 * PropertiesGeneral.java
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

/** <p>System preferences general panel.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PropertiesGeneral extends PropertiesBase {
    
    private SimplePreferencesPanel preferencesPanel;
    
    /** <p>Constructs a new instance. */
    public PropertiesGeneral() {
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

        String key = "startup.display.splash";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Display Splash Screen at Startup",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "startup.window.maximized";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Maximise window on startup",
                    new Boolean(SystemProperties.getProperty("user", key))));
/*
        key = "startup.display.openwindow";
        list.add(new UserPreference(
                    UserPreference.STRING_TYPE,
                    key,
                    "Open window on startup",
                    SystemProperties.getProperty(key),
                    new String[]{"No open window", "Query Editor", "Database Browser"}));

        key = "desktop.frame.icon";
        list.add(new UserPreference(
                    UserPreference.STRING_TYPE,
                    key,
                    "Iconified frame behaviour",
                    SystemProperties.getProperty(key),
                    new String[]{"No frame icon", "Display frame icon"}));
*/
        key = "recent.files.count";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    1,
                    key,
                    "Recent files to store",
                    SystemProperties.getProperty("user", key)));

        key = "general.save.prompt";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Prompt to save open documents",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "system.log.level";
        list.add(new UserPreference(
                    UserPreference.STRING_TYPE,
                    key,
                    "Output log level",
                    SystemProperties.getProperty("user", key),
                    Constants.LOG_LEVELS));

        UserPreference[] preferences = 
                (UserPreference[])list.toArray(new UserPreference[list.size()]);
        preferencesPanel = new SimplePreferencesPanel(preferences);
        addContent(preferencesPanel);
    }

    public void restoreDefaults() {
        preferencesPanel.restoreDefaults();
    }
    
    public void save() {      
        preferencesPanel.savePreferences();
    }
    
}



