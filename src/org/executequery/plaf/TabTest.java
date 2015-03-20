/*
 * TabTest.java
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


package org.executequery.plaf;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import org.underworldlabs.swing.CloseTabbedPane;

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
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class TabTest extends JFrame{
    
    /** Creates a new instance of TabTest */
    public TabTest() {
         super("Tab Test");

         try {
                javax.swing.plaf.metal.MetalLookAndFeel metal =
                        new javax.swing.plaf.metal.MetalLookAndFeel();
                metal.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
                UIManager.setLookAndFeel(metal);
         } catch (Exception e) {}

         addComponents();
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         setSize(450, 250);
         setVisible(true);
    }
    
    private void addComponents() {
//        CloseTabContainer container = new CloseTabContainer(JTabbedPane.TOP);
        CloseTabbedPane tabPane = new CloseTabbedPane(JTabbedPane.BOTTOM,
                                            JTabbedPane.SCROLL_TAB_LAYOUT);

        for (int i = 0; i < 6; i++) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.add(new JLabel("Panel " + i));
            tabPane.add("Panel " + i, panel);
        }

        JPanel base = new JPanel(new BorderLayout());
        base.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        base.add(tabPane, BorderLayout.CENTER);
        getContentPane().add(base, BorderLayout.CENTER);

    }

    public static void main(String args[]) {
        new TabTest();
    }
}



