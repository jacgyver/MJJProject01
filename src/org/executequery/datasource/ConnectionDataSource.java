/*
 * ConnectionDataSource.java
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


package org.executequery.datasource;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import javax.sql.DataSource;
import org.underworldlabs.util.DynamicLibraryLoader;
import org.underworldlabs.util.MiscUtils;
import org.executequery.databasemediators.*;
import org.executequery.util.Log;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * Acts as a wrapper to the actual data source and JDBC driver.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/05/14 06:56:57 $
 */
public class ConnectionDataSource implements DataSource {
    
    public static final int ORACLE = 1;
    public static final int SYBASE = 2;
    public static final int DB2 = 3;
    public static final int SQLSERVER = 4;
    public static final int MYSQL = 5;
    public static final int POSTGRESQL = 6;
    public static final int INFORMIX = 7;
    public static final int HSQL = 8;
    public static final int POINTBASE = 9;
    public static final int ODBC = 10;
    public static final int ACCESS = 11;
    public static final int OTHER = 99;
    
    public static final String PORT = "[port]";
    public static final String SOURCE = "[source]";
    public static final String HOST = "[host]";
    
    private static final String PORT_REGEX = "\\[port\\]";
    private static final String SOURCE_REGEX = "\\[source\\]";
    private static final String HOST_REGEX = "\\[host\\]";
    
    private static Map<DatabaseDriver,Driver> loadedDrivers;
    
    protected boolean usingOracleThinDriver;
    protected boolean usingODBC;
    
    /** the generated JDBC URL */
    private String jdbcUrl;
    
    /** driver properties object for this source */
    protected DatabaseDriver databaseDriver;
    
    /** the loaded java.sql.Driver */
    protected Driver _driver;
    
    /** the genrated driver connection properties */
    protected Properties driverProps;
    
    /** Whether the driver has been loaded */
    protected boolean driverLoaded;
    
    /** The database connection object of this data source */
    protected DatabaseConnection databaseConnection;

    public ConnectionDataSource(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        if (databaseConnection.hasAdvancedProperties()) {
            driverProps = new Properties(databaseConnection.getJdbcProperties());
        } else {
            driverProps = new Properties();
        }

        Log.info("Initialising data source for " +
                databaseConnection.getName());
        
        driverProps.put("user", databaseConnection.getUserName());
        driverProps.put("password", databaseConnection.getUnencryptedPassword());

        databaseDriver = databaseConnection.getJDBCDriver();
    }
    
    static {
        loadedDrivers = new HashMap<DatabaseDriver,Driver>();
    }
    
    protected void destroy() {
        _driver = null;
        databaseDriver = null;
        driverProps = null;
    }
    
    public boolean isUsingOracleThinDriver() {
        return usingOracleThinDriver;
    }
    
