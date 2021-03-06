/*
 * SystemResources.java
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


package org.executequery.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import org.executequery.Constants;

import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.underworldlabs.util.FileUtils;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * This object acts as a utility class for file input
 * and output. All open file and save file requests are
 * propagated via the relevant methods to this class.
 * This object is also responsible for user and default
 * system properties and handles all read and write methods
 * for these. System version information and SQL keywords
 * are also retrieved and maintained here.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.8 $
 * @date     $Date: 2006/06/25 12:45:19 $
 */
public class SystemResources {

    
    public static void resetLog(String log) {
        try {
            String path = SystemUtilities.getUserLogsPath() + log;
            FileUtils.writeFile(path, Constants.EMPTY, false);
        }
        catch (IOException e) {
            GUIUtilities.displayErrorMessage("Error resetting log file:\n" + log);
        }
    }

    public static Vector<String> getDatabaseNameList() {
        try {
            String path = "org/executequery/database.name.list";
            String values = FileUtils.loadResource(path);

            Vector<String> v = new Vector<String>();
            StringTokenizer st = new StringTokenizer(values, "|");
            while (st.hasMoreTokens()) {
                v.add(st.nextToken());
            }

            return v;            
        }
        catch (IOException e) {
            e.printStackTrace();
            GUIUtilities.displayErrorMessage("Error opening database list");
            return null;
        }
    }
    
    public static Properties getEqSystemProperties() {
        Properties properties = null;
        try {
            String path = "org/executequery/eq.system.properties";
            properties = FileUtils.loadPropertiesResource(path);
        }
        catch (IOException e) {
            System.err.println("Could not find version.");
            properties = new Properties();
            properties.setProperty("eq.version","-1");
            properties.setProperty("eq.build","-1");
            properties.setProperty("help.version","-1");
        }
        return properties;
    }

