/*
 * DatabaseConnection.java
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


package org.executequery.databasemediators;

import java.util.Properties;
import org.executequery.Constants;
import org.executequery.JDBCProperties;
import org.underworldlabs.util.DesEncrypter;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *  <p>This class maintains the necessary information for each
 *  saved database connection.<br>
 *  Each saved connection appears by name within the
 *  saved connections drop-down box displayed on respective
 *  windows.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:54 $
 */
public class DatabaseConnection {
    
    private static final String ENCRYPTION_KEY = "yb7UD9jH";
    
    /** The unique name for this connection */
    private String name;
    
    /** The user name for this connection */
    private String userName;
    
    /** The password for this connection */
    private String password;
    
    /** The host for this connection */
    private String host;
    
    /** The data source name for this connection */
    private String sourceName;
    
    /** The database vendor's name for this connection */
    private String databaseType;
    
    /** The port number for this connection */
    private String port;
    
    /** The driver specific URL for this connection */
    private String url;
    
    /** The unique name of the JDBC/ODBC driver used with this connection */
    private String dName;
    
    /** The unique ID of the JDBC/ODBC driver used with this connection */
    private long driverId;
    
    /** The JDBC/ODBC Driver used with this connection */
    private DatabaseDriver driver;
    
    /** The advanced Properties for this connection */
    private Properties jdbcProperties;
    
    /** Whether this connection's password is stored */
    private boolean passwordStored;
    
    /** Whether this connection is a new connection */
    private boolean newConn;
    
    /** Whether the password is encrypted */
    private boolean passwordEncrypted;
    
    /** the tx isolation level */
    private int transactionIsolation;
    
    /** the commit mode */
    private boolean autoCommit;
    
    /** Whether this connection is active */
    transient boolean connected;
    
    /**
     * Creates a new empty <code>DatabaseConnection</code> object.
     */
    public DatabaseConnection() {
        this(null);
    }
    
    /**
     * Creates a new empty <code>DatabaseConnection</code> object
     * with the specified name.
     *
     * @param A unique name for this connection.
     */
    public DatabaseConnection(String name) {
        this.name = name;
        autoCommit = true;
        transactionIsolation = -1;
    }

    public boolean isPasswordStored() {
        return passwordStored;
    }
    
    public void setPasswordStored(boolean storePwd) {
        this.passwordStored = storePwd;
    }
    
    public void setJdbcProperties(Properties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }
    
    public Properties getJdbcProperties() {
        return jdbcProperties;
    }
    
    public boolean hasAdvancedProperties() {
        return jdbcProperties != null && jdbcProperties.size() > 0;
    }
    
    public DatabaseDriver getJDBCDriver() {
        if (driver == null) {
            driver = JDBCProperties.getDatabaseDriver(driverId);
        }
        return driver;
    }
    
    public boolean hasURL() {
        return url != null && url.length() > 0;
    }
    
    public int getPortInt() {
        return Integer.parseInt(port);
    }
    
    public void setJDBCDriver(DatabaseDriver driver) {
        this.driver = driver;
    }
    
    public String getDriverName() {
        return dName;
    }

    public void setDriverName(String dName) {
        this.dName = dName;
    }
    
    public String getPort() {
        return port == null ? Constants.EMPTY : port;
    }
    
    public boolean hasPort() {
        return port != null  && port.length() > 0;
    }
    
    public void setPort(String port) {
        this.port = port;
    }
    
    public String getURL() {
        return url == null ? Constants.EMPTY : url;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
    
    public String getDatabaseType() {
        return databaseType;
    }
    
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
    
    public String getPassword() {
        return password;
    }

    public String getUnencryptedPassword() {
        String _password = password;
        if (passwordEncrypted && !MiscUtils.isNull(password)) {
            DesEncrypter encrypter = new DesEncrypter();
            _password = encrypter.decrypt(ENCRYPTION_KEY, password);
        }
        return _password;
    }
    
    public void setEncryptedPassword(String password) {
        this.password = password;
    }
    
    public void setPassword(String password) {
        if (passwordEncrypted && !MiscUtils.isNull(password)) {
            DesEncrypter encrypter = new DesEncrypter();
            this.password = encrypter.encrypt(ENCRYPTION_KEY, password);
        } else {
            this.password = password;
        }
    }
    
    public String getSourceName() {
        return sourceName == null ? Constants.EMPTY : sourceName;
    }
    
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    
    public boolean hasHost() {
        return host != null && host.length() > 0;
    }
    
    public boolean hasSourceName() {
        return sourceName != null && sourceName.length() > 0;
    }
    
    public String getHost() {
        return host == null ? Constants.EMPTY : host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return name;
    }

    public boolean isPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(boolean passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }
   
}

