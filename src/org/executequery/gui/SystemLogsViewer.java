/*
 * SystemLogsViewer.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;
import org.executequery.SystemUtilities;
import org.executequery.base.TabView;
import org.underworldlabs.swing.toolbar.PanelToolBar;
import org.executequery.gui.text.*;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.util.FileUtils;

import org.underworldlabs.util.MiscUtils;

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
 * @date     $Date: 2006/06/11 02:15:10 $
 */
public class SystemLogsViewer extends DefaultTextEditorContainer 
                              implements ItemListener,
                                         TabView,
                                         ActionListener {
    
    public static final String TITLE = "System Log Viewer";
    
    public static final String FRAME_ICON = "SystemOutput.gif";
    
    public static final int ACTIVITY = 0;
    public static final int EXPORT = 1;
    public static final int IMPORT = 2;
    
    private JTextArea textArea;
    
    private JComboBox logCombo;
    
    private RolloverButton reloadButton;
    private RolloverButton trashButton;

    public SystemLogsViewer(int type) {
        super(new BorderLayout());
        try {
            jbInit(type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit(final int type) throws Exception {
        String[] logs = {"System Log: ~/.executequery/logs/eq.output.log",
                         "Export Log: ~/.executequery/logs/eq.export.log",
                         "Import Log: ~/.executequery/logs/eq.import.log"};
        
        logCombo = new JComboBox(logs);
        logCombo.addItemListener(this);

        SimpleTextArea simpleTextArea = new SimpleTextArea();
        textArea = simpleTextArea.getTextAreaComponent();
        textComponent = textArea;

        reloadButton = new RolloverButton("/org/executequery/icons/Reload16.gif",
                                      "Reload this log file");
        trashButton = new RolloverButton("/org/executequery/icons/Delete16.gif",
                                        "Reset this log file");
        
        reloadButton.addActionListener(this);
        trashButton.addActionListener(this);

        // build the tools area
        PanelToolBar tools = new PanelToolBar();
        tools.addButton(reloadButton);
        tools.addButton(trashButton);
        tools.addSeparator();
        tools.addComboBox(logCombo);

        simpleTextArea.setBorder(BorderFactory.createEmptyBorder(1,3,3,3));
        
        JPanel base = new JPanel(new BorderLayout());
        base.add(tools, BorderLayout.NORTH);
        base.add(simpleTextArea, BorderLayout.CENTER);

        add(base, BorderLayout.CENTER);

        setFocusable(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (type == ACTIVITY) {
                    loadLogFile(ACTIVITY);
                } else {
                    logCombo.setSelectedIndex(type);
                }
            }
        });
        
    }

    public void itemStateChanged(ItemEvent e) {
        // interested in selections only
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }
        loadLogFile(logCombo.getSelectedIndex());
    }
    
    public void setSelectedLog(int type) {
        logCombo.setSelectedIndex(type);
    }

    private void loadLogFile(int type) {
        String path = SystemUtilities.getUserLogsPath();
        switch (type) {
            case ACTIVITY:
                path += SystemProperties.getProperty("system", "eq.output.log");
                break;
            case EXPORT:
                path += SystemProperties.getProperty("system", "eq.export.log");
                break;
            case IMPORT:
                path += SystemProperties.getProperty("system", "eq.import.log");
                break;
        }

        final String _path = path;
        GUIUtils.startWorker(new Runnable() {
            public void run() {
                try {
                    GUIUtilities.showWaitCursor();
                    GUIUtilities.showWaitCursor(textArea);
                    load(_path);
                }
                finally {
                    GUIUtilities.showNormalCursor();
                    GUIUtilities.showNormalCursor(textArea);
                }
            }
        });
    }

    private void load(String path) {
        try {
            final String text = FileUtils.loadFile(path);

            GUIUtils.invokeAndWait(new Runnable() {
                public void run() {
                    if (!MiscUtils.isNull(text)) {
                        textArea.setText(text);
                    } else {
                        textArea.setText("");
                    }
                    textArea.setCaretPosition(0);                    
                }
            });

        } 
        catch (OutOfMemoryError memExc) {
            GUIUtils.scheduleGC();
            GUIUtilities.showNormalCursor();
            GUIUtilities.displayErrorMessage(
                    "Out of Memory.\nThe file is too large to open for viewing.");
        } 
        catch (IOException ioExc) {
            GUIUtilities.showNormalCursor();
            String message = "Error Opening File:\n\n" + ioExc.getMessage();
            GUIUtilities.displayExceptionErrorDialog(message, ioExc);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == reloadButton) {
            loadLogFile(logCombo.getSelectedIndex());
        }
        else if (source == trashButton) {
            int type = logCombo.getSelectedIndex();
            switch (type) {
                case ACTIVITY:
                    GUIUtilities.resetSystemLog();
                    break;
                case EXPORT:
                    GUIUtilities.resetExportLog();
                    break;
                case IMPORT:
                    GUIUtilities.resetImportLog();
                    break;
            }
            textArea.setText("");
        }
    }
    
    // --------------------------------------------
    // TabView implementation
    // --------------------------------------------

    /**
     * Indicates the panel is being removed from the pane
     */
    public boolean tabViewClosing() {
        textArea = null;
        textComponent = null;
        return true;
    }

    /**
     * Indicates the panel is being selected in the pane
     */
    public boolean tabViewSelected() {
        return true;
    }

    /**
     * Indicates the panel is being de-selected in the pane
     */
    public boolean tabViewDeselected() {
        return true;
    }

    // --------------------------------------------

    public String getPrintJobName() {
        return "Execute Query - system log";
    }

    public String toString() {
        return TITLE;
    }
    
}



