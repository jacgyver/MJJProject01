/*
 * MainMenu.java
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

import java.awt.event.ActionListener;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.underworldlabs.util.SystemProperties;
import org.executequery.SystemUtilities;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.executequery.actions.filecommands.OpenRecentFileCommand;
import org.underworldlabs.swing.util.MenuBuilder;
import org.executequery.databasemediators.DatabaseConnection;
import org.underworldlabs.util.MiscUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Application main menu.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/14 15:04:55 $
 */
public class MainMenu extends JMenuBar {
   
    /** main menu cache */
    private Map<String,JMenu> menuCache;

    /** menu item cache */
    private Map<String,JMenuItem> menuItemCache;

    /** menu item action listener cache */
    private Map<String,ActionListener> listenerCache;

    /** menu builder utility class */
    private static MenuBuilder builder;   

    public MainMenu() {
        try {
            builder = new MenuBuilder();
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void jbInit() throws Exception {
        setBorder(null);
        
        InputStream input = null;
        ClassLoader cl = ActionBuilder.class.getClassLoader();
        String path = "org/executequery/menus.xml";
        
        if (cl != null) {
            input = cl.getResourceAsStream(path);
        }
        else {
            input = ClassLoader.getSystemResourceAsStream(path);
        }
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            
            SAXParser parser = factory.newSAXParser();
            MenuHandler handler = new MenuHandler();
            parser.parse(input, handler);
        } 
        catch (Exception e) {
            e.printStackTrace();
            throw new InternalError();
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioExc) {}
        }

        // add the desktop background menu
        /*
        JMenu menu = (JMenu)menuCache.get("viewMenu");
        JMenuItem menuItem = new ColorMenu("Desktop Background", ColorMenu.DESKTOP_BG);
        menu.insert(menuItem, 6);

        if (menuItemCache == null) {
            menuItemCache = new HashMap();
        }
        menuItemCache.put("desktopBackground", menuItem);
        */

        // add the saved connections menu items
        setConnectionsMenu(SystemUtilities.getSavedConnections());
        
        // add the recent files list
        setRecentFileMenu(SystemUtilities.getRecentFilesList());
        
        // set the options checks
        //setDesktopOptions();
        setEditorViewOptions();

    }

    protected void loadListenerCache() {
        if (listenerCache == null) {
            listenerCache = new HashMap<String,ActionListener>();
        }
    }
    
    public void reloadRecentFileMenu() {
        setRecentFileMenu(SystemUtilities.getRecentFilesList());
    }
    
    public void addNewRecentFileMenu(String name, String path) {
        setRecentFileMenu(SystemUtilities.getRecentFilesList());
        /*
        loadListenerCache();
        JMenu menu = (JMenu)menuCache.get("recentFiles");
        JMenuItem menuItem = builder.createMenuItem(menu,
                                                    builder.ITEM_PLAIN,
                                                    name, 
                                                    null, 
                                                    0, 
                                                    path,
                                                    null,
                                                    path);
        ActionListener listener = null;
        if (listenerCache.containsKey("openRecent")) {
            listener = (ActionListener)listenerCache.get("openRecent");
        } else {
            listener = new OpenRecentFileCommand();
            listenerCache.put("openRecent", listener);
        }

        menuItem.addActionListener(listener);
         **/
    }

    public void setRecentFileMenu(String[] files) {
        
        if (files == null || files.length == 0) {
            return;
        }

        loadListenerCache();
        JMenu menu = (JMenu)menuCache.get("recentFiles");
        menu.removeAll();

        ActionListener listener = null;
        if (listenerCache.containsKey("openRecent")) {
            listener = (ActionListener)listenerCache.get("openRecent");
        } else {
            listener = new OpenRecentFileCommand();
            listenerCache.put("openRecent", listener);
        }

        for (int i = 0; i < files.length; i++) {
            File file = new File(files[i]);
            JMenuItem menuItem = builder.createMenuItem(menu,
                                                        builder.ITEM_PLAIN,
                                                        file.getName(), 
                                                        null, 
                                                        0, 
                                                        file.getAbsolutePath(),
                                                        null,
                                                        file.getAbsolutePath());
            menuItem.addActionListener(listener);
        }

    }

    public void addNewConnectionMenu(DatabaseConnection sc) {
        JMenu menu = (JMenu)menuCache.get("databaseConnect");
        builder.createMenuItem(menu, 
                               builder.ITEM_PLAIN,
                               sc.getName(), 
                               null, 
                               0, 
                               null,
                               ActionBuilder.get("connect-command"));
    }
    
