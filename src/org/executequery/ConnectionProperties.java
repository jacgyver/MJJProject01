/*
 * ConnectionProperties.java
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

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.executequery.databasemediators.DatabaseDriver;
import org.executequery.databasemediators.DatabaseConnection;
import org.underworldlabs.util.FileUtils;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/28 08:35:55 $
 */
public class ConnectionProperties {
    
    // -------------------------------------------
    // XML tag names and attributes
    
    private static final String SAVED_CONNECTIONS = "savedconnections";
    private static final String CONNECTION = "connection";
    private static final String NAME = "name";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String ENCRYPTED = "encrypted";
    private static final String DRIVER_ID = "driverid";
    private static final String HOST = "host";
    private static final String DATA_SOURCE = "datasource";
    private static final String TX_ISOLATION = "txisolation";
    private static final String AUTO_COMMIT = "autocommit";
    private static final String PORT = "port";
    private static final String URL = "url";
    private static final String DRIVER_NAME = "drivername";
    private static final String ADVANCED = "advanced";
    private static final String PROPERTY = "property";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String STORE_PASSWORD = "storepassword";
    
    private static final String CDDATA = "CDATA";
    
    // -------------------------------------------
    
    /** the database connections collection */
    private static Vector<DatabaseConnection> connections;

    /** private constructor */
    private ConnectionProperties() {}

    public static String[] getConnectionNames() {
        String[] names = new String[connections.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = connections.get(i).toString();
        }
        return names;
    }
    
    /**
     * Returns whether the name specified exists within the saved 
     * connections. the connection object specified allows for it 
     * to be removed from consideration (checking against itself).
     * 
     * @param the connection to exclude
     * @param the name to validated
     */
    public static boolean nameExists(DatabaseConnection dc, String name) {
        if (name == null) {
            name = dc.getName();
        }
        for (int i = 0, k = connections.size(); i < k; i++) {
            DatabaseConnection _dc = connections.elementAt(i);
            String _name = _dc.getName();
            if (_dc != dc && _name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Returns the stored connections collection.
     *
     * @return the connections within a Vector object
     */
    public static Vector<DatabaseConnection> getConnectionsVector() {
        return connections;
    }
    
    /**
     * Returns the connection properties object with the specified name.
     *
     * @param the name of the connection sought
     * @return the connection properties object
     */
    public static DatabaseConnection getDatabaseConnection(String name) {
        for (int i = 0, k = connections.size(); i < k; i++) {
            DatabaseConnection dc = connections.elementAt(i);            
            if (dc.getName().equalsIgnoreCase(name)) {
                return dc;
            }
        } 
        return null;
    }
    
    /**
     * Returns the stored connections as an array.
     *
     * @return the connections as an array
     */
    public static DatabaseConnection[] getConnectionsArray() {
        DatabaseConnection[] dca = new DatabaseConnection[connections.size()];
        for (int i = 0; i < connections.size(); i++) {
            dca[i] = connections.elementAt(i);
        }         
        return dca;
    }
    
    /** 
     * Saves the connections to file.
     *
     * @return 1 if saved ok, 0 otherwise
     */
    public static synchronized int saveConnections() throws ValidationException {
        // validate the names are unique
        for (int i = 0, k = connections.size(); i < k; i++) {
            DatabaseConnection dc = connections.get(i);
            for (int j = 0; j < k; j++) {
                DatabaseConnection _dc = connections.get(j);
                if (nameExists(dc, null)) {
                    throw new ValidationException(
                            "The connection name " + dc.getName() +
                            " already exists.");
                }
            }
        }
        
        OutputStream os = null;
        try {
            
            // ---------------------------------------
            // ----- Save the connections to file ----
            // ---------------------------------------
            
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            ConnectionParser cp = new ConnectionParser();

            File connXML = new File(SystemUtilities.getUserPropertiesPath() +
                                    "savedconnections.xml");
            
            os = new FileOutputStream(connXML);
            DatabaseConnection[] dca = getConnectionsArray();
            SAXSource source = new SAXSource(cp, new ConnectionInputSource(dca));
            StreamResult r = new StreamResult(os);
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, r);
            
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {}
        }
    }
    
    
    /**
     * Loads the connections from file and stores them within
     * collection  - a Vector object.
     */
    public static synchronized void loadConnections() {
        String confPath = SystemUtilities.getUserPropertiesPath();
        String from = "org/executequery/savedconnections-default.xml";
        String to = confPath + "savedconnections.xml";

        File file = new File(to);
        if (file.exists()) {
            InputStream in = null;
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                
                SAXParser parser = factory.newSAXParser();
                XMLConnectionHandler handler = new XMLConnectionHandler();
                
                in = new FileInputStream(file);
                parser.parse(in, handler);
            }
            catch (Exception e) {
                e.printStackTrace();
                
                StringBuffer sb = new StringBuffer("Error opening connection definitions.\n");
                sb.append("The file savedconnections.xml is damaged and will be\nrenamed to ").
                   append("savedconnections.xml.old. A new empty file\nwill be created in ").
                   append("its place.");
                GUIUtilities.displayErrorMessage(sb.toString());
                
                if (file != null && file.exists()) {
                    file.renameTo(new File(confPath + "savedconnections.xml.old"));
                }

                recreateDefinitionsFile(from, to);
                connections = new Vector<DatabaseConnection>();
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {}
                }
            }
   
        }
        else {
            recreateDefinitionsFile(from, to);
            connections = new Vector<DatabaseConnection>();
        } 
        
        // check that the driver names have been removed for IDs
        for (int i = 0, n = connections.size(); i < n; i++) {
            DatabaseConnection dc = connections.get(i);
            if (dc.getDriverId() == 0) {
                DatabaseDriver driver = 
                        JDBCProperties.getDatabaseDriver(dc.getDriverName());
                if (driver != null) {
                    dc.setDriverId(driver.getId());
                }
            }
        }
        
        SystemUtilities.setSavedConnections(getConnectionsArray());
        
    }
    
