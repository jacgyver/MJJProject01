/*
 * InterruptibleProcess.java
 *
 * Created on 6 June 2006, 01:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.underworldlabs.swing.util;

/**
 * Defines an interruptible (usually threaded) process.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.1 $
 * @date     $Date: 2006/06/05 15:50:20 $
 */
public interface InterruptibleProcess {
  
    /**
     * Sets the process cancel flag as specified.
     */
    public void setCancelled(boolean cancelled);
    
    /**
     * Indicates thatthis process should be interrupted.
     */
    public void interrupt();
    
}
