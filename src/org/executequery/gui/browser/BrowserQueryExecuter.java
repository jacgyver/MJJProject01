/*
 * BrowserQueryExecuter.java
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


package org.executequery.gui.browser;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.executequery.GUIUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.QuerySender;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Performs SQL execution tasks from browser components.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:55 $
 */
public class BrowserQueryExecuter {
    
    public static final int UPDATE_CANCELLED = 99;
    
    /** query sender object */
    private QuerySender querySender;
    
    /** Creates a new instance of BorwserQueryExecuter */
    public BrowserQueryExecuter() {}
    
    /**
     * Drops the specified database object.
     *
     * @param dc - the database connection
     * @param object - the object to be dropped
     */
    public int dropObject(DatabaseConnection dc, DatabaseObject object) 
        throws SQLException {

        String queryStart = null;
        int type = object.getType();
        switch (type) {

            case BrowserConstants.CATALOG_NODE:
            case BrowserConstants.SCHEMA_NODE:
            case BrowserConstants.OTHER_NODE:
                GUIUtilities.displayErrorMessage(
                    "Dropping objects of this type is not currently supported");
                return UPDATE_CANCELLED;

            case BrowserConstants.FUNCTIONS_NODE:
                queryStart = "DROP FUNCTION ";
                break;

            case BrowserConstants.INDEX_NODE:
                queryStart = "DROP INDEX ";
                break;

            case BrowserConstants.PROCEDURE_NODE:
                queryStart = "DROP PROCEDURE ";
                break;

            case BrowserConstants.SEQUENCE_NODE:
                queryStart = "DROP SEQUENCE ";
                break;

            case BrowserConstants.SYNONYM_NODE:
                queryStart = "DROP SYNONYM ";
                break;

            case BrowserConstants.SYSTEM_TABLE_NODE:
            case BrowserConstants.TABLE_NODE:
                queryStart = "DROP TABLE ";
                break;

            case BrowserConstants.TRIGGER_NODE:
                queryStart = "DROP TRIGGER ";
                break;

            case BrowserConstants.VIEW_NODE:
                queryStart = "DROP VIEW ";
                break;

        }

        if (querySender == null) {
            querySender = new QuerySender(dc);
        } else {
            querySender.setDatabaseConnection(dc);
        }

        String name = object.getName();
        return querySender.updateRecords(queryStart + name).getUpdateCount();
    }

}



