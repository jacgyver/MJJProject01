/*
 * PropertiesBrowserGeneral.java
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

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PropertiesBrowserGeneral extends PropertiesBase {
    
    private SimplePreferencesPanel preferencesPanel;
    
    public PropertiesBrowserGeneral() {
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() {
        String[] values = {"100", "500", "1000", "5000", "10000",
                           "50000", "100000", "All Records"};

        ArrayList list = new ArrayList();

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "General",
                    null));

        String key = "browser.max.records";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    key,
                    "Maximum records returned",
                    SystemProperties.getProperty("user", key),
                    values));

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



