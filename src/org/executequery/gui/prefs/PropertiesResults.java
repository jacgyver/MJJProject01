/*
 * PropertiesResults.java
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

/** <p>The properties for the editor's results panel
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PropertiesResults extends PropertiesBase {
    
    private SimplePreferencesPanel preferencesPanel;
    
    /**
     * Constructs a new instance.
     */
    public PropertiesResults() {
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the state of this instance.
     */
    private void jbInit() throws Exception {

        ArrayList list = new ArrayList();

        list.add(new UserPreference(
                    UserPreference.CATEGORY_TYPE,
                    null,
                    "ResultSet Table",
                    null));

        String key = "results.table.column.resize";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Columns resizeable",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "results.table.column.reorder";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Column reordering",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "results.table.row.select";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Row selection",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "results.table.row.numbers";
        list.add(new UserPreference(
                    UserPreference.BOOLEAN_TYPE,
                    key,
                    "Row number header",
                    new Boolean(SystemProperties.getProperty("user", key))));

        key = "results.table.column.width";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    3,
                    key,
                    "Column width",
                    SystemProperties.getProperty("user", key)));

        key = "results.table.column.height";
        list.add(new UserPreference(
                    UserPreference.INTEGER_TYPE,
                    3,
                    key,
                    "Column Height",
                    SystemProperties.getProperty("user", key)));

        key = "results.table.cell.background.colour";
        list.add(new UserPreference(
                    UserPreference.COLOUR_TYPE,
                    key,
                    "Cell background",
                    SystemProperties.getColourProperty("user", key)));

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



