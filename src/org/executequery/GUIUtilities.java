/*
 * GUIUtilities.java
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


package org.executequery;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.executequery.components.StatusBarPanel;
import org.executequery.gui.MainMenu;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.gui.text.TextEditorContainer;
import org.underworldlabs.swing.toolbar.ToolBarProperties;
import org.executequery.toolbars.ToolBarManager;
import org.underworldlabs.swing.actions.BaseActionCommand;
import org.executequery.actions.editcommands.*;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.executequery.base.DesktopMediator;
import org.executequery.base.DockedTabListener;
import org.executequery.base.DockedTabView;
import org.executequery.base.TabComponent;
import org.underworldlabs.swing.ExceptionErrorDialog;
import org.underworldlabs.swing.util.IconUtilities;
import org.executequery.util.SystemResources;
import org.executequery.gui.SaveFunction;
import org.executequery.print.PrintFunction;
import org.executequery.gui.text.TextEditor;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.MultiplePanelInstance;
import org.executequery.gui.OpenComponentRegister;
import org.executequery.gui.PanelCacheObject;
import org.executequery.gui.SystemOutputPanel;
import org.executequery.gui.SystemPropertiesDockedTab;
import org.executequery.gui.UndoableComponent;
import org.underworldlabs.jdbc.DataSourceException;
import org.executequery.gui.browser.ConnectionsTreePanel;
import org.executequery.gui.drivers.DriversTreePanel;
import org.executequery.gui.editor.QueryEditorSettings;
import org.executequery.gui.keywords.KeywordsDockedPanel;
import org.executequery.gui.sqlstates.SQLStateCodesDockedPanel;
import org.executequery.util.Log;
import org.executequery.util.SystemErrLogger;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.util.SystemProperties;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * <p>The GUIUtilities is the primary 'controller' class for all
 * Execute Query GUI components. It provides access to resources
 * in addition to many utility helper methods such as displaying
 * simple dialogs and updating menus.
 *
 * <p>This class will hold a reference to all primary components
 * for access by other classes. This includes those currently in-focus
 * components such as the Query Editor or other text components.
 *
 * <p>All internal frames are added (and closed via relevant 'Close'
 * buttons as may apply) from here.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.14 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class GUIUtilities {
    
    /** The tool bar manager instance */
    private static ToolBarManager toolBar;
    
    /** The window status bar */
    private static StatusBarPanel statusBar;
    
    /** The window menu bar */
    private static MainMenu menuBar;
    
    /** The currently installed look and feel */
    private static int lookAndFeel;
    
    /** The maximum allowable open query editors */
    private static int maxOpenEditors;
    
    /** The open dialog in focus */
    private static JDialog focusedDialog;
    
    /** register for all open components - dialogs, tabs etc. */
    private static OpenComponentRegister register;
    
    /** the application frame */
    private static JFrame frame;
    
    /** panel and desktop mediator object */
    private static DesktopMediator desktopMediator;
    
    /** default icon for open tabs - when none is provided */
    private static Icon defaultIcon;
    
    /** the layout properties controller */
    private static UserLayoutProperties layoutProperties;
    
    /** docked panel cache of non-central pane tabs */
    private static Map<String,JPanel> dockedTabComponents;

    /** the resource path to the image directory */
    public static final String IMAGE_PATH = "/org/executequery/images/";

    /** the resource path to the icon directory */
    public static final String ICON_PATH = "/org/executequery/icons/";

    /** private constructor */
    private GUIUtilities() {}

    public static void initDesktop(JFrame aFrame) {
        frame = aFrame;
        // create the mediator object
        desktopMediator = new DesktopMediator(frame);
        
        // initialise and add the status bar
        statusBar = new StatusBarPanel(" Not Connected", Constants.EMPTY);
        statusBar.setFourthLabelText(
                "JDK" + System.getProperty("java.version").substring(0,5),
                SwingConstants.CENTER);

        frame.add(statusBar, BorderLayout.SOUTH);
        
        // init the layout properties
        layoutProperties = new UserLayoutProperties();
    }

    public static void initPanels() {
        // init the open component register and set as a listener
        register = new OpenComponentRegister();
        desktopMediator.addDockedTabListener(register);

        // setup the default docked tabs and their positions        
        setDockedTabViews(false);
        
        // add a query editor
        addCentralPane(QueryEditor.TITLE,
                       QueryEditor.FRAME_ICON, 
                       new QueryEditor(),
                       null,
                       false);

        // divider locations
        setDividerLocations();

        // add the split pane divider listener
        desktopMediator.addPropertyChangeListener(layoutProperties);
        desktopMediator.addDockedTabDragListener(layoutProperties);
        desktopMediator.addDockedTabListener(layoutProperties);
        
        // select the first main panel
        desktopMediator.setSelectedPane(SwingConstants.CENTER, 0);
        
        //desktopMediator.resetPaneToPreferredSizes(SwingConstants.WEST, false);
    }

    /**
     * Sets the divider locations to previously saved (or default) values.
     */
    protected static void setDividerLocations() {
        String[] keys = DesktopMediator.DIVIDER_LOCATION_KEYS;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            int location = SystemProperties.getIntProperty("user", key);
            //Log.debug("key: "+key+" loc: "+location);
            if (location > 0) {
                desktopMediator.setSplitPaneDividerLocation(key, location);
            }
        }
    }
    
    /**
     * Removes the specified docked tab listener.
     *
     * @param the listener
     */
    public void removeDockedTabListener(DockedTabListener listener) {
        desktopMediator.removeDockedTabListener(listener);
    }    

    /**
     * Adds the specified docked tab listener.
     *
     * @param the listener
     */
    public void addDockedTabListener(DockedTabListener listener) {
        desktopMediator.addDockedTabListener(listener);
    }

    /**
     * Sets the default icon to that specified.
     *
     * @param the icon to be set as the default
     */
    public static void setDefaultIcon(Icon icon) {
        defaultIcon = icon;
    }
    
    /**
     * Adds the specified component as a docked tab component
     * in the specified position.
     *
     * @param the tab title
     * @param the tab icon
     * @param the component
     * @param the tab's tool tip
     */
    public static void addCentralPane(String title, 
                                      Icon icon, 
                                      Component component, 
                                      String tip,
                                      boolean selected) {
        addDockedTab(title, icon, component, tip, SwingConstants.CENTER, selected);
    }

    /**
     * Adds the specified component as a docked tab component
     * in the specified position.
     *
     * @param the tab title
     * @param the tab icon
     * @param the component
     * @param the tab's tool tip
     */
    public static void addCentralPane(String title, 
                                      String icon, 
                                      Component component, 
                                      String tip,
                                      boolean selected) {
        addDockedTab(title, 
                     loadIcon(icon, true),
                     component, 
                     tip, 
                     SwingConstants.CENTER,
                     selected);
    }

    /**
     * Adds the specified component as a docked tab component
     * in the specified position.
     *
     * @param the tab title
     * @param the component
     * @param the position
     */
    public static void addDockedTab(String title, 
                                    Component component, 
                                    int position,
                                    boolean selected) {
        addDockedTab(title, null, component, null, position, selected);
    }


    /**
     * Adds the specified component as a docked tab component
     * in the specified position.
     *
     * @param the tab title
     * @param the tab icon
     * @param the component
     * @param the position
     */
    public static void addDockedTab(String title, 
                                    Icon icon, 
                                    Component component, 
                                    int position,
                                    boolean selected) {
        addDockedTab(title, icon, component, null, position, selected);
    }

    /**
     * Adds the specified component as a docked tab component
     * in the specified position.
     *
     * @param the tab title
     * @param the tab icon
     * @param the component
     * @param the tab's tool tip
     * @param the position
     */
    public static void addDockedTab(String title, 
                                    Icon icon, 
                                    Component component, 
                                    String tip, 
                                    int position,
                                    boolean selected) {
        
        // change the title if a save function
        if (component instanceof MultiplePanelInstance) {
            MultiplePanelInstance mpi = (MultiplePanelInstance)component;
            String _title = mpi.getDisplayName();
            if (_title.length() > 0) {
                title = _title;
                tip = _title;
            }
        }

        // if this is a main window component, add to cache
        if (position == SwingConstants.CENTER) {
            register.addOpenPanel(title, component);
        }

        desktopMediator.addDockedTab(title, icon, component, tip, position, selected);
        GUIUtils.scheduleGC();
    }
    
    /**
     * Closed the specfied docked component with name at the specified position.
     * 
     * @param the name of the tab component
     * @param the position
     */
    public static void closeDockedComponent(String name, int position) {
        desktopMediator.closeTabComponent(name, position);
    }
    
    
    // -------------------------------------------------------
    
    
    /** <p>Retrieves the parent frame of the application.
     *
     *  @return the parent frame
     */
    public static Frame getParentFrame() {
        return frame;
    }
    
    /**
     * Selects the next tab from the current selection.
     */
    public static void selectNextTab() {
        desktopMediator.selectNextTab();
    }

    /**
     * Selects the previous tab from the current selection.
     */
    public static void selectPreviousTab() {
        desktopMediator.selectPreviousTab();
    }

    public static void setMaximumOpenEditors(int _maxOpenEditors) {
        maxOpenEditors = _maxOpenEditors;
    }
    
    /** <p>Builds and sets the main tool bar. */
    public static void createToolBar() {
        toolBar = new ToolBarManager();
        frame.add(toolBar.getToolBarBasePanel(), BorderLayout.NORTH);
    }
    
    /**
     * <p>Determines whether upon selection of the print
     *  action, the currently open and in focus frame does
     *  have a printable area - is an instance of a <code>
     *  BrowserPanel</code> or <code>TextEditor</code>.
     * 
     * @return whether printing may be performed from the
     *          open frame
     */
    public static boolean canPrint() {

        // check the dialog in focus first
        if (focusedDialog != null) {
            
            if (focusedDialog instanceof PrintFunction) {
                return ((PrintFunction)focusedDialog).canPrint();
            }
            
        } 
        
        Object object = getSelectedCentralPane();
        if (!(object instanceof PrintFunction)) {
            return false;
        }
        
        PrintFunction printFunction = (PrintFunction)object;
        return printFunction.canPrint();
        
    }
    
    /** <p>Returns the <code>PrintFunction</code> object
     *  from the currently in-focus frame. If the in-focus
     *  frame is not an instance of <code>PrintFunction</code>,
     *  <code>null</code> is returned.
     *
     *  @return the in-focus <code>PrintFunction</code> object
     */
    public static PrintFunction getPrintableInFocus() {
        // check the open dialogs first
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                if (!dialog.isModal() || dialog.isFocused()) {
                    if (dialog instanceof BaseDialog) {
                        // check the content panel
                        JPanel panel = ((BaseDialog)dialog).getConentPanel();
                        if (panel instanceof PrintFunction) {
                            return (PrintFunction)panel;
                        }
                    }
                    else if (dialog instanceof PrintFunction) {
                        return (PrintFunction)dialog;
                    }
                }
            }
        }

        // check the open panels register
        if (register.getOpenPanelCount() > 0) {
            Component component = register.getSelectedComponent();
            //Log.debug("test print component: "+ component.getClass().getName());
            if (component instanceof PrintFunction) {
                return (PrintFunction)component;
            }
        }

        return null;
