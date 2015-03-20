
package org.executequery;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.UIManager;
import org.underworldlabs.swing.plaf.UIUtils;

/*
 * Constants.java
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


/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/14 06:56:53 $
 */
public interface Constants {

    public static final String ACTION_CONF_PATH = "org/executequery/actions.xml";
    
    //----------------------------
    // look and feel names
    //----------------------------

    public static final String[] LOOK_AND_FEELS = {"Execute Query Default",
                                                   "Smooth Gradient", 
                                                   "Bumpy Gradient", 
                                                   "Execute Query Theme", 
                                                   "Metal - Classic",
                                                   "Metal - Ocean (JDK1.5+)",
                                                   "CDE/Motif", 
                                                   "Windows", 
                                                   "GTK+", 
                                                   "Plugin"};

    public static final int EQ_DEFAULT_LAF = 0;
    public static final int SMOOTH_GRADIENT_LAF = 1;
    public static final int BUMPY_GRADIENT_LAF = 2;
    public static final int EQ_THM = 3;
    public static final int METAL_LAF = 4;
    public static final int OCEAN_LAF = 5;
    public static final int MOTIF_LAF = 6;
    public static final int WIN_LAF = 7;
    public static final int GTK_LAF = 8;
    public static final int PLUGIN_LAF = 9;
    
    //----------------------------
    // syntax colours and styles
    //----------------------------
    
    /** Recognised syntax types */
    public static final String[] SYNTAX_TYPES = {"normal", 
                                                 "keyword", 
                                                 "quote",
                                                 "singlecomment", 
                                                 "multicomment", 
                                                 "number",
                                                 "operator", 
                                                 "braces", 
                                                 "literal", 
                                                 "braces.match1",
                                                 "braces.error"};

    /** The properties file style name prefix */
    public static final String STYLE_NAME_PREFIX = "sqlsyntax.style.";
    
    /** The properties file style colour prefix */
    public static final String STYLE_COLOUR_PREFIX = "sqlsyntax.colour.";
    
    /** The literal 'Plain' */
    public static final String PLAIN = "Plain";
    /** The literal 'Italic' */
    public static final String ITALIC = "Italic";
    /** The literal 'Bold' */
    public static final String BOLD = "Bold";
    
    /** An empty string */
    public static final String EMPTY = "";
    
    public static final String NEW_LINE_STRING = "\n";
    public static final String QUOTE_STRING = "'";
    public static final char QUOTE_CHAR = '\'';
    public static final char NEW_LINE_CHAR = '\n';
    public static final char TAB_CHAR = '\t';
    public static final char COMMA_CHAR = ',';
    
    //-------------------------
    // literal SQL keywords
    //-------------------------
    public static final String NULL_LITERAL = "NULL";
    public static final String TRUE_LITERAL = "TRUE";
    public static final String FALSE_LITERAL = "FALSE";
    
    public static final char[] BRACES = {'(', ')', '{', '}', '[', ']'};
    
    public static final String COLOUR_PREFERENCE = "colourPreference";
    
    public static final int DEFAULT_FONT_SIZE = 11;
    public static final Dimension BUTTON_SIZE = new Dimension(75, 26);
    
    public static final String[] TRANSACTION_LEVELS = 
                                            {"TRANSACTION_NONE", 
                                             "TRANSACTION_READ_UNCOMMITTED",
                                             "TRANSACTION_READ_COMMITTED",
                                             "TRANSACTION_REPEATABLE_READ",
                                             "TRANSACTION_SERIALIZABLE"};

    // tool tip html tags
    public static final String TABLE_TAG_START = 
            "<table border='0' cellspacing='0' cellpadding='2'>";

    public static final String TABLE_TAG_END = 
            "</table>";

    public static final Insets EMPTY_INSETS = new Insets(0,0,0,0);

    public static final Dimension FORM_BUTTON_SIZE = new Dimension(100, 25);
    
    
    // Log4J logging levels
    public static final String[] LOG_LEVELS = {"INFO",
                                               "WARN",
                                               "DEBUG",
                                               "ERROR",
                                               "FATAL",
                                               "ALL"};

    
    /** docked tab property keys 
    public static final String[] DOCKED_TAB_KEYS = {
                            ConnectionsTreePanel.PROPERTY_KEY,
                            DriversTreePanel.PROPERTY_KEY,
                            KeywordsDockedPanel.PROPERTY_KEY,
                            SystemOutputPanel.PROPERTY_KEY};
    */
                                             
}