    private static void recreateDefinitionsFile(String from, String to) {
        try {
            FileUtils.copyResource(from, to);
        } catch (IOException ioExc) {
            GUIUtilities.displayErrorMessage(
                    "An error occurred recreating the connections definitions.");
        }
    }
    
    static class XMLConnectionHandler extends DefaultHandler {

        private CharArrayWriter contents;
        private DatabaseConnection dc;
        private Properties advancedProps;
        
        public XMLConnectionHandler() {
            if (connections == null) {
                connections = new Vector<DatabaseConnection>();
            }
            contents = new CharArrayWriter();
        }

        public void startElement(String nameSpaceURI, String localName,
                                 String qName, Attributes attrs) {
            contents.reset();
            
            if (localName.equals(CONNECTION)) {
                dc = new DatabaseConnection();
                String value = attrs.getValue(STORE_PASSWORD);
                if (!MiscUtils.isNull(value)) {
                    dc.setPasswordStored(Boolean.valueOf(value).booleanValue());
                }
            }
            else if (localName.equals(PASSWORD)) {
                String value = attrs.getValue(ENCRYPTED);
                if (!MiscUtils.isNull(value)) {
                    dc.setPasswordEncrypted(Boolean.valueOf(value).booleanValue());
                }
            }
            else if (localName.equals(PROPERTY)) {
                if (advancedProps == null) {
                    advancedProps = new Properties();
                }
                advancedProps.setProperty(attrs.getValue(KEY), attrs.getValue(VALUE));
            }
            
        }
        
