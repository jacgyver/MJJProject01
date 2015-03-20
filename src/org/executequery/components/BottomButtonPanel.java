/*
 * BottomButtonPanel.java
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


package org.executequery.components;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import org.executequery.Constants;

import org.executequery.actions.othercommands.CancelCommand;
import org.underworldlabs.swing.actions.ActionBuilder;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Simple button panel with help, ok, cancel buttons.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class BottomButtonPanel extends JPanel {
    
    /** The 'OK' or similar button */
    private JButton okButton;
    
    /** The 'Cancel' or close button */
    private JButton cancelButton;
    
    /** The 'Help' button */
    private JButton helpButton;
    
    /** Indicates whether this is a dialog */
    private boolean isDialog;
    
    /**
     * Constructs a new instance.
     */
    public BottomButtonPanel(boolean isDialog) {
        super(new GridBagLayout());
        this.isDialog = isDialog;
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public BottomButtonPanel(ActionListener okListener, String okText,
                             String helpActionCommand) {
        this(okListener, okText, helpActionCommand, null, false);
    }

    public BottomButtonPanel(ActionListener okListener, String okText,
                             String helpActionCommand, boolean isDialog) {
        this(okListener, okText, helpActionCommand, null, isDialog);
    }

    public BottomButtonPanel(ActionListener okListener, String okText,
                             String helpActionCommand, String okActionCommand) {
        this(okListener, okText, helpActionCommand, okActionCommand, false);
    }

    public BottomButtonPanel(ActionListener okListener, 
                             String okText,
                             String helpActionCommand, 
                             String okActionCommand, 
                             boolean isDialog) {
        this(isDialog);
        
        if (okActionCommand != null) {
            okButton.setActionCommand(okActionCommand);
        }
        
        if (okListener != null) {
            okButton.addActionListener(okListener);
        }

        helpButton.setAction(ActionBuilder.get("help-command"));
        helpButton.setText("Help");
        helpButton.setActionCommand(helpActionCommand);
        helpButton.setIcon(null);
        
        if (okText != null) {
            okButton.setText(okText);
        }
        
    }

    public BottomButtonPanel(ActionListener okListener, 
                             String okText,
                             AbstractAction helpAction) {
        this(false);
        okButton.addActionListener(okListener);
        helpButton.setAction(helpAction);
        helpButton.setText("Help");

        if (okText != null) {
            okButton.setText(okText);
        }
    }
    
    public BottomButtonPanel(AbstractAction okAction, AbstractAction helpAction) {
        this(false);
        okButton.setAction(okAction);
        helpButton.setAction(helpAction);
        helpButton.setText("Help");
    }
    
    /**
     * Initializes the state of this instance.
     */
    private void jbInit() throws Exception {
        
        helpButton = new JButton("Help");
        okButton = new JButton("OK");
        cancelButton = new JButton(new CancelCommand(isDialog));
        
        helpButton.setPreferredSize(Constants.BUTTON_SIZE);
        okButton.setPreferredSize(Constants.BUTTON_SIZE);
        cancelButton.setPreferredSize(Constants.BUTTON_SIZE);
        
        Insets buttonInsets = new Insets(2, 2, 2, 2);
        cancelButton.setMargin(buttonInsets);
        okButton.setMargin(buttonInsets);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.top = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.5;
        add(helpButton, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        add(okButton, gbc);
        gbc.insets.left = 5;
        gbc.gridx = 2;
        add(cancelButton, gbc);
    }
    
    public void enableButtons(boolean enable) {
        okButton.setEnabled(enable);
        cancelButton.setEnabled(enable);
    }
    
    public void setOkButtonActionListener(ActionListener l) {
        okButton.addActionListener(l);
    }
    
    public void setCancelButtonText(String s) {
        cancelButton.setText(s);
    }
    
    public void setOkButtonText(String s) {
        okButton.setText(s);
    }
    
    public void setOkButtonAction(AbstractAction a) {
        okButton.setAction(a);
    }
    
    public void setHelpButtonActionListener(ActionListener l) {
        helpButton.addActionListener(l);
    }
    
    public void setHelpButtonAction(AbstractAction a) {
        helpButton.setAction(a);
    }
    
}








