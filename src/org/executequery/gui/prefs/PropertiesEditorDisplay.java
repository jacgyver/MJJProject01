/*
 * PropertiesEditorDisplay.java
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


import org.underworldlabs.util.SystemProperties;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>The Query Editor properties panel.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PropertiesEditorDisplay extends PropertiesBase {
    
    private SimplePreferencesPanel preferencesPanel;
    
    /** <p>Constructs a new instance. */
    public PropertiesEditorDisplay() {       
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        
        UserPreference[] preferences = new UserPreference[7];

        int i = 0;
        String key = "editor.display.statusbar";
        preferences[i++] = new UserPreference(
                UserPreference.BOOLEAN_TYPE,
                key,
                "Status bar",
                new Boolean(SystemProperties.getProperty("user", key)));

        key = "editor.display.linenums";
        preferences[i++] = new UserPreference(
                UserPreference.BOOLEAN_TYPE,
                key,
                "Line numbers",
                new Boolean(SystemProperties.getProperty("user", key)));

        key = "editor.display.results";
        preferences[i++] = new UserPreference(
                UserPreference.BOOLEAN_TYPE,
                key,
                "Results panel",
                new Boolean(SystemProperties.getProperty("user", key)));

        key = "editor.display.linehighlight";
        preferences[i++] = new UserPreference(
                UserPreference.BOOLEAN_TYPE,
                key,
                "Current line highlight",
                new Boolean(SystemProperties.getProperty("user", key)));

        key = "editor.display.margin";
        preferences[i++] = new UserPreference(
                UserPreference.BOOLEAN_TYPE,
                key,
                "Right margin",
                new Boolean(SystemProperties.getProperty("user", key)));

        key = "editor.margin.size";
        preferences[i++] = new UserPreference(
                UserPreference.INTEGER_TYPE,
                3,
                key,
                "Right margin column",
                SystemProperties.getProperty("user", key));

        key = "editor.margin.colour";
        preferences[i++] = new UserPreference(
                UserPreference.COLOUR_TYPE,
                key,
                "Right margin colour",
                SystemProperties.getColourProperty("user", key));

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