    public void setConnectionsMenu(DatabaseConnection[] sc) {

        if (sc == null) {
            return;
        }

        JMenu menu = (JMenu)menuCache.get("databaseConnect");
        JMenuItem menuItem = menu.getItem(0);

        menu.removeAll();
        menu.add(menuItem);
        
        if (sc.length > 0) {
            menu.addSeparator();
        }

        Action connect = ActionBuilder.get("connect-command");        
        for (int i = 0; i < sc.length; i++) {
            menuItem = builder.createMenuItem(menu, 
                                              builder.ITEM_PLAIN,
                                              sc[i].getName(), 
                                              null, 
                                              0, 
                                              null, 
                                              connect);
            menuItem.setAccelerator(null);
        }

    }

    public void setEditorViewOptions() {
        setCheckBoxMenuItemSelections(viewMenuItems);
    }
    
    public void initialiseViewToolsMenuItems(boolean viewFile, 
                                             boolean viewEdit,
                                             boolean viewSearch, 
                                             boolean viewDatabase,
                                             boolean viewBrowser,
                                             boolean viewImportExport,
                                             boolean viewSystem) {
        
        JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItemCache.get("viewFileTools");
        item.setSelected(viewFile);

        item = (JCheckBoxMenuItem)menuItemCache.get("viewEditTools");
        item.setSelected(viewEdit);

        item = (JCheckBoxMenuItem)menuItemCache.get("viewSearchTools");
        item.setSelected(viewSearch);

        item = (JCheckBoxMenuItem)menuItemCache.get("viewDatabaseTools");
        item.setSelected(viewDatabase);

        item = (JCheckBoxMenuItem)menuItemCache.get("viewBrowserTools");
        item.setSelected(viewBrowser);

        item = (JCheckBoxMenuItem)menuItemCache.get("viewImportExportTools");
        item.setSelected(viewImportExport);

        item = (JCheckBoxMenuItem)menuItemCache.get("viewSystemTools");
        item.setSelected(viewSystem);
    }
    
    public void setViewOption(String key, boolean show) {
        if (menuItemCache.containsKey(key)) {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItemCache.get(key);
            item.setSelected(show);
        }
    }
    
    /** view check box menu items cache names and property keys */
    private Map<String,String> viewMenuItems;
    
    private void setCheckBoxMenuItemSelections(Map<String,String> cache) {
        for (Iterator i = cache.keySet().iterator(); i.hasNext();) {
            String key = i.next().toString();
            boolean checked = SystemProperties.getBooleanProperty("user", cache.get(key));
            JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItemCache.get(key);
            item.setSelected(checked);
        }
    }
    
    public void setViewOptions() {
        setCheckBoxMenuItemSelections(viewMenuItems);
    }
    
    private static final String MENUS = "menus";
    private static final String MENU = "menu";
    private static final String MENU_ITEM = "menu-item";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String ID = "id";
    private static final String BUTTON_GROUP = "button-group";
    private static final String MNEMONIC = "mnemonic";
    private static final String SEPARATOR = "separator";
    private static final String LISTENER = "listener";
    private static final String CACHE_NAME = "cache-name";
    private static final String TOOL_TIP = "tool-tip";
    private static final String ACTION_COMMAND = "action-command";
    private static final String ACCEL_KEY = "accel-key";
    private static final String VIEW_MENU_PROPERTY_KEY = "property-key";
    private static final String NULL_VALUE = "{-NULL-}";

    class MenuHandler extends DefaultHandler {
        
        private List<JMenu> menus;
        private Map<String,ActionListener> listeners;
        private Map<String,ButtonGroup> buttonGroups;
        private int menuCount;
        
        private CharArrayWriter contents;
        
        public MenuHandler() {
            menuCount = -1;
            listeners = new HashMap<String,ActionListener>();
            buttonGroups = new HashMap<String,ButtonGroup>();
            menus = new ArrayList<JMenu>();
            contents = new CharArrayWriter();
        }
       
