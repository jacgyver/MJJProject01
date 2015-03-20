/*
 * JDBCProperties.java
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
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

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
import org.executequery.datasource.ConnectionDataSource;
import org.underworldlabs.util.FileUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.8 $
 * @date     $Date: 2006/06/28 08:35:55 $
 */
public class JDBCProperties {
    
    /** the default ODBC driver ID */
    public static final long DEFAULT_ODBC_ID = 9999999999999l;
    
    /** whether we need to resave the drivers - format changes */
    private static boolean resaveDrivers;
    
    /** Saved drivers collection */
    private static Vector<DatabaseDriver> drivers;
    
    private JDBCProperties() {}
    
    public static DatabaseDriver getDatabaseDriver(String name) {
        DatabaseDriver dd = null;
        for (int i = 0, k = drivers.size(); i < k; i++) {
            if (name.equals(drivers.elementAt(i).toString())) {
                dd = drivers.elementAt(i);
                break;
            }            
        } 
        return dd;
    }

    public static DatabaseDriver getDatabaseDriver(long id) {
        for (int i = 0, k = drivers.size(); i < k; i++) {
            DatabaseDriver driver = drivers.get(i);
            if (id == driver.getId()) {
                return driver;
            }            
        } 
        return null;
    }

    
    public static Vector<DatabaseDriver> getDriversVector() {
        return drivers;
    }
    
    public static DatabaseDriver[] getDriversArray() {
        DatabaseDriver[] dda = new DatabaseDriver[drivers.size()];
        for (int i = 0; i < drivers.size(); i++) {
            dda[i] = drivers.get(i);
        } 
        return dda;
    }
    
