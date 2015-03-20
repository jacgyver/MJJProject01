/*
 * PrintPreviewer.java
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


package org.executequery.print;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import org.executequery.GUIUtilities;
import org.executequery.SystemUtilities;
import org.executequery.ActiveComponent;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.swing.layouts.XYConstraints;
import org.underworldlabs.swing.layouts.XYLayout;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Print preview panel.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.3 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class PrintPreviewer extends JDialog
                            implements ActionListener,
                                       ActiveComponent {
    
    /** The title of this panel */
    public static final String TITLE = "Print Preview";
    /** The icon for this panel */
    public static final String FRAME_ICON = "PrintPreview16.gif";
    
    /** The scale combo box */
    private JComboBox scaleCombo;
    
    /** The page width */
    private int m_wPage;
    
    /** The page height */
    private int m_hPage;
    
    /** The page format for this preview */
    private PageFormat format;
    
    /** The <code>Printable</code> object */
    private Printable printable;
    
    /** The preview container */
    private PreviewContainer previewContainer;
    
    /** The print function object */
    private PrintFunction printFunction;
    
    /** <p>Constructs a new instance with the specified
     *  <code>Printable</code> object.
     *
     *  @param the <code>Printable</code> object
     */
    public PrintPreviewer(PrintFunction printFunction) {
        super(GUIUtilities.getParentFrame(), TITLE, true);
        
        this.printFunction = printFunction;
        printable = printFunction.getPrintable();
        
        try  {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** <p>Initializes the state of this instance. */
    private void jbInit() throws Exception {
        format = SystemUtilities.getPageFormat();
        
        if (format.getHeight()==0 || format.getWidth()==0) {
            System.err.println("Unable to determine default page size");
            return;
        }
        
        
        JPanel base = new JPanel(new GridBagLayout());
        base.setPreferredSize(new Dimension(380, 500));
        previewContainer = new PreviewContainer();
        
/*
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        generatePreviewImage();
      }
    });
 */
        JScrollPane scroller = new JScrollPane(previewContainer);
        
        RolloverButton printButton = new RolloverButton("Print", null, 28, 55);
        RolloverButton setupButton = new RolloverButton("Page Setup", null, 28, 85);
        RolloverButton closeButton = new RolloverButton("Close", null, 28, 55);
        
        printButton.setMnemonic('P');
        setupButton.setMnemonic('S');
        closeButton.setMnemonic('C');
        
        String[] scales = {"10 %", "25 %", "50 %", "75 %", "100 %"};
        scaleCombo = new JComboBox(scales);
        scaleCombo.setActionCommand("scales");
        scaleCombo.setPreferredSize(new Dimension(70, 28));
        scaleCombo.setSelectedIndex(2);
        
        closeButton.addActionListener(this);
        setupButton.addActionListener(this);
        printButton.addActionListener(this);
        scaleCombo.addActionListener(this);
        
        JPanel buttonPanel = new JPanel(new XYLayout());
        XYConstraints xyc = new XYConstraints(0, 0, -1, -1);
        buttonPanel.add(printButton, xyc);
        xyc.setConstraints(60, 0, -1, -1);
        buttonPanel.add(scaleCombo, xyc);
        xyc.setConstraints(135, 0, -1, -1);
        buttonPanel.add(setupButton, xyc);
        xyc.setConstraints(225, 0, -1, -1);
        buttonPanel.add(closeButton, xyc);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7,7,0,7);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        base.add(buttonPanel, gbc);
        gbc.insets.bottom = 7;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        base.add(scroller, gbc);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                generatePreviewImage();
            }
        });
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(base, BorderLayout.CENTER);
        
        display();
    }
    
    /**
     * Packs, positions and displays the dialog.
     */
    private void display() {
        pack();
        setLocation(GUIUtilities.getLocationForDialog(getSize()));
        setVisible(true);
    }

    public void dispose() {
        cleanup();
        GUIUtilities.scheduleGC();
        super.dispose();
    }
    
    
    /** <p>How to dispose of this panel. */
    public void cleanup() {
        format = null;
        printable = null;
        previewContainer = null;
        printFunction = null;
    }
    
    /** <p>Performs the button actions.
     *
     *  @param the event object
     */
    public void actionPerformed(ActionEvent e) {
        final String command  = e.getActionCommand();
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                if (command.equals("Close")) {
                    dispose();
                }                
                else if (command.equals("Page Setup")) {
                    showPageSetup();
                }                
                else if (command.equals("Print")) {
                    doPrint();
                }                
                else if (command.equals("scales")) {
                    changeScale();
                }                
            }
            
        });
        
    }
    
    private void changeScale() {
        Thread runner = new Thread() {
            public void run() {
                String str = scaleCombo.getSelectedItem().toString();
                
                if (str.endsWith("%"))
                    str = str.substring(0, str.length() - 1);
                
                str = str.trim();
                int scale = 0;
                
                try {
                    scale = Integer.parseInt(str);
                } catch (NumberFormatException ex) {}
                
                int w = (int)(m_wPage * scale/100);
                int h = (int)(m_hPage * scale/100);
                
                Component[] comps = previewContainer.getComponents();
                for (int k = 0; k < comps.length; k++) {
                    
                    if (!(comps[k] instanceof PagePreview))
                        continue;
                    
                    PagePreview pp = (PagePreview)comps[k];
                    pp.setScaledSize(w, h);
                }
                
                previewContainer.doLayout();
                previewContainer.repaint();
                previewContainer.getParent().getParent().validate();
            }
        };
        
        runner.start();
    }
    
    private void doPrint() {
        PrintUtilities.print(printable, printFunction.getPrintJobName());
    }
    
    private void showPageSetup() {
        
        PageFormat newFormat = PrintUtilities.pageSetup();
        
        if (newFormat == null)
            return;
        
        format = newFormat;
        
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                
                if (printable instanceof TablePrinter) {
                    ((TablePrinter)printable).reset();
                }
                
                previewContainer.invalidate();
                previewContainer.removeAll();
                generatePreviewImage();
                previewContainer.revalidate();
                repaint();
                GUIUtilities.scheduleGC();
            }
            
        });
        
    }
    
    private void generatePreviewImage() {
        
        int orientation = format.getOrientation();
        
        m_wPage = (int)(format.getWidth());
        m_hPage = (int)(format.getHeight());
        
        int scale = 50;
        int w = (int)(m_wPage * scale/100);
        int h = (int)(m_hPage * scale/100);
        
        int pageIndex = 0;
        
        try {
            
            while (true) {
                BufferedImage img = new BufferedImage(m_wPage, m_hPage,
                                                      BufferedImage.TYPE_INT_RGB);
                Graphics g = img.getGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, m_wPage, m_hPage);

                if (printable.print(g, format, pageIndex) != Printable.PAGE_EXISTS) {
                    break;
                }

                PagePreview pp = new PagePreview(w, h, img);
                previewContainer.add(pp);
                pageIndex++;
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Printing error: " + e.toString());
        }
    }
    
    
    class PreviewContainer extends JPanel {
        
        protected int H_GAP = 5;
        protected int V_GAP = 5;
        
        public Dimension getPreferredSize() {
            int n = getComponentCount();
            
            if (n == 0)
                return new Dimension(H_GAP, V_GAP);
            
            Component comp = getComponent(0);
            Dimension dc = comp.getPreferredSize();
            int w = dc.width;
            int h = dc.height;
            
            Dimension dp = getParent().getSize();
            int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
            int nRow = n / nCol;
            
            if (nRow * nCol < n)
                nRow++;
            
            int ww = nCol * (w + H_GAP) + H_GAP;
            int hh = nRow * (h + V_GAP) + V_GAP;
            Insets ins = getInsets();
            
            return new Dimension(ww + ins.left + ins.right, hh + ins.top + ins.bottom);
        }
        
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
        
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        
        public void doLayout() {
            Insets ins = getInsets();
            int x = ins.left + H_GAP;
            int y = ins.top + V_GAP;
            
            int n = getComponentCount();
            
            if (n == 0)
                return;
            
            Component comp = getComponent(0);
            Dimension dc = comp.getPreferredSize();
            int w = dc.width;
            int h = dc.height;
            
            Dimension dp = getParent().getSize();
            int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
            int nRow = n / nCol;
            
            if (nRow*nCol < n)
                nRow++;
            
            int index = 0;
            
            for (int k = 0; k < nRow; k++) {
                
                for (int m = 0; m < nCol; m++) {
                    
                    if (index >= n)
                        return;
                    
                    comp = getComponent(index++);
                    comp.setBounds(x, y, w, h);
                    x += w + H_GAP;
                    
                }
                
                y += h + V_GAP;
                x = ins.left + H_GAP;
                
            }
            
        }
        
    } // class PreviewContainer
    
    
    class PagePreview extends JPanel {
        
        protected int m_w;
        protected int m_h;
        protected Image m_source;
        protected Image m_img;
        
        public PagePreview(int w, int h, Image source) {
            m_w = w;
            m_h = h;
            m_source= source;
            m_img = m_source.getScaledInstance(m_w, m_h, Image.SCALE_SMOOTH);
            m_img.flush();
            setBackground(Color.white);
            setBorder(new MatteBorder(1, 1, 2, 2, Color.black));
        }
        
        public void setScaledSize(int w, int h) {
            m_w = w;
            m_h = h;
            m_img = m_source.getScaledInstance(m_w, m_h, Image.SCALE_SMOOTH);
            repaint();
        }
        
        public Dimension getPreferredSize() {
            Insets ins = getInsets();
            return new Dimension(m_w + ins.left + ins.right,
            m_h + ins.top + ins.bottom);
        }
        
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
        
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
        
        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(m_img, 0, 0, this);
            paintBorder(g);
        }
        
    } // class PagePreview
    
}



