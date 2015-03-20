/*
 * GlassPaneSelectionListener.java
 *
 * Created on 15 June 2006, 02:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.underworldlabs.swing;

import java.awt.event.MouseEvent;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.1 $
 * @date     $Date: 2006/06/14 16:49:42 $
 */
public interface GlassPaneSelectionListener {

    /**
     * Indicates that the pane has been acted upon in some
     * way - usually any type of mouse event.
     */
    public void glassPaneSelected(MouseEvent e);
    
}
