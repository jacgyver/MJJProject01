/*
 * ConnectionsComboBox.java
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


package org.executequery.components;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.datasource.ConnectionManager;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Combo box pre-populated with database connection objects.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:53 $
 */
public class ConnectionsComboBox extends JComboBox {
    
    /** the selection model */
    private ConnectionSelectionModel model;
    
    /** Creates a new instance of ConnectionsComboBox */
    public ConnectionsComboBox() {
        model = new ConnectionSelectionModel();
        setModel(model);
    }
    
    public DatabaseConnection getSelectedConnection() {
        return (DatabaseConnection)model.getSelectedItem();
    }
    
    private class ConnectionSelectionModel extends DefaultComboBoxModel {
        
        /** the selected item */
        private DatabaseConnection selectedItem;
        
        /** the database connections vector */
        private Vector<DatabaseConnection> connections;
        
        public ConnectionSelectionModel() {
            connections = ConnectionManager.getActiveConnections();
        }
        
        public void addElement(Object object) {
            connections.add((DatabaseConnection)object);
            int index = connections.indexOf((DatabaseConnection)object);
            fireContentsChanged(this, index, index);
        }
        
        public void setSelectedItem(Object object) {
            selectedItem = (DatabaseConnection)object;
        }
        
        public void removeAllElements() {
            connections.clear();
            selectedItem = null;
        }
        
        public void removeElement(Object object) {
            connections.remove((DatabaseConnection)object);
        }
        
        public Object getSelectedItem() {
            return selectedItem;
        }
        
        public int getIndexOf(Object object) {
            return connections.indexOf((DatabaseConnection)object);
        }
        
        public int getSize() {
            return connections.size();
        }
    }
    
}



