/*
 * PanelToolBar.java
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


package org.underworldlabs.swing.toolbar;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.swing.util.IconUtilities;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Tab component tool bar panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/09/06 09:30:38 $
 */
public class PanelToolBar extends AbstractToolBarPanel {
   
    /** Creates a new instance of PanelToolBar */
    public PanelToolBar() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
    }

    public JButton addButton(ActionListener action, String actionCommand,
                              String icon, String toolTip, boolean enabled) {
        JButton button = new RolloverButton();
        
        if (icon != null) {
            button.setIcon(IconUtilities.loadIcon(icon));
        }
        
        button.setText(null);
        button.setToolTipText(toolTip);
        button.setActionCommand(actionCommand);
        button.addActionListener(action);
        button.setEnabled(enabled);
        add(button);
        return button;
    }

    public JButton addButton(ActionListener action, String actionCommand,
                              String icon, String toolTip) {
        return addButton(action, actionCommand, icon, toolTip, true);
    }

    public void addSeparator() {
        add(new PanelToolBarSeparator());
    }

    public void addLabel(String text) {
        add(new JLabel(text));
    }

    public void addTextField(JTextField textField) {
        add(textField);
    }

    public void addComboBox(JComboBox comboBox) {
        add(comboBox);
    }

    public void addButton(JButton button) {
        add(button);
    }

    private class PanelToolBarSeparator extends JLabel {
        
        private int preferredWidth;

        public PanelToolBarSeparator() {
            this(4);
        }

        public PanelToolBarSeparator(int preferredWidth) {
            this.preferredWidth = preferredWidth;
        }
        
        public boolean isOpaque() {
            return !GUIUtils.isDefaultLookAndFeel();
        }
        
        public Dimension getPreferredSize() {
            return new Dimension(preferredWidth, 1);
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

    }
    
}




