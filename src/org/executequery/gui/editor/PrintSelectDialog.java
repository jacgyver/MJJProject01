/*
 * PrintSelectDialog.java
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


package org.executequery.gui.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.util.*;
import org.executequery.print.*;
import org.underworldlabs.swing.util.SwingWorker;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>The print selection dialog for the Query Editor
 *  allowing the user to select which portion of the editor
 *  to print from - the text area or the results panel.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PrintSelectDialog extends JDialog {
    
    /** Indicates a call to print */
    public static final int PRINT = 0;
    /** Indicates a call for a print preview */
    public static final int PRINT_PREVIEW = 1;
    
/*
  public static final int EDITOR_TEXT = 0;
  public static final int RESULTS_TABLE = 1;
  public static final int CANCEL = 2;
  private int selection;
 */
    
    /** The owner of this dialog */
    private QueryEditor parent;
    /** The text area radio button */
    private JRadioButton queryRadio;
    /** The results area radio button */
    private JRadioButton resultsRadio;
    /** The worker to perform the process */
    private SwingWorker worker;
    /** The type of print - print or print preview */
    private int type;
    
    /** <p>Constructs a new instance with the specified title.
     *
     *  @param the dialog title
     */
    private PrintSelectDialog(QueryEditor parent, String title) {
        super(GUIUtilities.getParentFrame(), title, true);
        this.parent = parent;
    }
    
    /** <p>Constructs a new default instance with the title "Print". */
    public PrintSelectDialog(QueryEditor parent) {
        this(parent, "Print");
        type = PRINT;
        jbInit();
        display();
    }
    
    /** <p>Constructs a new instance with the specified print type.
     *
     *  @param the type of print
     */
    public PrintSelectDialog(QueryEditor parent, int type) {
        this(parent, type == PRINT_PREVIEW ? "Print Preview" : "Print");
        this.type = type;
        jbInit();
        display();
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() {
        try {
            JPanel base = new JPanel(new GridBagLayout());
            
            JButton okButton = new JButton(type == PRINT_PREVIEW ? 
                                           "Preview" : "Print");
            JButton cancelButton = new JButton("Cancel");
            
            Insets btnIns = new Insets(2, 2, 2, 2);
            okButton.setMargin(btnIns);
            cancelButton.setMargin(btnIns);
            
            queryRadio = new JRadioButton("SQL Query Text Area", true);
            resultsRadio = new JRadioButton("SQL Table Results Panel");
            
            queryRadio.setMnemonic('A');
            resultsRadio.setMnemonic('R');
            
            ButtonGroup bg = new ButtonGroup();
            bg.add(queryRadio);
            bg.add(resultsRadio);
            
            okButton.setMnemonic('P');
            cancelButton.setMnemonic('C');
            
            Dimension btnDim = new Dimension(65, 25);
            okButton.setPreferredSize(btnDim);
            cancelButton.setPreferredSize(btnDim);
            
            ActionListener btnListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    printButtons_actionPerformed(e); }
            };
            okButton.addActionListener(btnListener);
            cancelButton.addActionListener(btnListener);
            
            GridBagConstraints gbc = new GridBagConstraints();
            Insets ins = new Insets(15,50,0,50);
            gbc.gridwidth = 2;
            gbc.insets = ins;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            base.add(new JLabel("Select the print area:"), gbc);
            gbc.gridy = 1;
            gbc.insets.left = 52;
            gbc.insets.top = 5;
            base.add(queryRadio, gbc);
            gbc.insets.top = 0;
            gbc.gridy = 2;
            gbc.insets.bottom = 10;
            base.add(resultsRadio, gbc);
            gbc.insets.bottom = 15;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets.right = 0;
            gbc.insets.left = 67;
            gbc.gridy = 3;
            base.add(okButton, gbc);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets.left = 5;
            gbc.gridx = 1;
            gbc.weighty = 1.0;
            base.add(cancelButton, gbc);
            
            setResizable(false);
            this.getContentPane().add(base);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void display() {
        pack();
        this.setLocation(GUIUtilities.getLocationForDialog(this.getSize()));
        setVisible(true);
    }
    
    private String printTable() {
        
        if (!parent.isResultSetSelected()) {
            GUIUtilities.displayErrorMessage("No SQL table results.");
            parent = null;
            return "Failed";
        }
        
        String title = "SQL: " + 
                       parent.getEditorText().replaceAll("\n", "").trim();
        
        if (title.length() > 60) {
            title = title.substring(0, 60);
        }
        
        return PrintUtilities.print(parent.getPrintable(), "Execute Query - editor");
    }
    
    private String printText() {
        Book book = new Book();
        book.append(parent.getPrintable(), SystemUtilities.getPageFormat());
        return PrintUtilities.print(book, "Execute Query - editor");
    }
    
    private void printButtons_actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if (command.equals("Cancel")) {
            dispose();
            return;
        }
        
        final boolean printQuery = queryRadio.isSelected();
        parent.setPrintingText(printQuery);
        
        if (type == PRINT) {
            
            worker = new SwingWorker() {
                public Object construct() {
                    Object obj = null;
                    
                    if (printQuery)
                        obj = printText();
                    else
                        obj = printTable();
                    
                    return obj;
                }
                public void finished() {
                    dispose();
                }
            };
            
            setVisible(false);
            worker.start();
            
        }
        
        else {
            
            if (!printQuery && !parent.isResultSetSelected()) {
                GUIUtilities.displayErrorMessage("No SQL table results.");
                dispose();
                return;
            }
            setVisible(false);
            new PrintPreviewer((PrintFunction)parent);
            
            /*
            GUIUtilities.addInternalFrame(PrintPreviewer.TITLE,
            PrintPreviewer.FRAME_ICON,
            new PrintPreviewer((PrintFunction)parent),
            true, true, true, true);
            */

            parent = null;
            dispose();            
        }
        
    }
    
}








