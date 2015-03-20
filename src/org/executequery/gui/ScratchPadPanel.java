/*
 * ScratchPadPanel.java
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.base.TabView;
import org.underworldlabs.swing.toolbar.PanelToolBar;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.gui.text.DefaultTextEditorContainer;
import org.executequery.gui.text.SimpleTextArea;
import org.underworldlabs.swing.RolloverButton;


/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>The Scratch Pad simple text editor.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class ScratchPadPanel extends DefaultTextEditorContainer
                             implements FocusablePanel,
                                        TabView,
                                        ActionListener {
    
    public static final String TITLE = "Scratch Pad";
    public static final String FRAME_ICON = "ScratchPad16.gif";
    
    private JTextArea textArea;

    private RolloverButton editorButton;
    private RolloverButton newButton;
    private RolloverButton trashButton;
    
    private static int count = 1;
    
    /** Constructs a new instance. */
    public ScratchPadPanel() {
        this(null);
    }
    
    public ScratchPadPanel(String text) {
        super(new BorderLayout());
        
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (text != null) {
            textArea.setText(text);
            textArea.setCaretPosition(0);
        }

    }
    
    /** Initializes the state of this instance. */
    private void jbInit() throws Exception {
        editorButton = new RolloverButton("/org/executequery/icons/ScratchToEditor16.gif",
                                         "Paste to Query Editor");
        newButton = new RolloverButton("/org/executequery/icons/NewScratchPad16.gif",
                                      "New Scratch Pad");
        trashButton = new RolloverButton("/org/executequery/icons/Delete16.gif",
                                        "Clear");
        
        editorButton.addActionListener(this);
        trashButton.addActionListener(this);
        newButton.addActionListener(this);
        
        SimpleTextArea simpleTextArea = new SimpleTextArea();
        textArea = simpleTextArea.getTextAreaComponent();
        textArea.setMargin(new Insets(2,2,2,2));
        textComponent = textArea;

        PanelToolBar tools = new PanelToolBar();
        tools.addButton(newButton);
        tools.addButton(editorButton);
        tools.addButton(trashButton);

        JPanel base = new JPanel(new BorderLayout());
        base.add(tools, BorderLayout.NORTH);
        base.add(simpleTextArea, BorderLayout.CENTER);
        
        simpleTextArea.setBorder(BorderFactory.createEmptyBorder(0,3,3,3));
        add(base, BorderLayout.CENTER);
    }
    
    public Component getDefaultFocusComponent() {
        return textArea;
    }
    
    public void focusGained() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.requestFocusInWindow();//requestFocus();
            }
        });
    }
    
    public void focusLost() {}

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        
        if (obj == editorButton) {
            
            if (GUIUtilities.isPanelOpen(QueryEditor.TITLE)) {
                QueryEditor queryEditor = 
                        (QueryEditor)GUIUtilities.getOpenFrame(QueryEditor.TITLE);
                queryEditor.setEditorText(textArea.getText());
            }
            else {
                GUIUtilities.addCentralPane(QueryEditor.TITLE,
                                            QueryEditor.FRAME_ICON, 
                                            new QueryEditor(textArea.getText()),
                                            null,
                                            true);
            }
        }
        
        else if (obj == trashButton) {
            textArea.setText(Constants.EMPTY);
        }
        
        else if (obj == newButton) {
                GUIUtilities.addCentralPane(ScratchPadPanel.TITLE,
                                            ScratchPadPanel.FRAME_ICON, 
                                            new ScratchPadPanel(),
                                            null,
                                            true);
        }
        
    }

    public String getPrintJobName() {
        return "Execute Query - scratch pad";
    }

    public String getDisplayName() {
        return toString();
    }

    public String toString() {
        return TITLE + " - " + (count++);
    }
    
    // --------------------------------------------
    // DockedTabView implementation
    // --------------------------------------------

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        focusGained();
        return true;
    }

    /**
     * Indicates the panel is being de-selected in the pane
     */
    public boolean tabViewDeselected() {
        return true;
    }

    // --------------------------------------------

}