        public void startElement(String nameSpaceURI, 
                                 String localName,
                                 String qName, 
                                 Attributes attrs) {
            String value = null;
            contents.reset();

            if (localName.equals(MENU)) {

                String menuName = attrs.getValue(NAME);

                JMenu menu = null;
                value = attrs.getValue(MNEMONIC);
                if (!MiscUtils.isNull(value)) {
                    menu = builder.createMenu(menuName, value.charAt(0));
                } else {
                    menu = builder.createMenu(menuName, -1);
                }
                menus.add(menu);
                menuCount++;

                value = attrs.getValue(CACHE_NAME);
                if (!MiscUtils.isNull(value)) {
                    if (menuCache == null) {
                        menuCache = new HashMap<String,JMenu>();
                    }
                    menuCache.put(value, menu);
                }

            }
            else if (localName.equals(MENU_ITEM)) {

                int menuType = 0;
                value = attrs.getValue(TYPE);
                if (!MiscUtils.isNull(value)) {
                    menuType = Integer.parseInt(value);
                }
                
                JMenuItem menuItem = null;
                switch(menuType) {
                    case MenuBuilder.ITEM_RADIO:
                        menuItem = new JRadioButtonMenuItem();
                        break;
                    case MenuBuilder.ITEM_CHECK:
                        menuItem = new JCheckBoxMenuItem();
                        break;
                    default:
                        menuItem = new JMenuItem();
                        break;
                } 

                value = attrs.getValue(ID);
                if (!MiscUtils.isNull(value)) {                    
                    if (value.equals(SEPARATOR)) {
                        getMenu(menuCount).addSeparator();
                        return;
                    } else {
                        menuItem.setAction(ActionBuilder.get(value));
                    }
                }

                value = attrs.getValue(MNEMONIC);
                if (!MiscUtils.isNull(value)) {
                    menuItem.setMnemonic(value.charAt(0));
                }

                value = attrs.getValue(NAME);
                if (!MiscUtils.isNull(value)) {
                    menuItem.setText(value);
                }
                
                value = attrs.getValue(TOOL_TIP);
                if (!MiscUtils.isNull(value)) {
                    menuItem.setToolTipText(value);
                }
                
                value = attrs.getValue(ACTION_COMMAND);
                if (!MiscUtils.isNull(value)) {
                    menuItem.setActionCommand(value);
                }

                value = attrs.getValue(LISTENER);
                if (!MiscUtils.isNull(value)) {
                    
                    if (listeners.containsKey(value)) {
                        menuItem.addActionListener(listeners.get(value));
                    } else {
                        try {
                            Class _class = Class.forName(value, 
                                                         true,
                                                         ClassLoader.getSystemClassLoader());
                            Object object = _class.newInstance();
                            ActionListener listener = (ActionListener)object;
                            menuItem.addActionListener(listener);
                            listeners.put(value, listener);
                        } 
                        catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            throw new InternalError();
                        }         
                        catch (Exception e) {
                            e.printStackTrace();
                        } 
                    }

                }
                
                value = attrs.getValue(ACCEL_KEY);
                if (!MiscUtils.isNull(value)) {
                    
                    if (value.equals(NULL_VALUE)) {
                        menuItem.setAccelerator(null);
                    } else {                    
                        menuItem.setAccelerator(KeyStroke.getKeyStroke(value));
                    }

                }

                value = attrs.getValue(BUTTON_GROUP);
                if (!MiscUtils.isNull(value)) {
                    if (buttonGroups.containsKey(value)) {
                        ButtonGroup bg = buttonGroups.get(value);
                        bg.add(menuItem);
                    } else {
                        ButtonGroup bg = new ButtonGroup();
                        buttonGroups.put(value, bg);
                        bg.add(menuItem);
                    }
                }

                value = attrs.getValue(CACHE_NAME);
                if (!MiscUtils.isNull(value)) {
                    if (menuItemCache == null) {
                        menuItemCache = new HashMap<String,JMenuItem>();
                    }
                    menuItemCache.put(value, menuItem);
                    
                    if (value.startsWith("view")) {
                        if (viewMenuItems == null) {
                            viewMenuItems = new HashMap<String,String>();
                        }
                        String propertyKey = attrs.getValue(VIEW_MENU_PROPERTY_KEY);
                        if (!MiscUtils.isNull(propertyKey)) {
                            viewMenuItems.put(value, propertyKey);
                        }
                    }
                    
                }

                menuItem.setIcon(null);
                getMenu(menuCount).add(menuItem);
                
            }
            
        }
        
        public void endElement(String nameSpaceURI, String localName,
                               String qName) {
            
            if (localName.equals(MENU)) {

                if (menuCount > 0) {
                    getMenu(menuCount-1).add(getMenu(menuCount));
                } else {
                    MainMenu.this.add(getMenu(menuCount));
                }
                menus.remove(menuCount);
                menuCount--;
            }
            else if (localName.equals(MENUS)) {
                menus.clear();
                menus = null;
                listeners.clear();
                listeners = null;
                buttonGroups.clear();
                buttonGroups = null;
            }
            
        }
        
        private JMenu getMenu(int index) {
            if (index >= 0) {
                return menus.get(index);
            } else {
                return null;
            }
        }
        
        public void characters(char[] data, int start, int length) {
            contents.write(data, start, length);
        }
        
        public void ignorableWhitespace(char[] data, int start, int length) {
            characters(data, start, length);
        }

        public void error(SAXParseException spe) throws SAXException {
            throw new SAXException(spe.getMessage());
        }
    }
}



