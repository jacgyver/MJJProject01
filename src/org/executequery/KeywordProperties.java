/*
 * KeywordProperties.java
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;
import org.executequery.gui.keywords.SqlKeyword;
import org.executequery.util.Log;
import org.underworldlabs.util.FileUtils;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Maintains key SQL keywords lists.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/05/14 06:56:53 $
 */
public class KeywordProperties {
    
    /** All SQL92 key words */
    private static List<String> sql92KeyWords;
    
    /** The user's added key words */
    private static List<String> userDefinedKeyWords;

    /** list based on keyword objects */
    private static List<SqlKeyword> keywordObjects;
    
    private KeywordProperties() {}

    /**
     * Retrieves a concatenated list of SQL keywords including
     * both the user defined and SQL92 keywords in alpha order.
     *
     * @return the complete list of keywords
     */
    public static List<String> getSQLKeywords() {
        int sql92Size = getSQL92().size();
        int userSize = getUserDefinedSQL().size();
        List<String> allWords = new ArrayList<String>(sql92Size + userSize);
        
        for (int i = 0; i < sql92Size; i++) {
            allWords.add(sql92KeyWords.get(i));
        } 
        
        for (int i = 0; i < userSize; i++) {
            allWords.add(userDefinedKeyWords.get(i));
        } 
        
        // sort in alpha
        Collections.sort(allWords);
        return allWords;
    }

    public static List<SqlKeyword> getSQLKeywordsObjects() {
        if (keywordObjects == null) {
            int sql92Size = getSQL92().size();
            int userSize = getUserDefinedSQL().size();
            keywordObjects = new ArrayList<SqlKeyword>(sql92Size + userSize);
            
            for (int i = 0; i < sql92Size; i++) {
                keywordObjects.add(
                        new SqlKeyword(sql92KeyWords.get(i), true, false, false));
            }
            
            for (int i = 0; i < userSize; i++) {
                keywordObjects.add(
                        new SqlKeyword(userDefinedKeyWords.get(i), false, false, true));
            }
            
        }
        return keywordObjects;
    }
    
    /**
     * Retrieves the SQL92 keyword list.
     *
     * @return the pre-defined SQL92 keyword list
     */
    public static List<String> getSQL92() {
        if (sql92KeyWords == null) {
            sql92KeyWords = loadSQL92();
        }
        return sql92KeyWords;
    }

    /**
     * Retrieves the user defined keyword list.
     *
     * @return the user SQL92 keyword list
     */
    public static List<String> getUserDefinedSQL() {
        if (userDefinedKeyWords == null) {
            userDefinedKeyWords = loadUserDefinedKeywords();
        }
        return userDefinedKeyWords;
    }

    /**
     * Saves the user defined keywords in the specified collection to file.
     *
     * @param keywords - the user defined keywords list
     */
    public static boolean setUserDefinedKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return true;
        }

        int oldSize = userDefinedKeyWords.size();
        int newSize = keywords.size();
        
        try {
            String delimeter = "|";
            StringBuffer sb = new StringBuffer();

            for (int i = 0, k = keywords.size(); i < k; i++) {
                sb.append(keywords.get(i));
                if (i != k - 1) {
                    sb.append(delimeter);
                }
            }
            String path = getUserDefinedKeywordsPath();
            FileUtils.writeFile(path, sb.toString());
            userDefinedKeyWords = keywords;
            
            // fire the event to registered listeners
            EventMediator.fireEvent(new KeywordEvent("KeywordProperties"), 
                                    KeywordListener.KEYWORDS_ADDED);
            return true;
        }        
        catch (IOException e) {
            e.printStackTrace();
            GUIUtilities.displayErrorMessage("Error saving keywords to file");
            return false;
        }
        
    }

    /**
     * Loads the SQL92 keywords from file.
     *
     * @return the list of keywords
     */
    private static List<String> loadSQL92() {
        try {
            String path = "org/executequery/sql.92.keywords";
            String values = FileUtils.loadResource(path);

            StringTokenizer st = new StringTokenizer(values, "|");
            List<String> list = new ArrayList<String>(st.countTokens());

            while (st.hasMoreTokens()) {
                list.add(st.nextToken().trim());
            }
            return list;
        }
        catch (IOException e) {
            e.printStackTrace();
            GUIUtilities.displayErrorMessage("Error retrieving SQL92 keyword list");
            return new ArrayList<String>(0);
        }
    }

    /**
     * Loads the user added keywords from file.
     *
     * @return the list of keywords
     */
    private static List<String> loadUserDefinedKeywords() {
        File file = new File(getUserDefinedKeywordsPath());

        if (file.exists()) {
            try {
                String values = FileUtils.loadFile(file, false);
                StringTokenizer st = new StringTokenizer(values, "|");
                List<String> list = new ArrayList<String>(st.countTokens());

                while (st.hasMoreTokens()) {
                    list.add(st.nextToken().trim());
                }
                
                return list;
            }            
            catch (IOException e) {
                e.printStackTrace();
                GUIUtilities.displayErrorMessage("Error opening user defined keywords");
                return new ArrayList<String>(0);
            }   
        }
        else {
            try {                
                Log.info("Creating file for user defined keywords list");
                boolean created = file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                GUIUtilities.displayErrorMessage(
                        "Error creating file for user defined keywords");
            }
            return new ArrayList<String>(0);
        }
        
    }

    /** 
     * Returns the path to the user keywords file.
     */
    private static String getUserDefinedKeywordsPath() {
        return SystemUtilities.getUserPropertiesPath() + "sql.user.keywords";
    }
    
}



