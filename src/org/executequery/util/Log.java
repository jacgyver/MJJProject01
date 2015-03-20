/*
 * Log.java
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

import java.io.IOException;
import java.io.Serializable;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.executequery.SystemUtilities;
import org.underworldlabs.util.SystemProperties;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *  <p>Logger wrapper class.<br>
 *  Provides static methods to the Log4J logging methods.
 *  This is a convenience class only and can not be instantiated.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/09/14 07:37:46 $
 */
public class Log implements Serializable {

    /** The Log4J Logger object */
    private static Logger logger;

    /** the logger name */
    public static final String LOGGER_NAME = "system-logger";
    
    /** the log pattern string */
    public static final String PATTERN = "[%d{HH:mm:ss}] %m%n";
    
    /** the max number of log files rolled over */
    public static final int MAX_BACKUP_INDEX = 5;
    
    /** <p><code>private<code> constructor to prevent instantiation. */
    private Log() {}

    /**
     * Initialises the logger instance with the default level.
     */
    public static void init() {
        init("INFO");
    }

    /**
     * Initialises the logger instance with the specified level.
     *
     * @param level - the log level
     */
    public static void init(String level) {
        logger = Logger.getLogger(LOGGER_NAME);
        try {
            if (level != null) {
                setLevel(level);
            } else { // default to INFO
                setLevel("INFO");
            }

            // the log file path
            String path = SystemUtilities.getSystemLogPath();

            // init the patter layout
            PatternLayout patternLayout = new PatternLayout(PATTERN);
            RollingFileAppender appender = new RollingFileAppender(
                                                            patternLayout, 
                                                            path,
                                                            true);
            appender.setMaxBackupIndex(MAX_BACKUP_INDEX);
            appender.setMaxFileSize("1MB");
            logger.addAppender(appender);
        }
        catch (IOException ioExc) {
            ioExc.printStackTrace();
        }
    }

    /**
     * Adds the specified appender to the logger.
     *
     * @param appender - the appender to be added
     */
    public static void addAppender(Appender appender) {
        if (logger == null) {
            throw new RuntimeException("Logger not initialised.");
        }
        logger.addAppender(appender);
    }
    
    /**
     * Returns whether the log level is set to DEBUG.
     */
    public static boolean isDebugEnabled() {
        if (logger != null) {
            return logger.isDebugEnabled();
        }
        return false;
    }
    
    /**
     * Sets the logger level to that specified.
     *
     * @param level - the logger level to be set.<br>
     *        Valid values are: ERROR, DEBUG, INFO, WARN, ALL, FATAL, TRACE
     */
    public static void setLevel(String level) {
        if (level == null) {
            return;
        }

        level = level.toUpperCase();
        if (level.equals("INFO")) {
            logger.setLevel((Level)Level.INFO);
        }
        else if (level.equals("WARN")) {
            logger.setLevel((Level)Level.WARN);
        }
        else if (level.equals("DEBUG")) {
            logger.setLevel((Level)Level.DEBUG);
        }
        else if (level.equals("ERROR")) {
            logger.setLevel((Level)Level.ERROR);
        }
        else if (level.equals("FATAL")) {
            logger.setLevel((Level)Level.FATAL);
        }
        else if (level.equals("TRACE")) {
            logger.setLevel((Level)Level.TRACE);
        }
        else if (level.equals("ALL")) {
            logger.setLevel((Level)Level.ALL);
        }

    }

    /**
     * Logs a message at log level INFO.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public static void info(Object message, Throwable throwable) {
        if (logger == null) {
            return;
        }
        logger.info(message, throwable);
    }

    /**
     * Logs a message at log level WARN.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public static void warning(Object message, Throwable throwable) {
        if (logger == null) {
            return;
        }
        logger.warn(message, throwable);
    }

    /**
     * Logs a message at log level DEBUG.
     *
     * @param message  the log message.
     */
    public static void debug(Object message) {
        if (logger == null || !logger.isDebugEnabled()) {
            return;
        }
        logger.debug(message);
    }

    /**
     * Logs a message at log level DEBUG.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public static void debug(Object message, Throwable throwable) {
        if (logger == null || !logger.isDebugEnabled()) {
            return;
        }
        logger.debug(message, throwable);
    }

    /**
     * Logs a message at log level ERROR.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public static void error(Object message, Throwable throwable) {
        if (logger == null) {
            System.out.println("ERROR: " + message);
            throwable.printStackTrace();
            return;
        }
        logger.error(message, throwable);
    }

    /**
     * Logs a message at log level INFO.
     *
     * @param message  the log message.
     */
    public static void info(Object message) {
        if (logger == null) {
            return;
        }
        logger.info(message);
    }

    /**
     * Logs a message at log level WARN.
     *
     * @param message  the log message.
     */
    public static void warning(Object message) {
        if (logger == null) {
            return;
        }
        logger.warn(message);
    }

    /**
     * Logs a message at log level ERROR.
     *
     * @param message  the log message.
     */
    public static void error(Object message) {
        if (logger == null) {
            System.err.println("ERROR: " + message);
            return;
        }
        logger.error(message);
    }

    /**
     * Returns whether a logger exists and
     * has been initialised.
     *
     * @return <code>true</code> if initialised |
     *         <code>false</code> otherwise
     */
    public static boolean isLogEnabled() {
        return logger != null;
    }

}



