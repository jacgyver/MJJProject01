/*
 * AboutPanel.java
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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.executequery.Constants;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.executequery.ActiveComponent;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.GUIUtils;
import org.underworldlabs.swing.HeapMemoryPanel;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * System About panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.10 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class AboutPanel extends BaseDialog
                        implements ActiveComponent,
                                   ActionListener {
    
    public static final String TITLE = "About";
    public static final String FRAME_ICON = "Information16.gif";
    
    private JTabbedPane tabPane;
    private GridBagConstraints baseGBC;
    private HeapMemoryPanel heapPanel;
    private AboutImagePanel imagePanel;
    private ScrollingCreditsPanel creditsPanel;
    
    /** <p>Constructs a new instance. */
    public AboutPanel() {
        super(TITLE, true);
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        tabPane = new JTabbedPane();
        tabPane.add("System", systemDetails());
        tabPane.add("Resources", systemResources());
        tabPane.add("License", license());
        tabPane.add("Credits", credits());
        
        imagePanel = new AboutImagePanel();
        
        JPanel basePanel = new JPanel(new BorderLayout());
        basePanel.setPreferredSize(new Dimension(350, 420));
        basePanel.add(imagePanel, BorderLayout.NORTH);
        basePanel.add(tabPane, BorderLayout.CENTER);
        basePanel.add(addButtonPanel(), BorderLayout.SOUTH);
        
        addDisplayComponentWithEmptyBorder(basePanel);
        setResizable(false);
        display();
    }
    
    private GridBagConstraints resetConstraints(GridBagConstraints gbc) {
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets.top = 1;
        gbc.insets.bottom = 3;
        gbc.insets.left = 3;
        gbc.insets.right = 3;
        gbc.insets.top = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        return gbc;
    }
    
    private JPanel credits() {
        creditsPanel = new ScrollingCreditsPanel();
        JPanel main = new JPanel(new GridBagLayout());
        main.add(creditsPanel, resetConstraints(new GridBagConstraints()));
        return main;
    }
    
    private JPanel addButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setPreferredSize(new Dimension(350, 50));
        
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(Constants.BUTTON_SIZE);

        GridBagConstraints gbc = new GridBagConstraints();
        Insets ins = new Insets(7,0,0,0);
        gbc.insets = ins;
        
        buttonPanel.add(okButton, gbc);        
        okButton.addActionListener(this);

        return buttonPanel;
    }

    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    public void cleanup() {
        if (imagePanel != null) {
            imagePanel.stopTimer();
        }
        imagePanel = null;

        if (creditsPanel != null) {
            creditsPanel.stopTimer();
        }
        creditsPanel = null;
        
        if (heapPanel != null) {
            heapPanel.stopTimer();
        }
        heapPanel = null;
    }
    
    private JPanel license() {
        JPanel base = new JPanel(new GridBagLayout());
        
        JLabel line1 = new JLabel("This is free software, and you are welcome to");
        JLabel line2 = new JLabel("redistribute it under certain conditions.");
        JLabel line3 = new JLabel("See the GNU General Public License for details.");
        
        JButton button = new JButton(ActionBuilder.get("license-command"));
        button.setText("View License");
        button.setIcon(null);
        
        base.setBorder(BorderFactory.createEtchedBorder());
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        Insets ins = new Insets(5,5,5,5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = ins;
        base.add(line1, gbc);
        gbc.insets.top = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        base.add(line2, gbc);
        gbc.gridy = 4;
        gbc.insets.top = 7;
        gbc.insets.left = 7;
        gbc.insets.right = 7;
        gbc.insets.bottom = 7;
        base.add(line3, gbc);
        gbc.gridy = 7;
        gbc.ipady = 5;
        base.add(button, gbc);
        
        JPanel main = new JPanel(new GridBagLayout());
        main.add(base, resetConstraints(gbc));
        
        return main;
    }
    
    
    private JPanel systemResources() {
        heapPanel = new HeapMemoryPanel();
        return heapPanel;
    }
    
    private JPanel systemDetails() {
        return new SystemPropertiesPanel();
        /*
        PropertyWrapperModel model = new PropertyWrapperModel(
                                            System.getProperties(), 
                                            PropertyWrapperModel.SORT_BY_KEY);

        JTable propsTable = new JTable(model);
        propsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scroller = new JScrollPane(propsTable);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(scroller, resetConstraints(new GridBagConstraints()));
        
        return panel; */
    }
    
    class ScrollingCreditsPanel extends JPanel {
        
        private Timer timer;
        
        private Font nameFont;
        
        private Font titleFont;
        
        private String[] names = {"Takis Diakoumis",
                                  "Dragan Vasic",
                                  "Robert Stone",
                                  "Jeremy Pyman",
                                  "Mark Gordon"};
        
        private String[] titles = {"Original Developer",
                                   "Contributor",
                                   "Contributor",
                                   "Contributor",
                                   "DBA"};
        
        protected ScrollingCreditsPanel() {
            setBorder(BorderFactory.createEtchedBorder());
            nameFont = new Font("dialog", Font.BOLD, 12);
            titleFont = new Font("dialog", Font.PLAIN, 12);
            startTimer();
        }
        
        protected void startTimer() {
            final Runnable scroller = new Runnable() {
                public void run() {
                    yOffset--;
                    ScrollingCreditsPanel.this.repaint();
                }
            };
            
            TimerTask paintCredits = new TimerTask() {
                public void run() {
                    EventQueue.invokeLater(scroller);
                }
            };
            timer = new Timer();
            timer.schedule(paintCredits, 500, 40);
        }
        
        protected void ensureTimerRunning() {
            if (timer == null) {
                startTimer();
            }
        }
        
        private int yOffset;
        int count = 0;
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            Graphics2D g2d = (Graphics2D)g;
            g2d.setPaint(new GradientPaint(0, height / 2, Color.LIGHT_GRAY, width / 6,
                                           height / 2, Color.WHITE));
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);

            g.fillRect(0, 0, width, height);

            int x = 0;
            int y = 0;
            int stringWidth = 0;
            
            //g.setColor(Color.GRAY.brighter());
            //g.drawLine(width / 6, 0, width / 6, height);
            
            g.setColor(Color.BLACK);
            for (int i = 0; i < names.length; i++) {
                g.setFont(nameFont);
                FontMetrics fm = g.getFontMetrics();
                stringWidth = fm.stringWidth(names[i]);
                x = (width - stringWidth) / 2;
                y += fm.getHeight() + 1;
                g.drawString(names[i], x, y + yOffset + height);

                g.setFont(titleFont);
                fm = g.getFontMetrics();
                stringWidth = fm.stringWidth(titles[i]);
                x = (width - stringWidth) / 2;
                y += fm.getHeight() + 1;
                g.drawString(titles[i], x, y + yOffset + height);

                if (i == names.length - 1) {
                    //if (y + yOffset + height == 0) {
                    if (Math.abs(yOffset) >= (y+ height)) {
                        yOffset = 0;
                    }
                }

                y += 15;
            }

        }
        
        public void stopTimer() {
            if (timer != null) {
                timer.cancel();
            }
            timer = null;
        }

    }
    
    class AboutImagePanel extends JPanel {
        
        private static final int HEIGHT = 180;
        private static final int WIDTH = 350;
        
        private Timer timer;
        private Image eqImage;
        private Image eqText;
        private Image background;
        
        private Font versionFont;
        private String versionText;
        
        boolean stageOneComplete;
        boolean stageTwoComplete;
        boolean stageThreeComplete;
        boolean stageFourComplete;
        
        int leftOffsetImage;
        int leftOffsetText;
        int bottomOffsetVersion;
        
        private float alpha;
        
        private Color darkColour;
        private Color lightColour;
        
        protected AboutImagePanel() {
            stageOneComplete = false;
            stageTwoComplete = false;
            stageThreeComplete = false;
            stageFourComplete = false;
            
            leftOffsetImage = 0;
            leftOffsetText = 0;
            
            alpha = 0.0f;
            
            darkColour = new Color(124, 125, 203);
            lightColour = new Color(163,164,235);
            
            versionText = "Version " + 
                          System.getProperty("executequery.major.version");
            versionFont = new Font("dialog", Font.PLAIN, 12);
            
            ImageIcon icon = GUIUtilities.loadImage("AboutImage.png");
            eqImage = icon.getImage();
            
            ImageIcon backgroundIcon = GUIUtilities.loadImage("AboutBackground.png");
            background = backgroundIcon.getImage();

            icon = GUIUtilities.loadImage("AboutText.png");
            eqText = icon.getImage();

            final Runnable fader = new Runnable() {
                public void run() {
                    
                    if (!stageOneComplete) {
                        
                        if (alpha >= 0.999f)
                            stageOneComplete = true;
                        else
                            alpha += 0.020f;
                        
                    }
                    
                    if (stageOneComplete && !stageTwoComplete) {
                        
                        if (leftOffsetImage >= 90)
                            stageTwoComplete = true;
                        else
                            leftOffsetImage += 1;
                        
                    }
                    
                    if (stageTwoComplete && !stageThreeComplete) {
                        // 180
                        if (leftOffsetText >= 200)
                            stageThreeComplete = true;
                        else
                            leftOffsetText += 2;
                        
                    }
                    
                    if (stageThreeComplete && !stageFourComplete) {
                        
                        if (bottomOffsetVersion >= 20) { // 15
                            stageFourComplete = true;
                            timer.cancel();
                            GUIUtils.scheduleGC();
                            return;
                        }
                        else
                            bottomOffsetVersion += 1;
                        
                    }

                    AboutImagePanel.this.repaint();                    
                }
            };

            TimerTask paintImage = new TimerTask() {
                public void run() {
                    EventQueue.invokeLater(fader);
                }
            };
            timer = new Timer();
            timer.schedule(paintImage, 500, 40);
        }
        
        public void stopTimer() {
            if (timer != null) {
                timer.cancel();
            }
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D)g;
            
            int imageWidth = eqImage.getWidth(this);
            int imageHeight = eqImage.getHeight(this);

            int imageX = (WIDTH - imageWidth) / 2;
            int imageY = (HEIGHT - imageHeight) / 2;
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                 RenderingHints.VALUE_RENDER_QUALITY);
            
            AlphaComposite ac = AlphaComposite.getInstance(
                                            AlphaComposite.SRC_OVER, alpha);
            g2d.setComposite(ac);
            
            /*
            g2d.setPaint(new GradientPaint(WIDTH / 2, 0, darkColour, WIDTH / 2,
                                           HEIGHT, lightColour));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            */
            
            g2d.drawImage(background, 0, 0, WIDTH, HEIGHT, this);
            
            g2d.drawImage(eqImage, imageX - leftOffsetImage - 1, imageY - 1, this);
            
            if (stageTwoComplete) {
                imageWidth = eqText.getWidth(this);
                imageHeight = eqText.getHeight(this);
                
                imageX = WIDTH - leftOffsetText;
                imageY = 115;//130;

                g2d.setClip(imageX - 1, imageY, WIDTH - imageX, imageHeight + 1);
                g2d.drawImage(eqText, imageX, imageY, this);
            }

            if (stageThreeComplete) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(versionFont);

                FontMetrics fm = g.getFontMetrics(versionFont);
                int textLength = fm.stringWidth(versionText);
                imageX = imageX + ((imageWidth - textLength) / 2);
                
                g2d.setClip(imageX, 150, textLength, HEIGHT - 150);
                g2d.drawString(versionText, imageX, HEIGHT - bottomOffsetVersion);
            }

            g2d.setClip(0, 0, WIDTH, HEIGHT);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
            
        }
        
        public Dimension getPreferredSize() {
            return new Dimension(WIDTH, HEIGHT);
        }
        
    } // class AboutImagePanel
    
}