        public void endElement(String nameSpaceURI, String localName,
                               String qName) {

            if (localName.equals(NAME)) {
                dc.setName(contents.toString());
            } 
            else if (localName.equals(USER)) {
                dc.setUserName(contents.toString());
            } 
            else if (localName.equals(PASSWORD)) {
                String value = contents.toString();
                if (!MiscUtils.isNull(value)) {
                    if (dc.isPasswordEncrypted()) {
                        dc.setEncryptedPassword(value);
                    } else {
                        dc.setPassword(value);
                    }
                    dc.setPasswordStored(true);
                } else {
                    dc.setPasswordStored(false);
                }
            } 
            else if (localName.equals(HOST)) {
                dc.setHost(contents.toString());
            } 
            else if (localName.equals(DATA_SOURCE)) {
                dc.setSourceName(contents.toString());
            } 
            else if (localName.equals(PORT)) {
                dc.setPort(contents.toString());
            } 
            else if (localName.equals(URL)) {
                dc.setURL(contents.toString());
            } 
            else if (localName.equals(DRIVER_ID)) {
                dc.setDriverId(Long.parseLong(contents.toString()));
            }
            else if (localName.equals(DRIVER_NAME)) {
                dc.setDriverName(contents.toString());
            }
            else if (localName.equals(AUTO_COMMIT)) {
                String value = contents.toString();
                if (!MiscUtils.isNull(value)) {
                    dc.setAutoCommit(Boolean.valueOf(value));
                }
            }
            else if (localName.equals(TX_ISOLATION)) {
                String value = contents.toString();
                if (!MiscUtils.isNull(value)) {
                    dc.setTransactionIsolation(new Integer(value));
                } else {
                    dc.setTransactionIsolation(-1);
                }
            }
            else if (localName.equals(ADVANCED)) {
                if (advancedProps != null && advancedProps.size() > 0) {
                    dc.setJdbcProperties(advancedProps);
                }
            } 
            else if (localName.equals(CONNECTION)) {
                connections.add(dc);
                advancedProps = null;
                dc = null;
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
    } // XMLHandler
    
    static class ConnectionInputSource extends InputSource {
        private DatabaseConnection[] dca;
        
        public ConnectionInputSource(DatabaseConnection[] dc) {
            dca = dc;
        }
        
        public DatabaseConnection[] getConnections() {
            return dca;
        }
        
    } // class ConnectionInputSource
    
    static class ConnectionParser implements XMLReader {
        private String nsu = Constants.EMPTY;
        private AttributesImpl atts = new AttributesImpl();
        
        private ContentHandler handler;
        
        private static char[] newLine = {'\n'};
        private static String indent_1 = "\n   ";
        private static String indent_2 = "\n      ";
        private static String indent_3 = "\n         ";
        
        public ConnectionParser() {}
        
        public void parse(InputSource input) throws SAXException, IOException {
            if (!(input instanceof ConnectionInputSource)) {
                throw new SAXException("Parser can only accept a ConnectionInputSource");
            }
            
            parse((ConnectionInputSource)input);
        }
        
        public void parse(ConnectionInputSource input) throws IOException, SAXException {
            try {
                if (handler == null) {
                    throw new SAXException("No content handler");
                }
                
                DatabaseConnection[] conns = input.getConnections();
                
                handler.startDocument();
                handler.startElement(nsu, SAVED_CONNECTIONS, SAVED_CONNECTIONS, atts);
                handler.ignorableWhitespace(newLine, 0, 1);
                
                String marker = null;
                Properties advProps = null;
                
                for (int i = 0; i < conns.length; i++) {
                    DatabaseConnection conn = conns[i];
                    handler.ignorableWhitespace(indent_1.toCharArray(), 0, indent_1.length());
                    
                    atts.addAttribute(nsu, STORE_PASSWORD, STORE_PASSWORD, CDDATA,
                                            Boolean.toString(conns[i].isPasswordStored()));
                    
                    handler.startElement(nsu, CONNECTION, CONNECTION, atts);
                    atts.removeAttribute(atts.getIndex(STORE_PASSWORD));
                    
                    writeXML(NAME, conn.getName(), indent_2);
                    writeXML(USER, conn.getUserName(), indent_2);

                    atts.addAttribute(nsu, ENCRYPTED, ENCRYPTED, CDDATA,
                                      Boolean.toString(conn.isPasswordEncrypted()));

                    if (conn.isPasswordStored()) {
                        writeXML(PASSWORD, conn.getPassword(), indent_2);
                    } else {
                        writeXML(PASSWORD, Constants.EMPTY, indent_2);
                    }
                    atts.removeAttribute(atts.getIndex(ENCRYPTED));
                    
                    writeXML(HOST, conn.getHost(), indent_2);
                    writeXML(DATA_SOURCE, conn.getSourceName(), indent_2);
                    writeXML(PORT, conn.getPort(), indent_2);
                    writeXML(URL, conn.getURL(), indent_2);
                    // TODO: remove driver name from save
                    writeXML(DRIVER_NAME, conn.getDriverName(), indent_2);
                    
                    writeXML(DRIVER_ID, Long.toString(conn.getDriverId()), indent_2);
                    
                    writeXML(AUTO_COMMIT, 
                            Boolean.toString(conn.isAutoCommit()), indent_2);
                    writeXML(TX_ISOLATION, 
                            Integer.toString(conn.getTransactionIsolation()), indent_2);
                    
                    if (conn.hasAdvancedProperties()) {
                        handler.ignorableWhitespace(indent_2.toCharArray(), 0, indent_2.length());
                        handler.startElement(nsu, ADVANCED, ADVANCED, atts);
                        
                        advProps = conn.getJdbcProperties();
                        
                        for (Enumeration j = advProps.propertyNames(); 
                               j.hasMoreElements();) {
                            marker = (String)j.nextElement();
                            atts.addAttribute(Constants.EMPTY, KEY, KEY, CDDATA, marker);
                            atts.addAttribute(Constants.EMPTY, VALUE, VALUE, CDDATA,
                            advProps.getProperty(marker));
                            writeXML(PROPERTY, null, indent_3);
                            atts.removeAttribute(atts.getIndex(KEY));
                            atts.removeAttribute(atts.getIndex(VALUE));
                        } 
                        
                        handler.ignorableWhitespace(indent_2.toCharArray(), 0, indent_2.length());
                        handler.endElement(nsu, ADVANCED, ADVANCED);
                    } else {
                        writeXML(ADVANCED, null, indent_2);
                    }
                    
                    handler.ignorableWhitespace(indent_1.toCharArray(), 0, indent_1.length());
                    handler.endElement(nsu, CONNECTION, CONNECTION);
                    handler.ignorableWhitespace(newLine, 0, 1);
                    
                    //conn.setNewConnection(false);
                    marker = null;
                } 
                
                handler.ignorableWhitespace(newLine, 0, 1);
                handler.endElement(nsu, SAVED_CONNECTIONS, SAVED_CONNECTIONS);
                handler.endDocument();
                
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        
        private void writeXML(String name, String line, String space) throws SAXException {
            if (line == null) {
                line = Constants.EMPTY;
            }
            
            int textLength = line.length();
            handler.ignorableWhitespace(space.toCharArray(), 0, space.length());
            handler.startElement(nsu, name, name, atts);
            handler.characters(line.toCharArray(), 0, textLength);
            handler.endElement(nsu, name, name);
        }
        
        public void setContentHandler(ContentHandler handler) {
            this.handler = handler;
        }
        
        public ContentHandler getContentHandler() {
            return this.handler;
        }
        
        public void setErrorHandler(ErrorHandler handler) {}
        
        public ErrorHandler getErrorHandler() {
            return null;
        }
        
        public void parse(String systemId) throws IOException, SAXException {
        }
        
        public DTDHandler getDTDHandler() {
            return null;
        }
        
        public EntityResolver getEntityResolver() {
            return null;
        }
        
        public void setEntityResolver(EntityResolver resolver) {}
        
        public void setDTDHandler(DTDHandler handler) {}
        
        public Object getProperty(String name) {
            return null;
        }
        
        public void setProperty(String name, java.lang.Object value) {}
        
        public void setFeature(String name, boolean value) {}
        
        public boolean getFeature(String name) {
            return false;
        }
    } // class ConnectionParser
    
}








