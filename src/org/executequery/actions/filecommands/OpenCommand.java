/*
 * OpenCommand.java
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


package org.executequery.actions.filecommands;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.executequery.GUIUtilities;
import org.executequery.components.OpenFileDialog;
import org.underworldlabs.swing.actions.BaseCommand;

import org.executequery.util.FileLoader;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 * The File | Open command.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/06/03 01:24:59 $
 */
public class OpenCommand implements BaseCommand {
    
    public void execute(ActionEvent e) {
        OpenFileDialog fileChooser = new OpenFileDialog();
        int result = fileChooser.showOpenDialog(GUIUtilities.getParentFrame());

        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        FileLoader loader = new FileLoader();
        loader.openFile(file, fileChooser.getOpenWith());
    }

}



