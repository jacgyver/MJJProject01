/*
 * PropertiesLocales.java
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.underworldlabs.swing.DisabledField;
import org.underworldlabs.swing.util.StringSorter;

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
public class PropertiesLocales extends PropertiesBase
                               implements ListSelectionListener {
    
    private JList localeList;
    private JList timezoneList;
    
    private DisabledField selectedLocaleField;
    private DisabledField selectedTimeZoneField;
    
    private Locale[] locales;
    private String[] timezones;
    
    public PropertiesLocales() {
        GUIUtilities.showWaitCursor();        
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            GUIUtilities.showNormalCursor();
        }
    }
    
    private void jbInit() throws Exception {
        
        JLabel localesLabel = new JLabel("Language Locales:");
        JLabel timezonesLabel = new JLabel("Time Zones:");
        
        selectedLocaleField = new DisabledField();
        selectedTimeZoneField = new DisabledField();
        
        locales = Locale.getAvailableLocales();
        timezones = TimeZone.getAvailableIDs();

        Arrays.sort(locales, new LocalesComparator());
        Arrays.sort(timezones, new StringSorter());

        String[] locValues = new String[locales.length];
        String country = SystemProperties.getProperty("user", "locale.country");
        String language = SystemProperties.getProperty("user", "locale.language");
        
        boolean selectedFound = false;
        String selectedLocValue = null;
        
        for (int i = 0; i < locValues.length; i++) {
            locValues[i] = locales[i].getDisplayName();
            
            if (!selectedFound) {
                
                if (country.compareTo(locales[i].getCountry()) == 0 &&
                        language.compareTo(locales[i].getLanguage()) == 0) {
                    selectedLocValue = locValues[i];
                    selectedFound = true;
                }
                
            }
            
        }
        
        localeList = new JList(locValues);
        timezoneList = new JList(timezones);
        
        localeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timezoneList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane localeScroll = new JScrollPane(localeList);
        JScrollPane timezoneScroll = new JScrollPane(timezoneList);
        
        Dimension scrollerDim = new Dimension(370, 100);
        localeScroll.setPreferredSize(scrollerDim);
        timezoneScroll.setPreferredSize(scrollerDim);

        localeList.setSelectedValue(selectedLocValue, true);
        timezoneList.setSelectedValue(SystemProperties.getProperty("user", "locale.timezone"), true);
        
        selectedLocaleField.setText(selectedLocValue);
        selectedTimeZoneField.setText(SystemProperties.getProperty("user", "locale.timezone"));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.bottom = 5;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Time Zones:"), gbc);
        gbc.gridy++;
        panel.add(selectedTimeZoneField, gbc);
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(timezoneScroll, gbc);
        gbc.gridy++;
        gbc.insets.top = 10;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Language Locales:"), gbc);
        gbc.gridy++;
        gbc.insets.top = 0;
        panel.add(selectedLocaleField, gbc);
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.insets.bottom = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(localeScroll, gbc);

        addContent(panel);
        localeList.addListSelectionListener(this);
        timezoneList.addListSelectionListener(this);
    }
    
    public void restoreDefaults() {
        timezoneList.setSelectedValue(
        SystemProperties.getProperty("defaults","locale.timezone"), true);
        localeList.setSelectedValue(
        SystemProperties.getProperty("defaults","locale.country"), true);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        selectedTimeZoneField.setText((String)timezoneList.getSelectedValue());
        selectedLocaleField.setText(locales[localeList.getSelectedIndex()].getDisplayName());
    }
    
    public void save() {
        Locale loc = locales[localeList.getSelectedIndex()];
        SystemProperties.setProperty("user", "locale.country", loc.getCountry());
        SystemProperties.setProperty("user", "locale.language", loc.getLanguage());
        SystemProperties.setProperty("user", "locale.timezone", selectedTimeZoneField.getText());
        
        System.setProperty("user.country", loc.getCountry());
        System.setProperty("user.language", loc.getLanguage());
        System.setProperty("user.timezone", selectedTimeZoneField.getText());
    }
    
    class LocalesComparator implements Comparator {

        public int compare(Object obj1, Object obj2) {
            Locale loc1 = (Locale)obj1;
            Locale loc2 = (Locale)obj2;
            
            String value1 = loc1.getDisplayName();
            String value2 = loc2.getDisplayName();
            
            int result = value1.compareTo(value2);

            if (result < 0)
                return -1;
            else if (result > 0)
                return 1;
            else
                return 0;
            
        }
        
    } // class LocalesComparator

}