    public static Properties getUserActionShortcuts() {       
        try {
            String fileName = SystemUtilities.getUserPropertiesPath() +
                              "eq.shortcuts.properties";
            File file = new File(fileName);

            if (!file.exists()) {
                return null;
            }

            return FileUtils.loadProperties(file);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setUserActionShortcuts(Properties properties) {
        try {
            String path = SystemUtilities.getUserPropertiesPath() +
                          "eq.shortcuts.properties";
            FileUtils.storeProperties(path, properties, 
                    "Execute Query - User Defined System Shortcuts");
        }
        catch (IOException e) {
            e.printStackTrace();
            GUIUtilities.displayErrorMessage("Error saving shortcuts");
        }
    }

    public static synchronized void setUserPreferences(Properties properties) {
        try {
            String path = SystemUtilities.getUserPropertiesPath() +
                          "eq.user.properties";
            FileUtils.storeProperties(path, properties, 
                    "Execute Query - User Defined System Properties");
        }
        catch (IOException e) {
            e.printStackTrace();
            GUIUtilities.displayErrorMessage("Error saving preferences:\n"+
                    e.getMessage());
        }
    }
    
    public static Properties getConsoleProperties() {
        Properties properties = null;
        try {
            String path = "org/executequery/console.properties";
            properties = FileUtils.loadPropertiesResource(path);
        }
        catch (IOException e) {
            e.printStackTrace();
            GUIUtilities.displayErrorMessage(
                    "Error opening system console properties");
        }
        return properties;
    }
    
    public static Properties getDefaultProperties() {
        Properties properties = null;
        try {
            String path = "org/executequery/eq.default.properties";
            properties = FileUtils.loadPropertiesResource(path);
        }
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                            GUIUtilities.getParentFrame(),
                            "Error opening default\nsystem properties", "Error",
                            JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }        
        return properties;
    }

    public static Properties getUserProperties() {
        try {
            String path = SystemUtilities.getUserPropertiesPath() + 
                          "eq.user.properties";
            Properties defaults = getDefaultProperties();
            Properties properties = FileUtils.loadProperties(path, defaults);
            return properties;
        }
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(GUIUtilities.getParentFrame(),
                                "Error opening user\nsystem properties", "Error",
                                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Creates the eq system home directory structure in ~/.executequery.
     */
    public static boolean createUserHomeDirSettings() {
        String fileSeparator = System.getProperty("file.separator");
        String eqUserHomeDir = 
                        System.getProperty("user.home") +
                        fileSeparator +
                        System.getProperty("executequery.user.home.dir");

        File equeryDir = new File(eqUserHomeDir);
        File confDir = new File(SystemUtilities.getUserPropertiesPath());
        File logsDir = new File(SystemUtilities.getUserLogsPath());

        try {
            // whether the equery base dir exists
            boolean equeryDirExists = false;

            // whether the conf dir exists
            boolean confDirExists = false;
            
            // whether the logs dir exists
            boolean logsDirExists = false;
            
            // whether to copy confg files from old dir
            boolean copyOldFiles = false;
            
            // -------------------------------------------
            // -- Check for ~/.executequery
            // -------------------------------------------
            if (!equeryDir.exists()) {
                equeryDirExists = equeryDir.mkdir();
                // create the conf directory
                confDirExists = confDir.mkdir();
            }            
            // -------------------------------------------
            // -- Check for ~/.executequery/<build_number>
            // -------------------------------------------
            else if (!confDir.exists()) {
                confDirExists = confDir.mkdir();
                copyOldFiles = true;
            }
            else { // they exist
                equeryDirExists = true;
                confDirExists = true;
            }

            String newConfPath = confDir.getAbsolutePath() + fileSeparator;
            
            int lastBuildNumber = -1;
            if (copyOldFiles) {
                // check for old conf build number dirs
                int currentBuild = Integer.parseInt(
                        System.getProperty("executequery.build"));
                File[] dirs = equeryDir.listFiles();
                for (int i = 0; i < dirs.length; i++) {
                    String name = dirs[i].getName();
                    if (MiscUtils.isValidNumber(name)) {
                        int buildNumber = Integer.parseInt(name);
                        if (currentBuild > buildNumber) {
                            lastBuildNumber = Math.max(lastBuildNumber, buildNumber);
                        }
                    }
                }
            }
            
            // if we have a valid last build dir - use it
            File oldConfDir = null;
            if (lastBuildNumber != -1) {
                oldConfDir = new File(eqUserHomeDir + fileSeparator + lastBuildNumber);
            } else {
                // otherwise check for old format ~/.executequery/conf
                oldConfDir = new File(eqUserHomeDir + fileSeparator + "conf");
            }
            
            // if an old conf dir exists, move relevant
            // files to the new build number dir

            if (copyOldFiles && oldConfDir.exists()) {
                String oldFromPath = oldConfDir.getAbsolutePath();
                String[] oldFiles = {"eq.shortcuts.properties",
                                     "eq.user.properties",
                                     "jdbcdrivers.xml",
                                     "lookandfeel.xml",
                                     //"toolbars.xml",
                                     "print.setup",
                                     "savedconnections.xml",
                                     "sql.user.keywords"};

                File file = null;
                // move the above files to the new build dir
                for (int i = 0; i < oldFiles.length; i++) {
                    file = new File(oldFromPath, oldFiles[i]);
                    if (file.exists()) {
                        String path1 = file.getAbsolutePath();
                        file = new File(confDir, oldFiles[i]);
                        String path2 = file.getAbsolutePath();
                        FileUtils.copyFile(path1, path2);
                    }
                }

            }
            
            if (!logsDir.exists()) {
                logsDirExists = logsDir.mkdir();
            }
            
            if (!equeryDirExists && !confDirExists && !logsDirExists) {
                GUIUtilities.displayErrorMessage(
                   "Error creating profile in user's home directory.\nExiting.");
                System.exit(0);
            }
            
            boolean created = false;

            // -------------------------------------------
            // -- Check for ~/.executequery/conf/sql.user.keywords
            // -------------------------------------------
            File props = new File(confDir, "sql.user.keywords");
            
            // create the user defined keywords file
            if (!props.exists()) {
                created = props.createNewFile();
            } else {
                created = true;
            }

            if (!created) {
                return false;
            }
            
            // -------------------------------------------
            // -- Check for ~/.executequery/conf/eq.user.properties
            // -------------------------------------------
            props = new File(confDir, "eq.user.properties");
            
            if (!props.exists()) {
                Log.debug("Creating user properties file eq.user.properties");
                created = props.createNewFile();
            } else {
                created = true;
            }
            
            if (!created) {
                return false;
            }
            
            // -------------------------------------------
            // -- Check for ~/.executequery/conf/jdbcdrivers.xml
            // -------------------------------------------
            props = new File(confDir, "jdbcdrivers.xml");
            if (!props.exists()) {
                Log.debug("Creating user properties file jdbcdrivers.xml");
                FileUtils.copyResource(
                        "org/executequery/jdbcdrivers-default.xml",
                        newConfPath + "jdbcdrivers.xml");
                props = new File(confDir, "jdbcdrivers.xml");
                created = props.exists();
            } else {
                created = true;
            }
            
            if (!created) {
                return false;
            }
            
            // -------------------------------------------
            // -- Check for ~/.executequery/conf/lookandfeel.xml
            // -------------------------------------------
            props = new File(confDir, "lookandfeel.xml");
            if (!props.exists()) {
                Log.debug("Creating user properties file lookandfeel.xml");
                FileUtils.copyResource(
                            "org/executequery/lookandfeel-default.xml",
                            newConfPath + "lookandfeel.xml");
                props = new File(confDir, "lookandfeel.xml");
                created = props.exists();
            } else {
                created = true;
            }
            
            if (!created) {
                return false;
            }
            
            // -------------------------------------------
            // -- Check for ~/.executequery/conf/savedconnections.xml
            // -------------------------------------------
            props = new File(confDir, "savedconnections.xml");
            if (!props.exists()) {
                Log.debug("Creating user properties file savedconnections.xml");
                FileUtils.copyResource(
                            "org/executequery/savedconnections-default.xml",
                            newConfPath + "savedconnections.xml");
                props = new File(confDir, "savedconnections.xml");
                created = props.exists();
            } else {
                created = true;
            }
            
            if (!created) {
                return false;
            }
            
            // -------------------------------------------
            // -- Check for ~/.executequery/conf/toolbars.xml
            // -------------------------------------------
            props = new File(confDir, "toolbars.xml");
            
            if (!props.exists()) {
                Log.debug("Creating user properties file toolbars.xml");
                FileUtils.copyResource(
                            "org/executequery/toolbars-default.xml", 
                            newConfPath + "toolbars.xml");
                props = new File(confDir, "toolbars.xml");
                created = props.exists();
            } else {
                created = true;
            }

            return created;
            
        }
        
        catch (IOException exc) {
            exc.printStackTrace();
            GUIUtilities.displayErrorMessage(
                 "Error creating profile in user's home directory.\nExiting.");
            return false;
        }
        
    }
   
}


