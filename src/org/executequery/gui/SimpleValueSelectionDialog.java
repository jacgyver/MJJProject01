/*
 * SimpleValueSelectionDialog.java
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


package org.executequery.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.executequery.GUIUtilities;

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
public class SimpleValueSelectionDialog {
   
    protected String title;
    protected String value;
    protected String[] values;
    
    protected boolean isCancelled;
    
    /** Creates a new instance of SimpleValueSelectionDialog */
    public SimpleValueSelectionDialog(String title, String[] values) {
        this.title = title;
        this.values = values;
    }
    
    public int showDialog() {
        SelectionDialog dialog = new SelectionDialog(title, values);
        dialog.pack();
        dialog.setLocation(GUIUtilities.getLocationForDialog(dialog.getSize()));
        dialog.setVisible(true);
        return isCancelled ? JOptionPane.CANCEL_OPTION : JOptionPane.OK_OPTION;
    }

    public String getValue() {        
        return value;
    }
    
    class SelectionDialog extends JDialog
                          implements ActionListener,
                                     MouseListener,
                                     KeyListener,
                                     WindowListener {
        
        private JList list;
        
        public SelectionDialog(String title, String[] values) {
            super(GUIUtilities.getParentFrame(), title, true);

            JButton cancel = new JButton("Cancel");
            JButton ok = new JButton("OK");

            Dimension btnDim = new Dimension(70,25);
            cancel.setPreferredSize(btnDim);
            ok.setPreferredSize(btnDim);

            Insets btnInsets = new Insets(0,0,0,0);
            cancel.setMargin(btnInsets);
            ok.setMargin(btnInsets);

            cancel.addActionListener(this);
            ok.addActionListener(this);

            list = new JList(values);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            list.addMouseListener(this);
            list.addKeyListener(this);
            this.addWindowListener(this);

            list.setPreferredSize(new Dimension(400, 100));

            JPanel base = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5,5,5,5);
            gbc.weighty = 1.0;
            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridy++;
            base.add(new JScrollPane(list), gbc);
            gbc.gridy++;
            gbc.insets.top = 0;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridwidth = 1;
            gbc.weighty = 0;
            gbc.insets.left = 0;
            base.add(ok, gbc);
            gbc.weightx = 0;
            gbc.gridx = 1;
            base.add(cancel, gbc);

            base.setBorder(BorderFactory.createEtchedBorder());
            Container c = this.getContentPane();
            c.setLayout(new GridBagLayout());
            c.add(base, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                               GridBagConstraints.SOUTHEAST, 
                                               GridBagConstraints.BOTH,
                                               new Insets(5, 5, 5, 5), 0, 0));
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setResizable(false);
        }
        
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                selectionMade();
                dispose();
            }
        }
        public void mouseEntered(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                selectionMade();
                dispose();
            }
        }
        public void keyReleased(KeyEvent e) {}
        public void keyTyped(KeyEvent e) {}

        public void windowClosing(WindowEvent e) {
            isCancelled = true;
        }
        public void windowActivated(WindowEvent e) {}
        public void windowClosed(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowOpened(WindowEvent e) {}

        private void selectionMade() {
            Object object = list.getSelectedValue();
            
            if (object != null) {
                value = object.toString();
            }

        }
        
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Cancel")) {
                value = null;
                isCancelled = true;
            }
            else {
                selectionMade();
            }
            dispose();
        }
        
    } // class SelectionDialog

}



