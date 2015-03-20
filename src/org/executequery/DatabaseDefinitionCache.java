/*
 * DatabaseDefinitionCache.java
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.executequery.datasource.DatabaseDefinition;
import org.underworldlabs.swing.actions.ActionBuilder;
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
 * Database definition loader and cache.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/06/28 08:35:55 $
 */
public class DatabaseDefinitionCache {

    public static final int INVALID_DATABASE_ID = -1;
    
    /** database definition cache */
    private static List<DatabaseDefinition> databaseDefinitions;
    
    private DatabaseDefinitionCache() {}

    public static DatabaseDefinition getDatabaseDefinition(int id) {
        if (databaseDefinitions == null) {
            load();
        }
        for (int i = 0, n = databaseDefinitions.size(); i < n; i++) {
            DatabaseDefinition dd = databaseDefinitions.get(i);
            if (dd.getId() == id) {
                return dd;
            }
        }
        return null;
    }

    public static DatabaseDefinition getDatabaseDefinitionAt(int index) {
        if (databaseDefinitions == null) {
            load();
        }
        return databaseDefinitions.get(index);
    }
    
    /**
     * Returns the database definitions within a collection.
     */
    public static List<DatabaseDefinition> getDatabaseDefinitions() {
        if (databaseDefinitions == null) {
            load();
        }
        return databaseDefinitions;
    }
    
    /**
     * Loads the definitions from file.
     */
    public static synchronized void load() {
        InputStream input = null;
        ClassLoader cl = ActionBuilder.class.getClassLoader();
        
        String path = "org/executequery/databases.xml";
        if (cl != null) {
            input = cl.getResourceAsStream(path);
        }
        else {
            input = ClassLoader.getSystemResourceAsStream(path);
        }

        databaseDefinitions = new ArrayList<DatabaseDefinition>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            
            SAXParser parser = factory.newSAXParser();
            DatabaseHandler handler = new DatabaseHandler();
            parser.parse(input, handler);
        } 
        catch (Exception e) {
            e.printStackTrace();
            throw new InternalError();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {}
            }
        }

    }

    static class DatabaseHandler extends DefaultHandler {
        
        private DatabaseDefinition database = new DatabaseDefinition();
        private CharArrayWriter contents = new CharArrayWriter();

        public DatabaseHandler() {}
        
        public void startElement(String nameSpaceURI, String localName,
                                 String qName, Attributes attrs) {           
            contents.reset();
            if (localName.equals("database")) {
                database = new DatabaseDefinition();
            }            
        }
        
        public void endElement(String nameSpaceURI, String localName,
                               String qName) {
            if (localName.equals("id")) {
                database.setId(Integer.parseInt(contents.toString()));
            } 
            else if (localName.equals("name")) {
                database.setName(contents.toString());
            } 
            else if (localName.equals("url")) {
                database.addUrlPattern(contents.toString());
            }
            else if (localName.equals("database")) {
                databaseDefinitions.add(database);
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
    } // DatabaseHandler
    
}



