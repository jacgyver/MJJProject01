/*
 * SplashPanel.java
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


package org.underworldlabs.swing;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Window;
import org.underworldlabs.swing.plaf.UIUtils;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * This class creates a splash panel to the size of the image to be 
 * displayed. The panel is displayed for as long as is required to 
 * load required classes and build the application frame and associated 
 * components.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.8 $
 * @date     $Date: 2006/07/15 12:51:52 $
 */
public class SplashPanel extends Canvas {
    
    /** This object's font metrics */
    private FontMetrics fontMetrics;
    
    /** The window displayed */
    private Window window;
    
    /** The splash image */
    private Image image;
    
    /** The off-screen image */
    private Image offscreenImg;
    
    /** The off-screen graphics */
    private Graphics offscreenGfx;
    
    /** The startup progress posiiton */
    private int progress;
    
    /** The version info string */
    private String version;
    
    /** The progress bar's colour */
    private Color progressColour;

    /** the light gradient colour */
    private Color gradientColour;
    
    /** the x-coord of the version string */
    private int versionLabelX;
    
    /** the y-coord of the version string */
    private int versionLabelY;
    
    /** The progress bar height */
    private int PROGRESS_HEIGHT = 20;
    
    /** Creates a new instance of the splash panel. */
    public SplashPanel(Color progressBarColour, 
                       String imageResourcePath,
                       String versionNumber) {
        this(progressBarColour, imageResourcePath, versionNumber, -1, -1);
    }
    
    public SplashPanel(Color progressBarColour, 
                       String imageResourcePath,
                       String versionNumber,
                       int versionLabelX,
                       int versionLabelY) {

        this.versionLabelX = versionLabelX;
        this.versionLabelY = versionLabelY;
        
        progressColour = progressBarColour;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setBackground(Color.white);
        
        gradientColour = UIUtils.getBrighter(progressBarColour, 0.65);
        
        Font font = new Font("Dialog", Font.PLAIN, 15);
        setFont(font);
        fontMetrics = getFontMetrics(font);
        
        image = getToolkit().getImage(getClass().getResource(imageResourcePath));
        
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);

        if (versionNumber != null) {
            version = "Version " + versionNumber;
        }
        
        try {
            tracker.waitForAll();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        window = new Window(new Frame());
        
        Dimension screen = getToolkit().getScreenSize();
        Dimension size = new Dimension(image.getWidth(this),
        image.getHeight(this));
        window.setSize(size);
        
        window.setLayout(new BorderLayout());
        window.add(BorderLayout.CENTER, this);
        
        window.setLocation((screen.width - size.width) / 2,
        (screen.height - size.height) / 2);
        window.validate();
        window.setVisible(true);

    }

    public synchronized void advance() {
        progress++;
        repaint();
        
        // wait for it to be painted to ensure
        // progress is updated continuously
        try {
            wait();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        
    }
    
    public synchronized void paint(Graphics g) {
        Dimension size = getSize();
        
        if(offscreenImg == null) {
            offscreenImg = createImage(size.width, size.height);
            offscreenGfx = offscreenImg.getGraphics();
            Font font = new Font("dialog", Font.PLAIN, 12);
            offscreenGfx.setFont(font);
        }

        offscreenGfx.drawImage(image, 0, 0, this);
        
        offscreenGfx.setColor(progressColour);
        /*
        offscreenGfx.fillRect(0, 
                              image.getHeight(this) - PROGRESS_HEIGHT,
                              (window.getWidth() * progress) / 9, 
                              PROGRESS_HEIGHT);
        */
        ((Graphics2D)offscreenGfx).setPaint(
                                    new GradientPaint(0, 
                                    image.getHeight(this) - PROGRESS_HEIGHT, 
                                    gradientColour,//new Color(95,95,190), 
                                    0,
                                    image.getHeight(this), progressColour));

        offscreenGfx.fillRect(0, 
                              image.getHeight(this) - PROGRESS_HEIGHT,
                              (window.getWidth() * progress) / 9, 
                              PROGRESS_HEIGHT);

        if (version != null) {
            
            if (versionLabelX == -1) {
                versionLabelX = (getWidth() - fontMetrics.stringWidth(version)) / 2;
            }

            if (versionLabelY == -1) {
                // if no y value - set just above progress bar
                versionLabelY = image.getHeight(this) - PROGRESS_HEIGHT - fontMetrics.getHeight();
            }

            ((Graphics2D)offscreenGfx).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON);

            offscreenGfx.setColor(Color.WHITE);
            offscreenGfx.drawString(version,
                                    versionLabelX, 
                                    versionLabelY);
        }

        g.drawImage(offscreenImg, 0, 0, this);
        
        notify();
    }

    public void dispose() {
        // wait a moment
        try {
            Thread.sleep(600);
            //Thread.sleep(20000);
        } catch (Exception e) {}
        window.dispose();
    }
    
    public void update(Graphics g) {
        paint(g);
    }
    
}