    public void setUsingOracleThinDriver(boolean thin) {
        usingOracleThinDriver = thin;
    }
    
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    
    private void loadDriver() throws SQLException {
        
        if (databaseDriver == null) {
            throw new SQLException("No JDBC driver specified");
        }
        
        try {
            driverLoaded = false;
            int driverType = databaseDriver.getType();
            
            if (driverType == ORACLE) {
                usingOracleThinDriver = true;
            }
            else if (driverType == ODBC) {
                usingODBC = true;
            }

            jdbcUrl = databaseConnection.getURL();
            
            // if the url is null - generate it
            if (MiscUtils.isNull(jdbcUrl)) {
                
                /* Generate the JDBC URL as specfied in jdbcdrivers.xml
                 * using the server, port and source values for the connection. */
                String value = null;
                jdbcUrl = databaseDriver.getURL();
                
                Log.info("JDBC URL pattern: "+jdbcUrl);
                
                // check if this url needs the server/host name
                if (jdbcUrl.indexOf(HOST) != -1) {
                    value = databaseConnection.getHost();
                    if (MiscUtils.isNull(value)) {
                        handleInformationException();
                    }
                    jdbcUrl = jdbcUrl.replaceAll(HOST_REGEX, value);
                }

                // check if this url needs the port number
                if (jdbcUrl.indexOf(PORT) != -1) {
                    value = databaseConnection.getPort();
                    if (MiscUtils.isNull(value)) {
                        handleInformationException();
                    }
                    jdbcUrl = jdbcUrl.replaceAll(PORT_REGEX, value);
                }

                // check if this url needs the source name
                if (jdbcUrl.indexOf(SOURCE) != -1) {
                    value = databaseConnection.getSourceName();
                    if (MiscUtils.isNull(value)) {
                        handleInformationException();
                    }
                    jdbcUrl = jdbcUrl.replaceAll(SOURCE_REGEX, value);
                }

                Log.info("JDBC URL generated: "+jdbcUrl);
            } 
            else {
                Log.info("Using user specified JDBC URL: "+jdbcUrl);
            }
            
            // check if this driver has already been loaded
            if (loadedDrivers.containsKey(databaseDriver)) {
                _driver = loadedDrivers.get(databaseDriver);
                driverLoaded = true;
                return;
            }
            
            Class clazz = null;
            String driverName = databaseDriver.getClassName();
            
            if (!usingODBC) {                
                URL[] urls = MiscUtils.loadURLs(databaseDriver.getPath());

                /* Load the JDBC libraries and initialise the driver. */
                DynamicLibraryLoader loader = new DynamicLibraryLoader(urls);
                clazz = loader.loadLibrary(driverName);
            }
            else {
                clazz = Class.forName(driverName, true,
                                      ClassLoader.getSystemClassLoader());
            } 

            Object object = clazz.newInstance();
            _driver = (Driver)object;
            loadedDrivers.put(databaseDriver, _driver);
            driverLoaded = true;
            //DriverManager.setLogStream(System.out);
            
        }
        catch (ClassNotFoundException cExc) {
            driverLoaded = false;
            throw new SQLException("The specified JDBC driver class was not found");
        }
        catch (IllegalAccessException e) {
            driverLoaded = false;
            throw new SQLException("The specified JDBC driver class was not accessible");            
        }
        catch (MalformedURLException e) {
            driverLoaded = false;
            throw new SQLException(e.getMessage());
        }
        catch (InstantiationException e) {
            driverLoaded = false;
            throw new SQLException(e.getMessage());            
        }

    }

    private void handleInformationException() throws SQLException {
        driverLoaded = false;
        throw new SQLException(
                "Insufficient information was provided to establish the connection.\n" +
                "Please ensure all required details have been entered.");
    }
    
    public Connection getConnection() throws SQLException{
        return getConnection(databaseConnection.getUserName(),
                             databaseConnection.getUnencryptedPassword());
    }
    
    public Connection getConnection(String user, String password)
        throws SQLException {
        
        if (!driverLoaded) {
            loadDriver();
        }

        if (driverProps == null) {
            driverProps = new Properties();
            if (!MiscUtils.isNull(user)) {
                driverProps.put("user", user);
            }
            if (!MiscUtils.isNull(password)) {
                driverProps.put("password", password);
            }
        } 

        //Log.info("Retrieving connection from URL: " + jdbcUrl);
        if (_driver != null) {
            return _driver.connect(jdbcUrl, driverProps);
        }

        return null;
    }
    
    public void setDriverObject(DatabaseDriver d) {
        databaseDriver = d;
    }
    
    public String getDriverClassName() {
        return databaseDriver.getClassName();
    }
    
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }
    
    public void setLoginTimeout(int timeout) throws SQLException {
        DriverManager.setLoginTimeout(timeout);
    }
    
    public void setLogWriter(PrintWriter writer) throws SQLException {
        DriverManager.setLogWriter(writer);
    }

    public boolean isDriverLoaded() {
        return driverLoaded;
    }
    
}



