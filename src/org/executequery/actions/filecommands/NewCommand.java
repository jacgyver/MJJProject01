/*
 * NewCommand.java
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
import org.executequery.actions.toolscommands.EditorCommand;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** <p>Command execution for File | New.
 *  This will open a new Query Editor frame only.
 *
 *  @author   Takis Diakoumis
 * @version  $Revision: 1.4 $
 * @date     $Date: 2006/05/14 06:56:52 $
 */
public class NewCommand extends EditorCommand { //OpenFrameCommand
//                        implements BaseCommand {

    /*
    public void execute(ActionEvent e) {
        if (!isFrameOpen(QueryEditor.TITLE)) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUIUtilities.addInternalFrame(QueryEditor.TITLE,
                                                  QueryEditor.FRAME_ICON, 
                                                  new QueryEditor(),
                                                  true, true, true, true, false);
                }
            });
        }
    }
*/
}



