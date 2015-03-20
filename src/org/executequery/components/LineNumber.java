/*
 * LineNumber.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

import javax.swing.JComponent;
import org.executequery.GUIUtilities;
import org.underworldlabs.util.SystemProperties;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class LineNumber extends JComponent {
    
    private static final Font DEFAULT_FONT = new Font("monospaced", Font.PLAIN, 12);
    
    //  LineNumber height (abends when I use MAX_VALUE)
    private static final int HEIGHT = Integer.MAX_VALUE - 1000000;
    
    //  Set right/left margin
    private static final int MARGIN = 5;
    
    //  Variables for this LineNumber component
    private FontMetrics fontMetrics;
    private int lineHeight;
    private int currentRowWidth;
    
    private int executingLine;
    
    //  Metrics of the component used in the constructor
    private JComponent component;
    private int componentFontHeight;
    private int componentFontAscent;
    private int totalRows;
    
    // line number gutter colours
    private Color foregroundColour;
    private Color backgroundColour;
    
    private Image executingIcon;
    private int iconHeight;
    private int iconWidth;

    /** Convenience constructor for Text Components. */
    public LineNumber(JComponent component) {
        
        foregroundColour = SystemProperties.getColourProperty("user",
                "editor.linenumber.foreground");
        backgroundColour = SystemProperties.getColourProperty("user",
                "editor.linenumber.background");

        setForeground(foregroundColour);
        setBackground(backgroundColour);
        
//        Font font = DEFAULT_FONT;
        
        if (component == null) {
            this.component = this;
        }
        else {
            this.component = component;
        }

        Font font = component.getFont();
        setFont(component.getFont());

        if (font != null) {
          componentFontHeight = component.getFontMetrics(
                                          component.getFont()).getHeight();
          componentFontAscent = component.getFontMetrics(
                                          component.getFont()).getAscent();
        }
        
        setPreferredWidth(9999);
        totalRows = 1;
    }
    
    /**
     *  Using FontMetrics, calculate the width of the given integer and then
     *  set the preferred size of the component.
     */
    public void setPreferredWidth(int row) {
        
        if (fontMetrics == null)
            return;
        
        int width = fontMetrics.stringWidth(String.valueOf(row));
        
        if (currentRowWidth < width) {
            currentRowWidth = width;
            setPreferredSize(new Dimension(2 * MARGIN + width, HEIGHT));
        }
    }
    
    public void updatePreferences(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());
        componentFontHeight = fontMetrics.getHeight();
        componentFontAscent = fontMetrics.getAscent();
        
        foregroundColour = SystemProperties.getColourProperty("user", "editor.linenumber.foreground");
        backgroundColour = SystemProperties.getColourProperty("user", "editor.linenumber.background");
        
        setForeground(foregroundColour);
        setBackground(backgroundColour);
        
    }
    
    /** Reset variables that are dependent on the font. */
    public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());
        if (fontMetrics != null) {
            componentFontHeight = fontMetrics.getHeight();
            componentFontAscent = fontMetrics.getAscent();
        }
    }
    
    /** The line height defaults to the line height of the font for this component. */
    public int getLineHeight() {
        if (lineHeight == 0)
            return componentFontHeight;
        else
            return lineHeight;
    }
    
    /**
     *  Override the default line height with a positive value.
     *  For example, when you want line numbers for a JTable you could
     *  use the JTable row height.
     */
    public void setLineHeight(int lineHeight) {
        if (lineHeight > 0) {
            this.lineHeight = lineHeight;
        }
    }
    
    /**
     * Sets the total row count on the border and 
     * calls a repaint if required.
     */
    public void setRowCount(int rows) {
        if (totalRows != rows) {
            totalRows = rows;
            repaint();
        }
    }

    public int getStartOffset() {
        return component.getInsets().top + componentFontAscent;
    }
    
    public void paintComponent(Graphics g) {
        int lineHeight = getLineHeight();
        int startOffset = getStartOffset();
        Rectangle drawHere = g.getClipBounds();

        // Paint the background
        g.setColor(getBackground());
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
        
/*
    g.setColor(LINE_GUTTER_COLOUR);
    g.drawLine(drawHere.width - 1, drawHere.y, drawHere.width - 1, drawHere.height);
 */
        
        // Determine the number of lines to draw in the foreground.
        g.setColor(getForeground());
        int startLineNumber = (drawHere.y / lineHeight) + 1;
        int tempEndLineNumber = startLineNumber + (drawHere.height / lineHeight);
        int endLineNumber;
        
        if (totalRows > tempEndLineNumber) {
            endLineNumber = tempEndLineNumber;
        } else {
            endLineNumber = totalRows;
        }

        String lineNumber = null;
        int start = (drawHere.y / lineHeight) * lineHeight + startOffset;

        for (int i = startLineNumber; i <= endLineNumber; i++) {
            lineNumber = String.valueOf(i);
            int width = fontMetrics.stringWidth(lineNumber);

            if (executingLine == i) {
                
                if (executingIcon == null) {
                    ImageIcon icon = GUIUtilities.loadIcon(
                                        "ExecutingPointer.gif", true);
                    iconWidth = icon.getIconWidth();
                    iconHeight = icon.getIconHeight();
                    executingIcon = icon.getImage();
                }

                g.drawImage(executingIcon, 
                            MARGIN + currentRowWidth - width - 2,
                            start - iconHeight + 2,
                            iconWidth,
                            iconHeight,
                            this);

            }
            else {
                g.drawString(lineNumber, 
                             MARGIN + currentRowWidth - width, 
                             start);
            }

            start += lineHeight;
        }

        setPreferredWidth(endLineNumber);
    }

    public void resetExecutingLine() {        
        if (executingLine != -1) {
            executingLine = -1;
            repaint();
        }
    }
    
    public void setExecutingLine(int lineNumber) {
        executingLine = lineNumber + 1;
    }
    
}






