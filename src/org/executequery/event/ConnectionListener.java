/*
 * ConnectionListener.java
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


package org.executequery.event;

import org.executequery.databasemediators.*;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Connection listener interface.
 * Implementing classes will be notified of connection
 * creation and disconnection events.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public interface ConnectionListener {
    
    public static final String CONNECTED = "connected";
    
    public static final String DISCONNECTED = "disconnected";
    
    /**
     * Indicates a connection has been established.
     * 
     * @param the encapsulating event
     */
    public void connected(ConnectionEvent connectionEvent);

    /**
     * Indicates a connection has been closed.
     * 
     * @param the encapsulating event
     */
    public void disconnected(ConnectionEvent connectionEvent);
    
}



