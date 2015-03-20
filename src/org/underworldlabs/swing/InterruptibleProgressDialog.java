/*
 * InterruptibleProgressDialog.java
 *
 * Created on 6 June 2006, 01:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.underworldlabs.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
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
import org.underworldlabs.Constants;
import org.underworldlabs.swing.util.InterruptibleProcess;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.2 $
 * @date     $Date: 2006/07/15 11:47:49 $
 */
public class InterruptibleProgressDialog extends JDialog
                                         implements Runnable,
                                                    ActionListener {
    
    /** The event parent to this object */
    private InterruptibleProcess process;

    /** The progress bar widget */
    private IndeterminateProgressBar progressBar;
    
    /** The parent frame of this dialog */
    private Frame parentFrame;
    
    /** The progress bar label text */
    private String labelText;
    
    public InterruptibleProgressDialog(Frame parentFrame,
                                       String title,
                                       String labelText,
                                       InterruptibleProcess process) {
        super(parentFrame, title, true);
        this.process = process;
        this.labelText = labelText;
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        pack();
        setLocation(GUIUtils.getLocationForDialog(parentFrame, getSize()));
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
        base.add(new JLabel(labelText), gbc);
        gbc.gridy = 1;
        gbc.insets.top = 0;
        base.add(progressBar, gbc);
        gbc.gridy = 2;
        gbc.weighty = 1.0;
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
    }

    public void actionPerformed(ActionEvent e) {    
        process.setCancelled(true);
        process.interrupt();
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
