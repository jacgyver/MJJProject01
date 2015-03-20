/*
 * SQLHistoryDialog.java
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.executequery.GUIUtilities;
import org.executequery.Constants;
import org.executequery.gui.editor.QueryEditor;
import org.underworldlabs.util.MiscUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The History Dialog displays the executed SQL statement history
 * from within the Query Editor. The data represented as a
 * <code>Vector</code> object, is displayed within a <code>JLIst</code>.
 * Selection of a stored statement can be achieved by double-clicking the
 * statement, selecting and pressing the ENTER key or by selecting
 * and clicking the SELECT button.<br>
 * The selected statement is displayed within the Query Editor that
 * initiated the frame.<br>
 * Selecting the CANCEL button closes the dialog.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/04 07:16:47 $
 */
public class SQLHistoryDialog extends JDialog
                              implements ActionListener {
    
    /** The statement history list object represented by a <code>JList</code> */
    private JList historyList;
    /** The history data <code>Vector</code> */
    private Vector data;
    /** The <code>QueryEditor</code> owning this dialog */
    private QueryEditor queryEditor;

    private JTextField searchField; 
    
    /** <p>Creates a new object with history data. 
     *
     * @param - the statement history <code>Vector</code>
     */    
    public SQLHistoryDialog(Vector data) {
        this(data, null);
    }

    /** <p>Creates a new object with history data 
     *  to be set within the specified editor.
     *
     * @param - the statement history <code>Vector</code>
     * @param - the editor
     */
    public SQLHistoryDialog(Vector data, QueryEditor queryEditor) {
        super(GUIUtilities.getParentFrame(), "SQL Command History", true);
        
        try {
            this.data = data;
            this.queryEditor = queryEditor;
            historyList = new JList(data);

            jbInit();
            pack();
            setLocation(GUIUtilities.getLocationForDialog(getSize()));
            setVisible(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** <p>Initialises the state of this instance
     * and positions all components.
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {
        // initialise the buttons
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMargin(new Insets(2, 2, 2, 2));
        JButton selectButton = new JButton("Select");

        cancelButton.setPreferredSize(Constants.BUTTON_SIZE);
        selectButton.setPreferredSize(Constants.BUTTON_SIZE);
        
        if (historyList == null) {
            historyList = new JList();
        }
        
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // add the historyList to a JScrollPane
        JScrollPane historyScrollPane = new JScrollPane(historyList);
        historyScrollPane.setPreferredSize(new Dimension(500, 275));

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        
        searchField = new JTextField();
        searchField.addActionListener(this);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx++;
        gbc.insets.right = 5;
        searchPanel.add(new JLabel("Find:"), gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(searchField, gbc);
        gbc.gridx++;
        gbc.weightx = 0;
        gbc.insets.right = 0;
        searchPanel.add(searchButton, gbc);
        
        // layout the components
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets.top = 5;
        gbc.insets.left = 5;
        gbc.insets.right = 5;
        gbc.gridwidth = 2;
        gbc.insets.bottom = 5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        c.add(searchPanel, gbc);
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets.top = 0;
        gbc.fill = GridBagConstraints.BOTH;
        c.add(historyScrollPane, gbc);
        gbc.weightx = 0;
        gbc.insets.left = 10;
        gbc.insets.bottom = 7;
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(cancelButton, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets.right = 0;
        c.add(selectButton, gbc);
        
        selectButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        historyList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                historyList_mouseClicked(e); }
        });

        historyList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                historyList_keyPressed(e); }
        });
        
    }
    
    /** <p>Sets the statement history data to the
     * <code>JList</code>.
     *
     * @param - the statement history <code>Vector</code>
     */
    public void setHistoryData(Vector data) {
        this.data = data;
        historyList.setListData(data);
    }
    
    /** <p>Initiates the action of the "Select" button adding
     * the selected statement to the open Query Editor.
     *
     * @param - the action event
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Select")) {
            selectSQLCommand();
        }
        else if (command.equals("Search") || 
                e.getSource() == searchField) {
            String text = searchField.getText();
            
            if (MiscUtils.isNull(text)) {
                return;
            }
            
            int start = historyList.getSelectedIndex();
            if (start == -1 || start == data.size() - 1) {
                start = 0;
            }
            else {
                start++;
            }

            search(text, start);
        }
        else {
            dispose();
        }

    }
    
    private void search(String text, int start) {
        Pattern pattern = Pattern.compile("\\b" + text, 
                                          Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(Constants.EMPTY);

        for (int i = start, k = data.size(); i < k; i++) {
            matcher.reset((String)data.elementAt(i));
            if (matcher.find()) {
                historyList.setSelectedIndex(i);
                return;
            }
        }

        GUIUtilities.displayInformationMessage("Search string not found");
    }
    
    private void selectSQLCommand() {
        if (historyList.isSelectionEmpty()) {
            GUIUtilities.displayErrorMessage("No selection made.");
            return;
        }

        if (queryEditor != null) {
            queryEditor.setEditorText((String)data.elementAt(
                    historyList.getSelectedIndex()));
        }
        dispose();
    }

    /** <p>Initiates the action on the history list after
     * double clicking a selected statement and propagates
     * the action to the method <code>selectButton_actionPerformed</code>.
     *
     * @param - the mouse event
     */
    private void historyList_mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) {
            selectSQLCommand();
        }
    }
    
    /** <p>Initiates the action on the history list after
     * pressing the ENTER key on a selected statement and propagates
     * the action to the method <code>selectButton_actionPerformed</code>.
     *
     * @param - the key event
     */
    private void historyList_keyPressed(KeyEvent e) {
        if(e.getKeyCode() == e.VK_ENTER) {
            selectSQLCommand();
        }
    }
    
}





