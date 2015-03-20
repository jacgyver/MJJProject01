/*
 * EventMediator.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Event controller class.
 * Global application events are registered and mediated through this class.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:53 $
 */
public class EventMediator {
    
    public static final int CONNECTION_EVENT = 0;
    
    public static final int KEYWORD_EVENT = 1;
    
    private static List<ConnectionListener> connectionListeners;
    
    private static List<KeywordListener> keywordListeners;

    /*
    public static void fireConnectionEvent(ConnectionEvent event, String method) {
        try {
            Object[] arguments = new Object[]{event};
            Class[] argumentTypes = new Class[]{event.getClass()};
            for (int i = 0, k = connectionListeners.size(); i < k; i++) {
                ConnectionListener listener = connectionListeners.get(i);
                Method _method = listener.getClass().
                                    getMethod(method, argumentTypes);
                _method.invoke(listener, arguments);
            }
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    */

    public static void fireEvent(EventObject event, String method) {
        try {
            Object[] arguments = new Object[]{event};
            Class[] argumentTypes = new Class[]{event.getClass()};
            List list = null;
            
            // check the event type
            if (event instanceof ConnectionEvent) {
                list = connectionListeners;
            }
            else if (event instanceof KeywordEvent) {
                list = keywordListeners;
            }
            else {
                throw new IllegalArgumentException("Unknown event type");
            }
            
            for (int i = 0, k = list.size(); i < k; i++) {
                Object object = list.get(i);
                Method _method = object.getClass().getMethod(method, argumentTypes);
                _method.invoke(object, arguments);
            }
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void registerListener(int type, Object object) {
        switch (type) {
            case CONNECTION_EVENT:
                if (connectionListeners == null) {
                    connectionListeners = new ArrayList<ConnectionListener>();
                }
                connectionListeners.add((ConnectionListener)object);
                break;

            case KEYWORD_EVENT:
                if (keywordListeners == null) {
                    keywordListeners = new ArrayList<KeywordListener>();
                }
                keywordListeners.add((KeywordListener)object);
                break;
        }
    }
    
    public static void deregisterListener(int type, Object object) {
        switch (type) {
            case CONNECTION_EVENT:
                connectionListeners.remove((ConnectionListener)object);
                break;
            case KEYWORD_EVENT:
                keywordListeners.remove((KeywordListener)object);
                break;
        }
    }
    
    
    /** Prevent instantiation */
    private EventMediator() {}
    
    
    
}



