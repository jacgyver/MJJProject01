/*
 * ExecuteQuery.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;

import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.gui.HelpWindow;
import org.underworldlabs.swing.CustomKeyboardFocusManager;
import org.underworldlabs.swing.SplashPanel;
import org.underworldlabs.swing.plaf.base.CustomTextAreaUI;
import org.underworldlabs.swing.plaf.base.CustomTextPaneUI;
import org.executequery.plaf.ExecuteQueryTheme;
import org.executequery.gui.MainMenu;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.underworldlabs.jdbc.DataSourceException;
import org.executequery.plaf.ExecuteQueryTheme2;
import org.executequery.util.Log;
import org.executequery.util.PluginLookAndFeelManager;
import org.executequery.util.SystemResources;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.PasswordDialog;
import org.underworldlabs.util.MiscUtils;
import org.underworldlabs.util.SystemProperties;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The entry point for Execute Query. Here all system
 * properties and user preferences are loaded including
 * connection and JDBC driver information, display options,
 * colours and look & feel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.17 $
 * @date     $Date: 2006/09/14 07:37:19 $
 */
public class ExecuteQuery {
    
    /** The application frame */
    private static JFrame frame;
    
    /** <p>Creates a new Execute Query instance */
    public ExecuteQuery() {
        
        try {
            
            // load the application system info
            SystemProperties.loadPropertiesResource(
                    "system", "org/executequery/eq.system.properties");
            
            System.setProperty("executequery.user.home.dir",
                    SystemProperties.getProperty("system", "eq.user.home.dir"));

            System.setProperty("executequery.build",
                    SystemProperties.getProperty("system", "eq.build"));

            // run the startup check routine
            SystemUtilities.startup();

            // initialise the logger
            Log.init();

            // load the default properties
            Log.debug("Loading application default properties.");
            SystemProperties.loadPropertiesResource(
                    "defaults", "org/executequery/eq.default.properties");

            // load the user properties with the defaults set
            Properties defaults = SystemProperties.getProperties("defaults");
            Log.debug("Loading user application properties.");
            SystemProperties.loadProperties("user",
                          SystemUtilities.getUserPropertiesPath() + 
                          "eq.user.properties", defaults);
            
            // reset the log level from the user properties
            Log.init(SystemProperties.getProperty("user", "system.log.level"));
            
            // set the version number to display on the splash panel
            System.setProperty("executequery.major.version",
                    SystemProperties.getProperty("system", "eq.major.version"));

            System.setProperty("executequery.minor.version",
                    SystemProperties.getProperty("system", "eq.minor.version"));

            SplashPanel splash = null;
            if (SystemProperties.getBooleanProperty("user", "startup.display.splash")) {
                splash = new SplashPanel(
                        new Color(55,55,120),//new Color(60,60,120),
                        "/org/executequery/images/SplashImage.png",
                        System.getProperty("executequery.minor.version"), 
                        245, 210);
            }
            advanceSplash(splash);

            try {
                // set the custom keyboard focus manager
                KeyboardFocusManager.
                        setCurrentKeyboardFocusManager(
                        new CustomKeyboardFocusManager());
            } catch (SecurityException e) {}

            
            if (SystemUtilities.hasLocaleSettings()) {
                // set locale and timezone info
                System.setProperty("user.country",
                        SystemProperties.getStringProperty("user", "locale.country"));
                System.setProperty("user.language",
                        SystemProperties.getStringProperty("user", "locale.language"));
                System.setProperty("user.timezone",
                        SystemProperties.getStringProperty("user", "locale.timezone"));
            } else {
                SystemProperties.setProperty("user", "locale.country",
                        System.getProperty("user.country"));
                SystemProperties.setProperty("user", "locale.language",
                        System.getProperty("user.language"));
                SystemProperties.setProperty("user", "locale.timezone",
                        System.getProperty("user.timezone"));
                Log.debug("User locale settings not available - resetting");
                GUIUtilities.updatePreferencesToFile();
            }

            advanceSplash(splash);
            
            // load the JDBC drivers - names and paths only
            JDBCProperties.loadDrivers();

            // load the saved connections
            ConnectionProperties.loadConnections();
            
            advanceSplash(splash);
            
            // set the look and feel
            boolean loadDefaultLook = false;
            int look = SystemProperties.getIntProperty(
                            "user", "startup.display.lookandfeel");

            try {

                switch (look) {
                    case Constants.EQ_DEFAULT_LAF:
                        loadDefaultLookAndFeel();
                        //loadDefaultLookAndFeelTheme2();
                        break;
                    case Constants.SMOOTH_GRADIENT_LAF:
                        loadDefaultLookAndFeel();
                        break;
                    case Constants.BUMPY_GRADIENT_LAF:
                        org.underworldlabs.swing.plaf.bumpygradient.BumpyGradientLookAndFeel laf =
                            new org.underworldlabs.swing.plaf.bumpygradient.BumpyGradientLookAndFeel();
                        laf.setCurrentTheme(new ExecuteQueryTheme());
                        UIManager.setLookAndFeel(laf);
                        break;
                    case Constants.EQ_THM:
                        loadDefaultLookAndFeelTheme();
                        break;
                    case Constants.METAL_LAF:
                        loadDefaultMetalLookAndFeelTheme();
                        break;
                    case Constants.OCEAN_LAF:
                        UIManager.setLookAndFeel(
                            "javax.swing.plaf.metal.MetalLookAndFeel");
                        break;
                    case Constants.MOTIF_LAF:
                        UIManager.setLookAndFeel(
                                "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                        break;
                    case Constants.WIN_LAF:
                        UIManager.setLookAndFeel(
                                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                        break;
                    case Constants.GTK_LAF:
                        UIManager.setLookAndFeel(
                                "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                        break;
                    case Constants.PLUGIN_LAF:
                        loadCustomLookAndFeel();
                        break;
                    default:
                        loadDefaultLookAndFeel();
                        break;
                }
                
            } catch (UnsupportedLookAndFeelException e) {
                //e.printStackTrace();
                lookAndFeelError();
                loadDefaultLook = true;
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
                lookAndFeelError();
                loadDefaultLook = true;
            }
            
            if (loadDefaultLook) {
                SystemProperties.setProperty(
                        "user", "startup.display.lookandfeel", "0");
                Log.debug("Loading system default look and feel.");
                GUIUtilities.updatePreferencesToFile();
                loadDefaultLookAndFeel();
            }
            
            if (SystemProperties.getBooleanProperty("user", "decorate.dialog.look")) {
                JDialog.setDefaultLookAndFeelDecorated(true);
            }
            if (SystemProperties.getBooleanProperty("user", "decorate.frame.look")) {
                JFrame.setDefaultLookAndFeelDecorated(true);
            }

            advanceSplash(splash);
            
            // initialise the custom text UI
            CustomTextAreaUI.initialize();
            CustomTextPaneUI.initialize();
            
            GUIUtilities.setLookAndFeel(look);
            GUIUtilities.startLogger();

            // initialise the frame
            frame = new ExecuteQueryFrame();

            GUIUtilities.initDesktop(frame);
            
            // initialise the actions from actions.xml
            ActionBuilder.build(GUIUtilities.getActionMap(),
                    GUIUtilities.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
                    Constants.ACTION_CONF_PATH);

            advanceSplash(splash);
            
            // build the tool bar
            GUIUtilities.createToolBar();

            JMenuBar menuBar = new MainMenu();
            frame.setJMenuBar(menuBar);

            GUIUtilities.registerMenuBar(menuBar);
            GUIUtilities.initialiseViewToolsMenuItems();
            
            ImageIcon _frameIcon = GUIUtilities.loadIcon(
                    "DefaultApplicationIcon16.gif");
            GUIUtilities.setDefaultIcon(_frameIcon);
            
            advanceSplash(splash);
            
            System.setProperty("executequery.help.version",
                    SystemProperties.getProperty("system", "help.version"));
            
            boolean openConnection = SystemProperties.getBooleanProperty(
                                            "user", "startup.connection.connect");

            advanceSplash(splash);
            
            Log.info("Execute Query version: " + 
                    System.getProperty("executequery.major.version"));            
            Log.info("Execute Query build: " + 
                    System.getProperty("executequery.build"));
            Log.info("Using Java version " +
                    SystemUtilities.getVMVersionFull());
            Log.info("System is ready.");
            
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();

            advanceSplash(splash);
            
            Dimension frameDim = new Dimension(screenSize.width - 200,
                                               screenSize.height - 150);

            if (SystemProperties.getBooleanProperty("user", "startup.window.maximized")) {
                frame.setSize(frameDim);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            else if (SystemProperties.containsKey("user", "window.position")) {
                ((ExecuteQueryFrame)frame).setSizeAndPosition();
            }
            else { // center the frame
                frame.setSize(frameDim);
                if (frameDim.height > screenSize.height) {
                    frameDim.height = screenSize.height;
                }
                if (frameDim.width > screenSize.width) {
                    frameDim.width = screenSize.width;
                }
                frame.setLocation((screenSize.width - frameDim.width) / 2,
                                  (screenSize.height - frameDim.height) / 2);
            }

            /*
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUIUtilities.setDisplayOptions(false); 
                }
            });
            */
            GUIUtilities.setDisplayOptions(false); 
            
            ActionBuilder.setActionMaps(
                                frame.getRootPane(),
                                SystemResources.getUserActionShortcuts());

            GUIUtilities.initPanels();
            /*
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUIUtilities.initPanels();
                }
            });
            */

            advanceSplash(splash);
            
            //Thread.sleep(15000);
            
            // kill the splash panel
            if (splash != null) {
                splash.dispose();
            }

            GUIUtils.invokeLater(new Runnable() {
                public void run() {
                    frame.setVisible(true);
                }
            });

            // auto-login if selected
            if (openConnection) {
                final String name = 
                        SystemProperties.getProperty("user", "startup.connection.name");
                if (!MiscUtils.isNull(name)) {                
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            openConnection(
                                    ConnectionProperties.getDatabaseConnection(name));
                        }
                    });
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** 
     * Loads the custom look and feel plugin.
     */
    private void loadCustomLookAndFeel() {
        try {
            PluginLookAndFeelManager lfManager = new PluginLookAndFeelManager();
            lfManager.loadLookAndFeel();
            
            if (!lfManager.isInstalled()) {
                loadDefaultLookAndFeel();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.error("An error occurred loading the selected look and feel: " +
                      e.getMessage());
            Log.error("Reverting to system default look and feel");
            loadDefaultLookAndFeel();
        }
        
    }
    
    /** 
     * Sets the default metal look and feel theme on Metal.
     */
    private void loadDefaultMetalLookAndFeelTheme() {
        try {
            javax.swing.plaf.metal.MetalLookAndFeel metal =
                    new javax.swing.plaf.metal.MetalLookAndFeel();
            metal.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
            UIManager.setLookAndFeel(metal);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            lookAndFeelError();
            loadCrossPlatformLookAndFeel();
        }
        
    }
    
    /** 
     * Sets the default look and feel theme on Metal.
     */
    private void loadDefaultLookAndFeelTheme() {
        try {
            javax.swing.plaf.metal.MetalLookAndFeel metal =
                    new javax.swing.plaf.metal.MetalLookAndFeel();
            metal.setCurrentTheme(new ExecuteQueryTheme());
            UIManager.setLookAndFeel(metal);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            lookAndFeelError();
            loadCrossPlatformLookAndFeel();
        }
        
    }

    /** 
     * Sets the default look and feel theme on Metal.
     */
    private void loadDefaultLookAndFeelTheme2() {
        try {
            javax.swing.plaf.metal.MetalLookAndFeel metal =
                    new javax.swing.plaf.metal.MetalLookAndFeel();
            metal.setCurrentTheme(new ExecuteQueryTheme2());
            UIManager.setLookAndFeel(metal);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            lookAndFeelError();
            loadCrossPlatformLookAndFeel();
        }
        
    }

    /**
     * Simple dialog with generic look and feel error message.
     */
    private void lookAndFeelError() {
        GUIUtilities.displayWarningMessage(
                "The selected look and feel is not " +
                "supported within this environment." +
                "\nThe system will revert to the default look and feel.");
    }
    
    /** 
     * Sets the default 'Execute Query' look and feel.
     */
    private void loadDefaultLookAndFeel() {
        try {
            org.underworldlabs.swing.plaf.ShadesOfGrayLookAndFeel metal = 
                    new org.underworldlabs.swing.plaf.ShadesOfGrayLookAndFeel();
            /*
            org.underworldlabs.swing.plaf.smoothgradient.SmoothGradientLookAndFeel metal =
                    new org.underworldlabs.swing.plaf.smoothgradient.SmoothGradientLookAndFeel();
            metal.setCurrentTheme(new ExecuteQueryTheme());
             */
            UIManager.setLookAndFeel(metal);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            lookAndFeelError();
            loadCrossPlatformLookAndFeel();
        }
        
    }
    
    /**
     * Loads the cross-platform look and feel.
     */
    private void loadCrossPlatformLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** <p>Opens the first frame to be displayed
     *  on the desktop - the Login Frame
     */
    /*
    private void openFirstFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginPanel panel = new LoginPanel(LoginPanel.STARTUP);
                GUIUtilities.addInternalFrame("Login", LoginPanel.FRAME_ICON,
                        panel, true, false, false, false);
            }
        });
    }
    */
    
    /** 
     * Advances the splash progress bar. 
     */
    private void advanceSplash(SplashPanel splash) {
        if (splash != null) {
            splash.advance();
        }
    }
    
    /** 
     * Returns the main frame.
     *
     * @return the primary <code>JFrame</code>
     */
    public static JFrame getFrame() {
        return frame;
    }
    
    /** 
     * Opens a database connection at startup if this
     * option was selected.
     *
     * @param dc the connection to be opened as a 
     *          <code>DatabaseConnection</code> object
     */
    private static void openConnection(DatabaseConnection dc) {
        if (dc == null) {
            return;
        }
        
        if (!dc.isPasswordStored()) {
            PasswordDialog pd = new PasswordDialog(frame,
                    "Password",
                    "Enter password");
            
            int result = pd.getResult();
            String pwd = pd.getValue();
            pd.dispose();

            if (result <= PasswordDialog.CANCEL) {
                return;
            }
            
            dc.setPassword(pwd);
        }
        
        dc.setJDBCDriver(JDBCProperties.getDatabaseDriver(dc.getDriverId()));
        
        try {
            SystemUtilities.connect(dc);
        }
        catch (DataSourceException e) {
            GUIUtilities.displayErrorMessage(e.getMessage());
        }
        
    }
    
    /** 
     * Application entry point - main method.
     */
    public static void main(String[] args) {
        // check args for help or uninstall option 
        if (args.length > 0) {
            String value = args[0];
            if (value.toUpperCase().equals("HELP")) {
                HelpWindow.main(args);
            }
            /*
            else if (value.toUpperCase().equals("UNINSTALL")) {
                Uninstall.main(args);
            }
             */
        }
        else {
            new ExecuteQuery();
        }

    }
    
}


