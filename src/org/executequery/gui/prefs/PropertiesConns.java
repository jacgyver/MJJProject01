/*
 * PropertiesConns.java
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
import org.executequery.ConnectionProperties;
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
public class PropertiesConns extends PropertiesBase {
    
    private SimplePreferencesPanel preferencesPanel;
    /*
    private JCheckBox reuseCheck;
    private JCheckBox startupCheck;
    private JComboBox useCountCombo;
    private JComboBox schemeCombo;
    private JComboBox numberCombo;
    private JComboBox connStartCombo;
    private JLabel useCountLabel;
    
    private Vector dca;
    */
    public PropertiesConns() {
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() {

        ArrayList list = new ArrayList();

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "General",
                    null));

        String key = "connection.initialcount";
        list.add(new UserPreference(
                UserPreference.INTEGER_TYPE,
                1,
                key,
                "Initial open connections",
                SystemProperties.getProperty("user", key)));

        key = "connection.scheme";
        list.add(new UserPreference(
                UserPreference.STRING_TYPE,
                key,
                "Connection Scheme",
                SystemProperties.getProperty("user", key),
                new String[]{"Dynamic", "Static"}));

        key = "connection.reuse";
        list.add(new UserPreference(
                UserPreference.BOOLEAN_TYPE,
                key,
                "Reuse connection",
                new Boolean(SystemProperties.getProperty("user", key))));

        key = "connection.reuse.count";
        list.add(new UserPreference(
                UserPreference.INTEGER_TYPE,
                2,
                key,
                "Connection reuse count",
                SystemProperties.getProperty("user", key)));

        key = "startup.connection.connect";
        list.add(new UserPreference(
                UserPreference.BOOLEAN_TYPE,
                key,
                "Connect at startup",
                new Boolean(SystemProperties.getProperty("user", key))));
        
        key = "startup.connection.name";
        list.add(new UserPreference(
                UserPreference.STRING_TYPE,
                key,
                "Startup connection",
                SystemProperties.getProperty("user", key),
                ConnectionProperties.getConnectionNames()));

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






