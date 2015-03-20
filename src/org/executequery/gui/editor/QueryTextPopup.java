/*
 * QueryTextPopup.java
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.underworldlabs.swing.LinkButton;
import org.executequery.gui.text.SQLTextPane;
import org.underworldlabs.swing.ActionPanel;
import org.underworldlabs.swing.RolloverButton;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Popup panel for the results panel tab rollovers.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.7 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class QueryTextPopup extends JPanel
                            implements MouseListener,
                                       ActionListener {
    
    /** the text pane area */
    private SQLTextPane textPane;
    
    /** the tabe panel owner */
    private QueryEditorResultsPanel parent;
    
    /** the toolbar header */
    private QueryTextPopupToolbar header;
    
    /** the hide timer */
    private Timer timer;
    
    /** Indicates the timer has begun */
    private boolean timerStarted;
    
    /** Indicates the mouse is currently over the panel */
    private boolean mouseOverPanel;
    
    /** the timeout in millis */
    private int timeout;
    
    /** the last x-coord */
    private int mouseX;
    
    /** the last y-coord */
    private int mouseY;
    
    /** the tab index */
    private int index;
    
    /** the last shown query */
    private String displayQuery;
    
    /** the query as displayed (no regex stuff) */
    private String query;
    
    /** The result set number label */
    private JLabel rsLabel;
    
    // ----------------------------------
    // fixed dimensions
    
    private static int HEIGHT = 240;
    private static int WIDTH = 450;
    
    /** Creates a new instance of QueryTextPopup */
    public QueryTextPopup(QueryEditorResultsPanel parent) {
        super(new BorderLayout());
        this.parent = parent;

        header = new QueryTextPopupToolbar();
        Color shadow = UIManager.getColor("controlDkShadow");
        if (shadow == null) {
            shadow = Color.DARK_GRAY;
        }
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,shadow));
        
        textPane = new SQLTextPane();
        textPane.setBackground(new Color(255,255,235));
        textPane.setEditable(false);
        
        header.addMouseListener(this);
        textPane.addMouseListener(this);

        JScrollPane scroller = new JScrollPane(textPane,
                                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setBorder(null);

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,shadow));

        rsLabel = new JLabel();
        rsLabel.setBorder(BorderFactory.createEmptyBorder(2,3,2,2));
        labelPanel.add(rsLabel, BorderLayout.WEST);

        rsLabel.addMouseListener(this);
        labelPanel.addMouseListener(this);
        
        add(header, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);
        add(labelPanel, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createLineBorder(shadow));
        setVisible(false);
    }
    
    /**
     * Sets the specified query text at position x,y for the tab
     * index specified.
     *
     * @param x - the x-coord
     * @param y - the y-coord
     * @param query - the query to be shown
     * @param index - the index of the tab selected
     */
    public void setQueryText(int x, int y, String query, int index) {
        // stop the timer if its running
        stopTimer();

        // reset panel mouse flag
        mouseOverPanel = false;
        
        // no whitespace
        this.query = query.trim();
        this.displayQuery = query.replaceAll(
                QueryEditorConstants.BLOCK_COMMENT_REGEX, Constants.EMPTY).trim();

        this.index = index;
        setRsLabelForIndex();
        header.enableScrollButtons();
        
        // if we are already visible, just change the content
        if (isVisible()) {
            // set the new query text
            textPane.setText(displayQuery);

            if (x > 0 && y > 0) {
                // start the hide timer and return
                timeout = 2500;
                startHideTimer();
            }
            return;
        }
        
        mouseX = x;
        mouseY = y;

        // start the display timer
        timeout = 750;
        startShowTimer();
    }

    private void setRsLabelForIndex() {
        int _index = index;
        if (!parent.hasOutputPane()) {
            _index = index + 1;
        }
        rsLabel.setText("Result Set " + _index);
    }
    
    private void showPanel() {
        // set the new query text
        textPane.setText(displayQuery);

        // convert the x-y coords
        Frame frame = GUIUtilities.getParentFrame();
        Point p1 = SwingUtilities.convertPoint(parent, mouseX, mouseY, frame);

        // determine the parent components position
        Point p2 = SwingUtilities.convertPoint(
                        parent, parent.getX(), parent.getY(), frame);

        int xPos = Math.min(p1.x - 10, p2.x + parent.getWidth() - WIDTH - 5);
        int yPos = (p2.y + parent.getHeight() - 60) - HEIGHT;
        setBounds(new Rectangle(xPos, yPos, WIDTH, HEIGHT));

        if (!isVisible()) {
            setVisible(true);
            GUIUtilities.getFrameLayeredPane().
                                add(this, JLayeredPane.POPUP_LAYER);
        }
        mouseOverPanel = false;
        timeout = 2500;
        startHideTimer();
    }
    
    /**
     * Starts the timer to dispose of this panel from the 
     * frame's layered pane.
     */
    public void dispose() {
        if (mouseOverPanel) {
            return;
        }
        stopTimer();
        timeout = 500;
        startHideTimer();
    }

    /**
     * Creates and starts the setQueryText panel timer task.
     */
    private void startShowTimer() {
        final Runnable runner = new Runnable() {
            public void run() {
                showPanel();
            }
        };
        
        TimerTask showPanel = new TimerTask() {
            public void run() {
                EventQueue.invokeLater(runner);
            }
        };

        timer = new Timer();
        timerStarted = true;
        timer.schedule(showPanel, timeout);
    }

    /**
     * Creates and starts the hide panel timer task.
     */
    private void startHideTimer() {
        final Runnable runner = new Runnable() {
            public void run() {
                hidePanel();
            }
        };
        
        TimerTask hidePanel = new TimerTask() {
            public void run() {
                EventQueue.invokeLater(runner);
            }
        };

        timer = new Timer();
        timerStarted = true;
        timer.schedule(hidePanel, timeout);
    }
    
    /**
     * Cancels a running timer task.
     */
    private void stopTimer() {
        if (timer == null || !timerStarted) {
            return;
        }
        timer.cancel();
        timerStarted = false;
    }

    /**
     * Disposes of this panel from the frame's layered pane.
     */
    private void hidePanel() {
        setVisible(false);
        GUIUtilities.getFrameLayeredPane().remove(this);
        GUIUtilities.getFrameLayeredPane().repaint();
        timerStarted = false;
        mouseOverPanel = false;
        header.resetButtons();
    }
    
    public void actionPerformed(ActionEvent e) {
        hidePanel();
        parent.caretToQuery(query);
    }
    
    // -------------------------------------------------------------
    // MouseListener implementation for text pane entry/exit events
    // -------------------------------------------------------------

    public void mouseEntered(MouseEvent e) {
        stopTimer();
        mouseOverPanel = true;
    }

    public void mouseExited(MouseEvent e) {
        mouseOverPanel = false;
        startHideTimer();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

    // -------------------------------------------------------------
    
    
    protected class QueryTextPopupToolbar extends ActionPanel {
        
        /** the link button to hide the popup */
        private LinkButton linkButton;

        // nav and selection buttons
        private RolloverButton previousButton;
        private RolloverButton nextButton;
        private RolloverButton copyButton;
        private RolloverButton goToButton;
        
        public QueryTextPopupToolbar() {
            super(new GridBagLayout());
            
            linkButton = new LinkButton("Hide");
            linkButton.setActionCommand("hidePopup");
            linkButton.setAlignmentX(LinkButton.RIGHT);
            linkButton.addActionListener(this);
            linkButton.addMouseListener(QueryTextPopup.this);
            
            previousButton = createButton("PreviousResultSetQuery16.gif", 
                                "previous", "Previous executed result set");

            nextButton = createButton("NextResultSetQuery16.gif", 
                                "next", "Next executed result set");

            copyButton = createButton("Copy16.gif", 
                                "copy", "Copy this query to the system clipboard");
            
            goToButton = createButton("GoToResultSetQuery16.gif", 
                                "goToQuery", "Go to this query in the editor");
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets.top = 1;
            gbc.insets.left = 1;
            add(previousButton, gbc);
            gbc.gridx++;
            gbc.insets.left = 0;
            add(nextButton, gbc);
            gbc.gridx++;
            add(copyButton, gbc);
            gbc.gridx++;
            gbc.weightx = 1.0;
            add(goToButton, gbc);
            gbc.gridx++;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.insets.right = 3;
            add(linkButton, gbc);
        }

        protected void resetButtons() {
            previousButton.reset();
            nextButton.reset();
            copyButton.reset();
            goToButton.reset();
        }
        
        public void next() {
            setQueryText(-1, -1, parent.getQueryTextAt(index + 1), index + 1);
        }
        
        public void previous() {
            setQueryText(-1, -1, parent.getQueryTextAt(index - 1), index - 1);
        }

        protected void enableScrollButtons() {
            if (parent.hasOutputPane()) {
                previousButton.setEnabled(index > 1);
                nextButton.setEnabled(index < parent.getResultSetTabCount());
            }
            else {
                previousButton.setEnabled(index > 0);
                nextButton.setEnabled(index < parent.getResultSetTabCount() - 1);
            }
        }
        
        public void hidePopup() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    hidePanel();
                }
            });
        }        
        
        public void copy() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Toolkit.getDefaultToolkit().getSystemClipboard().
                                setContents(new StringSelection(query), null);
                    hidePanel();
                }
            });
        }

        public void goToQuery() {
            final String _query = query;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    hidePanel();
                    parent.caretToQuery(_query);
                }
            });
        }

        private RolloverButton createButton(String icon, 
                                            String actionCommand, 
                                            String toolTipText) {
            RolloverButton button = 
                    new RolloverButton(GUIUtilities.loadIcon(icon), toolTipText);
            button.setText(Constants.EMPTY);
            button.setActionCommand(actionCommand);
            button.addActionListener(this);
            button.addMouseListener(QueryTextPopup.this);
            return button;
        }

        
    }
    
}



