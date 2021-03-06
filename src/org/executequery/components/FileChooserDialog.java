/*
 * FileChooserDialog.java
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.HeadlessException;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;

import org.executequery.Constants;
import org.executequery.GUIUtilities;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * This class provides a minor modification to the
 * <code>JFileChooser</code> class by setting the
 * location of the resultant dialog to the center of the
 * desktop pane as opposed to the default implementation
 * which is relative to the parent frame and centered to
 * the screen.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.6 $
 * @date     $Date: 2006/09/06 09:30:58 $
 */
public class FileChooserDialog extends JFileChooser {
    
    /** The last open file path */
    private static String lastOpenFilePath;
    /** Any custom panels */
    protected JPanel customPanel;
    
    /** <p>Constructs a new instance propagating
     *  the call to the super class constructor. */
    public FileChooserDialog() {
        super(lastOpenFilePath);
    }
    
    /** <p>Constructs a new instance using the specified
     *  file propagating the call to the super class constructor.
     *
     *  @param a 'default' <code>File</code> object
     */
    public FileChooserDialog(File defaultFile) {
        super(defaultFile);
    }
    
    /** <p>Constructs a new instance using the specified
     *  path propagating the call to the super class constructor.
     *
     *  @param a <code>String</code> giving the path
     *         to a file or directory
     */
    public FileChooserDialog(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }
    
    public int showOpenDialog(Component parent) throws HeadlessException {
        int result = super.showOpenDialog(parent);
        resetLastOpenFilePath(result);
        return result;
    }
    
    public int showSaveDialog(Component parent) throws HeadlessException {
        int result = super.showSaveDialog(parent);
        resetLastOpenFilePath(result);
        File file = getSelectedFile();

        if (file == null|| result == CANCEL_OPTION) {
            return CANCEL_OPTION;
        }
        
        if (file.exists()) {
            int _result = GUIUtilities.displayConfirmCancelDialog("Overwrite existing file?");

            if (_result == JOptionPane.CANCEL_OPTION) {
                return CANCEL_OPTION;
            } else if (_result == JOptionPane.NO_OPTION) {
                return showSaveDialog(parent);
            }
            
        }
        
        return result;
    }
    
    public int showDialog(Component parent, String approveButtonText)
      throws HeadlessException {
        int result = super.showDialog(parent, approveButtonText);
        resetLastOpenFilePath(result);
        return result;
    }
    
    public static String getLastOpenFilePath() {
        return lastOpenFilePath;
    }
    
    private void resetLastOpenFilePath(int result) {
        if (result != JFileChooser.CANCEL_OPTION) {
            lastOpenFilePath = getSelectedFile().getPath();
        }
    }
    
    protected JDialog createDialog(Component parent) throws HeadlessException {
        Frame frame = parent instanceof Frame ? (Frame)parent :
            (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);
            
        String title = getUI().getDialogTitle(this);

        JDialog dialog = new JDialog(frame, title, true);

        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);

        // add any custom panel
        if (customPanel != null) {
            contentPane.add(customPanel, BorderLayout.SOUTH);
        }

        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations =
            UIManager.getLookAndFeel().getSupportsWindowDecorations();

            if (supportsWindowDecorations) {
                dialog.getRootPane().setWindowDecorationStyle(
                                            JRootPane.FILE_CHOOSER_DIALOG);
            }

        }

        setFileView(new DefaultFileView());
        dialog.pack();

        // set location to the center of the desktop pane
        dialog.setLocation(GUIUtilities.getLocationForDialog(dialog.getSize()));
        return dialog;
    }
