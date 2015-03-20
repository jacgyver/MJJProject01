/*
 * DynamicLibraryLoader.java
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


package org.underworldlabs.util;

import java.io.IOException;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.Enumeration;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:07 $
 */
public class DynamicLibraryLoader extends URLClassLoader {
    
    private ClassLoader parent = null;

    public DynamicLibraryLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader());
        parent = ClassLoader.getSystemClassLoader();
    }
    
    public Class loadLibrary(String clazz)
      throws ClassNotFoundException {
        return loadClass(clazz, true);
    }
    
    public Class loadLibrary(String clazz, boolean resolve)
      throws ClassNotFoundException {
        return loadClass(clazz, resolve);
    }
    
    protected synchronized Class loadClass(String classname, boolean resolve)
      throws ClassNotFoundException {

        Class theClass = findLoadedClass(classname);

        if (theClass != null) {
            return theClass;
        }
        
        try {
            theClass = findBaseClass(classname);
        } 
        catch (ClassNotFoundException cnfe) {
            theClass = findClass(classname);
        } 
        
        if (resolve) {
            resolveClass(theClass);
        }

        return theClass;
        
    }
    
    private Class findBaseClass(String name) throws ClassNotFoundException {

        if (parent == null) {
            return findSystemClass(name);
        } else {
            return parent.loadClass(name);
        }
        
    }
    
    
}