    /**
     * Returns whether the name specified exists within the saved 
     * connections. the connection object specified allows for it 
     * to be removed from consideration (checking against itself).
     * 
     * @param the connection to exclude
     * @param the name to validated
     */
    public static boolean nameExists(DatabaseDriver dd, String name) {
        if (name == null) {
            name = dd.getName();
        }
        for (int i = 0, k = drivers.size(); i < k; i++) {
            DatabaseDriver _dd = drivers.get(i);
            String _name = _dd.getName();
            if (_dd != dd && _name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /** 
     * Saves the drivers to file.
     *
     * @return 1 if saved ok, 0 otherwise
     */
    public static synchronized int saveDrivers() throws ValidationException {
        // validate the names are unique
        for (int i = 0, k = drivers.size(); i < k; i++) {
            DatabaseDriver dd = drivers.get(i);
            for (int j = 0; j < k; j++) {
                DatabaseDriver _dc = drivers.get(j);
                if (nameExists(dd, null)) {
                    throw new ValidationException(
                            "The driver name " + dd.getName() +
                            " already exists.");
                }
            }
        }
        
        OutputStream os = null;
        try {
            
            // ---------------------------------------
            // ----- Save the drivers to file ----
            // ---------------------------------------
            
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DriverParser parser = new DriverParser();

            File connXML = new File(SystemUtilities.getUserPropertiesPath() +
                                    "jdbcdrivers.xml");
            
            os = new FileOutputStream(connXML);
            DatabaseDriver[] dda = getDriversArray();
            SAXSource source = new SAXSource(parser, new DriverInputSource(dda));
            StreamResult r = new StreamResult(os);
            
            // transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "savedconnections.dtd");
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

    public static synchronized int saveDrivers(DatabaseDriver[] dda) {
        OutputStream os = null;
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DriverParser cp = new DriverParser();
            
            File connXML = new File(SystemUtilities.getUserPropertiesPath() +
                                    "jdbcdrivers.xml");
            
            os = new FileOutputStream(connXML);
            SAXSource source = new SAXSource(cp, new DriverInputSource(dda));
            StreamResult r = new StreamResult(os);
            transformer.transform(source, r);
            
            drivers.clear();
            for (int i = 0; i < dda.length; i++) {
                drivers.add(dda[i]);
            }
            
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
     * Loads the drivers from file and stores them within collection.
     */
    public static synchronized void loadDrivers() {
        String confPath = SystemUtilities.getUserPropertiesPath();
        String from = "org/executequery/jdbcdrivers-default.xml";
        String to = confPath + "jdbcdrivers.xml";
        File file = new File(to);
        
        //Log.info("Initialising JDBC drivers.");
        
        if (file.exists()) {            
            InputStream in = null;
            
            try {
                drivers = new Vector<DatabaseDriver>();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);

                SAXParser parser = factory.newSAXParser();
                XMLDriverHandler handler = new XMLDriverHandler();
                
                in = new FileInputStream(file);
                parser.parse(in, handler);
                
                // check if we need to save here
                if (resaveDrivers) {
                    saveDrivers();
                }
                
            }             
            catch (Exception e) {
                e.printStackTrace();
                
                StringBuffer sb = new StringBuffer("Error opening JDBC driver definitions.\n");
                sb.append("The file jdbcdrivers.xml is damaged and will be\nrenamed to ").
                   append("jdbcdrivers.xml.old. A new empty file\nwill be created in ").
                   append("its place.");
                GUIUtilities.displayErrorMessage(sb.toString());
                
                if (file != null && file.exists()) {
                    file.renameTo(new File(confPath + "jdbcdrivers.xml.old"));
                }

                recreateDefinitionsFile(from, to);
                loadDrivers();
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
            // copy the default file
            recreateDefinitionsFile(from, to);
            // reload the drivers
            loadDrivers();
        }
        
    }
    
    private static void recreateDefinitionsFile(String from, String to) {
        try {
            FileUtils.copyResource(from, to);
        } catch (IOException ioExc) {
            GUIUtilities.displayErrorMessage(
                    "An error occurred recreating the driver definitions.");
        }
    }

    private static int idCount = 1;
    
    static class XMLDriverHandler extends DefaultHandler {
        
        private DatabaseDriver dd = new DatabaseDriver();
        private CharArrayWriter contents = new CharArrayWriter();

        public XMLDriverHandler() {}
        
        public void startElement(String nameSpaceURI, String localName,
                                 String qName, Attributes attrs) {
            contents.reset();
        }
        
        public void endElement(String nameSpaceURI, String localName,
                               String qName) {
            if (dd == null) {
                dd = new DatabaseDriver();
            }

            if (localName.equals("id")) {
                dd.setId(Long.parseLong(contents.toString()));
            } else if (localName.equals("name")) {
                dd.setName(contents.toString());
            } else if (localName.equals("description")) {
                dd.setDescription(contents.toString());
            } else if (localName.equals("type")) {
                dd.setDatabaseType(Integer.parseInt(contents.toString()));
            } else if (localName.equals("path")) {
                dd.setPath(contents.toString());
            } else if (localName.equals("classname")) {
                dd.setClassName(contents.toString());
                
                // check the default id is set against the ODBC driver
                if ("sun.jdbc.odbc.JdbcOdbcDriver".equals(dd.getClassName()) &&
                        dd.getId() != DEFAULT_ODBC_ID) {
                    dd.setId(DEFAULT_ODBC_ID);
                    resaveDrivers = true;
                }
                
            } else if (localName.equals("url")) {
                // replace the old format of [server] etc with the new
                String url = contents.toString();
                if (url.contains("[server]")) {
                    resaveDrivers = true;
                    url = url.replaceAll("\\[server\\]", ConnectionDataSource.HOST);
                }
                /*
                if (url.contains("[port]")) {
                    resaveDrivers = true;
                    url = url.replaceAll("\\[port\\]", ConnectionDataSource.PORT);
                }
                if (url.contains("[source]")) {
                    resaveDrivers = true;
                    url = url.replaceAll("\\[source\\]", ConnectionDataSource.SOURCE);
                }*/
                dd.setURL(url);
            }
            else if (localName.equals("databasedrivers")) {
                if (dd.getId() == 0) {
                    resaveDrivers = true;
                    dd.setId(System.currentTimeMillis() + idCount);
                    idCount++;
                }
                drivers.add(dd);
                dd = null;                
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
    
    static class DriverParser implements XMLReader {
        private String nsu = "";
        private Attributes atts = new AttributesImpl();
        
        private static String rootElement = "jdbcdrivers";
        private static String DATABASE_DRIVERS = "databasedrivers";
        private static String NAME = "name";
        private static String DESCRIPTION = "description";
        private static String TYPE = "type";
        private static String PATH = "path";
        private static String CLASS_NAME = "classname";
        private static String URL = "url";
        private static String ID = "id";
        
        private ContentHandler handler;
        
        private static char[] newLine = {'\n'};
        private static String indent_1 = "\n   ";
        private static String indent_2 = "\n      ";
        
        public DriverParser() {}
        
        public void parse(InputSource input) throws SAXException, IOException {
            if (!(input instanceof DriverInputSource))
                throw new SAXException("Parser can only accept a DriverInputSource");
            
            parse((DriverInputSource)input);
        }
        
        public void parse(DriverInputSource input) throws IOException, SAXException {
            try {
                if (handler == null) {
                    throw new SAXException("No content handler");
                }
                
                DatabaseDriver[] dd = input.getDrivers();
                
                handler.startDocument();
                handler.startElement(nsu, rootElement, rootElement, atts);
                handler.ignorableWhitespace(newLine, 0, 1);
                
                String marker = null;
                
                for (int i = 0; i < dd.length; i++) {
                    handler.ignorableWhitespace(indent_1.toCharArray(), 0, indent_1.length());
                    handler.startElement(nsu, DATABASE_DRIVERS, DATABASE_DRIVERS, atts);
                    
                    if (dd[i].getId() == 0) {
                        dd[i].setId(System.currentTimeMillis() + idCount);
                        idCount++;
                    }
                    
                    writeXML(ID, Long.toString(dd[i].getId()), indent_2);
                    writeXML(NAME, dd[i].getName(), indent_2);
                    writeXML(DESCRIPTION, dd[i].getDescription(), indent_2);
                    writeXML(TYPE, Integer.toString(dd[i].getType()), indent_2);
                    writeXML(PATH, dd[i].getPath(), indent_2);
                    writeXML(CLASS_NAME, dd[i].getClassName(), indent_2);
                    writeXML(URL, dd[i].getURL(), indent_2);
                    
                    handler.ignorableWhitespace(indent_1.toCharArray(), 0, indent_1.length());
                    handler.endElement(nsu, DATABASE_DRIVERS, DATABASE_DRIVERS);
                    handler.ignorableWhitespace(newLine, 0, 1);
                }
                
                handler.ignorableWhitespace(newLine, 0, 1);
                handler.endElement(nsu, rootElement, rootElement);
                handler.endDocument();
                
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        
        private void writeXML(String name, String line, String space)
            throws SAXException {
            
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
    } // class DriverParser
    
    static class DriverInputSource extends InputSource {
        private DatabaseDriver[] dda;
        
        public DriverInputSource(DatabaseDriver[] dd) {
            dda = dd;
        }
        
        public DatabaseDriver[] getDrivers() {
            return dda;
        }
        
    } // class DriverInputSource
    
    
}



