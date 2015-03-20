/*
 * DynamicComboBoxModel.java
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


package org.underworldlabs.swing;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Simple combo box model that allows complete removal 
 * and resetting of all values.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class DynamicComboBoxModel extends DefaultComboBoxModel {
    
    /** the selected object */
    private Object selectedObject;
    
    /** the items vector */
    private Vector objects;
    
    /** Creates a new instance of DynamicComboBoxModel */
    public DynamicComboBoxModel() {}
    
    /** Creates a new instance of DynamicComboBoxModel */
    public DynamicComboBoxModel(Vector objects) {
        this.objects = objects;
        if (objects != null && !objects.isEmpty()) {
            setSelectedItem(objects.elementAt(0));
        }
    }
    
    public void setElements(Object[] items) {
        if (objects != null && !objects.isEmpty()) {
            removeAllElements();
            /*
            int size = objects.size();
            objects.clear();
            fireIntervalRemoved(this, 0, size - 1);
             */
        }
        else {
            objects = new Vector(items.length);
        }
        for (int i = 0; i < items.length; i++) {
            objects.add(items[i]);
        }
        int size = objects.size();
        fireContentsChanged(this, 0, (size > 0) ? size - 1 : 0);
        setSelectedItem(objects.get(0));
    }
    
    public void setElements(Vector elements) {
        if (objects != null && !objects.isEmpty()) {
            removeAllElements();
            /*
            int size = objects.size();
            objects.clear();
            if (size > 0) {
                fireIntervalRemoved(this, 0, size - 1);
            } else {
                fireIntervalRemoved(this, 0, 0);
            }
             */
        }
        objects = elements;
        int size = objects.size();
        //fireContentsChanged(this, 0, (size > 0) ? size - 1 : 0);
        fireContentsChanged(this, -1, -1);
        if (size > 0) {
            setSelectedItem(objects.get(0));
        }
    }
    
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
    public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals(anObject)) ||
                selectedObject == null && anObject != null) {
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
        }
    }
    
    // implements javax.swing.ComboBoxModel
    public Object getSelectedItem() {
        return selectedObject;
    }
    
    // implements javax.swing.ListModel
    public int getSize() {
        if (objects == null) {
            return 0;
        }
        return objects.size();
    }
    
    // implements javax.swing.ListModel
    public Object getElementAt(int index) {
        if (index >= 0 && index < objects.size())
            return objects.elementAt(index);
        else
            return null;
    }
    
    /**
     * Returns the index-position of the specified object in the list.
     *
     * @param anObject
     * @return an int representing the index position, where 0 is
     *         the first position
     */
    public int getIndexOf(Object anObject) {
        return objects.indexOf(anObject);
    }
    

    // implements javax.swing.MutableComboBoxModel
    public void addElement(Object anObject) {
        if (objects == null) {
            objects = new Vector();
        }
        objects.addElement(anObject);
        fireIntervalAdded(this,objects.size()-1, objects.size()-1);
        if (objects.size() == 1 && selectedObject == null && anObject != null) {
            setSelectedItem(anObject);
        }
    }
    
    public void contentsChanged() {
        fireContentsChanged(this,-1, -1);
    }

    // implements javax.swing.MutableComboBoxModel
    public void insertElementAt(Object anObject,int index) {
        objects.insertElementAt(anObject,index);
        fireIntervalAdded(this, index, index);
    }
    
    // implements javax.swing.MutableComboBoxModel
    public void removeElementAt(int index) {
        if (getElementAt(index) == selectedObject) {
            if (index == 0) {
                setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
            } else {
                setSelectedItem(getElementAt(index - 1));
            }
        }
        
        objects.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
    }
    
    // implements javax.swing.MutableComboBoxModel
    public void removeElement(Object anObject) {
        int index = objects.indexOf(anObject);
        if (index != -1) {
            removeElementAt(index);
        }
    }
    
    /**
     * Empties the list.
     */
    public void removeAllElements() {
        if (objects == null) {
            return;
        }
        if (objects.size() > 0) {
            int firstIndex = 0;
            int lastIndex = objects.size() - 1;
            objects.removeAllElements();
            selectedObject = null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
        } else {
            selectedObject = null;
        }
    }

    
/*
    public void addElement(Object object) {
        items.add(object);
        int index = items.indexOf(object);
        fireContentsChanged(this, index, index);
    }
     
    public void removeAllElements() {
        items.clear();
    }
     
    public Object getElementAt(int index) {
        if (index >= 0 && index < items.size()) {
            return items.elementAt(index);
        } else {
            return null;
        }
    }
     
    public void removeElement(Object object) {
        items.remove(object);
    }
     
    public int getIndexOf(Object object) {
        return items.indexOf(object);
    }
     
    public int getSize() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }
     */
}



