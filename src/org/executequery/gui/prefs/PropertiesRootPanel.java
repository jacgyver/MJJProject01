/*
 * PropertiesRootPanel.java
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


package org.executequery.gui.prefs;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PropertiesRootPanel extends JPanel 
                                 implements UserPreferenceFunction {
    
    private Color darkColour;
    private Color lightColour;
    private Image preferencesImage;
    private Image textImage;

    public PropertiesRootPanel() {
        darkColour = new Color(151,155,235);
        lightColour = new Color(181,184,241);

        ImageIcon icon = new ImageIcon(getClass().getResource(
                        "/org/executequery/images/PreferencesIconImage.gif"));
        preferencesImage = icon.getImage();

        icon = new ImageIcon(getClass().getResource(
                        "/org/executequery/images/PreferencesText.png"));
        textImage = icon.getImage();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        int width = getWidth();
        int height = getHeight();

        g2d.setPaint(new GradientPaint(0, 0, darkColour, width, height, lightColour));
        g2d.fillRect(0, 0, width, height);

        g2d.drawImage(preferencesImage, 30, 20, this);

        int xOffset = width - textImage.getWidth(this) - 15;
        int yOffset = height - textImage.getHeight(this) - 20;
        g2d.drawImage(textImage, xOffset, yOffset, this);
    }
    
    public void save() {}
    public void restoreDefaults() {}

}



