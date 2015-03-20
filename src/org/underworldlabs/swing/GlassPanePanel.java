/*
 * GlassPanePanel.java
 *
 * Created on 6 June 2006, 12:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.underworldlabs.swing;

import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 * Empty non-opaque panel to be used as a glass pane for event capture.
 * 
 * @author   Takis Diakoumis
 * @version  $Revision: 1.2 $
 * @date     $Date: 2006/06/07 16:01:18 $
 */
public class GlassPanePanel extends JPanel
                            implements MouseInputListener {
    
    /** Creates a new instance of GlassPanePanel */
    public GlassPanePanel() {
        setVisible(false);
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
 
    /**
     * Override to return false.
     */
    public boolean isOpaque() {
        return false;
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged.
     */
    public void mouseDragged(MouseEvent e) {}

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {}

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {}

}
