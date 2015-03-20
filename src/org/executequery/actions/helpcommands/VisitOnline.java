/*
 * VisitOnline.java
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

package org.executequery.actions.helpcommands;

import java.awt.event.ActionEvent;
import org.executequery.util.BrowserLauncherUtils;
import org.underworldlabs.swing.actions.BaseCommand;

/** 
 * Executes the Help | Visit executequery.org command.<br>
 * This will open a browser window with URL http://executequery.org.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.5 $
 * @date     $Date: 2006/09/15 13:02:49 $
 */
public class VisitOnline implements BaseCommand {

    public void execute(ActionEvent e) {
        BrowserLauncherUtils.launch("http://executequery.org");
        //BrowserLauncherUtils.launch("mailto:takisd@executequery.org");
    }

}