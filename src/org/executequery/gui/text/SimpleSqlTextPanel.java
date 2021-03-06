/*
 * SimpleSqlTextPanel.java
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


package org.executequery.gui.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.executequery.Constants;
import org.executequery.event.KeywordEvent;
import org.executequery.event.KeywordListener;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * This panel is used within those components that display
 * SQL text. Typically this will be used within functions that
 * modify the database schema and the SQL produced as a result
 * will be displayed here with complete syntax highlighting and
 * other associated visual enhancements.<br>
 *
 * Examples of use include within the Create Table and Browser
 * Panel features where table modifications are reflected in
 * executable SQL.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class SimpleSqlTextPanel extends DefaultTextEditorContainer
                                implements KeywordListener {

    /** The SQL text pane */
    protected SQLTextPane textPane;

    /** Whether test is to be appended */
    private boolean appending;
    
    /** The StringBuffer if appending */
    private StringBuffer sqlBuffer;
    
    /** The text area's scroller */
    private JScrollPane sqlScroller;
    
    /** The default border */
    private Border defaultBorder;

    public SimpleSqlTextPanel() {
        this(false);
    }

    public SimpleSqlTextPanel(boolean appending) {
        super(new BorderLayout());
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqlBuffer = new StringBuffer();
        this.appending = appending;
    }

    /** <p>Initialises the state of this instance. */
    private void jbInit() throws Exception {
        setBorder(BorderFactory.createTitledBorder("SQL Text"));
        
        textPane = new SQLTextPane();
        textPane.setFont(new Font("monospaced", Font.PLAIN, 12));
        textPane.setBackground(null);
        textPane.setDragEnabled(true);
        textComponent = textPane;
        
        TextUtilitiesPopUpMenu popup = new TextUtilitiesPopUpMenu();
        popup.registerTextComponent(textPane);

        sqlScroller = new JScrollPane(textPane);
        defaultBorder = sqlScroller.getBorder();
        //sqlScroller.setBorder(null);
        add(sqlScroller, BorderLayout.CENTER);
        
        // register as a keyword listener
        //EventMediator.registerListener(EventMediator.KEYWORD_EVENT, this);
        
    }
    
    public void setSQLKeywords(boolean reset) {
        textPane.setSQLKeywords(true);
    }
    
    /**
     * Notification of a new keyword added to the list.
     */
    public void keywordsAdded(KeywordEvent e) {
        textPane.setSQLKeywords(true);
    }

    /**
     * Notification of a keyword removed from the list.
     */
    public void keywordsRemoved(KeywordEvent e) {
        textPane.setSQLKeywords(true);
    }

    public void setDefaultBorder() {
        sqlScroller.setBorder(defaultBorder);
    }
    
    /** <p>Sets the SQL text as specified.
     *
     *  @param the SQL text
     */
    public void setSQLText(String text) {
        textPane.deleteAll();
        textPane.setText(text == null ? Constants.EMPTY : text);
        
        if (appending) {
            sqlBuffer.setLength(0);
            //sqlBuffer.delete(0, sqlBuffer.length());
            sqlBuffer.append(text);
        }
        
    }
    
    public void disableUpdates(boolean disable) {
        textPane.disableUpdates(disable);        
    }
    
    public void setCaretPosition(int position) {
        textPane.setCaretPosition(position);
    }
    
    /** <p>Sets the SQL text pane's background colour
     *  to the specified value.
     *
     *  @param the background colour to apply
     */
    public void setSQLTextBackground(Color background) {
        textPane.setBackground(background);
    }
    
    /** <p>Appends the specified text to the SQL text pane.
     *
     *  @param the text to append
     */
    public void appendSQLText(String text) {
        textPane.deleteAll();

        if (text == null) {
            text = Constants.EMPTY;
        }

        if (appending) {
            sqlBuffer.append(text);
            textPane.setText(sqlBuffer.toString());
        }
        else {
            textPane.setText(text);
        }
    }
    
    /** <p>Retrieves the SQL text as contained
     *  within the SQL text pane.
     *
     *  @return the SQL text
     */
    public String getSQLText() {
        return textPane.getText();
    }
    
    /** <p>Returns wether the text pane contains any text.
     *
     *  @return <code>true</code> if the text pane has text |
     *          <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return textPane.getText().length() == 0;
    }
    
    /** <p>Sets the SQL text pane to be editable or not
     *  as specified by the passed in value.
     *
     *  @param <code>true</code> to be editable |
     *         <code>false</code> otherwise
     */
    public void setSQLTextEditable(boolean editable) {
        textPane.setEditable(editable);
    }
    
    /** <p>Sets the SQL text pane appending as specified.
     *
     *  @param <code>true</code> to append |
     *         <code>false</code> otherwise
     */
    public void setAppending(boolean appending) {
        this.appending = appending;
    }
    
    public String getPrintJobName() {
        return "Execute Query - SQL Editor";
    }

    public int save(File file) {
        String text = textPane.getText();
        TextFileWriter writer = null;
        
        if (file != null) {
            writer = new TextFileWriter(text, file.getAbsolutePath());
        } else {
            writer = new TextFileWriter(text, Constants.EMPTY);
        }        
        return writer.write();
    }

    public boolean promptToSave() {
        return true;
    }
    
}


