/*
 * ParseDateSelectionPanel.java
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

package org.executequery.gui.importexport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.gui.help.HelpPopupBase;
import org.executequery.util.Log;
import org.underworldlabs.swing.ComponentTitledPanel;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.swing.actions.ActionUtilities;
import org.underworldlabs.util.FileUtils;
import org.underworldlabs.util.MiscUtils;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/06/28 14:12:54 $
 */
public class ParseDateSelectionPanel extends ComponentTitledPanel 
                                     implements ItemListener,
                                                ActionListener {
    
    /** the parse check box */
    private JCheckBox parseDatesCheck;
    
    /** the formats editable combo */
    private JComboBox dateFormats;
    
    /** the help popup button */
    private JButton helpButton;
    
    /** the date format label */
    private JLabel dateFormatLabel;
    
    /** the parent process */
    private ImportExportProcess importExportProcess;
    
    /** Creates a new instance of ParseDateSelectionPanel */
    public ParseDateSelectionPanel(ImportExportProcess importExportProcess) {
        parseDatesCheck = new JCheckBox("Parse Date/Time Values");
        setTitleComponent(parseDatesCheck);
        parseDatesCheck.addItemListener(this);
        
        this.importExportProcess = importExportProcess;
        try {
            String dateFormatsStrings = FileUtils.loadResource(
                    "org/executequery/gui/importexport/date.formats");
            String[] _dateFormatsStrings = 
                    MiscUtils.splitSeparatedValues(dateFormatsStrings, "\n");        
            dateFormats = new JComboBox(_dateFormatsStrings);
        }
        catch (IOException e) {
            Log.error("Error loading predefined date format masks.", e);
            dateFormats = new JComboBox();
        }

        dateFormats.setEditable(true);
        
        JPanel panel = getContentPane();
        panel.setLayout(new GridBagLayout());

        dateFormatLabel = new JLabel("Date format:");
        
        // disable all on startup
        dateFormats.setEnabled(false);
        dateFormats.setOpaque(false);
        dateFormatLabel.setEnabled(false);
        
        helpButton = new RolloverButton(
                "/org/executequery/icons/TipOfTheDay16.gif", "Date/time format masks");
        helpButton.addActionListener(this);
        helpButton.setEnabled(false);
        helpButton.addMouseListener(new HelpPopupAdapter());

        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(10,5,10,5);
        gbc.insets = ins;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(dateFormatLabel, gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.insets.top = 5;
        gbc.insets.right = 2;
        gbc.insets.bottom = 10;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(dateFormats, gbc);
        gbc.gridx++;
        gbc.weightx = 0;
        gbc.insets.left = 0;
        gbc.insets.right = 5;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(helpButton, gbc);
    }
    
    /**
     * Override to enable/disable the check box and associated fields.
     */
    public void setEnabled(boolean enabled) {
        parseDatesCheck.setEnabled(enabled);
        if (parseDatesCheck.isSelected()) {
            dateFormats.setOpaque(enabled);
            dateFormats.setEnabled(enabled);
            dateFormatLabel.setEnabled(enabled);
            helpButton.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }
    
    /**
     * Returns whether to parse date values using the format
     * selected/specified.
     *
     * @return true | false
     */
    public boolean parseDates() {
        return parseDatesCheck.isSelected();
    }

    /**
     * Returns the selected/specified date format to use for parsing
     * and formatting date values or NULL if the parse dates check
     * box has not been selected.
     *
     * @return the date format
     */
    public String getDateFormat() {
        if (parseDates()) {
            return dateFormats.getEditor().getItem().toString();
        }
        return null;
    }
    
    public void actionPerformed(ActionEvent e) {        
        //new DateFormatDialog();
    }
    
    /**
     * Invoked when the parse dates checkbox has been selected/deselected
     * to enable/disable the date format combo box.
     */    
    public void itemStateChanged(ItemEvent e) {
        boolean selected = e.getStateChange() == ItemEvent.SELECTED;
        dateFormats.setEnabled(selected);
        dateFormats.setOpaque(selected);
        dateFormatLabel.setEnabled(selected);
        helpButton.setEnabled(selected);
    }

    class HelpPopupAdapter extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            new HelpPopupBase(
                    "Date Format Masks", 
                    new DateFormatDialog(),
                    importExportProcess.getDialog(),
                    e);
        }
    }
    
    
    class DateFormatDialog extends JPanel {
        
        public DateFormatDialog() {
            super(new BorderLayout());
            try {
                String path = "/org/executequery/gui/importexport/date-masks.html";
                JEditorPane textPane = new JEditorPane(getClass().getResource(path));
                textPane.setEditable(false);
                setPreferredSize(new Dimension(480,300));
                add(new JScrollPane(textPane), BorderLayout.CENTER);
            }
            catch (IOException e) {
                String message = "Error loading date mask help file.";
                Log.error(message, e);
                GUIUtilities.displayExceptionErrorDialog(
                        message + "\n" + e.getMessage(), e);
            }
        }
        
    }

    
}