/*
    class DefaultFileView extends FileView {

        private ImageIcon ZIP_ICON;
        private ImageIcon JAR_ICON;
        private ImageIcon TEXT_ICON;
        private ImageIcon SQL_ICON;
        private ImageIcon EQ_ICON;
        private ImageIcon JPEG_ICON;
        private ImageIcon GIF_ICON;
        private ImageIcon XML_ICON;
        private ImageIcon LOG_ICON;
        private ImageIcon EXE_ICON;
        private ImageIcon SH_ICON;
        private ImageIcon DEFAULT_ICON;

        public DefaultFileView() {

            SQL_ICON = GUIUtilities.loadIcon("DBImage16.gif", true);
            JAR_ICON = GUIUtilities.loadIcon("Jar16.gif", true);
            LOG_ICON = GUIUtilities.loadIcon("LogFile16.gif", true);
            EQ_ICON = GUIUtilities.loadIcon("ApplicationIcon16.gif", true);

            if (GUIUtilities.getLookAndFeel() == Constants.SMOOTH_GRADIENT_LAF ||
                  GUIUtilities.getLookAndFeel() == Constants.EQ_THM) {
                ZIP_ICON = GUIUtilities.loadIcon("ZipFile16.gif", true);
                TEXT_ICON = GUIUtilities.loadIcon("TextFile16.gif", true);
                JPEG_ICON = GUIUtilities.loadIcon("JpegFile16.gif", true);
                GIF_ICON = GUIUtilities.loadIcon("GifFile16.gif", true);
                XML_ICON = GUIUtilities.loadIcon("XmlFile16.gif", true);
                EXE_ICON = GUIUtilities.loadIcon("ExeFile16.gif", true);
                SH_ICON = GUIUtilities.loadIcon("ShFile16.gif", true);
                DEFAULT_ICON = GUIUtilities.loadIcon("DefaultFile16.gif", true);
            }

            
        }

        public String getName(File f) {
            String name = f.getName();
            return name.equals(Constants.EMPTY) ? f.getPath() : name;
        }

        public String getDescription(File f) {
            return getTypeDescription(f);
        }

        public String getTypeDescription(File f) {
            String name = f.getName().toLowerCase();

            if (name.endsWith(".jar"))
                return "Java Archive File";

            else if (name.endsWith(".sql"))
                return "SQL Script File";

            else if (name.endsWith(".eqd"))
                return "Execute Query ERD File";

            else if (name.endsWith(".zip"))
                return "ZIP Archive File";

            else if (name.endsWith(".txt"))
                return "Text File";

            else if (name.endsWith(".gif"))
                return "GIF Image File";

            else if (name.endsWith(".jpeg"))
                return "JPEG Image File";

            else if (name.endsWith(".jpg"))
                return "JPEG Image File";

            else if (name.endsWith(".xml"))
                return "XML File";

            else if (name.endsWith(".log"))
                return "System Log File";

            else if (name.endsWith(".exe"))
                return "Executable File";

            else if (name.endsWith(".bat"))
                return "Windows Batch Script";

            else if (name.endsWith(".sh"))
                return "Unix Shell Script";

            else
                return "File";

        }

        public Icon getIcon(File f) {
            ImageIcon icon = null;
            String name = f.getName().toLowerCase();

            if (name.endsWith(".jar"))
                icon = JAR_ICON;

            else if (name.endsWith(".sql"))
                icon = SQL_ICON;

            else if (name.endsWith(".eqd"))
                icon = EQ_ICON;

            else if (name.endsWith(".log"))
                icon = LOG_ICON;

            else {

                if (GUIUtilities.getLookAndFeel() == Constants.SMOOTH_GRADIENT_LAF ||
                      GUIUtilities.getLookAndFeel() == Constants.EQ_THM) {

                    if (name.endsWith(".zip"))
                        icon = ZIP_ICON;

                    else if (name.endsWith(".txt"))
                        icon = TEXT_ICON;

                    else if (name.endsWith(".gif"))
                        icon = GIF_ICON;

                    else if (name.endsWith(".jpeg"))
                        icon = JPEG_ICON;

                    else if (name.endsWith(".jpg"))
                        icon = JPEG_ICON;

                    else if (name.endsWith(".xml"))
                        icon = XML_ICON;

                    else if (name.endsWith(".exe"))
                        icon = EXE_ICON;

                    else if (name.endsWith(".bat") || name.endsWith(".sh"))
                        icon = SH_ICON;

                    else if (!f.isDirectory())
                        icon = DEFAULT_ICON;

                    else
                        icon = null;

                }

            }

            return icon;

        }

        public Boolean isTraversable(File f) {
            return f.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
        }

    } // class DefaultFileView
*/
}

    class DefaultFileView extends FileView {

        private ImageIcon ZIP_ICON;
        private ImageIcon JAR_ICON;
        private ImageIcon TEXT_ICON;
        private ImageIcon SQL_ICON;
        private ImageIcon EQ_ICON;
        private ImageIcon JPEG_ICON;
        private ImageIcon GIF_ICON;
        private ImageIcon XML_ICON;
        private ImageIcon LOG_ICON;
        private ImageIcon EXE_ICON;
        private ImageIcon SH_ICON;
        private ImageIcon DEFAULT_ICON;

        public DefaultFileView() {

            SQL_ICON = GUIUtilities.loadIcon("DBImage16.gif", true);
            JAR_ICON = GUIUtilities.loadIcon("Jar16.gif", true);
            LOG_ICON = GUIUtilities.loadIcon("LogFile16.gif", true);
            EQ_ICON = GUIUtilities.loadIcon("ApplicationIcon16.gif", true);

            if (GUIUtilities.getLookAndFeel() == Constants.SMOOTH_GRADIENT_LAF ||
                  GUIUtilities.getLookAndFeel() == Constants.EQ_THM) {
                ZIP_ICON = GUIUtilities.loadIcon("ZipFile16.gif", true);
                TEXT_ICON = GUIUtilities.loadIcon("TextFile16.gif", true);
                JPEG_ICON = GUIUtilities.loadIcon("JpegFile16.gif", true);
                GIF_ICON = GUIUtilities.loadIcon("GifFile16.gif", true);
                XML_ICON = GUIUtilities.loadIcon("XmlFile16.gif", true);
                EXE_ICON = GUIUtilities.loadIcon("ExeFile16.gif", true);
                SH_ICON = GUIUtilities.loadIcon("ShFile16.gif", true);
                DEFAULT_ICON = GUIUtilities.loadIcon("DefaultFile16.gif", true);
            }
        }

        public String getName(File f) {
            String name = f.getName();
            return name.equals(Constants.EMPTY) ? f.getPath() : name;
        }

        public String getDescription(File f) {
            return getTypeDescription(f);
        }

        public String getTypeDescription(File f) {
            String name = f.getName().toLowerCase();

            if (name.endsWith(".jar"))
                return "Java Archive File";

            else if (name.endsWith(".sql"))
                return "SQL Script File";

            else if (name.endsWith(".eqd"))
                return "Execute Query ERD File";

            else if (name.endsWith(".zip"))
                return "ZIP Archive File";

            else if (name.endsWith(".txt"))
                return "Text File";

            else if (name.endsWith(".gif"))
                return "GIF Image File";

            else if (name.endsWith(".jpeg"))
                return "JPEG Image File";

            else if (name.endsWith(".jpg"))
                return "JPEG Image File";

            else if (name.endsWith(".xml"))
                return "XML File";

            else if (name.endsWith(".log"))
                return "System Log File";

            else if (name.endsWith(".exe"))
                return "Executable File";

            else if (name.endsWith(".bat"))
                return "Windows Batch Script";

            else if (name.endsWith(".sh"))
                return "Unix Shell Script";

            else
                return "File";

        }

        public Icon getIcon(File f) {
            ImageIcon icon = null;
            String name = f.getName().toLowerCase();

            if (name.endsWith(".jar"))
                icon = JAR_ICON;

            else if (name.endsWith(".sql"))
                icon = SQL_ICON;

            else if (name.endsWith(".eqd"))
                icon = EQ_ICON;

            else if (name.endsWith(".log"))
                icon = LOG_ICON;

            else {

                if (GUIUtilities.getLookAndFeel() == Constants.SMOOTH_GRADIENT_LAF ||
                      GUIUtilities.getLookAndFeel() == Constants.EQ_THM) {

                    if (name.endsWith(".zip"))
                        icon = ZIP_ICON;

                    else if (name.endsWith(".txt"))
                        icon = TEXT_ICON;

                    else if (name.endsWith(".gif"))
                        icon = GIF_ICON;

                    else if (name.endsWith(".jpeg"))
                        icon = JPEG_ICON;

                    else if (name.endsWith(".jpg"))
                        icon = JPEG_ICON;

                    else if (name.endsWith(".xml"))
                        icon = XML_ICON;

                    else if (name.endsWith(".exe"))
                        icon = EXE_ICON;

                    else if (name.endsWith(".bat") || name.endsWith(".sh"))
                        icon = SH_ICON;

                    else if (!f.isDirectory())
                        icon = DEFAULT_ICON;

                    else
                        icon = null;

                }

            }

            return icon;

        }

        public Boolean isTraversable(File f) {
            return f.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
        }

    } // class DefaultFileView