/*
        
        // check the dialog in focus first
        if (focusedDialog != null) {
            
            if (focusedDialog instanceof PrintFunction) {
                return (PrintFunction)focusedDialog;
            }
            
        } 
        
        Object object = getSelectedCentralPane();
        if (object instanceof PrintFunction) {
            return (PrintFunction)object;
        }
        else {
            return null;
        }
        */
    }
    
    /**
     * Sets the selected tab in the central pane as the tab
     * component with the specified name.
     *
     * @param the name of the tab to be selected in the central pane
     */
    public static void setSelectedCentralPane(String name) {
        desktopMediator.setSelectedPane(SwingConstants.CENTER, name);
    }
    
    public static JPanel getCentralPane(String name) {
        return (JPanel)register.getOpenPanel(name);
        /*
        TabComponent tabComponent = 
                desktopMediator.getTabComponent(SwingConstants.CENTER, name);
        if (tabComponent != null) {
            return ((JPanel)tabComponent.getComponent());
        }
         */
    }

    /**
     * Returns the tab component with the specified name at
     * the specified position within the tab structure.
     *
     * @param the position (SwingContants)
     * @name the panel name/title
     */
    public static TabComponent getTabComponent(int position, String name) {
        return desktopMediator.getTabComponent(position, name);
    }
    
    public static JPanel getSelectedCentralPane(String name) {
        return (JPanel)register.getSelectedComponent();
        /*
        TabComponent tabComponent = 
                desktopMediator.getSelectedComponent(SwingConstants.CENTER);
        //JInternalFrame frame = desktop.getSelectedFrame();
        if (tabComponent != null) {
            // if its a name check
            if (name != null) {
                if (tabComponent.getTitle().startsWith(name)) {
                    return (JPanel)tabComponent.getComponent();
                    //return ((BaseInternalFrame)frame).getFrameContents();
                }
            }
            return (JPanel)tabComponent.getComponent();            
        }
        return null;
         */
    }
    
    /**
     * Registers the specified dialog with the cache.
     *
     * @param the dialog to be registered
     */
    public static void registerDialog(JDialog dialog) {
        register.addDialog(dialog);
    }

    /**
     * Registers the specified dialog with the cache.
     *
     * @param the dialog to be registered
     */
    public static void deregisterDialog(JDialog dialog) {
        register.removeDialog(dialog);
    }

    public static void setFocusedDialog(JDialog _focusedDialog) {
        focusedDialog = _focusedDialog;
    }
    
    public static void removeFocusedDialog(JDialog _focusedDialog) {
        if (focusedDialog == _focusedDialog) {
            focusedDialog = null;
        }        
    }
    
    /**
     * Retrieves the <code>TextEditor</code> instance
     * that currently has focus or NULL if none exists.
     * 
     * @return that instance of <code>TeTextEditorcode>
     */
    public static TextEditor getTextEditorInFocus() {
        // check the open dialogs first
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                // check if its focused or not modal
                if (!dialog.isModal() || dialog.isFocused()) {
                    // check if its a base dialog
                    if (dialog instanceof BaseDialog) {
                        // check the content panel
                        JPanel panel = ((BaseDialog)dialog).getConentPanel();
                        if (panel instanceof TextEditor) {
                            return (TextEditor)panel;
                        }
                        else if (panel instanceof TextEditorContainer) {
                            return ((TextEditorContainer)panel).getTextEditor();
                        }
                    }
                    else if (dialog instanceof TextEditor) {
                        return (TextEditor)dialog;
                    }
                    else if (dialog instanceof TextEditorContainer) {
                        return ((TextEditorContainer)dialog).getTextEditor();
                    }
                }
            }
        }

        // check the open panels register
        if (register.getOpenPanelCount() > 0) {
            Component component = register.getSelectedComponent();
            if (component instanceof TextEditor) {
                return (TextEditor)component;
            }
            else if (component instanceof TextEditorContainer) {
                return ((TextEditorContainer)component).getTextEditor();
            }

        }
        return null;
    }
    
    /** <p>Retrieves the contents of the in-focus
     *  internal frame as a <code>JPanel</code>.
     *
     *  @return the panel in focus
     */
    public static JPanel getSelectedCentralPane() {
        return getSelectedCentralPane(null);
    }
    
    /** <p>Retrieves the <code>SaveFunction</code> in focus.
     *
     *  @return the <code>SaveFunction</code> in focus
     */
    public static SaveFunction getSaveFunctionInFocus() {
        // check the open dialogs first
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                // check if its focused
                // TODO: try a focus lost on the dialog ?????????????????
                if (!dialog.isModal() || dialog.isFocused()) {
                    // check if its a base dialog
                    if (dialog instanceof BaseDialog) {
                        // check the content panel
                        JPanel panel = ((BaseDialog)dialog).getConentPanel();
                        if (panel instanceof SaveFunction) {
                            return (SaveFunction)panel;
                        }
                    }
                    else if (dialog instanceof SaveFunction) {
                        return (SaveFunction)dialog;
                    }
                }
            }
        }

        // check the open panels register
        if (register.getOpenPanelCount() > 0) {
            Component component = register.getSelectedComponent();
            if (component instanceof SaveFunction) {
                return (SaveFunction)component;
            }
        }
        return null;
    }
    
    public static UndoableComponent getUndoableInFocus() {
        // check the open dialogs first
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                // check if its focused
                if (dialog.isFocused()) {
                    if (dialog instanceof UndoableComponent) {
                        return (UndoableComponent)dialog;
                    }
                }
            }
        }

        // check the open panels register
        if (register.getOpenPanelCount() > 0) {
            Component component = register.getSelectedComponent();
            if (component instanceof UndoableComponent) {
                return (UndoableComponent)component;
            }
        }
        return null;
        /*
        // check the dialog in focus first
        if (focusedDialog != null) {

            if (focusedDialog instanceof UndoableComponent) {
                return (UndoableComponent)focusedDialog;
            }

        } 
        else {
            JPanel panel = getSelectedCentralPane();

            if (panel instanceof UndoableComponent) {
                return (UndoableComponent)panel;
            } else {
                return null;
            }

        } 
        return null;
         */
    }

    public static void registerUndoRedoComponent(UndoableComponent undoable) {
        BaseActionCommand undo = (BaseActionCommand)ActionBuilder.get("undo-command");
        BaseActionCommand redo = (BaseActionCommand)ActionBuilder.get("redo-command");

        if (undoable == null) {
            undo.setEnabled(false);
            redo.setEnabled(false);
        }

        UndoCommand _undo = (UndoCommand)undo.getCommand();
        RedoCommand _redo = (RedoCommand)redo.getCommand();

        _undo.setUndoableComponent(undoable);
        _redo.setUndoableComponent(undoable);
    }
     
    /** <p>Retrieves the applications <code>InputMap</code>.
     *
     *  @return the <code>InputMap</code>
     */
    public static InputMap getInputMap(int condition) {
//        return desktop.getDesktopInputMap(condition);
        
        // TODO: INPUT MAP
        return desktopMediator.getInputMap(condition);
        //return desktop.getInputMap(condition);
    }
    
    /** <p>Retrieves the applications <code>ActionMap</code>.
     *
     *  @return the <code>ActionMap</code>
     */
    public static ActionMap getActionMap() {
        // TODO: ACTION MAP
        return  desktopMediator.getActionMap();
    }
    
    /** <p>Registers the main menu bar with this class.
     *
     *  @param the menu bar
     */
    public static void registerMenuBar(JMenuBar menu) {
        menuBar = (MainMenu)menu;
    }
    
    /** 
     * Initialises and starts the system logger.
     * The logger's stream is also registered for
     * <code>System.err</code> and <code>System.out</code>.
     */
    public static void startLogger() {
        // init the cache
        if (dockedTabComponents == null) {
            dockedTabComponents = new HashMap<String, JPanel>();
        }
        // add the output panel to the cache
        SystemOutputPanel panel = new SystemOutputPanel();
        dockedTabComponents.put(SystemOutputPanel.PROPERTY_KEY, panel);

        // set system error stream to the output panel
        PrintStream stream = new PrintStream(new SystemErrLogger(), true);
        System.setErr(stream);
        //System.setOut(stream);
    }
    
    /** <p>Calculates and returns the centered position
     *  of a dialog with the specified size to be added
     *  to the desktop area - ie. taking into account the
     *  size and location of all docked panels.
     *
     *  @param the size of the dialog to be added as a
     *         <code>Dimension</code> object
     *  @return the <code>Point</code> at which to add the dialog
     */
    public static Point getLocationForDialog(Dimension dialogDim) {        
        return GUIUtils.getLocationForDialog(frame, dialogDim);       
    }
    
    /** 
     * Propagates the call to GUIUtils and schedules 
     * the garbage collector to run.
     */
    public static void scheduleGC() {
        GUIUtils.scheduleGC();
    }
    
    /**
     * Returns whether the frame's glass pane is visible or not.
     *
     * @return true | false
     */
    public static boolean isGlassPaneVisible() {
        return frame.getRootPane().getGlassPane().isVisible();
    }
    
    /**
     * Shows/hides the frame's glass pane as specified.
     *
     * @param visible - true | false
     */
    public static void setGlassPaneVisible(final boolean visible) {
        GUIUtils.invokeLater(new Runnable() {
            public void run() {
                if (isGlassPaneVisible() == visible) {
                    return;
                }
                frame.getRootPane().getGlassPane().setVisible(visible);
            }
        });
    }
    
    /** 
     * Sets the application cursor to the system normal cursor 
     */
    public static void showNormalCursor() {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                GUIUtils.showNormalCursor(frame);
            }
        });
        //GUIUtils.showNormalCursor(frame);
    }
    
    /** 
     * Sets the application cursor to the system wait cursor 
     */
    public static void showWaitCursor() {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                GUIUtils.showWaitCursor(frame);
            }
        });
        //GUIUtils.showWaitCursor(frame);
    }

    /** 
     * Sets the application cursor to the system wait cursor 
     * on the specified component.
     */
    public static void showWaitCursor(final Component component) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                GUIUtils.showWaitCursor(component);
            }
        });
    }

    /** 
     * Sets the application cursor to the system normal cursor 
     * on the specified component.
     */
    public static void showNormalCursor(final Component component) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                GUIUtils.showNormalCursor(component);
            }
        });
    }

    /** <p>Resets the tool bars. */
    public static void resetToolBar(final boolean resetMenu) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toolBar.buildToolbars(true);
                ToolBarProperties.saveTools();
                if (resetMenu) {
                    initialiseViewToolsMenuItems();
                }
            }
        });
    }
    
    /**
     * Loads and returns the specified image with the specified name.
     * The default path to the image dir appended to the start of
     * the name is /org/executequery/images.
     *
     * @param name - the image file name to load
     * @return the loaded image
     */
    public static ImageIcon loadImage(String name) {
        return IconUtilities.loadImage(IMAGE_PATH + name);
    }
    
    /**
     * Loads and returns the specified icon with the specified name.
     * The default path to the icon dir appended to the start of
     * the name is /org/executequery/icons.
     *
     * @param name - the icon file name to load
     * @return the loaded icon image
     */    
    public static ImageIcon loadIcon(String name) {
        return loadIcon(name, false);
    }

    /**
     * Loads and returns the specified icon with the specified name.
     * The default path to the icon dir appended to the start of
     * the name is /org/executequery/icons.
     *
     * @param name - the icon file name to load
     * @param store - whether to store the icon in the icon cache 
     *                for future use after loading
     * @return the loaded icon image
     */ 
    public static ImageIcon loadIcon(String name, boolean store) {
        return IconUtilities.loadIcon(ICON_PATH + name, store);
    }

    /**
     * Returns the absolute icon resource path by appending
     * the package icon path to the specified icon file name.
     *
     * @param name - the icon file name
     * @return the absolute package path of the icon
     */
    public static String getAbsoluteIconPath(String name) {
        return ICON_PATH + name;
    }
    
    /** 
     * Sets the system display options and writes the
     * preferences to file if specified.
     *
     * @param whether to save the preferences to file
     */
    public static void setDisplayOptions(boolean writeFile) {
        // apply these properties to relevant objects
        displayStatusBar(SystemProperties.getBooleanProperty(
                                "user", "system.display.statusbar"));

        // set the menu's display options as retrieved above
        menuBar.setViewOptions();

        // update the action shortcuts
        ActionBuilder.updateUserDefinedShortcuts(
                getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
                SystemResources.getUserActionShortcuts());
    }
    
    /**
     * Convenience method for consistent border colour.
     *
     * @return the system default border colour
     */
    public static Color getDefaultBorderColour() {
        return UIManager.getColor("controlShadow");
    }
    
    /**
     * Returns the docked component (non-central pane) with
     * the specified name.
     *
     * @param the name of the component
     * @return the panel component
     */
    public static JPanel getDockedTabComponent(String key) {
        if (dockedTabComponents == null || 
                dockedTabComponents.isEmpty() || 
                !dockedTabComponents.containsKey(key)) {
            return null;
        }
        return dockedTabComponents.get(key);
    }

    /**
     * Initialises the docked tab view with the specified
     * property key.
     *
     * @param the property key of the panel to be initialised
     */
    private static void initDockedTabView(String key) {
        if (dockedTabComponents.containsKey(key)) {
            return;
        }

        JPanel panel = null;
        // determine which panel to initialise
        if (key.equals(ConnectionsTreePanel.PROPERTY_KEY)) {
            panel = new ConnectionsTreePanel();
        }
        else if (key.equals(DriversTreePanel.PROPERTY_KEY)) {
            panel = new DriversTreePanel();
        }
        else if (key.equals(SystemPropertiesDockedTab.PROPERTY_KEY)) {
            panel = new SystemPropertiesDockedTab();
        }
        else if (key.equals(SystemOutputPanel.PROPERTY_KEY)) {
            // init the logger 
            // this method will add the output panel
            startLogger();
        }
        else if (key.equals(KeywordsDockedPanel.PROPERTY_KEY)) {
            panel = new KeywordsDockedPanel();
        }
        else if (key.equals(SQLStateCodesDockedPanel.PROPERTY_KEY)) {
            panel = new SQLStateCodesDockedPanel();
        }
        
        if (panel != null) {
            dockedTabComponents.put(key, panel);
        }
        
    }
    
    /**
     * Returns the docked component (non-central pane) with
     * the specified name. If the option to display is set to
     * true, the component will be displayed in the default or
     * user preferred position.
     *
     * @param key - the property key of the component
     */
    public static void ensureDockedTabVisible(String key) {
        
        JPanel panel = getDockedTabComponent(key);
        if (panel != null && panel instanceof DockedTabView) {
            DockedTabView _panel = (DockedTabView)panel;
            String title = _panel.getTitle();
            
            // check if its visible already
            int position = getDockedComponentPosition(key);
            TabComponent tab = getTabComponent(position, title);
            if (tab == null) {

                // check if its minimised
                if (desktopMediator.isMinimised(position, title)) {
                    desktopMediator.restore(position, title);
                }
                // otherwise add the component to the view
                else {
                    addDockedTab(title, panel, position, true);
                    layoutProperties.setDockedPaneVisible(key, true);
                    SystemProperties.setBooleanProperty("user", key, true);
                }

            } 
            else { // otherwise make sure its selected
                desktopMediator.setSelectedPane(position, title);
            }

        }
        else { // otherwise, initialise the tab
            initDockedTabView(key);
            ensureDockedTabVisible(key); // replay
        }

    }

    /**
     * Returns the user specified (or default) position for the
     * non-central pane docked component with the specified name.
     *
     * @param the key
     * @return the position (SwingConstants)
     */
    public static int getDockedComponentPosition(String key) {
        int position = layoutProperties.getPosition(key);
        if (position == -1) {
            // default NORTH_WEST position
            position = SwingConstants.NORTH_WEST;
        }
        return position;
    }

    /**
     * Called to indicate that a docked tab view was selected.
     *
     * @param the property key
     */
    public static void dockedTabComponentSelected(String key) {
        // update the system properties visible key
        SystemProperties.setBooleanProperty("user", key, true);
        
        // update the view menu item
        JPanel panel = getDockedTabComponent(key);
        if (panel != null && panel instanceof DockedTabView) {
            DockedTabView tab = (DockedTabView)panel;
            menuBar.setViewOption(tab.getMenuItemKey(), true);
        }
        
        // update properties to file
        updatePreferencesToFile();        
    }

    /**
     * Called to indicate that a docked tab view has closed.
     *
     * @param the property key
     */
    public static void dockedTabComponentClosed(String key) {
        // save the change
        //layoutProperties.setDockedPaneVisible(key, false);
        
        // update the system properties visible key
        SystemProperties.setBooleanProperty("user", key, false);
        
        // update the view menu item
        JPanel panel = getDockedTabComponent(key);
        if (panel != null && panel instanceof DockedTabView) {
            DockedTabView tab = (DockedTabView)panel;
            menuBar.setViewOption(tab.getMenuItemKey(), false);
        }
        
        // update properties to file
        updatePreferencesToFile();        
    }
    
    /**
     * Displays or hides the docked tab component of the specified type.
     *
     * @param the property key of the component
     * @param show/hide the view
     */
    public static void displayDockedComponent(String key, boolean display) {
        
        if (display) {
            ensureDockedTabVisible(key);
        }
        else {
            // retrieve the panel from the cache
            JPanel panel = getDockedTabComponent(key);            
            if (panel != null && panel instanceof DockedTabView) {
                DockedTabView _panel = (DockedTabView)panel;
                String title = _panel.getTitle();
                int position = getDockedComponentPosition(key);

                // remove it from the pane
                closeDockedComponent(title, position);

                // save the change
                layoutProperties.setDockedPaneVisible(key, false);
            }
        }
        
    }
    
    /** 
     * Closes the dialog with the specified title.
     */
    public static void closeDialog(String title) {
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                if (dialog.getTitle().startsWith(title)) {
                    dialog.dispose();
                    return;
                }
            }
        }
    }

    /** 
     * Closes the currently in-focus dialog. 
     */
    public static void closeSelectedDialog() {
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                // check if this is focused
                if (dialog.isFocused()) {
                    // dialog dispose
                    dialog.dispose();
                    return;
                }
            }
        }
    }
    
    /** <p>Displays or hides the specified tool bar.
     *
     *  @param the tool bar
     *  @param <code>true</code> to display | <code>false</code> to hide
     */
    public static void displayToolBar(String name, boolean display) {
        ToolBarProperties.setToolBarVisible(name, display);
        resetToolBar(false);
    }
    
    public static void initialiseViewToolsMenuItems() {
        menuBar.initialiseViewToolsMenuItems(
                ToolBarProperties.isToolBarVisible(ToolBarManager.FILE_TOOLS),
                ToolBarProperties.isToolBarVisible(ToolBarManager.EDIT_TOOLS),
                ToolBarProperties.isToolBarVisible(ToolBarManager.SEARCH_TOOLS),
                ToolBarProperties.isToolBarVisible(ToolBarManager.DATABASE_TOOLS),
                ToolBarProperties.isToolBarVisible(ToolBarManager.BROWSER_TOOLS),
                ToolBarProperties.isToolBarVisible(ToolBarManager.IMPORT_EXPORT_TOOLS),
                ToolBarProperties.isToolBarVisible(ToolBarManager.SYSTEM_TOOLS)
        );
    }
    
    /** 
     * Displays or hides the main application status bar.
     *
     * @param <code>true</code> to display | <code>false</code> to hide
     */
    public static void displayStatusBar(boolean display) {
        statusBar.setVisible(display);
        SystemProperties.setBooleanProperty("user", 
                            "system.display.statusbar", display);
    }
    
    /** <p>Registers the application status bar with this class.
     *
     *  @param the status bar
     */
    public static void registerStatusBar(StatusBarPanel status) {
        statusBar = status;
    }
    
    /** <p>Retrieves the main frame's layered pane object.
     *
     *  @return the frame's <code>JLayeredPane</code>
     */
    public static JLayeredPane getFrameLayeredPane() {
        return ((JFrame)getParentFrame()).getLayeredPane();
    }
    
    /** <p>Retrieves the application's status bar as
     *  registered with this class.
     *
     *  @return the application status bar
     */
    public static final StatusBarPanel getStatusBar() {
        return statusBar;
    }
    
    public static void refreshConnectionMenu(DatabaseConnection[] savedConns) {        
        if (menuBar != null) {
            menuBar.setConnectionsMenu(savedConns);
        }
    }
    
    public static void addNewConnToMenu(DatabaseConnection c) {
        menuBar.addNewConnectionMenu(c);
    }

    public static void addNewRecentFileToMenu(File file) {
        String path = file.getAbsolutePath();
        SystemUtilities.addRecentOpenFile(path);
        menuBar.reloadRecentFileMenu();
        /*
        if (SystemUtilities.addRecentOpenFile(path)) {
            menuBar.addNewRecentFileMenu(file.getName(), path);
        }*/
    }
    
    public static void setLookAndFeel(int _lookAndFeel) {
        lookAndFeel = _lookAndFeel;
    }

    /**
     * Returns the current look and feel value.
     */
    public static final int getLookAndFeel() {
        return lookAndFeel;
    }
    
    /**
     * Saves the user preferences to file.
     */
    protected static void updatePreferencesToFile() {
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                SystemResources.setUserPreferences(
                                    SystemProperties.getProperties("user"));
            }
        });
    }

    
    /**
     * Sets the docked tab views according to user preference.
     */
    private static void setDockedTabViews(boolean reload) {
        List<UserLayoutObject> list = layoutProperties.getLayoutObjectsSorted();
        for (int i = 0, n = list.size(); i < n; i++) {
            UserLayoutObject object = list.get(i);
            String key = object.getKey();
            if (object.isVisible()) {
                initDockedTabView(key);
                JPanel panel = getDockedTabComponent(key);
                DockedTabView tab = (DockedTabView)panel;

                String title = tab.getTitle();
                int position = object.getPosition();
                // first check if its already displayed
                if (desktopMediator.getTabComponent(position, title) == null) {
                    // add the component to the view
                    addDockedTab(title, panel, position, false);

                    //Log.debug("Adding docked tab: " + title);
                    
                    // check if its minimised
                    if (object.isMinimised()) {
                        desktopMediator.minimiseDockedTab(position, title);
                    }
                    
                }

            }
            else {
                if (reload) {
                    displayDockedComponent(key, false);
                }
            }
        }
    }

    /**
     * Notifies that user preferences have changed.<br>
     * This will update relevant components and settings
     * and save the new preferences to file.
     */
    public static void preferencesChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // save to file
                    updatePreferencesToFile();

                    // update the logger level
                    Log.setLevel(SystemProperties.getProperty(
                                            "user", "system.log.level"));

                    // set misc display options
                    setDisplayOptions(false);

                    // update any open editors
                    updateOpenEditors(false);

                    // update the docked tab view options
                    String[] keys = {ConnectionsTreePanel.PROPERTY_KEY,
                                     DriversTreePanel.PROPERTY_KEY,
                                     KeywordsDockedPanel.PROPERTY_KEY,
                                     SQLStateCodesDockedPanel.PROPERTY_KEY,
                                     SystemPropertiesDockedTab.PROPERTY_KEY,
                                     SystemOutputPanel.PROPERTY_KEY};

                    for (int i = 0; i < keys.length; i++) {
                        layoutProperties.setDockedPaneVisible(
                                keys[i], 
                                SystemProperties.getBooleanProperty("user", keys[i]), 
                                false);
                    }
                    layoutProperties.persist(); // save after looping all

                    // update the docked tab view display
                    setDockedTabViews(true);

                    //setBrowserDisplayOptions(false);

                    JDialog.setDefaultLookAndFeelDecorated(
                        SystemProperties.getBooleanProperty("user", "decorate.dialog.look"));

                    JFrame.setDefaultLookAndFeelDecorated(
                        SystemProperties.getBooleanProperty("user", "decorate.frame.look"));

                    // todo: conn options

                    /*
                    if (SystemUtilities.isConnected()) {
                        DBConnection dbConn = new DBConnection();
                        dbConn.updateConnectionProperties();
                    }                
                    */

                    Log.info("System properties modified.");
                }
                finally {
                    showNormalCursor();
                }
            }
        });
    }
    
    /**
     * Updates open editors with the user preferences.
     * This will usually only be called following a user 
     * preference change.
     */
    public static void updateOpenEditors(boolean writeFile) {
        QueryEditorSettings.initialise();

        if (register.getOpenPanelCount() > 0) {
            List<PanelCacheObject> list = register.getOpenPanels();
            for (int i = 0, k = list.size(); i < k; i++) {
                Component component = list.get(i).getComponent();
                if (component instanceof QueryEditor) {
                    QueryEditor editor = (QueryEditor)component;
                    editor.setEditorPreferences();
                }
            }
        }
        menuBar.setEditorViewOptions();
        
        if (writeFile) {
            updatePreferencesToFile();
        }
        
    }
    
    /**
     * Retrieves a list of the open central panels that implement
     * SaveFunction.
     *
     * @return the open SaveFunction panels
     */
    public static List<SaveFunction> getOpenSaveFunctionPanels() {
        List<SaveFunction> saveFunctions = new ArrayList<SaveFunction>();
        List<PanelCacheObject> panels = register.getOpenPanels();
        for (int i = 0, k = panels.size(); i < k; i++) {
            Component c = panels.get(i).getComponent();
            if (c instanceof SaveFunction) {
                SaveFunction saveFunction = (SaveFunction)c;
                if (saveFunction.promptToSave()) {
                    saveFunctions.add(saveFunction);
                }
            }
        }
        return saveFunctions;
    }

    /**
     * Retrieves the number of open central panels that implement
     * SaveFunction.
     *
     * @return the open SaveFunction panels count
     */
    public static int getOpenSaveFunctionCount() {
        int count = 0;
        List<PanelCacheObject> panels = register.getOpenPanels();
        for (int i = 0, k = panels.size(); i < k; i++) {
            if (panels.get(i).getComponent() instanceof SaveFunction) {
                count++;
            }
        }
        return count;
    }

    
    public static void closeConnection() {
        // grab the selected connection from the 
        // connections tree docked panel and close it
        JPanel panel = getDockedTabComponent(ConnectionsTreePanel.PROPERTY_KEY);
        if (panel != null) {
            DatabaseConnection dc = ((ConnectionsTreePanel)panel).
                                        getSelectedDatabaseConnection();
            if (dc != null && dc.isConnected()) {
                try {
                    SystemUtilities.disconnect(dc);
                } catch (DataSourceException e) {
                    displayErrorMessage(
                            "Error disconnecting selected data source:\n"+
                            e.getMessage());
                }
            }
        }
    }

    public static final void shuttingDown() {
        Log.info("System exiting...");
        /*
        JPanel panel = getDockedTabComponent(SystemOutputPanel.PROPERTY_KEY);
        if (panel != null) {
            ((SystemOutputPanel)panel).shuttingDown();
        }*/
    }
    
    
    /**
     * Sets the title for the specified component to the newTitle
     * within central tab pane.
     *
     * @param the component to be renamed
     * @the new title to set
     */
    public static void setTabTitleForComponent(JPanel contents, String newTitle) {
        setTabTitleForComponent(SwingUtilities.CENTER, contents, newTitle);
    }

    /**
     * Sets the tool tip for the specified component to toolTipText.
     *
     * @param the tab pane position
     * @param the component where the tool tip should be set
     * @param the tool tip text to be displayed in the tab
     */
    public static void setToolTipTextForComponent(int position,
                                           Component component, String toolTipText) {
        desktopMediator.setToolTipTextForComponent(position, component, toolTipText);
    }

    /**
     * Sets the title for the specified component to toolTipText.
     *
     * @param the tab pane position
     * @param the component where the tool tip should be set
     * @param the title to be displayed in the tab
     */
    public static void setTabTitleForComponent(int position,
                                        Component component, String title) {
        desktopMediator.setTabTitleForComponent(position, component, title);
    }

    /**
     * Sets the tool tip for the specified component within the 
     * central main pane to title.
     *
     * @param the component where the tool tip should be set
     * @param the tool tip text to be displayed in the tab
     */
    public static void setToolTipTextForComponent(Component component, String toolTipText) {
        setToolTipTextForComponent(SwingConstants.CENTER, component, toolTipText);
    }

    /**
     * Sets the title for the specified component within the 
     * central main pane to toolTipText.
     *
     * @param the component where the tool tip should be set
     * @param the title text to be displayed in the tab
     */
    public static void setTabTitleForComponent(Component component, String title) {
        setTabTitleForComponent(SwingConstants.CENTER, component, title);
    }

    /**
     * Attempts to locate the actionable dialog that is
     * currently open and brings it to the front.
     */
    public static void acionableDialogToFront() {
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);

                // check if its a BaseDialog
                if (dialog instanceof BaseDialog) {
                    // check the content panel
                    JPanel panel = ((BaseDialog)dialog).getConentPanel();
                    if (panel instanceof ActiveComponent) {
                        dialog.toFront();
                    }
                }
                else if (dialog instanceof ActiveComponent) {
                    dialog.toFront();
                }
            }
        }
    }
    
    /**
     * Checks if an actionable dialog is currently open.
     *
     * @return true | false
     */
    public static boolean isActionableDialogOpen() {
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                // check if its a BaseDialog
                if (dialog instanceof BaseDialog) {
                    // check the content panel
                    JPanel panel = ((BaseDialog)dialog).getConentPanel();
                    if (panel instanceof ActiveComponent) {
                        return true;
                    }
                }
                else if (dialog instanceof ActiveComponent) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean hasValidSaveFunction() {
        // check the open panels register first
        if (register.getOpenPanelCount() > 0) {
            List<PanelCacheObject> list = register.getOpenPanels();
            for (int i = 0, k = list.size(); i < k; i++) {
                Component component = list.get(i).getComponent();
                if (component instanceof SaveFunction) {
                    SaveFunction saveFunction = (SaveFunction)component;
                    if (saveFunction.promptToSave()) {
                        return true;
                    } 
                }
            }
        }
        
        // check the open dialogs
        if (register.getOpenDialogCount() > 0) {
            List<JDialog> list = register.getOpenDialogs();
            for (int i = 0, k = list.size(); i < k; i++) {
                JDialog dialog = list.get(i);
                if (dialog instanceof SaveFunction) {
                    SaveFunction saveFunction = (SaveFunction)dialog;
                    if (saveFunction.promptToSave()) {
                        return true;
                    } 
                }
            }
        }
        
        return false;
        
        /*
        
        // if we have none return false
        if (desktopMediator.getTabCount(SwingConstants.CENTER) == 0) {
            return false;
        }        

        List<TabComponent> tabs = desktopMediator.getOpenTabs(SwingConstants.CENTER);
        for (int i = 0, k = tabs.size(); i < k; i++) {
            TabComponent tab = tabs.get(i);
            Component component = tab.getComponent();

            if (component instanceof SaveFunction) {
                SaveFunction saveFunction = (SaveFunction)component;
                if (saveFunction.promptToSave()) {
                    return true;
                } 
            }

        }
        return false;

        /*
        JInternalFrame[] _frames = getAllOpenFrames();
        
        for (int i = 0; i < _frames.length; i++) {
            
            if (_frames[i] instanceof BaseInternalFrame) {
                BaseInternalFrame frame = (BaseInternalFrame)_frames[i];
                JPanel contents = frame.getFrameContents();
                
                if (contents instanceof SaveFunction) {
                    SaveFunction saveFunction = (SaveFunction)contents;
                    
                    if (saveFunction.promptToSave()) {
                        validSaves = true;
                        break;
                    } 
                    
                } 
                
            } 
            
        } 

        return validSaves; */
    }
    
    /**
     * Checks if the panel with the specified title is open.
     * 
     * @return true | false
     */
    public static boolean isPanelOpen(String title) {
        return register.isPanelOpen(title);
        //return getCentralPane(title) != null;
        //return getOpenFrame(title) != null;
    }

    /**
     * Checks if the dialog with the specified title is open.
     * 
     * @return true | false
     */
    public static boolean isDialogOpen(String title) {
        return register.isDialogOpen(title);
    }

    /**
     * Checks if the dialog with the specified title is open.
     * 
     * @return true | false
     */
    public static void setSelectedDialog(String title) {
        JDialog dialog = register.getOpenDialog(title);
        if (dialog != null) {
            dialog.toFront();
        }
    }

    public static JPanel getOpenFrame(String title) {
        return (JPanel)register.getOpenPanel(title);
    }

    /**
     * Resets (clears) the system output log.
     */
    private static void resetSystemOutputLog() {
        JPanel panel = getDockedTabComponent(SystemOutputPanel.PROPERTY_KEY);
        if (panel != null) {
            ((SystemOutputPanel)panel).resetSystemLog();
        }  
    }
    
    public static void resetSystemLog() {
        int yesno = displayConfirmDialog(
            "Are you sure you want to reset the system activity log?");

        if (yesno == JOptionPane.YES_OPTION) {
            resetSystemOutputLog();
        }
    }

    public static void resetImportLog() {
        int yesno = displayConfirmDialog(
                        "Are you sure you want to reset the data import log?");
        
        if (yesno == JOptionPane.YES_OPTION) {
            SystemResources.resetLog("import.log");
        }
    }
    
    public static void resetExportLog() {
        int yesno = displayConfirmDialog(
                        "Are you sure you want to reset the data export log?");
        
        if (yesno == JOptionPane.YES_OPTION) {
            SystemResources.resetLog("export.log");
        }
    }
    
    public static void resetAllLogs() {
        int yesno = displayConfirmDialog(
                        "Are you sure you want to reset ALL system logs?");

        if (yesno == JOptionPane.YES_OPTION) {
            resetSystemOutputLog();
            SystemResources.resetLog("export.log");
            SystemResources.resetLog("import.log");
        } 

    }

    public static boolean saveOpenChanges(SaveFunction saveFunction) {
        int result = displayConfirmCancelDialog(
                        "Do you wish to save changes to " +
                         saveFunction.getDisplayName() + "?");

        if (result == JOptionPane.YES_OPTION) {
            int saved = saveFunction.save(false);
            if (saved != SaveFunction.SAVE_COMPLETE) {
                return false;
            }
        } else if (result == JOptionPane.CANCEL_OPTION) {
            return false;
        }
        return true;
    }
    
    
    /**
     * Displays the error dialog displaying the stack trace from a
     * throws/caught exception.
     *
     * @param message - the error message to display
     * @param e - the throwable
     */
    public static void displayExceptionErrorDialog(
            final String message, final Throwable e) {
        GUIUtils.invokeAndWait(new Runnable() {
            public void run() {
                new ExceptionErrorDialog(frame, message, e);
            }
        });
    }
    
    
    // -------------------------------------------------------
    // ------ Helper methods for various option dialogs ------
    // -------------------------------------------------------
    
    // These have been revised to use JDialog as the wrapper to
    // ensure the dialog is centered within the dektop pane and not
    // within the entire screen as you get with JOptionPane.showXXX()
    
    public static final void displayInformationMessage(String message) {
        GUIUtils.displayInformationMessage(frame, message);
    }
    
    public static final void displayWarningMessage(String message) {
        GUIUtils.displayWarningMessage(frame, message);
    }
    
    public static final void displayErrorMessage(String message) {
        GUIUtils.displayErrorMessage(frame, message);
    }

    public static final String displayInputMessage(String title, String message) {
        return GUIUtils.displayInputMessage(frame, title, message);
    }

    public static final int displayConfirmCancelErrorMessage(String message) {
        return GUIUtils.displayConfirmCancelErrorMessage(frame, message);
    }

    public static final int displayYesNoDialog(String message, String title) {
        return GUIUtils.displayYesNoDialog(frame, message, title);
    }
    
    public static final int displayConfirmCancelDialog(String message) {
        return GUIUtils.displayConfirmCancelDialog(frame, message);
    }
    
    public static final int displayConfirmDialog(String message) {
        return GUIUtils.displayConfirmDialog(frame, message);
    }
    
}


