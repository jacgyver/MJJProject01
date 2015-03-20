/*
 * ConnectionProgressDialog.java
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


package org.executequery.databasemediators;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.executequery.Constants;

import org.executequery.GUIUtilities;
import org.executequery.util.Log;
import org.underworldlabs.swing.IndeterminateProgressBar;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/07/15 11:48:00 $
 */
public class ConnectionProgressDialog extends JDialog
                                      implements Runnable,
                                                 ActionListener {
    
    /** The connection event parent to this object */
    private ConnectionProcess connectonEvent;

    /** The progress bar widget */
    private IndeterminateProgressBar progressBar;
    
    public ConnectionProgressDialog(ConnectionProcess _connectonEvent) {
        super(GUIUtilities.getParentFrame(), "Connecting...", true);        
        connectonEvent = _connectonEvent;
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        progressBar.start();
        setVisible(true);
    }
    
    private void jbInit() throws Exception {
        
        progressBar = new IndeterminateProgressBar();
        progressBar.setPreferredSize(new Dimension(260, 18));

        JPanel base = new JPanel(new GridBagLayout());
        
        JButton cancelButton = new CancelButton();
        cancelButton.addActionListener(this);

        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(10, 20, 10, 20);
        gbc.insets = ins;
        base.add(new JLabel("Establishing connection to " +
                                connectonEvent.getConnectionName()), gbc);
        gbc.gridy = 1;
        gbc.insets.top = 0;
        base.add(progressBar, gbc);
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.insets.left = 10;
        gbc.insets.right = 10;
        base.add(cancelButton, gbc);
        
        base.setBorder(BorderFactory.createEtchedBorder());
        
        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(base, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                           GridBagConstraints.SOUTHEAST, 
                                           GridBagConstraints.BOTH,
                                           new Insets(5, 5, 5, 5), 0, 0));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        pack();
        setLocation(GUIUtilities.getLocationForDialog(getSize()));
    }

    public void actionPerformed(ActionEvent e) {    
        Log.info("Connection cancelled");
        connectonEvent.setCancelled(true);
        connectonEvent.interrupt();
        dispose();
    }
    
    public void dispose() {
        if (progressBar != null) {
            progressBar.stop();
            progressBar.cleanup();
        }
        super.dispose();        
    }

    
    class CancelButton extends JButton {
        
        private int DEFAULT_WIDTH = 75;
        private int DEFAULT_HEIGHT = 30;
        
        public CancelButton() {
            super("Cancel");
            setMargin(Constants.EMPTY_INSETS);
        }
        
        public int getWidth() {
            int width = super.getWidth();
            if (width < DEFAULT_WIDTH) {
                return DEFAULT_WIDTH;
            }
            return width;
        }
        
        public int getHeight() {
            int height = super.getHeight();
            if (height < DEFAULT_HEIGHT) {
                return DEFAULT_HEIGHT;
            }
            return height;
        }
        
        public Dimension getPreferredSize() {
            return new Dimension(getWidth(), getHeight());
        }
    }
    
}



